package net.aurynj.rne.locatmonster.data;

import net.aurynj.rne.locatmonster.R;
import net.aurynj.rne.locatmonster.model.*;

public final class Character_ID_3 extends CharacterClass {

    public Character_ID_3() {
        super();
    }

    @Override
    public String getName() {
        return "노랑전기쥐";
    }

    @Override
    public int getDefaultLevel() {
        return 1;
    }

    @Override
    public int getBaseMaxHP() {
        return 70;
    }

    @Override
    public int getBaseMaxMP() {
        return 90;
    }

    @Override
    public int getBaseMaxSP() {
        return 40;
    }

    @Override
    public SkillClass[] getSkills() {
        final SkillClass skills[] = { new Skill_ID_3_1(), new Skill_ID_3_2() };
        return skills;
    }

    @Override
    public int drawableRes() {
        return R.drawable.yellowmouse;
    }
}
