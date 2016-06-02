package net.aurynj.rne.locatmonster.model;

public abstract class SkillClass {
    protected SkillClass() {}
    public abstract String getName();
    public abstract void perform(CharacterStatus near, CharacterStatus far);
    public abstract double delay();
}
