package net.aurynj.rne.locatmonster.model;

public abstract class SkillClass {
    protected SkillClass() {}
    public abstract String getName();
    public abstract float getSuccessProbability(CharacterStatus near, CharacterStatus far);
    public abstract SkillEffectClass[] getEffects();
    public abstract double getDelay();
}
