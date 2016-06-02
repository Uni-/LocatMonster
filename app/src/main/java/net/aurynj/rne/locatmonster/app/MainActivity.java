// http://developer.android.com/intl/ko/guide/components/services.html
// http://developer.android.com/intl/ko/training/location/index.html
package net.aurynj.rne.locatmonster.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.aurynj.rne.locatmonster.*;
import net.aurynj.rne.locatmonster.widget.*;

public class MainActivity extends AppCompatActivity {
    TextView mHelloWorldTextView;
    Button mStartMapsButton;
    Button mStartBattleControlButton;
    Button mRefreshLocationButton;
    Button mClearLogButton;
    Button mBindServiceButton;
    Button mUnbindServiceButton;
    Button mStartServiceButton;
    Button mStopServiceButton;
    CharacterBriefStatusView mCharacterBriefStatusView;

    LocatMonsterService mLocatMonsterService;
    LocationServiceConnection mLocationServiceConnection;
    boolean mBoundToService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelloWorldTextView = (TextView) findViewById(R.id.activity_main_text_helloworld);
        mStartMapsButton = (Button) findViewById(R.id.activity_main_button_start_map);
        mStartBattleControlButton = (Button) findViewById(R.id.activity_main_button_start_battle_control);
        mRefreshLocationButton = (Button) findViewById(R.id.activity_main_button_refresh_location);
        mClearLogButton = (Button) findViewById(R.id.activity_main_button_clear_log);
        mBindServiceButton = (Button) findViewById(R.id.activity_main_button_bind_service);
        mUnbindServiceButton = (Button) findViewById(R.id.activity_main_button_unbind_service);
        mStartServiceButton = (Button) findViewById(R.id.activity_main_button_start_service);
        mStopServiceButton = (Button) findViewById(R.id.activity_main_button_stop_service);
        mCharacterBriefStatusView = (CharacterBriefStatusView) findViewById(R.id.activity_main_view_character_brief_status);

        mCharacterBriefStatusView.setMaxHP(100);
        mCharacterBriefStatusView.setHP(40);
        mCharacterBriefStatusView.setMaxMP(100);
        mCharacterBriefStatusView.setMP(40);
        mCharacterBriefStatusView.setMaxSP(100);
        mCharacterBriefStatusView.setSP(50);
        Button buttonNewNoti = (Button) findViewById(R.id.activity_main_button_new_noti);
        buttonNewNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCharacterBriefStatusView.incrementHPBy(10);
                mCharacterBriefStatusView.incrementMPBy(-10);
                mCharacterBriefStatusView.incrementSPBy(10);
            }
        });

        mStartMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });
        mStartBattleControlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, BattleControlActivity.class));
            }
        });
        mRefreshLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUiLocation();
            }
        });
        mClearLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeUiLocation();
            }
        });
        mBindServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindLocatMonsterService();
            }
        });
        mUnbindServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unbindLocatMonsterService();
            }
        });
        mStartServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocatMonsterServiceHelper.startLocatMonsterService(MainActivity.this);
            }
        });
        mStopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocatMonsterServiceHelper.stopLocatMonsterService(MainActivity.this);
            }
        });
    }

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

    private void bindLocatMonsterService() {
        if (mLocationServiceConnection == null) {
            mLocationServiceConnection = new LocationServiceConnection();
        }
        if (!LocatMonsterServiceHelper.isLocatMonsterServiceRunning(MainActivity.this)) {
            Toast.makeText(MainActivity.this, "Start service first please", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(MainActivity.this, "Bound service", Toast.LENGTH_SHORT).show();
        }
    }

    private void unbindLocatMonsterService() {
        if (!LocatMonsterServiceHelper.isLocatMonsterServiceRunning(MainActivity.this)) {
            Toast.makeText(MainActivity.this, "Service is not even running", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mBoundToService && mLocationServiceConnection != null && mLocatMonsterService != null) {
            unbindService(mLocationServiceConnection);
            mBoundToService = false;
            Toast.makeText(MainActivity.this, "Unbound service", Toast.LENGTH_SHORT).show();
        }
    }

    protected void removeUiLocation() {
        mHelloWorldTextView.setText("");
    }

    protected void updateUiLocation() {
        String text = mHelloWorldTextView.getText() + "\n";
        if (mLocatMonsterService == null) {
            text += "Unbound to service yet";
        } else {
            Location location = mLocatMonsterService.getLastLocation();
            if (location != null) {
                text +=
                        "lat:" + String.valueOf(location.getLatitude()) + ", " +
                                "lng:" + String.valueOf(location.getLongitude()) + ", " +
                                "alt:" + String.valueOf(location.getAltitude()) + " / " +
                                "acc:" + String.valueOf(location.getAccuracy());
            } else {
                text += "null";
            }
        }
        mHelloWorldTextView.setText(text);
    }

    private class LocationServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.v("ServiceConnection", "onServiceConnected");
            LocatMonsterService.LocalBinder binder = (LocatMonsterService.LocalBinder) iBinder;
            mLocatMonsterService = binder.getService();
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
