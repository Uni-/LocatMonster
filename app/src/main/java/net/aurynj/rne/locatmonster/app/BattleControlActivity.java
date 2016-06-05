package net.aurynj.rne.locatmonster.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.aurynj.rne.locatmonster.*;
import net.aurynj.rne.locatmonster.appframework.*;
import net.aurynj.rne.locatmonster.widget.*;

import java.util.Currency;
import java.util.List;

public class BattleControlActivity extends BaseActivity {

    CharacterBriefStatusView mFarView, mNearView;
    Button mStartArenaButton;
    TextView mArenaLogTextView;
    Arena mCurrentArena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_control);

        mFarView = (CharacterBriefStatusView) findViewById(R.id.activity_battle_control_brief_status_far);
        mNearView = (CharacterBriefStatusView) findViewById(R.id.activity_battle_control_brief_status_near);

        mStartArenaButton = (Button) findViewById(R.id.activity_battle_control_start_arena);
        mArenaLogTextView = (TextView) findViewById(R.id.activity_battle_control_arena_log);

        mStartArenaButton.setEnabled(false);
        mStartArenaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<String> battleLog = mCurrentArena.proceed();
                String logStr = new String();
                for (String battleLogItem: battleLog) {
                    logStr += battleLogItem + "\n";
                }
                mArenaLogTextView.setText(mArenaLogTextView.getText() + "\n" + logStr);
            }
        });
    }

    @Override
    protected void onBindService() {
        super.onBindService();
        if (mCurrentArena == null) {
            mCurrentArena = getLocatMonsterService().getCurrentArena();

            if (mCurrentArena != null) {
                mStartArenaButton.setEnabled(true);
                mArenaLogTextView.setText("New Arena Generated.\n" + mCurrentArena.printStatus());
            } else {
                mArenaLogTextView.setText("No Arena Now.");
            }
        }
    }
}
