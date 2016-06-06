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
            if (Math.random() < farSkill.getSuccessProbability(farAtFront, nearAtFront)) {
                result.add("Far side " + farAtFront.Name + " used skill: " + farSkill.getName() + ", delay " + farSkill.getDelay());
                SkillEffectClass[] farSkillEffects = farSkill.getEffects();
                for (SkillEffectClass farSkillEffect : farSkillEffects) {
                    if (Math.random() <= farSkillEffect.getSuccessProbability(farAtFront, nearAtFront)) {
                        PointClass targetPoint = farSkillEffect.getTargetPoint(farAtFront, nearAtFront);
                        int pointIncrement = farSkillEffect.getPointIncrement(farAtFront, nearAtFront);
                        if (farSkillEffect.getTargetSide(farAtFront, nearAtFront) == BattleSide.NEAR) {
                            farAtFront.apply(targetPoint, pointIncrement);
                        } else { // BattleSide.FAR
                            nearAtFront.apply(targetPoint, pointIncrement);
                        }
                    }
                }
            } else {
                result.add("Far side " + farAtFront.Name + " tried to use skill but failed: " + farSkill.getName());
            }
            result.add(printStatus());

            if (nearAtFront.HP <= 0) {
                result.add(messageFarWins);
                break;
            }
            if (farAtFront.HP <= 0) { // TODO data-unreachable but may change; more consistent check
                result.add(messageNearWins);
                break;
            }

            int nearSkillIdx = (int) (Math.random() * nearSkills.length);
            SkillClass nearSkill = nearSkills[nearSkillIdx];
            if (Math.random() < nearSkill.getSuccessProbability(nearAtFront, farAtFront)) {
                result.add("Near side " + nearAtFront.Name + " used skill: " + nearSkill.getName() + ", delay " + nearSkill.getDelay());
                SkillEffectClass[] nearSkillEffects = nearSkill.getEffects();
                for (SkillEffectClass nearSkillEffect: nearSkillEffects) {
                    if (Math.random() <= nearSkillEffect.getSuccessProbability(nearAtFront, farAtFront)) {
                        PointClass targetPoint = nearSkillEffect.getTargetPoint(nearAtFront, farAtFront);
                        int pointIncrement = nearSkillEffect.getPointIncrement(nearAtFront, farAtFront);
                        if (nearSkillEffect.getTargetSide(nearAtFront, farAtFront) == BattleSide.NEAR) {
                            nearAtFront.apply(targetPoint, pointIncrement);
                        } else { // BattleSide.FAR
                            farAtFront.apply(targetPoint, pointIncrement);
                        }
                    }
                }
            } else {
                result.add("Near side " + nearAtFront.Name + " tried to use skill but failed: " + nearSkill.getName());
            }
            result.add(printStatus());

            if (farAtFront.HP <= 0) {
                result.add(messageNearWins);
                break;
            }
            if (nearAtFront.HP <= 0) { // TODO data-unreachable but may change; more consistent check
                result.add(messageFarWins);
                break;
            }
        }
        return result;
    }

    public String printStatus() {
        return "Near: " + near.toString() + ", Far: " + far.toString();
    }
}
