package net.aurynj.rne.locatmonster.data;

import net.aurynj.rne.locatmonster.R;
import net.aurynj.rne.locatmonster.model.*;

public class Character_ID_2 extends CharacterClass {

    public Character_ID_2() {
        super();
    }

    @Override
    public String getName() {
        return "도둑고양이";
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
        return 60;
    }

    @Override
    public int getBaseMaxSP() {
        return 70;
    }

    @Override
    public SkillClass[] getSkills() {
        final SkillClass skills[] = { new Skill_ID_2_1(), new Skill_ID_2_2() };
        return skills;
    }

    @Override
    public int drawableRes() {
        return R.drawable.cat;
    }
}
