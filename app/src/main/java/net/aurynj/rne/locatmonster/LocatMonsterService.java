package net.aurynj.rne.locatmonster;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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

import java.util.Date;

public class LocatMonsterService extends Service
        implements LocationContainer.OnLocationRefreshedListener {
    NotificationManager mNotificationManager;
    private long mTimeMilisServiceStarted;
    private final Binder mBinder = new LocalBinder();
    private static final int NOTIFICATION_ID = 1;

    GoogleApiManager mGoogleApiManager;
    LocationContainer mLocationContainer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mTimeMilisServiceStarted == 0) {
            mTimeMilisServiceStarted = System.currentTimeMillis();
        }
        Toast.makeText(LocatMonsterService.this, "LocatMonster Service Started", Toast.LENGTH_SHORT).show();

        if (mNotificationManager == null) {
            //mNotificationManager = NotificationManagerCompat.from(LocatMonsterService.this);
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        Bundle bundle;
        if (intent != null && (bundle = intent.getExtras()) != null) {
            Log.v("LocatMonsterService", "Bundle found!");
            if (bundle.getString("Tag", "").equals("NotificationClicked")) {
                mNotificationManager.cancel(NOTIFICATION_ID);
            }
        } else {
            Log.v("LocatMonsterService", "Bundle not found!");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(LocatMonsterService.this);
            builder.setSmallIcon(R.drawable.ic_media_play);
            builder.setContentTitle("LocatMonster");
            builder.setContentText("Test");
            Intent intent_ = new Intent(LocatMonsterService.this.getApplicationContext(), LocatMonsterService.class);
            intent_.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent_.putExtra("Tag", "NotificationClicked");
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(LocatMonsterService.this.getApplicationContext());
            taskStackBuilder.addParentStack(MainActivity.class);
            taskStackBuilder.addNextIntent(intent_);
            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            Notification noti = builder.build();

            mNotificationManager.notify(NOTIFICATION_ID, noti);
        }

        if (mGoogleApiManager == null) {
            mGoogleApiManager = new GoogleApiManager(
                    LocatMonsterService.this.getApplicationContext()
            );
        }

        if (mLocationContainer == null) {
            assert(mGoogleApiManager != null);
            mLocationContainer = new LocationContainer(
                    LocatMonsterService.this,
                    mGoogleApiManager
            );
            mLocationContainer.attachOnLocationRefreshedListener(LocatMonsterService.this);
        }
        mLocationContainer.connect();

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
}
