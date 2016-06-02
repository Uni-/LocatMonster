package net.aurynj.rne.locatmonster.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import java.util.TimerTask;

import net.aurynj.rne.locatmonster.*;

public class LocatMonsterService extends Service
        implements LocationContainer.OnLocationRefreshedListener {
    private long mTimeMilisServiceStarted;
    private final Binder mBinder = new LocalBinder();
    private final LocationRefreshTask mLocationRefreshTask = new LocationRefreshTask();

    GoogleApiManager mGoogleApiManager;
    LocationContainer mLocationContainer;
    LocatMonsterNotificationHelper mNotificationHelper;

    Arena mUserPrefs = new Arena();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mTimeMilisServiceStarted == 0) {
            mTimeMilisServiceStarted = System.currentTimeMillis();
        }
        Toast.makeText(LocatMonsterService.this, "LocatMonster Service Started", Toast.LENGTH_SHORT).show();

        if (mGoogleApiManager == null) {
            mGoogleApiManager = new GoogleApiManager(LocatMonsterService.this.getApplicationContext());
        }

        if (mLocationContainer == null) {
            mLocationContainer = new LocationContainer(LocatMonsterService.this, mGoogleApiManager);
        }
        mLocationContainer.connect();

        if (mNotificationHelper == null) {
            mNotificationHelper = new LocatMonsterNotificationHelper(LocatMonsterService.this);
        }

        Bundle bundle;
        if (intent != null && (bundle = intent.getExtras()) != null) {
            Log.v("LocatMonsterService", "Bundle found!");
            if (bundle.getString("Tag", "").equals("NotificationClicked")) {
                mNotificationHelper.hide();
            }
        } else {
            mNotificationHelper.show();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mTimeMilisServiceStarted = 0;
        mLocationContainer.disconnect();
        mNotificationHelper.hide();
        Toast.makeText(LocatMonsterService.this, "LocatMonster Service Destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public void onLocationRefreshed(@Nullable Location location) {
    }

    public Location getLastLocation() {
        return mLocationContainer.mLastLocation;
    }

    protected class LocalBinder extends Binder {
        LocatMonsterService getService() {
            return LocatMonsterService.this;
        }
    }

    protected class LocationRefreshTask extends TimerTask {
        @Override
        public void run() {
            Location location = LocatMonsterService.this.getLastLocation();
        }
    }

    public class LocatMonsterNotificationHelper {
        private static final int NOTIFICATION_ID = R.string.app_name;
        final LocatMonsterService mService;
        final NotificationManager mNotificationManager;

        public LocatMonsterNotificationHelper(LocatMonsterService service) {
            mService = service;
            mNotificationManager = (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        public void build() {
        }

        public void show() {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mService.getApplicationContext());
            builder.setSmallIcon(android.R.drawable.ic_media_play);
            builder.setContentTitle("LocatMonster");
            builder.setContentText("Test");
            Intent intent_ = new Intent(mService.getApplicationContext(), LocatMonsterService.class);
            intent_.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent_.putExtra("Tag", "NotificationClicked");
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(mService.getApplicationContext());
            taskStackBuilder.addParentStack(MainActivity.class);
            taskStackBuilder.addNextIntent(intent_);
            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            Notification notification = builder.build();

            mNotificationManager.notify(NOTIFICATION_ID, notification);
        }

        public void hide() {
            mNotificationManager.cancel(NOTIFICATION_ID);
        }
    }
}
