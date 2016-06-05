package net.aurynj.rne.locatmonster.appframework;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;

public class PreferencesManager {
    private static final String SHAREDPREFS_KEY = "LocatMonsterSharedPrefs";
    private static final String PREFITEM_TIMESTAMP_KEY = "Timestamp";
    private static final String PREFITEM_SERVICEON_KEY = "ServiceAutoOn";
    private final SharedPreferences mSharedPreferences;

    public PreferencesManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(SHAREDPREFS_KEY, Context.MODE_PRIVATE);
    }

    private String[] getCharacterTimestampsString() {
        if (mSharedPreferences.contains(PREFITEM_TIMESTAMP_KEY)) {
            return new String[0];
        }
        return mSharedPreferences.getString(PREFITEM_TIMESTAMP_KEY, "").split(" ");
    }

    public boolean getServiceAutoOn() {
        return mSharedPreferences.getBoolean(PREFITEM_SERVICEON_KEY, false);
    }

    public void setServiceAutoOn(boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(PREFITEM_SERVICEON_KEY, value);
        editor.commit();
    }

    public long[] getCharacterTimestamps() {
        final String[] s = getCharacterTimestampsString();
        final long[] result = new long[s.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Long.parseLong(s[i]);
        }
        return result;
    }

    public void appendCharacterTimestamp(long characterTimestamp) {
        final String[] timestamps = getCharacterTimestampsString();
        final String[] newTimestamps = Arrays.copyOf(timestamps, timestamps.length + 1);
        newTimestamps[timestamps.length] = String.valueOf(characterTimestamp);
        String s = "";
        for (int i = 0; i < newTimestamps.length; i++) {
            s = s.concat(" ").concat(newTimestamps[i]);
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PREFITEM_TIMESTAMP_KEY, s);
        editor.commit();
    }

    public void prune() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
