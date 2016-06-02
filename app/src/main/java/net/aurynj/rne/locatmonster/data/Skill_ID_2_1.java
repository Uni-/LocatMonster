package net.aurynj.rne.locatmonster.data;

import net.aurynj.rne.locatmonster.model.CharacterStatus;
import net.aurynj.rne.locatmonster.model.SkillClass;

public class Skill_ID_2_1 extends SkillClass {
    public Skill_ID_2_1() {
        super();
    }

    @Override
    public String getName() {
        return "긁기";
    }

    @Override
    public void perform(CharacterStatus near, CharacterStatus far) {
        near.SP -= 20 * near.Level;
        far.HP -= 40 * near.Level;
    }

    @Override
    public double delay() {
        return 0;
    }
}
