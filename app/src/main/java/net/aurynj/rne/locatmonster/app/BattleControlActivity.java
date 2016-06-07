package net.aurynj.rne.locatmonster.app;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.aurynj.rne.locatmonster.*;
import net.aurynj.rne.locatmonster.appframework.*;
import net.aurynj.rne.locatmonster.model.CharacterStatus;
import net.aurynj.rne.locatmonster.widget.*;

import java.util.List;

public class BattleControlActivity extends BaseActivity {

    ImageView mFarImageView, mNearImageView;
    CharacterBriefStatusView mFarStatusView, mNearStatusView;
    Button mStartArenaButton;
    TextView mArenaLogTextView;
    Arena mCurrentArena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_control);

        mFarImageView = (ImageView) findViewById(R.id.activity_battle_control_image_far);
        mNearImageView = (ImageView) findViewById(R.id.activity_battle_control_image_near);

        mFarStatusView = (CharacterBriefStatusView) findViewById(R.id.activity_battle_control_brief_status_far);
        mNearStatusView = (CharacterBriefStatusView) findViewById(R.id.activity_battle_control_brief_status_near);

        mStartArenaButton = (Button) findViewById(R.id.activity_battle_control_start_arena);
        mArenaLogTextView = (TextView) findViewById(R.id.activity_battle_control_arena_log);
        mArenaLogTextView.setMovementMethod(new ScrollingMovementMethod());

        mArenaLogTextView.setText("No Arena Now.");

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

                CharacterStatus farAtFront = mCurrentArena.far.get(0), nearAtFront = mCurrentArena.near.get(0);

                mFarImageView.setImageResource(farAtFront.DrawableRes);
                if (farAtFront.Name != null)
                    mFarStatusView.setName(farAtFront.Name);
                if (farAtFront.Alias != null)
                    mFarStatusView.setAlias(farAtFront.Alias);
                mFarStatusView.setLevel(farAtFront.Level);
                mFarStatusView.setMaxHP(farAtFront.HP);
                mFarStatusView.setHP(farAtFront.HP);
                mFarStatusView.setMaxMP(farAtFront.MP);
                mFarStatusView.setMP(farAtFront.MP);
                mFarStatusView.setMaxSP(farAtFront.SP);
                mFarStatusView.setSP(farAtFront.SP);

                mNearImageView.setImageResource(nearAtFront.DrawableRes);
                if (nearAtFront.Name != null)
                    mNearStatusView.setName(nearAtFront.Name);
                if (nearAtFront.Alias != null)
                    mNearStatusView.setAlias(nearAtFront.Alias);
                mNearStatusView.setLevel(nearAtFront.Level);
                mNearStatusView.setMaxHP(nearAtFront.HP);
                mNearStatusView.setHP(nearAtFront.HP);
                mNearStatusView.setMaxMP(nearAtFront.MP);
                mNearStatusView.setMP(nearAtFront.MP);
                mNearStatusView.setMaxSP(nearAtFront.SP);
                mNearStatusView.setSP(nearAtFront.SP);
            } else {
                mArenaLogTextView.setText("No Arena Now.");
            }
        }
    }
}
