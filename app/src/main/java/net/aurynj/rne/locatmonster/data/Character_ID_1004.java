package net.aurynj.rne.locatmonster.data;

import net.aurynj.rne.locatmonster.R;
import net.aurynj.rne.locatmonster.model.*;

public class Character_ID_1004 extends CharacterClass {

    public Character_ID_1004() {
        super();
    }

    @Override
    public String getName() {
        return "안암 호랑이";
    }

    @Override
    public int getDefaultLevel() {
        return 40;
    }

    @Override
    public int getBaseMaxHP() {
        return 4000;
    }

    @Override
    public int getBaseMaxMP() {
        return 500;
    }

    @Override
    public int getBaseMaxSP() {
        return 3500;
    }

    @Override
    public SkillClass[] getSkills() {
        final SkillClass skills[] = { new Skill_ID_1004_1(), new Skill_ID_1004_2(), new Skill_ID_1004_3() };
        return skills;
    }

    @Override
    public int drawableRes() {
        return R.drawable.squirrel;
    }
}
