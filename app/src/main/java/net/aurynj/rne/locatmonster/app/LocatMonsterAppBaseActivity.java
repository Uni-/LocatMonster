package net.aurynj.rne.locatmonster.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class LocatMonsterAppBaseActivity extends AppCompatActivity {
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
        if (!LocatMonsterServiceHelper.isLocatMonsterServiceRunning(LocatMonsterAppBaseActivity.this)) {
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
            Toast.makeText(LocatMonsterAppBaseActivity.this, "Activity bound to service", Toast.LENGTH_SHORT).show();
        }
    }

    protected void unbindLocatMonsterService() {
        // This method should be called only in MainActivity where service gets manually on and off
        if (!LocatMonsterServiceHelper.isLocatMonsterServiceRunning(LocatMonsterAppBaseActivity.this)) {
            // Service is not even running
            return;
        }
        if (mBoundToService && mLocatMonsterService != null) {
            unbindService(mLocationServiceConnection);
            mBoundToService = false;
            mLocatMonsterService = null;
            Toast.makeText(LocatMonsterAppBaseActivity.this, "Activity unbound from service", Toast.LENGTH_SHORT).show();
        }
    }
    
    protected class LocationServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.v("ServiceConnection", "onServiceConnected");
            LocatMonsterService.LocalBinder binder = (LocatMonsterService.LocalBinder) iBinder;
            mLocatMonsterService = binder.getService();
            mLocatMonsterService.mLivingActivity = LocatMonsterAppBaseActivity.this;
            mBoundToService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.v("ServiceConnection", "onServiceDisconnected");
            mLocatMonsterService = null;
            mBoundToService = false;
        }
    }
}
