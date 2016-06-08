package net.aurynj.rne.locatmonster.data;

import net.aurynj.rne.locatmonster.R;
import net.aurynj.rne.locatmonster.model.*;

public final class Character_ID_1001 extends CharacterClass {

    public Character_ID_1001() {
        super();
    }

    @Override
    public String getName() {
        return "고요한 언덕의 간호사";
    }

    @Override
    public int getDefaultLevel() {
        return 20;
    }

    @Override
    public int getBaseMaxHP() {
        return 2000;
    }

    @Override
    public int getBaseMaxMP() {
        return 100;
    }

    @Override
    public int getBaseMaxSP() {
        return 1900;
    }

    @Override
    public SkillClass[] getSkills() {
        final SkillClass skills[] = { new Skill_ID_1001_1(), new Skill_ID_1001_2(), new Skill_ID_1001_3() };
        return skills;
    }

    @Override
    public int drawableRes() {
        return R.drawable.silentnurse;
    }
}
