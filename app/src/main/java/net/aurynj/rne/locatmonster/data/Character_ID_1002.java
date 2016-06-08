package net.aurynj.rne.locatmonster.data;

import net.aurynj.rne.locatmonster.R;
import net.aurynj.rne.locatmonster.model.*;

public final class Character_ID_1002 extends CharacterClass {

    public Character_ID_1002() {
        super();
    }

    @Override
    public String getName() {
        return "수도승";
    }

    @Override
    public int getDefaultLevel() {
        return 15;
    }

    @Override
    public int getBaseMaxHP() {
        return 1500;
    }

    @Override
    public int getBaseMaxMP() {
        return 800;
    }

    @Override
    public int getBaseMaxSP() {
        return 700;
    }

    @Override
    public SkillClass[] getSkills() {
        final SkillClass skills[] = { new Skill_ID_1002_1(), new Skill_ID_1002_2(), new Skill_ID_1002_3(), new Skill_ID_1002_4() };
        return skills;
    }

    @Override
    public int drawableRes() {
        return R.drawable.monk;
    }
}
