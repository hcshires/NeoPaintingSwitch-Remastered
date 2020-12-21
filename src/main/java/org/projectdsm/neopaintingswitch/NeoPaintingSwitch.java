package org.projectdsm.neopaintingswitch;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.projectdsm.neopaintingswitch.events.PaintingBreakEvent;
import org.projectdsm.neopaintingswitch.events.PlayerEvent;

/**
 * Static instance of NeoPaintingSwitch and its plugin events
 */
public class NeoPaintingSwitch extends JavaPlugin {

    /**
     * Event handler when plugin is enabled. Binds Player and PaintingBreak events
     */
    @Override
    public void onEnable() {
        Server server = getServer();
        server.getPluginManager().registerEvents(new PlayerEvent(), this);
        server.getPluginManager().registerEvents(new PaintingBreakEvent(), this);

        getLogger().info(getName() + " is enabled!");
    }

    /**
     * Event handler when plugin is disabled
     */
    @Override
    public void onDisable() {
        getLogger().info(getName() + " is disabled!");
    }
}
