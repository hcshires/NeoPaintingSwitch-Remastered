package org.projectdsm.neopaintingswitch;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class NeoPaintingSwitch extends JavaPlugin {

    private static final Logger LOGGER = Logger.getLogger("Minecraft.neoPaintingSwitch");
    private static boolean usePerms = true;

    /**
     * Event handler when plugin is enabled. Setup the config file as well as bind the player and painting break events
     */
    @Override
    public void onEnable() {
        Server server = getServer();
        setupConfig();

        server.getPluginManager().registerEvents(new PlayerEvent(), this);
        server.getPluginManager().registerEvents(new PaintingBreakEvent(), this);

        LOGGER.info(getName() + " is enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        LOGGER.info(getName() + " is disabled!");
    }

    public static boolean isUsePerms() {
        return usePerms;
    }

    /**
     * Generate/use config file for optional use of permission nodes
     */
    private void setupConfig() {
        File configFile = new File(this.getDataFolder() + "/config.yml");
        FileConfiguration config = this.getConfig();
        if (!configFile.exists()) {
            config.set("userPermissionNodes", usePerms);
            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        usePerms = config.getBoolean("usePermissionNodes");
    }
}
