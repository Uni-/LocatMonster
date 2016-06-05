package net.aurynj.rne.locatmonster.appframework;

import net.aurynj.rne.locatmonster.data.*;
import net.aurynj.rne.locatmonster.model.*;

import java.util.ArrayList;
import java.util.List;

public class Arena extends ArenaClass {
    public final List<CharacterStatus> near = new ArrayList<>(), far = new ArrayList<>();

    public Arena() {
        near.add(CharacterStatus.fromClass(new Character_ID_1()));
        near.get(0).Alias = "울트라캡숑";
        far.add(CharacterStatus.fromClass(new Character_ID_2()));
    }

    public List<String> proceed() {
        final CharacterStatus nearAtFront = near.get(0), farAtFront = far.get(0);
        final SkillClass[] nearSkills = nearAtFront.Skills;
        final SkillClass[] farSkills = farAtFront.Skills;
        final List<String> result = new ArrayList<String>();

        final String messageNearWins = "Win";
        final String messageFarWins = "Lose";

        result.add(printStatus());

        while (true) {
            int farSkillIdx = (int) (Math.random() * farSkills.length);
            SkillClass farSkill = farSkills[farSkillIdx];
            farSkill.perform(farAtFront, nearAtFront);
            result.add("Far side used skill: " + farSkill.getName());
            result.add(printStatus());
            if (farAtFront.HP < 0) {
                result.add(messageFarWins);
                break;
            }

            int nearSkillIdx = (int) (Math.random() * nearSkills.length);
            SkillClass nearSkill = nearSkills[nearSkillIdx];
            nearSkill.perform(nearAtFront, farAtFront);
            result.add("Near side used skill: " + nearSkill.getName());
            result.add(printStatus());
            if (nearAtFront.HP < 0) {
                result.add(messageNearWins);
                break;
            }
        }
        return result;
    }

    public String printStatus() {
        return "Near: " + near.toString() + ", Far: " + far.toString();
    }
}
