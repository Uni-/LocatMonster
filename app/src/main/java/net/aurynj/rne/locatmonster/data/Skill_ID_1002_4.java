package net.aurynj.rne.locatmonster.data;

import net.aurynj.rne.locatmonster.model.*;

public class Skill_ID_1002_4 extends SkillClass {
    public Skill_ID_1002_4() {
        super();
    }

    @Override
    public String getName() {
        return "회복";
    }

    @Override
    public float getSuccessProbability(CharacterStatus near, CharacterStatus far) {
        return 1;
    }

    @Override
    public SkillEffectClass[] getEffects() {
        final SkillEffectClass[] skillEffects = { new SkillEffect_ID_1002_4_1(), new SkillEffect_ID_1002_4_2() };
        return skillEffects;
    }

    @Override
    public double getDelay() {
        return 1;
    }

    private class SkillEffect_ID_1002_4_1 extends SkillEffectClass {
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
            return PointClass.MP;
        }

        @Override
        public int getPointIncrement(CharacterStatus near, CharacterStatus far) {
            return -300;
        }
    }

    private class SkillEffect_ID_1002_4_2 extends SkillEffectClass {
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
            return PointClass.HP;
        }

        @Override
        public int getPointIncrement(CharacterStatus near, CharacterStatus far) {
            return 500;
        }
    }
}
