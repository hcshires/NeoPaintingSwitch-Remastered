package org.projectdsm.neopaintingswitch;

import java.util.HashMap;

public class SettingsList {
    private static final HashMap<String, Settings> serverSettings = new HashMap<>();

    /**
     * Get the settings of a player
     * @param playerName - the specified player's name
     * @return - an object of settings for that player
     */
    public static Settings getSettings(String playerName) {
        if (serverSettings.get(playerName) == null) {
            newSettings(playerName, new Settings());
        }
        return serverSettings.get(playerName);
    }

    public static HashMap<String, Settings> getSettingsList() {
        return serverSettings;
    }

    public static void newSettings(String playerName, Settings settings) {
        serverSettings.put(playerName, settings);
    }

    public static void clear(String playerName) {
        serverSettings.get(playerName).setClicked(false);
        serverSettings.get(playerName).setBlock(null);
        serverSettings.get(playerName).setPainting(null);
        serverSettings.get(playerName).setLocation(null);
    }
}
