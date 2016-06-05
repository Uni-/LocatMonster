package net.aurynj.rne.locatmonster.appframework;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class LocatMonsterServiceHelper extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            PreferencesManager preferencesManager = new PreferencesManager(context);
            if (preferencesManager.getServiceAutoOn()) {
                startLocatMonsterService(context);
            }
        } else if (action.equals(Intent.ACTION_SHUTDOWN)) {
            stopLocatMonsterService(context);
        }
    }

    public static Intent getLocatMonsterServiceIntent(Context context) {
        return new Intent(context, LocatMonsterService.class);
    }

    public static void startLocatMonsterService(Context context) {
        Intent intent = new Intent(context, LocatMonsterService.class);
        context.startService(intent);
    }

    public static void stopLocatMonsterService(Context context) {
        Intent intent = new Intent(context, LocatMonsterService.class);
        context.stopService(intent);
    }

    public static boolean isLocatMonsterServiceRunning(Context context) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo: runningServices) {
            if (runningServiceInfo.service.getClassName().equals(LocatMonsterService.class.getName())) {
                return true;
            }
        }
        return false;
    }
}
