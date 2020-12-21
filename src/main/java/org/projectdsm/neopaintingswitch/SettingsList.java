package org.projectdsm.neopaintingswitch;

import java.util.HashMap;

/**
 * Contains the NeoPaintingSwitch Painting cache for all players who've interacted with this plugin
 */
public class SettingsList {
    private static final HashMap<String, Settings> serverSettings = new HashMap<>();

    /**
     * Get the Painting cache of a player
     * @param playerName - the specified player's name
     * @return an object of settings for that player
     */
    public static Settings getSettings(String playerName) {
        if (serverSettings.get(playerName) == null) {
            newSettings(playerName, new Settings());
        }
        return serverSettings.get(playerName);
    }

    /**
     * Get the entire list of Painting cache for all players who have used this plugin
     * @return a map of Settings fields for each player
     */
    public static HashMap<String, Settings> getSettingsList() {
        return serverSettings;
    }

    /**
     * Create a new map entry with Settings fields for a specified player
     * @param playerName - the given player's name
     * @param settings - an instance of Settings with metadata fields
     */
    public static void newSettings(String playerName, Settings settings) {
        serverSettings.put(playerName, settings);
    }

    /**
     * Clear cache regarding the Painting the Player is currently interacting with
     * @param playerName - the Player's name
     */
    public static void clear(String playerName) {
        serverSettings.get(playerName).setClicked(false);
        serverSettings.get(playerName).setBlock(null);
        serverSettings.get(playerName).setPainting(null);
        serverSettings.get(playerName).setLocation(null);
    }
}
