package net.aurynj.rne.locatmonster.appframework;

import android.util.Log;

import net.aurynj.rne.locatmonster.data.*;
import net.aurynj.rne.locatmonster.model.*;

import java.util.ArrayList;
import java.util.List;

public class Arena extends ArenaClass {
    public final List<CharacterStatus> near = new ArrayList<>(), far = new ArrayList<>();
    final List<String> log = new ArrayList<String>();

    BattleSide currentTurnSide = BattleSide.FAR;

    public Arena(UserPrefs userPrefs) {
        List<CharacterStatus> characterStatusList = userPrefs.getCharacterStatusList();
        near.addAll(characterStatusList);
        //near.add(CharacterStatus.fromClass(new Character_ID_1()));
        far.add(CharacterStatus.fromClass(new Character_ID_2()));
    }

    final String messageNearWins = "Win";
    final String messageFarWins = "Lose";

    BattleSide wonSide = null; // TODO implement undetermined. please don't do this for enum

    public void addCurrentStatusToLog() {
        addToLog(printStatus());
    }

    public double proceed() { // remaining delay, -1 means end of battle
        final CharacterStatus nearAtFront = near.get(0), farAtFront = far.get(0);
        final SkillClass[] nearSkills = nearAtFront.Skills;
        final SkillClass[] farSkills = farAtFront.Skills;

        final BattleSide turn = currentTurnSide;
        final CharacterStatus characterInTurn = turn.select(nearAtFront, farAtFront);
        final CharacterStatus characterOpposite = turn.select(farAtFront, nearAtFront);

        final SkillClass[] skills = turn.select(nearSkills, farSkills);
        final int skillIdx = (int) (Math.random() * skills.length);
        final SkillClass skill = skills[skillIdx];
        double skillDelay = skill.getDelay();

        if (Math.random() < skill.getSuccessProbability(characterInTurn, characterOpposite)) {

            addToLog(turn.name() + " side " + characterInTurn + " used skill: " + skill.getName());

            SkillEffectClass[] skillEffects = skill.getEffects();

            for (SkillEffectClass skillEffect: skillEffects) {

                if (Math.random() < skillEffect.getSuccessProbability(characterInTurn, characterOpposite)) {

                    addToLog("skill effect " + skillEffect.toString() + " applied");

                    final BattleSide targetSide = turn.multiply(skillEffect.getTargetSide(characterInTurn, characterOpposite));
                    final PointClass pointClass = skillEffect.getTargetPoint(characterInTurn, characterOpposite);
                    final int pointIncrement = skillEffect.getPointIncrement(characterInTurn, characterOpposite);

                    targetSide.select(characterInTurn, characterOpposite).apply(pointClass, pointIncrement);
                    mChangeListener.onStateChange(StateChangeEntry.create((int) skillDelay, targetSide, pointClass, pointIncrement));
                } else {
                    addToLog("skill effect " + skillEffect.toString() + " failed");
                }
            }

            VisualEffectClass[] visualEffects = skill.getVisualEffects(true);

            for (VisualEffectClass visualEffect: visualEffects) {
                mChangeListener.onShallowChange(ShallowChangeEntry.crate((int) skillDelay, turn, visualEffect));
                // TODO;
                // TODO: refactor for binding visual effects to each skill effect
                // TODO: and run vfx only if its bound skill effect ?success
            }
        } else {
            addToLog(turn.name() + " side " + characterInTurn + " tried to use skill but failed: " + skill.getName());
        }

        if (characterOpposite.HP <= 0) {
            addToLog(turn.select(messageNearWins, messageFarWins));
            wonSide = turn;
            mChangeListener.onEnd(wonSide);
        } else if (characterInTurn.HP <= 0) {
            addToLog(turn.select(messageFarWins, messageNearWins));
            wonSide = turn.next();
            mChangeListener.onEnd(wonSide);
        }

        currentTurnSide = turn.next();

        if (wonSide != null) {
            return (double) -1;
        } else {
            return skillDelay;
        }
    }

    public void addToLog(String msg) {
        log.add(msg);
        Log.v("Arena Log", msg);
    }

    public String printStatus() {
        return "Near: " + near.toString() + ", Far: " + far.toString();
    }
}
