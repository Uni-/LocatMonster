package net.aurynj.rne.locatmonster.model;

public abstract class SkillEffectClass {
    protected SkillEffectClass() {}
    public abstract float getSuccessProbability(CharacterStatus near, CharacterStatus far);
    public abstract BattleSide getTargetSide(CharacterStatus near, CharacterStatus far);
    public abstract PointClass getTargetPoint(CharacterStatus near, CharacterStatus far);
    public abstract int getPointIncrement(CharacterStatus near, CharacterStatus far);
}
