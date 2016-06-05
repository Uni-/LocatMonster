package net.aurynj.rne.locatmonster.appframework;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public abstract class BaseActivity extends AppCompatActivity {
    private final LocationServiceConnection mLocationServiceConnection = new LocationServiceConnection();
    private LocatMonsterService mLocatMonsterService;
    private boolean mBoundToService;

    @Override
    protected void onStart() {
        super.onStart();
        bindLocatMonsterService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindLocatMonsterService();
    }

    protected void onBindService() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LocatMonsterService.LocationContainer.REQUEST_CHECK_LOCATION_SETTINGS) {
            if (resultCode == RESULT_OK) {
                mLocatMonsterService.mLocationContainer.startLocationUpdates();
            } else {
                LocatMonsterServiceHelper.stopLocatMonsterService(BaseActivity.this);
                unbindLocatMonsterService();
                onServiceOff();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LocatMonsterService.LocationContainer.REQUEST_PERMISSION_LOCATION_ACCESS) {
            String msg = "Result: ";
            for (int i = 0; i < permissions.length; i++) {
                // TODO debug and impl post-result care
                msg += "\n" + permissions[i] + " " + grantResults[i];
            }
            Log.v("requestPermissions", msg);
        }
    }

    protected boolean boundToService() {
        return mBoundToService;
    }

    protected LocatMonsterService getLocatMonsterService() {
        return mLocatMonsterService;
    }

    protected void bindLocatMonsterService() {
        // This method does not start service with bindService
        // (because unbindService may not stop the service)
        // Instead, the service should be started before this method then bindService inside
        // if service is not running, this method is silently ignored
        // This method should be called only in MainActivity where service gets manually on and off
        if (!LocatMonsterServiceHelper.isLocatMonsterServiceRunning(BaseActivity.this)) {
            // Service is not even running
            return;
        }
        if (!mBoundToService) {
            boolean success = bindService(
                    LocatMonsterServiceHelper.getLocatMonsterServiceIntent(this),
                    mLocationServiceConnection,
                    Context.BIND_AUTO_CREATE
            );
            if (!success) {
                // TODO: make user notified and failsafe route
            }
            Log.v(getClass().getSimpleName(), "Activity " + getClass().getSimpleName() + " (extending BaseActivity) bound to service");
        }
    }

    protected void unbindLocatMonsterService() {
        // This method should be called only in MainActivity where service gets manually on and off
        if (!LocatMonsterServiceHelper.isLocatMonsterServiceRunning(BaseActivity.this)) {
            // Service is not even running
            return;
        }
        if (mBoundToService && mLocatMonsterService != null) {
            mLocatMonsterService.removeLivingActivity(BaseActivity.this);
            unbindService(mLocationServiceConnection);
            mBoundToService = false;
            mLocatMonsterService = null;
            Log.v(getClass().getSimpleName(), "Activity " + getClass().getSimpleName() + " (extending BaseActivity) unbound from service");
        }
    }

    protected void onServiceOff() {}

    protected void onLocationRefreshed() {}
    
    protected class LocationServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.v("ServiceConnection", "onServiceConnected");
            LocatMonsterService.LocalBinder binder = (LocatMonsterService.LocalBinder) iBinder;
            mLocatMonsterService = binder.getService();
            mLocatMonsterService.addLivingActivity(BaseActivity.this);
            mBoundToService = true;
            BaseActivity.this.onBindService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.v("ServiceConnection", "onServiceDisconnected");
            mLocatMonsterService = null;
            mBoundToService = false;
        }
    }
}
