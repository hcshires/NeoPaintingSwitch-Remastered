package org.projectdsm.neopaintingswitch.events;

import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.projectdsm.neopaintingswitch.Settings;
import org.projectdsm.neopaintingswitch.SettingsList;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

/**
 * Handles events between Player and Painting entity
 */
public class PlayerEvent implements Listener {

    /** Available Art to iterate over **/
    Registry<Art> artRegistry = Bukkit.getRegistry(Art.class);

    /**
     * Whether the given player has sufficient permission to use NeoPaintingSwitch on paintings
     * @param player - the specified player
     * @return whether the given player can modify a painting
     */
    private boolean canModifyPainting(Player player) {
        final String usePermsNode = "neopaintingswitch.use";
        if (!player.hasPermission(usePermsNode)) {
            return WorldGuardPlugin.inst().hasPermission(player,"worldguard.build.*") || WorldGuardPlugin.inst().hasPermission(player,"worldguard.region.bypass." + player.getWorld().getName().toLowerCase());
        }

        return true;
    }

    /**
     * When a user places a painting, remember the user's last used painting and select that as the current painting
     * @param event - the specified hanging place event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onHangingPlace(HangingPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        if (player != null && canModifyPainting(player)) {
            Settings settings = SettingsList.getSettings(player.getName());
            if (settings.getPreviousPainting() != null && event.getEntity() instanceof Painting) {
                Painting painting = (Painting) event.getEntity();

                /* Place the user's previous painting, if they have one.
                If it doesn't fit, iterate through available art until one fits */
                if (!painting.setArt(settings.getPreviousPainting().getArt())) {
                    for (Art a : artRegistry) {
                        if (painting.setArt(a)) break;
                    }
                }
            }
        }
    }

    /**
     * Handle when a player interacts with an existing, placed Painting
     * @param event - the PlayerInteractEntity event with the Painting
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (event.getHand() == EquipmentSlot.HAND && entity instanceof Painting) {
            if (canModifyPainting(player)) {
                Set<String> keys = SettingsList.getSettingsList().keySet();

                for (String playerName : keys) {
                    if (SettingsList.getSettings(playerName).getPainting() != null && SettingsList.getSettings(playerName).getPainting().getEntityId() == entity.getEntityId() && !playerName.equals(player.getName())) {
                        player.sendMessage(playerName + ChatColor.RED + " is already editing this painting");
                        return;
                    }
                }

                /* Cache current painting */
                Settings settings = SettingsList.getSettings(player.getName());
                settings.setBlock(player.getTargetBlock(null, 20));
                settings.setPainting((Painting) entity);
                settings.setLocation(player.getLocation());

                if (settings.isClicked()) {
                    player.sendMessage(ChatColor.RED + "Painting locked");
                    SettingsList.clear(player.getName());
                }
                else {
                    player.sendMessage(ChatColor.GREEN + "Scroll to change painting");
                    settings.setClicked(true); // Painting has now been clicked
                }
            }
            else {
                player.sendMessage(ChatColor.RED + "You do not have permission to edit this painting");
                event.setCancelled(true);
            }
        }
    }

    /**
     * Handles if a Players moves a certain distance away from the current Painting while unlocked, then lock the Painting from further edits
     * @param event - The PlayerMoveEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        Settings settings = SettingsList.getSettings(player.getName());

        try {
            if (settings.getBlock() != null && settings.getLocation() != null && settings.isClicked() && hasPlayerMovedSignificantly(event)) {
                player.sendMessage(ChatColor.RED + "Painting locked");
                SettingsList.clear(player.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles when a Player scrolls through the hotbar to change a Painting
     * @param event - The PlayerItemHeldEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Settings settings = SettingsList.getSettings(player.getName());
        int previousSlot = event.getPreviousSlot();
        int newSlot = event.getNewSlot();
        boolean reverse = (previousSlot - newSlot) > 0;

        /* Account for switching between slot 0 and 8 or slot 8 and 0 */
        if (((previousSlot == 0) && (newSlot == 8)) || ((previousSlot == 8) && (newSlot == 0))) {
            reverse = !reverse;
        }

        /* Scroll to change painting */
        if (settings.isClicked() && settings.getPainting() != null && settings.getBlock() != null) {
            Painting painting = settings.getPainting();
            Art currentArt = painting.getArt();

            /* Find the current art within the list of options */
            ArrayList<Art> artList = new ArrayList<>();
            for (Art art : artRegistry) {
                artList.add(art);
            }

            /* Set iteration direction based on scroll and loop until next compatible painting */
            int startIndex = artList.indexOf(currentArt);
            int i = startIndex;
            int listSize = artList.size();
            int step = reverse ? -1 : 1;

            do {
                i = (i + step + listSize) % listSize; // Wrap around if edges are reached

                if (painting.setArt(artList.get(i))) {
                    break;
                }
            } while (i != startIndex);
            settings.setPreviousPainting(painting);
        }
    }

    /**
     * Returns if the player has moved enough distance from the Painting placement Block to lock the painting and prevent further unintentional edits
     * @param event - A PlayerMoveEvent
     * @return true if the Player has moved a set distance away, false otherwise
     */
    private boolean hasPlayerMovedSignificantly(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Settings settings = SettingsList.getSettings(player.getName());
        int oldPlayerPosX = Math.abs(settings.getLocation().getBlockX() + 100);
        int oldPlayerPosY = Math.abs(settings.getLocation().getBlockY() + 100);
        int oldPlayerPosZ = Math.abs(settings.getLocation().getBlockZ() + 100);
        int newPlayerPosX = Math.abs(Objects.requireNonNull(event.getTo()).getBlockX() + 100);
        int newPlayerPosY = Math.abs(event.getTo().getBlockY() + 100);
        int newPlayerPosZ = Math.abs(event.getTo().getBlockZ() + 100);

        /* If player moved further than original coordinate, swap the old coordinate */
        if (newPlayerPosX > oldPlayerPosX) {
            int temp = oldPlayerPosX;
            oldPlayerPosX = newPlayerPosX;
            newPlayerPosX = temp;
        }
        if (newPlayerPosY > oldPlayerPosY) {
            int temp = oldPlayerPosY;
            oldPlayerPosY = newPlayerPosY;
            newPlayerPosY = temp;
        }
        if (newPlayerPosZ > oldPlayerPosZ) {
            int temp = oldPlayerPosZ;
            oldPlayerPosZ = newPlayerPosZ;
            newPlayerPosZ = temp;
        }

        /* Pitch and Yaw differences */
        int oldPlayerYaw = (int) Math.abs(settings.getLocation().getYaw());
        int newPlayerYaw = (int) Math.abs(player.getLocation().getYaw());
        int oldPlayerPitch = (int) settings.getLocation().getPitch();
        int newPlayerPitch = (int) player.getLocation().getPitch();

        boolean hasYawChanged = hasYawChangedSignificantly(oldPlayerYaw, newPlayerYaw);
        boolean hasPitchChanged = hasPitchChangedSignificantly(oldPlayerPitch, newPlayerPitch);
        // -X or +X direction
        boolean hasXDirectionChanged = ((newPlayerYaw <= 315 && newPlayerYaw >= 225) || (newPlayerYaw <= 135 && newPlayerYaw >= 45)) &&
                ((oldPlayerPosX % newPlayerPosX > 7) || (oldPlayerPosY % newPlayerPosY > 2) || (oldPlayerPosZ % newPlayerPosZ > 2));
        // -Z or +Z direction
        boolean hasZDirectionChanged = ((newPlayerYaw < 45 || newPlayerYaw > 315) || (newPlayerYaw < 225 && newPlayerYaw > 135)) &&
                ((oldPlayerPosX % newPlayerPosX > 2) || (oldPlayerPosY % newPlayerPosY > 2) || (oldPlayerPosZ % newPlayerPosZ > 7));

        if ((hasYawChanged || hasPitchChanged) || hasXDirectionChanged || hasZDirectionChanged) {
            return !settings.getBlock().equals(player.getTargetBlock(null, 15));
        }

        return false;
    }

    /**
     * Return whether the Player's pitch has changed an amount determined by the method
     * @param oldPlayerPitch - the original pitch of the Player
     * @param newPlayerPitch - the current pitch of the Player
     * @return true if the Pitch has changed a set distance, false otherwise
     */
    private boolean hasPitchChangedSignificantly(int oldPlayerPitch, int newPlayerPitch) {
        if (newPlayerPitch > oldPlayerPitch) {
            int temp = oldPlayerPitch;
            oldPlayerPitch = newPlayerPitch;
            newPlayerPitch = temp;
        }
        return (oldPlayerPitch - newPlayerPitch) > 30;
    }

    /**
     * Return whether the Player's yaw has changed an amount determined by the method
     * @param oldYaw - the original yaw of the Player
     * @param newYaw - the current yaw of the Player
     * @return true if the Pitch has changed a set distance, false otherwise
     */
    private boolean hasYawChangedSignificantly(int oldYaw, int newYaw) {
        oldYaw = Math.abs(oldYaw) + 360;
        newYaw = Math.abs(newYaw) + 360;
        if (oldYaw < newYaw) {
            int temp = oldYaw;
            oldYaw = newYaw;
            newYaw = temp;
        }
        return (oldYaw % newYaw) > 30;
    }
}
