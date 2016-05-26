package net.aurynj.rne.locatmonster;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashSet;

public class GoogleApiManager
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private HashSet<DelegatedOnConnectedListener> mOnConnectedListenerSet = new HashSet<>();

    public GoogleApiManager(Context context) {
        mContext = context;
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(GoogleApiManager.this)
                .addOnConnectionFailedListener(GoogleApiManager.this)
                .addApi(LocationServices.API)
                .build();
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void attachOnConnectedListener(DelegatedOnConnectedListener onConnectedListener) {
        mOnConnectedListenerSet.add(onConnectedListener);
    }

    public void detachOnConnectedListener(DelegatedOnConnectedListener onConnectedListener) {
        mOnConnectedListenerSet.remove(onConnectedListener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("GoogleApiManager", "Connection Failed");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v("GoogleApiManager", "Connected");
        for (DelegatedOnConnectedListener listener: mOnConnectedListenerSet) {
            listener.onConnected(bundle);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("GoogleApiManager", "Connection Suspended");
    }

    public interface DelegatedOnConnectedListener {
        public void onConnected(@Nullable Bundle bundle);
    }
}
