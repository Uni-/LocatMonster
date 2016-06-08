package net.aurynj.rne.locatmonster.appframework;

import net.aurynj.rne.locatmonster.data.*;
import net.aurynj.rne.locatmonster.model.*;

import java.util.ArrayList;
import java.util.List;

public class Arena extends ArenaClass {
    public final List<CharacterStatus> near = new ArrayList<>(), far = new ArrayList<>();
    final List<String> log = new ArrayList<String>();

    public Arena(UserPrefs userPrefs) {
        List<CharacterStatus> characterStatusList = userPrefs.getCharacterStatusList();
        near.addAll(characterStatusList);
        //near.add(CharacterStatus.fromClass(new Character_ID_1()));
        far.add(CharacterStatus.fromClass(new Character_ID_2()));
    }

    public List<String> proceed() {
        final CharacterStatus nearAtFront = near.get(0), farAtFront = far.get(0);
        final SkillClass[] nearSkills = nearAtFront.Skills;
        final SkillClass[] farSkills = farAtFront.Skills;

        final String messageNearWins = "Win";
        final String messageFarWins = "Lose";

        log.add(printStatus());

        while (true) {
            int farSkillIdx = (int) (Math.random() * farSkills.length);
            SkillClass farSkill = farSkills[farSkillIdx];
            if (Math.random() < farSkill.getSuccessProbability(farAtFront, nearAtFront)) {
                log.add("Far side " + farAtFront.Name + " used skill: " + farSkill.getName() + ", delay " + farSkill.getDelay());
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
                log.add("Far side " + farAtFront.Name + " tried to use skill but failed: " + farSkill.getName());
            }
            log.add(printStatus());

            if (nearAtFront.HP <= 0) {
                log.add(messageFarWins);
                break;
            }
            if (farAtFront.HP <= 0) { // TODO data-unreachable but may change; more consistent check
                log.add(messageNearWins);
                break;
            }

            int nearSkillIdx = (int) (Math.random() * nearSkills.length);
            SkillClass nearSkill = nearSkills[nearSkillIdx];
            if (Math.random() < nearSkill.getSuccessProbability(nearAtFront, farAtFront)) {
                log.add("Near side " + nearAtFront.Name + " used skill: " + nearSkill.getName() + ", delay " + nearSkill.getDelay());
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
                log.add("Near side " + nearAtFront.Name + " tried to use skill but failed: " + nearSkill.getName());
            }
            log.add(printStatus());

            if (farAtFront.HP <= 0) {
                log.add(messageNearWins);
                break;
            }
            if (nearAtFront.HP <= 0) { // TODO data-unreachable but may change; more consistent check
                log.add(messageFarWins);
                break;
            }
        }
        return log;
    }

    public String printStatus() {
        return "Near: " + near.toString() + ", Far: " + far.toString();
    }
}
