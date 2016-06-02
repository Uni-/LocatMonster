package net.aurynj.rne.locatmonster.data;

import net.aurynj.rne.locatmonster.model.CharacterStatus;
import net.aurynj.rne.locatmonster.model.SkillClass;

public class Skill_ID_2_2 extends SkillClass {
    public Skill_ID_2_2() {
        super();
    }

    @Override
    public String getName() {
        return "물기";
    }

    @Override
    public void perform(CharacterStatus near, CharacterStatus far) {
        near.SP -= 30 * near.Level;
        far.HP -= 60 * near.Level;
    }

    @Override
    public double delay() {
        return 0;
    }
}
