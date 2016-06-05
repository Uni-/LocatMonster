package net.aurynj.rne.locatmonster.model;

import java.util.Formatter;

public class CharacterStatus {
    public String Name;
    public String Alias;
    public int Level;
    public int HP, MP, SP;
    public SkillClass[] Skills;
    public static CharacterStatus fromClass(CharacterClass characterClass) {
        CharacterStatus characterStatus = new CharacterStatus();
        characterStatus.Name = characterClass.getName();
        characterStatus.Level = characterClass.getDefaultLevel();
        characterStatus.HP = characterClass.getBaseMaxHP() * characterStatus.Level;
        characterStatus.MP = characterClass.getBaseMaxMP() * characterStatus.Level;
        characterStatus.SP = characterClass.getBaseMaxSP() * characterStatus.Level;
        characterStatus.Skills = characterClass.getSkills();
        return characterStatus;
    }

    public String toString() {
        String aliasPart = (Alias != null && Alias.length() != 0) ? ("alias=" + Alias) : "no_alias";
        Formatter formatter = new Formatter();
        formatter.format("Character %s (%s) [Lv: %d, HP: %d, MP: %d, SP: %d]",
                Name, aliasPart, Level, HP, MP, SP);
        return formatter.toString();
    }
}
