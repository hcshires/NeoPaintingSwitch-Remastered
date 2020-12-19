package org.projectdsm.neopaintingswitch;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

public class PaintingBreakEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPaintingBreak(HangingBreakEvent event) {
        if (event.isCancelled())
            return;

        Set<String> keys = SettingsList.getSettingsList().keySet();
        if (event instanceof HangingBreakByEntityEvent) {
            HangingBreakByEntityEvent entityBreakEvent = (HangingBreakByEntityEvent) event;
            if (entityBreakEvent.getRemover() instanceof Player) {
                Player player = (Player) entityBreakEvent.getRemover();
                Settings settings = SettingsList.getSettings(player.getName());
                if (settings.getPainting() != null && settings.getPainting().getEntityId() == event.getEntity().getEntityId()) {
                    SettingsList.clear(player.getName());
                }
                else {
                    for(String playerName : keys) {
                        if (SettingsList.getSettings(playerName).getPainting() != null && SettingsList.getSettings(playerName).getPainting().getEntityId() == event.getEntity().getEntityId() && !playerName.equals(player.getName())) {
                            player.sendMessage(ChatColor.RED + "This painting is being edited by " + ChatColor.WHITE + playerName);
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
        else {
            for(String playerName : keys) {
                if (SettingsList.getSettings(playerName).getPainting() != null && SettingsList.getSettings(playerName).getPainting().getEntityId() == event.getEntity().getEntityId()) {
                    SettingsList.clear(playerName);
                    return;
                }
            }
        }
    }
}
