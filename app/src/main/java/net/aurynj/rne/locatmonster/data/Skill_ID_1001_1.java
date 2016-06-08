package net.aurynj.rne.locatmonster.data;

import net.aurynj.rne.locatmonster.model.*;

public final class Skill_ID_1001_1 extends SkillClass {
    public Skill_ID_1001_1() {
        super();
    }

    @Override
    public String getName() {
        return "쇠파이프 후려치기";
    }

    @Override
    public float getSuccessProbability(CharacterStatus near, CharacterStatus far) {
        return 1;
    }

    @Override
    public SkillEffectClass[] getEffects() {
        final SkillEffectClass[] skillEffects = { new SkillEffect_ID_1001_1_1(), new SkillEffect_ID_1001_1_2() };
        return skillEffects;
    }

    @Override
    public VisualEffectClass[] getVisualEffects(boolean success) {
        final VisualEffectClass[] skillVisualEffectClassesOnSuccess = { new Skill_ID_1001_1_Visual_1() };
        if (success)
            return skillVisualEffectClassesOnSuccess;
        else
            return new VisualEffectClass[0];
    }

    @Override
    public double getDelay() {
        return 1;
    }

    private final class SkillEffect_ID_1001_1_1 extends SkillEffectClass {
        @Override
        public float getSuccessProbability(CharacterStatus near, CharacterStatus far) {
            return 1;
        }

        @Override
        public BattleSide getTargetSide(CharacterStatus near, CharacterStatus far) {
            return BattleSide.NEAR;
        }

        @Override
        public PointClass getTargetPoint(CharacterStatus near, CharacterStatus far) {
            return PointClass.SP;
        }

        @Override
        public int getPointIncrement(CharacterStatus near, CharacterStatus far) {
            return -100;
        }
    }

    private final class SkillEffect_ID_1001_1_2 extends SkillEffectClass {
        @Override
        public float getSuccessProbability(CharacterStatus near, CharacterStatus far) {
            return 1;
        }

        @Override
        public BattleSide getTargetSide(CharacterStatus near, CharacterStatus far) {
            return BattleSide.FAR;
        }

        @Override
        public PointClass getTargetPoint(CharacterStatus near, CharacterStatus far) {
            return PointClass.HP;
        }

        @Override
        public int getPointIncrement(CharacterStatus near, CharacterStatus far) {
            return -200;
        }
    }

    private final class Skill_ID_1001_1_Visual_1 extends VisualEffectClass {
        @Override
        public Type getType() {
            return Type.NONE;
        }

        @Override
        public BattleSide getTargetSide() {
            return BattleSide.FAR;
        }
    }
}
