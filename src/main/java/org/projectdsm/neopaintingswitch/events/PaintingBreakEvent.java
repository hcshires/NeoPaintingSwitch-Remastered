package org.projectdsm.neopaintingswitch.events;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.projectdsm.neopaintingswitch.Settings;
import org.projectdsm.neopaintingswitch.SettingsList;

/**
 * Handles when a Painting breaks after being placed by a Player with this plugin
 */
public class PaintingBreakEvent implements Listener {

    /**
     * Handles Painting breaks from the Painting editor, another player, or another entity
     * Clears the Player's Painting data if it is removed by themselves, or prevents another Player from removing it if they are not the editor of the given Painting
     * @param event - a HangingBreakEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPaintingBreak(HangingBreakEvent event) {
        if (event.isCancelled())
            return;
        Set<String> keys = SettingsList.getSettingsList().keySet();

        /* If a player removed the given Painting, clear their Painting cache if it is theirs, otherwise prevent removal */
        if (event instanceof HangingBreakByEntityEvent) {
            HangingBreakByEntityEvent entityBreakEvent = (HangingBreakByEntityEvent) event;
            if (entityBreakEvent.getRemover() instanceof Player) {
                Player player = (Player) entityBreakEvent.getRemover();
                Settings settings = SettingsList.getSettings(player.getName());
                if (settings.getPainting() != null && settings.getPainting().getEntityId() == event.getEntity().getEntityId()) {
                    SettingsList.clear(player.getName());
                }
                else {
                    for (String playerName : keys) {
                        if (!playerName.equals(player.getName()) && SettingsList.getSettings(playerName).getPainting() != null && SettingsList.getSettings(playerName).getPainting().getEntityId() == event.getEntity().getEntityId()) {
                            player.sendMessage(ChatColor.RED + "This painting is being edited by " + ChatColor.WHITE + playerName);
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }

        /* If a mechanic removed the painting, find the original owner, and clear their Painting cache */
        else {
            for (String playerName : keys) {
                if (SettingsList.getSettings(playerName).getPainting() != null && SettingsList.getSettings(playerName).getPainting().getEntityId() == event.getEntity().getEntityId()) {
                    SettingsList.clear(playerName);
                    return;
                }
            }
        }
    }
}
