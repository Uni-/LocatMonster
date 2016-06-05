package net.aurynj.rne.locatmonster.model;

public abstract class CharacterClass {
    protected CharacterClass() {}
    public abstract String getName();
    public abstract int getDefaultLevel();
    public abstract int getBaseMaxHP();
    public abstract int getBaseMaxMP();
    public abstract int getBaseMaxSP();
    public abstract SkillClass[] getSkills();
    public abstract int drawableRes();
}
