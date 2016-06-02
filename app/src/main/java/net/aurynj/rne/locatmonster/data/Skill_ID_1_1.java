package net.aurynj.rne.locatmonster.data;

import net.aurynj.rne.locatmonster.model.*;

public class Skill_ID_1_1 extends SkillClass {
    public Skill_ID_1_1() {
        super();
    }

    @Override
    public String getName() {
        return "후리기";
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
