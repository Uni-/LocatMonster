package net.aurynj.rne.locatmonster.app;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.HashSet;
import java.util.TimerTask;

import net.aurynj.rne.locatmonster.*;

public class LocatMonsterService extends Service
        implements OnLocationRefreshedListener {
    private long mTimeMilisServiceStarted;
    private final Binder mBinder = new LocalBinder();
    private final LocationRefreshTask mLocationRefreshTask = new LocationRefreshTask();

    GoogleApiManager mGoogleApiManager;
    LocationContainer mLocationContainer;
    LocatMonsterNotificationHelper mNotificationHelper;

    public AppCompatActivity mLivingActivity;

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

        AlertDialog.Builder builder = new
                AlertDialog.Builder(this, android.support.v7.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("Hi")
                .setMessage("Service Dialog");
        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();

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

    public class LocationContainer
            implements GoogleApiManager.DelegatedOnConnectedListener {
        @NonNull
        final Context mContext;
        @NonNull
        final GoogleApiManager mGoogleApiManager;
        @NonNull
        final GmsLocationHandler mGmsLocationHandler;
        @NonNull
        LocationRequest mLocationRequest;
        @NonNull
        HashSet<OnLocationRefreshedListener> mOnLocationRefreshedListenerSet;
        Location mLastLocation;

        public LocationContainer(@NonNull Context context, @NonNull GoogleApiManager googleApiManager) {
            mContext = context;
            mGoogleApiManager = googleApiManager;
            mGmsLocationHandler = new GmsLocationHandler();
            mOnLocationRefreshedListenerSet = new HashSet<>();
            googleApiManager.attachOnConnectedListener(LocationContainer.this);
            initLocationRequest();
        }

        public void attachOnLocationRefreshedListener(OnLocationRefreshedListener onLocationRefreshedListener) {
            mOnLocationRefreshedListenerSet.add(onLocationRefreshedListener);
        }

        public void detachOnLocationRefreshedListener(OnLocationRefreshedListener onLocationRefreshedListener) {
            mOnLocationRefreshedListenerSet.remove(onLocationRefreshedListener);
        }

        public void initLocationRequest() {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            LocationSettingsRequest.Builder lsrBuilder =
                    new LocationSettingsRequest.Builder();
            lsrBuilder.addLocationRequest(mLocationRequest);

            final LocationSettingsRequest locationSettingsRequest = lsrBuilder.build();

            final PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(
                            mGoogleApiManager.getGoogleApiClient(), locationSettingsRequest
                    );

            result.setResultCallback(new LocationSettingsResultCallback());
        }

        public void connect() {
            mGoogleApiManager.getGoogleApiClient().connect();
        }

        public void disconnect() {
            mGoogleApiManager.getGoogleApiClient().connect();
        }

        public void poke() {
            refreshLocation();
            broadcastLocationRefreshed();
        }

        protected void broadcastLocationRefreshed() {
            for (OnLocationRefreshedListener listener : mOnLocationRefreshedListenerSet) {
                listener.onLocationRefreshed(mLastLocation);
            }
        }

        protected void refreshLocation() {
            if (!LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiManager.getGoogleApiClient()).isLocationAvailable()) {
                Log.e("LocationContainer", "Location Unavailable");
                return;
            }
            if (
                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                    PackageManager.PERMISSION_GRANTED
                    ) {
                Log.v("LocationContainer", "Permission denied to access location");
                //AppCompatActivity activity;
                //activity.requestPermissions();

                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiManager.getGoogleApiClient()
            );
        }

        protected void startLocationUpdates() {
            if (
                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                    PackageManager.PERMISSION_GRANTED
                    ) {
                Log.v("LocationContainer", "Permission denied to access location");
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiManager.getGoogleApiClient(), mLocationRequest, mGmsLocationHandler
            );
        }

        protected void stopLocationUpdates() {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiManager.getGoogleApiClient(), mGmsLocationHandler
            );
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.v("LocationContainer", "Conntected to Google API");
            startLocationUpdates();
        }

        private class LocationSettingsResultCallback implements ResultCallback<LocationSettingsResult> {
            protected static final int REQUEST_CHECK_SETTINGS = 0x1;

            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.v("LSRCallback", "Status: Success");
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.v("LSRCallback", "Status: Resolution Required");
//                    try {
//                        status.startResolutionForResult(
//                                mActivity, REQUEST_CHECK_SETTINGS
//                        );
//                    } catch (IntentSender.SendIntentException e) {
//                        e.printStackTrace();
//                    }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.v("LSRCallback", "Status: Settings Change Unavailable");
                        break;
                    default:
                        Log.v("LSRCallback", "Status: `" + LocationSettingsStatusCodes.getStatusCodeString(status.getStatusCode()));
                        break;
                }
            }

        }

        private class GmsLocationHandler implements LocationListener {
            @Override
            public void onLocationChanged(Location location) {
                Log.v("LocationContainer", "GmsLocationHandler: Location changed");
                mLastLocation = location;
                LocationContainer.this.broadcastLocationRefreshed();
            }
        }
    }
}
