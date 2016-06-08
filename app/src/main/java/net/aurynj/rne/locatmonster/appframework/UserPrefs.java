package net.aurynj.rne.locatmonster.appframework;

import android.content.Context;

import net.aurynj.rne.locatmonster.model.CharacterClass;
import net.aurynj.rne.locatmonster.model.CharacterStatus;

import java.util.ArrayList;

public class UserPrefs {
    private final Context mContext;
    private final PreferencesManager mPreferencesManager;
    long[] mCharacterTimestampList;
    ArrayList<CharacterStatus> mCharacterStatusList;

    // TODO: load and save CharacterStatus in PreferencesManager and UserPrefs

    public UserPrefs(final Context context) {
        mContext = context;
        mPreferencesManager = new PreferencesManager(context);


        PreferencesManager preferencesManager = new PreferencesManager(context);
        mCharacterTimestampList = preferencesManager.getCharacterTimestamps();

        mCharacterStatusList = new ArrayList<CharacterStatus>();
        for (long characterTimestamp: mCharacterTimestampList) {
            String className = preferencesManager.getClassName(characterTimestamp);
            String characterName = preferencesManager.getCharacterName(characterTimestamp);
            CharacterStatus characterStatus;
            try {
                characterStatus = CharacterStatus.fromClass((CharacterClass) Class.forName("net.aurynj.rne.locatmonster.data." + className).newInstance());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }

            characterStatus.Name = characterName;
        }
    }

    public void addCharacter(CharacterStatus characterStatus) {
        mCharacterStatusList.add(characterStatus);
    }

    public ArrayList<CharacterStatus> getCharacterStatusList() {
        return mCharacterStatusList;
    }
}
