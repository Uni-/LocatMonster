package net.aurynj.rne.locatmonster.app;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.aurynj.rne.locatmonster.*;
import net.aurynj.rne.locatmonster.appframework.*;
import net.aurynj.rne.locatmonster.model.ArenaClass;
import net.aurynj.rne.locatmonster.model.BattleSide;
import net.aurynj.rne.locatmonster.model.CharacterStatus;
import net.aurynj.rne.locatmonster.model.VisualEffectClass;
import net.aurynj.rne.locatmonster.widget.*;

import java.util.List;

public class BattleControlActivity extends BaseActivity implements Arena.OnChangeListener {

    ImageView mFarImageView, mNearImageView;
    CharacterBriefStatusView mFarStatusView, mNearStatusView;
    ImageView mFarEffectImageView, mNearEffectImageView;
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

        mFarEffectImageView = (ImageView) findViewById(R.id.activity_battle_control_image_far_effect);
        mNearEffectImageView = (ImageView) findViewById(R.id.activity_battle_control_image_near_effect);

        mStartArenaButton = (Button) findViewById(R.id.activity_battle_control_start_arena);
        mArenaLogTextView = (TextView) findViewById(R.id.activity_battle_control_arena_log);
        mArenaLogTextView.setMovementMethod(new ScrollingMovementMethod());

        mArenaLogTextView.setText("No Arena Now.");

        mStartArenaButton.setEnabled(false);
        mStartArenaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocatMonsterService().startBattle();
            }
        });
    }

    @Override
    protected void onBindService() {
        super.onBindService();
        if (mCurrentArena == null) {
            mCurrentArena = getLocatMonsterService().getCurrentArena();

            if (mCurrentArena == null) {
                // double check, because there is no current arena so service returns null
                mArenaLogTextView.setText("No Arena Now.");
                return;
            }

            mStartArenaButton.setEnabled(true);

            mArenaLogTextView.setText("New Arena Generated.\n" + mCurrentArena.printStatus());

            CharacterStatus farAtFront = mCurrentArena.far.get(0), nearAtFront = mCurrentArena.near.get(0);

            mFarImageView.setImageResource(farAtFront.DrawableRes);
            mFarStatusView.setAllFrom(farAtFront);

            mNearImageView.setImageResource(nearAtFront.DrawableRes);
            mNearStatusView.setAllFrom(nearAtFront);

            mCurrentArena.setOnChangeListener(this);
            mStartArenaButton.setEnabled(true);
        }
    }

    @Override
    public void onShallowChange(ArenaClass.ShallowChangeEntry shallowChangeEntry) {
        BattleSide side = shallowChangeEntry.getSourceSide().multiply(shallowChangeEntry.getVisualEffect().getTargetSide());
        side.select(mNearEffectImageView, mFarEffectImageView).setImageResource(getDrawableResId(shallowChangeEntry.getVisualEffect().getType()));
    }

    @Override
    public void onStateChange(ArenaClass.StateChangeEntry stateChangeEntry) {
        BattleSide side = stateChangeEntry.getTargetSide();
        side.select(mNearStatusView, mFarStatusView).incrementPointOfClassBy(stateChangeEntry.getPointClass(), stateChangeEntry.getPointIncrement());
    }

    @Override
    public void onEnd(BattleSide win) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(win.select("승리!", "패배!"));
    }

    public int getDrawableResId(VisualEffectClass.Type skillVisualEffectClass) {
        switch (skillVisualEffectClass) {
            case NONE:
                return 0;
            case HIT:
                return R.drawable.effect_hit;
            case CLAW:
                return R.drawable.effect_claw;
            case MAGIC_GLOW:
                return R.drawable.effect_magic_glow;
            case MAGIC_FLASH:
                return R.drawable.effect_magic_flash;
        }
        return 0; // unreachable expected
    }
}
