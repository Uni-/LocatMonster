package net.aurynj.rne.locatmonster.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.aurynj.rne.locatmonster.R;
import net.aurynj.rne.locatmonster.model.*;

/**
 * CharacterBriefStatusView
 *
 * Provides three ProgressBar-like visual slots for HP, MP, SP.
 */
public class CharacterBriefStatusView extends LinearLayout implements View.OnClickListener {

    private static final int SUBSTEPS_MULTIPLIER = 100;

    private TextView mTextViewName, mTextViewAlias, mTextViewLevel;
    private ProgressBar mProgressBarHP, mProgressBarMP, mProgressBarSP;
    private TextView mTextViewHP, mTextViewMP, mTextViewSP;
    private int mMaxHP, mMaxMP, mMaxSP;
    private int mHP, mMP, mSP;

    private final UiValueChangeManager[] mUiValueChangeManagers = new UiValueChangeManager[PointClass.values().length];

    private ValueChangeListener mValueChangeListener;

    public CharacterBriefStatusView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CharacterBriefStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CharacterBriefStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_character_brief_status, this, true);

        this.setOnClickListener(this);

        mTextViewName = (TextView) findViewById(R.id.view_character_brief_status_text_name);
        mTextViewAlias = (TextView) findViewById(R.id.view_character_brief_status_text_alias);
        mTextViewLevel = (TextView) findViewById(R.id.view_character_brief_status_text_level);

        mProgressBarHP = (ProgressBar) findViewById(R.id.view_character_brief_status_progress_hp);
        mProgressBarMP = (ProgressBar) findViewById(R.id.view_character_brief_status_progress_mp);
        mProgressBarSP = (ProgressBar) findViewById(R.id.view_character_brief_status_progress_sp);

        mTextViewHP = (TextView) findViewById(R.id.view_character_brief_status_text_hp);
        mTextViewMP = (TextView) findViewById(R.id.view_character_brief_status_text_mp);
        mTextViewSP = (TextView) findViewById(R.id.view_character_brief_status_text_sp);

        refreshHPValues();
        refreshMPValues();
        refreshSPValues();
    }

    public void setAllFrom(CharacterStatus characterStatus) {
        setName(characterStatus.Name);
        setAlias(characterStatus.Alias);
        setLevel(characterStatus.Level);
        setMaxHP(characterStatus.MaxHP);
        setMaxMP(characterStatus.MaxMP);
        setMaxSP(characterStatus.MaxSP);
        setHP(characterStatus.HP);
        setMP(characterStatus.MP);
        setSP(characterStatus.SP);
    }

    public void setAllTo(CharacterStatus characterStatus) {
        characterStatus.MaxHP = getMaxHP();
        characterStatus.MaxMP = getMaxMP();
        characterStatus.MaxSP = getMaxSP();
        characterStatus.HP = getHP();
        characterStatus.MP = getMP();
        characterStatus.SP = getSP();
    }

    public void setName(String name) {
        mTextViewName.setText(name);
    }

    public void setAlias(String alias) {
        mTextViewAlias.setText(alias);
    }

    public void setLevel(int level) {
        mTextViewLevel.setText("Lv. " + String.valueOf(level));
    }

    public int getMaxHP() {
        return mMaxHP;
    }

    public int getMaxMP() {
        return mMaxMP;
    }

    public int getMaxSP() {
        return mMaxSP;
    }

    public int getPointOfClass(PointClass pointClass) {
        switch (pointClass) {
            case HP:
                return getHP();
            case MP:
                return getMP();
            case SP:
                return getSP();
        }
        return 0; // unreachable, in fact
    }

    public int getHP() {
        return mHP;
    }

    public int getMP() {
        return mMP;
    }

    public int getSP() {
        return mSP;
    }

    public void setMaxHP(int maxHP) {
        mProgressBarHP.setMax(maxHP * SUBSTEPS_MULTIPLIER);
        refreshHPValues();
    }

    public void setMaxMP(int maxMP) {
        mProgressBarMP.setMax(maxMP * SUBSTEPS_MULTIPLIER);
        refreshMPValues();
    }

    public void setMaxSP(int maxSP) {
        mProgressBarSP.setMax(maxSP * SUBSTEPS_MULTIPLIER);
        refreshSPValues();
    }

    public void setPointOfClass(PointClass pointClass, int point) {
        switch (pointClass) {
            case HP:
                setHP(point);
                break;
            case MP:
                setMP(point);
                break;
            case SP:
                setSP(point);
                break;
        }
    }

    public void incrementPointOfClassBy(PointClass pointClass, int diffPoint) {
        switch (pointClass) {
            case HP:
                incrementHPBy(diffPoint);
                break;
            case MP:
                incrementMPBy(diffPoint);
                break;
            case SP:
                incrementSPBy(diffPoint);
                break;
        }
    }

    private void internalSetPointOfClass(PointClass pointClass, int point) {
        switch (pointClass) {
            case HP:
                internalSetHP(point);
                break;
            case MP:
                internalSetMP(point);
                break;
            case SP:
                internalSetSP(point);
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        internalSetHP2();
        internalSetMP2();
        internalSetSP2();
    }

    private int _hp = 0, _mp = 0, _sp = 0;


    private void internalSetHP(int hp) {
        mProgressBarHP.setProgress(hp * SUBSTEPS_MULTIPLIER);
        refreshHPValues();
    }

    private void internalSetMP(int mp) {
        mProgressBarMP.setProgress(mp * SUBSTEPS_MULTIPLIER);
        refreshMPValues();
    }

    private void internalSetSP(int sp) {
        mProgressBarSP.setProgress(sp * SUBSTEPS_MULTIPLIER);
        refreshSPValues();
    }
    /*
    private void internalSetHP(int hp) {
        _hp = hp;
    }

    private void internalSetMP(int mp) {
        _mp = mp;
    }

    private void internalSetSP(int sp) {
        _sp = sp;
    }
*/
    private void internalSetHP2() {
        mProgressBarHP.setProgress(_hp * SUBSTEPS_MULTIPLIER);
        refreshHPValues();
    }

    private void internalSetMP2() {
        mProgressBarMP.setProgress(_mp * SUBSTEPS_MULTIPLIER);
        refreshMPValues();
    }

    private void internalSetSP2() {
        mProgressBarSP.setProgress(_sp * SUBSTEPS_MULTIPLIER);
        refreshSPValues();
    }

    public void setHP(int hp) {
        UiValueChangeManager uiValueChangeManager = new UiValueChangeManager(PointClass.HP);
        uiValueChangeManager.set(hp);
    }

    public void setMP(int mp) {
        UiValueChangeManager uiValueChangeManager = new UiValueChangeManager(PointClass.MP);
        uiValueChangeManager.set(mp);
    }

    public void setSP(int sp) {
        UiValueChangeManager uiValueChangeManager = new UiValueChangeManager(PointClass.SP);
        uiValueChangeManager.set(sp);
    }

    public void incrementHPBy(int diffHP) {
        boolean animated = true;
        UiValueChangeManager uiValueChangeManager = new UiValueChangeManager(PointClass.HP);
        if (animated) {
            uiValueChangeManager.applyAnimated(diffHP);
        } else {
            uiValueChangeManager.apply(diffHP);
        }
    }

    public void incrementMPBy(int diffMP) {
        boolean animated = true;
        UiValueChangeManager uiValueChangeManager = new UiValueChangeManager(PointClass.MP);
        if (animated) {
            uiValueChangeManager.applyAnimated(diffMP);
        } else {
            uiValueChangeManager.apply(diffMP);
        }
    }

    public void incrementSPBy(int diffSP) {
        boolean animated = true;
        UiValueChangeManager uiValueChangeManager = new UiValueChangeManager(PointClass.SP);
        if (animated) {
            uiValueChangeManager.applyAnimated(diffSP);
        } else {
            uiValueChangeManager.apply(diffSP);
        }
    }

    private ProgressBar getProgressBarObjectOfClass(PointClass pointClass) {
        switch (pointClass) {
            case HP:
                return mProgressBarHP;
            case MP:
                return mProgressBarMP;
            case SP:
                return mProgressBarSP;
        }
        return null; // unreachable, in fact
    }

    private void refreshValuesOfClass(PointClass pointClass) {
        switch (pointClass) {
            case HP:
                refreshHPValues();
                break;
            case MP:
                refreshMPValues();
                break;
            case SP:
                refreshSPValues();
                break;
        }
    }

    private void refreshHPValues() {
        mHP = (mProgressBarHP.getProgress() + SUBSTEPS_MULTIPLIER / 2) / SUBSTEPS_MULTIPLIER;
        mMaxHP = mProgressBarHP.getMax() / SUBSTEPS_MULTIPLIER;
        mTextViewHP.setText(mHP + "/" + mMaxHP);
    }

    private void refreshMPValues() {
        mMP = (mProgressBarMP.getProgress() + SUBSTEPS_MULTIPLIER / 2) / SUBSTEPS_MULTIPLIER;
        mMaxMP = mProgressBarMP.getMax() / SUBSTEPS_MULTIPLIER;
        mTextViewMP.setText(mMP + "/" + mMaxMP);
    }

    private void refreshSPValues() {
        mSP = (mProgressBarSP.getProgress() + SUBSTEPS_MULTIPLIER / 2) / SUBSTEPS_MULTIPLIER;
        mMaxSP = mProgressBarSP.getMax() / SUBSTEPS_MULTIPLIER;
        mTextViewSP.setText(mSP + "/" + mMaxSP);
    }

    @Override
    public void onClick(View view) {
        if (view != this)
            return;

        for (UiValueChangeManager animationManager: mUiValueChangeManagers) {
            if (animationManager != null) {
                animationManager.stop();
            }
        }
    }

    public void setValueChangeListener(ValueChangeListener valueChangeListener) {
        mValueChangeListener = valueChangeListener;
    }

    private class UiValueChangeManager implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
        static final int DURATION = 1000;
        final PointClass mPointClass;
        ObjectAnimator mAnimator;
        int mSrcValue, mDiffValue, mDestValue;

        public UiValueChangeManager(PointClass pointClass) {
            mPointClass = pointClass;
        }
        
        public void set(int value) {
            UiValueChangeManager runningManagerOfClass = mUiValueChangeManagers[mPointClass.ordinal()];
            if (runningManagerOfClass != null) {
                runningManagerOfClass.stop();
                mSrcValue = runningManagerOfClass.mDestValue;
            } else {
                mSrcValue = getPointOfClass(mPointClass);
            }

            mDestValue = value;
            mDiffValue = mDestValue - mSrcValue;

            internalSetPointOfClass(mPointClass, mDestValue);
        }

        public void setAnimated(int value) {
            UiValueChangeManager runningManagerOfClass = mUiValueChangeManagers[mPointClass.ordinal()];
            if (runningManagerOfClass != null) {
                runningManagerOfClass.stop();
                mSrcValue = runningManagerOfClass.mDestValue;
            } else {
                mSrcValue = getPointOfClass(mPointClass);
            }

            mDestValue = value;
            mDiffValue = mDestValue - mSrcValue;

            ProgressBar progressBar = getProgressBarObjectOfClass(mPointClass);
            mAnimator = ObjectAnimator.ofInt(progressBar, "progress", mSrcValue * SUBSTEPS_MULTIPLIER, mDestValue * SUBSTEPS_MULTIPLIER);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.setDuration(DURATION);
            mAnimator.addListener(this);
            mAnimator.addUpdateListener(this);
            mUiValueChangeManagers[mPointClass.ordinal()] = this;
            mAnimator.start();
        }

        public void apply(int diffValue) {
            UiValueChangeManager runningManagerOfClass = mUiValueChangeManagers[mPointClass.ordinal()];
            if (runningManagerOfClass != null) {
                runningManagerOfClass.stop();
                mSrcValue = runningManagerOfClass.mDestValue;
            } else {
                mSrcValue = getPointOfClass(mPointClass);
            }

            mDiffValue = diffValue;
            mDestValue = mSrcValue + mDiffValue;

            internalSetPointOfClass(mPointClass, mDestValue);
        }

        public void applyAnimated(int diffValue) {
            UiValueChangeManager runningManagerOfClass = mUiValueChangeManagers[mPointClass.ordinal()];
            if (runningManagerOfClass != null) {
                runningManagerOfClass.stop();
                mSrcValue = runningManagerOfClass.mDestValue;
            } else {
                mSrcValue = getPointOfClass(mPointClass);
            }

            mDiffValue = diffValue;
            mDestValue = mSrcValue + mDiffValue;

            ProgressBar progressBar = getProgressBarObjectOfClass(mPointClass);
            mAnimator = ObjectAnimator.ofInt(progressBar, "progress", mSrcValue * SUBSTEPS_MULTIPLIER, mDestValue * SUBSTEPS_MULTIPLIER);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.setDuration(DURATION);
            mAnimator.addListener(this);
            mAnimator.addUpdateListener(this);
            mUiValueChangeManagers[mPointClass.ordinal()] = this;
            mAnimator.start();
        }

        private void stop() {
            mAnimator.cancel();
        }

        @Override
        public void onAnimationStart(Animator animator) {}

        @Override
        public void onAnimationEnd(Animator animator) {
            mUiValueChangeManagers[mPointClass.ordinal()] = null;
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            internalSetPointOfClass(mPointClass, mDestValue);
        }

        @Override
        public void onAnimationRepeat(Animator animator) {}

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            refreshValuesOfClass(mPointClass);
        }
    }

    public interface ValueChangeListener {
        void onValueChangeStart(PointClass pointClass, int pointFrom, int pointTo);
        void onValueChange(PointClass pointClass, int point, int pointFrom, int pointTo);
        void onValueChangeEnd(PointClass pointClass, int pointFrom, int pointTo);
    }
}
