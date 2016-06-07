package net.aurynj.rne.locatmonster.appframework;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
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
import android.util.Log;

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
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.aurynj.rne.locatmonster.*;
import net.aurynj.rne.locatmonster.model.RegionClass;

public class LocatMonsterService extends Service
        implements OnLocationRefreshedListener {
    private static final int SECOND_BY_MILIS = 1000;
    private final Binder mBinder = new LocalBinder();
    private final Timer mTimer = new Timer();
    private final TimerTask mTimerTask = new LocationCheckTask();

    private boolean mTimerTaskScheduled = false;
    private long mTimeMilisServiceStarted;
    private List<BaseActivity> mLivingActivityList = new ArrayList<>();

    GoogleApiManager mGoogleApiManager;
    LocationContainer mLocationContainer;
    LocatMonsterNotificationHelper mNotificationHelper;

    Arena mCurrentArena;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mTimeMilisServiceStarted == 0) {
            mTimeMilisServiceStarted = System.currentTimeMillis();
        }
        Log.v("LocatMonsterService", "Service started with command");

        if (mGoogleApiManager == null) {
            mGoogleApiManager = new GoogleApiManager(LocatMonsterService.this.getApplicationContext());
        }

        if (mLocationContainer == null) {
            mLocationContainer = new LocationContainer(LocatMonsterService.this, mGoogleApiManager);
        }
        mLocationContainer.attachOnLocationRefreshedListener(this);
        mLocationContainer.connect();

        if (mNotificationHelper == null) {
            mNotificationHelper = new LocatMonsterNotificationHelper(LocatMonsterService.this);
        }

        /*
        AlertDialog.Builder builder = new
                AlertDialog.Builder(this, android.support.v7.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("Hi")
                .setMessage("Service Dialog");
        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
        */

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void onLocationAvailable() {
        if (mTimerTaskScheduled == false) {
            mTimer.schedule(mTimerTask, 5 * SECOND_BY_MILIS, 60 * SECOND_BY_MILIS);
            mTimerTaskScheduled = true;
        }
    }

    @Override
    public void onDestroy() {
        Log.v("LocatMonsterService", "Service destroyed");

        mTimeMilisServiceStarted = 0;
        mLocationContainer.disconnect();
        mNotificationHelper.hide();
        if (mTimerTaskScheduled) {
            mTimer.cancel();
            mTimerTaskScheduled = false;
        }

        super.onDestroy();
    }

    @Override
    public void onLocationRefreshed(@Nullable Location location) {
        for (BaseActivity activity: mLivingActivityList) {
            activity.onLocationRefreshed();
        }
    }

    protected void addLivingActivity(BaseActivity livingActivity) {
        mLivingActivityList.add(livingActivity);
    }

    protected void removeLivingActivity(BaseActivity divingActivity) {
        mLivingActivityList.remove(divingActivity);
    }

    protected BaseActivity lastLivingActivity() {
        return mLivingActivityList.size() > 0 ? mLivingActivityList.get(mLivingActivityList.size() - 1) : null;
    }

    public Location getLastLocation() {
        return mLocationContainer.mLastLocation;
    }

    public Arena getCurrentArena() {
        return mCurrentArena;
    }

    protected class LocalBinder extends Binder {
        LocatMonsterService getService() {
            return LocatMonsterService.this;
        }
    }

    protected class LocationCheckTask extends TimerTask {
        @Override
        public void run() {
            RegionHelper regionHelper = new RegionHelper();
            Location location = LocatMonsterService.this.getLastLocation();
            RegionClass region = regionHelper.findRegion(new LatLng(location.getLatitude(), location.getLongitude()));
            // TODO generate monster
            mCurrentArena = new Arena();
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

        public void show() throws Exception {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mService.getApplicationContext());
            builder.setSmallIcon(android.R.drawable.ic_media_play);
            builder.setContentTitle("LocatMonster");
            builder.setContentText("자동 전투 중입니다. 수동 전투에 진입하려면 터치하세요!");

            Intent intent = new Intent(mService.getApplicationContext(), LocatMonsterService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("Tag", "NotificationClicked");
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(mService.getApplicationContext());
            taskStackBuilder.addParentStack(BaseActivity.class); // TODO fix this erroneous line before use
            taskStackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(pendingIntent);

            Notification notification = builder.build();

            mNotificationManager.notify(NOTIFICATION_ID, notification);
        }

        public void hide() {
            mNotificationManager.cancel(NOTIFICATION_ID);
        }
    }

    public class LocationContainer implements GoogleApiManager.DelegatedOnConnectedListener {
        protected static final int REQUEST_CHECK_LOCATION_SETTINGS = 0x1FCC;
        protected static final int REQUEST_PERMISSION_LOCATION_ACCESS = 0x34FB;
        @NonNull final Context mContext;
        @NonNull final GoogleApiManager mGoogleApiManager;
        @NonNull final GmsLocationHandler mGmsLocationHandler;
        @NonNull LocationRequest mLocationRequest;
        @NonNull HashSet<OnLocationRefreshedListener> mOnLocationRefreshedListenerSet;
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
                String[] permissions = {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                };
                if (lastLivingActivity() != null) {
                    ActivityCompat.requestPermissions(lastLivingActivity(), permissions, REQUEST_PERMISSION_LOCATION_ACCESS);
                } else {
                    LocatMonsterService.this.stopSelf(); // TODO safe stop
                }
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
                String[] permissions = {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                };
                if (lastLivingActivity() != null) {
                    ActivityCompat.requestPermissions(lastLivingActivity(), permissions, REQUEST_PERMISSION_LOCATION_ACCESS);
                } else {
                    LocatMonsterService.this.stopSelf(); // TODO safe stop
                }
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiManager.getGoogleApiClient(), mLocationRequest, mGmsLocationHandler
            );
            LocatMonsterService.this.onLocationAvailable();
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
                        if (lastLivingActivity() == null) {
                            // TODO: request changing location settings
                            LocatMonsterService.this.stopSelf(); // TODO safe stop
                        }
                        try {
                            status.startResolutionForResult(lastLivingActivity(), REQUEST_CHECK_LOCATION_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.v("LSRCallback", "Status: Settings Change Unavailable");
                        break;
                    default:
                        Log.v("LSRCallback", "Status: " + LocationSettingsStatusCodes.getStatusCodeString(status.getStatusCode()));
                        break;
                }
            }

        }

        private class GmsLocationHandler implements LocationListener {
            @Override
            public void onLocationChanged(Location location) {
                Log.v("LocationContainer", "onLocationChanged");
                mLastLocation = location;
                LocationContainer.this.broadcastLocationRefreshed();
            }
        }
    }
}
