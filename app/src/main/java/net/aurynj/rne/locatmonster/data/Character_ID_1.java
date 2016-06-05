package net.aurynj.rne.locatmonster.data;

import net.aurynj.rne.locatmonster.model.*;

public class Character_ID_1 extends CharacterClass {

    public Character_ID_1() {
        super();
    }

    @Override
    public String getName() {
        return "새끼 호랑이";
    }

    @Override
    public int getDefaultLevel() {
        return 1;
    }

    @Override
    public int getBaseMaxHP() {
        return 100;
    }

    @Override
    public int getBaseMaxMP() {
        return 30;
    }

    @Override
    public int getBaseMaxSP() {
        return 70;
    }

    @Override
    public SkillClass[] getSkills() {
        final SkillClass skills[] = { new Skill_ID_1_1(), new Skill_ID_1_2() };
        return skills;
    }
}
