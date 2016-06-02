package net.aurynj.rne.locatmonster.model;

public class CharacterStatus {
    public String Name;
    public String Alias;
    public int Level;
    public int HP, MP, SP;
    public static CharacterStatus fromClass(CharacterClass characterClass) {
        CharacterStatus characterStatus = new CharacterStatus();
        characterStatus.Name = characterClass.getName();
        characterStatus.Alias = "";
        characterStatus.Level = characterClass.getDefaultLevel();
        characterStatus.HP = characterClass.getBaseMaxHP() * characterStatus.Level;
        characterStatus.MP = characterClass.getBaseMaxMP() * characterStatus.Level;
        characterStatus.SP = characterClass.getBaseMaxSP() * characterStatus.Level;
        return characterStatus;
    }
}
