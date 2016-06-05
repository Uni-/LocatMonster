// http://developer.android.com/intl/ko/guide/components/services.html
// http://developer.android.com/intl/ko/training/location/index.html
package net.aurynj.rne.locatmonster.app;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import net.aurynj.rne.locatmonster.*;
import net.aurynj.rne.locatmonster.widget.*;

public class MainActivity extends LocatMonsterAppBaseActivity {
    TextView mHelloWorldTextView;
    Switch mServiceSwitch;
    Button mStartMapsButton;
    Button mStartBattleControlButton;
    Button mRefreshLocationButton;
    Button mClearLogButton;
    CharacterBriefStatusView mCharacterBriefStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelloWorldTextView = (TextView) findViewById(R.id.activity_main_text_helloworld);
        mServiceSwitch = (Switch) findViewById(R.id.activity_main_switch_service);
        mStartMapsButton = (Button) findViewById(R.id.activity_main_button_start_map);
        mStartBattleControlButton = (Button) findViewById(R.id.activity_main_button_start_battle_control);
        mRefreshLocationButton = (Button) findViewById(R.id.activity_main_button_refresh_location);
        mClearLogButton = (Button) findViewById(R.id.activity_main_button_clear_log);
        mCharacterBriefStatusView = (CharacterBriefStatusView) findViewById(R.id.activity_main_view_character_brief_status);

        mServiceSwitch.setChecked(LocatMonsterServiceHelper.isLocatMonsterServiceRunning(this));
        mServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    LocatMonsterServiceHelper.startLocatMonsterService(MainActivity.this);
                    bindLocatMonsterService();
                } else {
                    unbindLocatMonsterService();
                    LocatMonsterServiceHelper.stopLocatMonsterService(MainActivity.this);
                }
            }
        });

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
    }

    protected void removeUiLocation() {
        mHelloWorldTextView.setText("");
    }

    protected void updateUiLocation() {
        String text = mHelloWorldTextView.getText() + "\n";
        if (!this.boundToService()) {
            text += "Not bound to service yet";
        } else {
            Location location = getLocatMonsterService().getLastLocation();
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
}
