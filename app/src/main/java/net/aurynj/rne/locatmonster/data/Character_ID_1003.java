package net.aurynj.rne.locatmonster.data;

import net.aurynj.rne.locatmonster.R;
import net.aurynj.rne.locatmonster.model.*;

public final class Character_ID_1003 extends CharacterClass {

    public Character_ID_1003() {
        super();
    }

    @Override
    public String getName() {
        return "전투 다람쥐";
    }

    @Override
    public int getDefaultLevel() {
        return 5;
    }

    @Override
    public int getBaseMaxHP() {
        return 500;
    }

    @Override
    public int getBaseMaxMP() {
        return 100;
    }

    @Override
    public int getBaseMaxSP() {
        return 400;
    }

    @Override
    public SkillClass[] getSkills() {
        final SkillClass skills[] = { new Skill_ID_1003_1(), new Skill_ID_1003_2(), new Skill_ID_1003_3() };
        return skills;
    }

    @Override
    public int drawableRes() {
        return R.drawable.squirrel;
    }
}
