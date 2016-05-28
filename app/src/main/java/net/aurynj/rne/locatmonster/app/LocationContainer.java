package net.aurynj.rne.locatmonster.app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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

import java.util.HashSet;

public class LocationContainer
        implements GoogleApiManager.DelegatedOnConnectedListener {
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
        for (OnLocationRefreshedListener listener: mOnLocationRefreshedListenerSet) {
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

    public interface OnLocationRefreshedListener {
        public void onLocationRefreshed(Location location);
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
