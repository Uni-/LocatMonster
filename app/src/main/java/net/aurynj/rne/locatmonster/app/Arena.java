package net.aurynj.rne.locatmonster.app;

import net.aurynj.rne.locatmonster.data.*;
import net.aurynj.rne.locatmonster.model.*;

import java.util.ArrayList;
import java.util.List;

public class Arena extends ArenaClass {
    public final List<CharacterStatus> near = new ArrayList<>(), far = new ArrayList<>();

    public Arena() {
        near.add(CharacterStatus.fromClass(new Character_ID_1()));
        far.add(CharacterStatus.fromClass(new Character_ID_2()));
    }

    public void proceed() {
    }

    public String printStatus() {
        return "Near: " + near.toString() + ", Far: " + far.toString();
    }
}
