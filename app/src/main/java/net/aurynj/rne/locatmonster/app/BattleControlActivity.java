package net.aurynj.rne.locatmonster.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.aurynj.rne.locatmonster.*;
import net.aurynj.rne.locatmonster.widget.CharacterBriefStatusView;

public class BattleControlActivity extends AppCompatActivity {

    CharacterBriefStatusView mFarView, mNearView;
    Button mStartArenaButton;
    TextView mArenaLogTextView;
    Arena mArena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_control);

        mFarView = (CharacterBriefStatusView) findViewById(R.id.activity_battle_control_brief_status_far);
        mNearView = (CharacterBriefStatusView) findViewById(R.id.activity_battle_control_brief_status_near);

        mStartArenaButton = (Button) findViewById(R.id.activity_battle_control_start_arena);
        mArenaLogTextView = (TextView) findViewById(R.id.activity_battle_control_arena_log);

        mArena = new Arena();
        mArenaLogTextView.setText("New Arena Generated.\n" + mArena.printStatus());

        mStartArenaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArena.proceed();
            }
        });
    }
}
