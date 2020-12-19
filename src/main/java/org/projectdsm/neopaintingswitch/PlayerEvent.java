package org.projectdsm.neopaintingswitch;

import org.bukkit.Art;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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
import org.bukkit.util.BlockIterator;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import java.util.*;

public class PlayerEvent implements Listener {

    private final String usePermsNode = "neopaintingswitch.use";

    /**
     * Whether the given player can modify a painting
     * @param player - the specified player
     * @param e - the current painting
     * @return whether the given player can modify a painting
     */
    private boolean canModifyPainting(Player player, Entity e) {
        if (NeoPaintingSwitch.isUsePerms() && !player.hasPermission(usePermsNode)) {
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

        if (player != null && player.hasPermission(usePermsNode)) {
            Settings settings = SettingsList.getSettings(player.getName());
            if (settings.getPreviousPainting() != null && event.getEntity() instanceof Painting) {
                Painting painting = (Painting) event.getEntity();
                if (!painting.setArt(settings.getPreviousPainting().getArt())) {
                    Art[] art = Art.values();
                    int count = new Random().nextInt(Art.values().length - 1);
                    int tempCount = count;
                    count--;
                    if (count == -1) count = 0;
                    while (!painting.setArt(art[count])) {
                        if (count == 0)
                            count = art.length - 1;
                        else
                            count--;
                        if (count == tempCount) break;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (event.getHand() == EquipmentSlot.HAND && entity instanceof Painting && player.hasPermission(usePermsNode)) {
            if (canModifyPainting(player, entity)) {
                Set<String> keys = SettingsList.getSettingsList().keySet();

                for(String playerName : keys) {
                    if (SettingsList.getSettings(playerName).getPainting() != null && SettingsList.getSettings(playerName).getPainting().getEntityId() == entity.getEntityId() && !playerName.equals(player.getName())) {
                        player.sendMessage(playerName + ChatColor.RED + " is already editing this painting.");
                        return;
                    }
                }

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
                    settings.setClicked(true);
                }
            }
            else {
                player.sendMessage(ChatColor.RED + "This Painting is locked by worldguard.");
                event.setCancelled(true);
            }
        }
    }

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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Settings settings = SettingsList.getSettings(player.getName());
        int previousSlot = event.getPreviousSlot();
        int newSlot = event.getNewSlot();
        boolean reverse = (previousSlot - newSlot) > 0;
        if (((previousSlot == 0) && (newSlot == 8)) || ((previousSlot == 8) && (newSlot == 0))) {
            reverse = !reverse;
        }
        if (settings.isClicked() && settings.getPainting() != null && settings.getBlock() != null && !reverse) {
            Painting painting = settings.getPainting();
            Art[] art = Art.values();
            int currentID = painting.getArt().ordinal();
            if (currentID == art.length - 1) {
                int count = 0;
                while (!painting.setArt(art[count])) {
                    if (count == art.length - 1) break;
                    count++;
                }
            }
            else {
                int count = painting.getArt().ordinal();
                int tempCount = count;
                count++;
                while (!painting.setArt(art[count])) {
                    if (count == art.length - 1)
                        count = 0;
                    else
                        count++;
                    if (count == tempCount) break;
                }
            }
            settings.setPreviousPainting(painting);
        }
        else if (settings.isClicked() && settings.getPainting() != null && settings.getBlock() != null) {
            Painting painting = settings.getPainting();
            Art[] art = Art.values();
            int currentID = painting.getArt().ordinal();
            if (currentID == 0) {
                int count = art.length - 1;
                while (!painting.setArt(art[count])) {
                    count--;
                    if (count == 0) break;
                }
            }
            else {
                int count = painting.getArt().ordinal();
                int tempCount = count;
                count--;
                while (!painting.setArt(art[count])) {
                    if (count == 0)
                        count = art.length - 1;
                    else
                        count--;
                    if (count == tempCount) break;
                }
            }
            settings.setPreviousPainting(painting);
        }
    }

    private boolean hasPlayerMovedSignificantly(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Settings settings = SettingsList.getSettings(player.getName());
        int oldPlayerPosX = Math.abs(settings.getLocation().getBlockX() + 100);
        int oldPlayerPosY = Math.abs(settings.getLocation().getBlockY() + 100);
        int oldPlayerPosZ = Math.abs(settings.getLocation().getBlockZ() + 100);
        int newPlayerPosX = Math.abs(event.getTo().getBlockX() + 100);
        int newPlayerPosY = Math.abs(event.getTo().getBlockY() + 100);
        int newPlayerPosZ = Math.abs(event.getTo().getBlockZ() + 100);
        if (oldPlayerPosX < newPlayerPosX) {
            int temp = oldPlayerPosX;
            oldPlayerPosX = newPlayerPosX;
            newPlayerPosX = temp;
        }
        if (oldPlayerPosY < newPlayerPosY) {
            int temp = oldPlayerPosY;
            oldPlayerPosY = newPlayerPosY;
            newPlayerPosY = temp;
        }
        if (oldPlayerPosZ < newPlayerPosZ) {
            int temp = oldPlayerPosZ;
            oldPlayerPosZ = newPlayerPosZ;
            newPlayerPosZ = temp;
        }
        int oldPlayerYaw = (int) Math.abs(settings.getLocation().getYaw());
        int newPlayerYaw = (int) Math.abs(player.getLocation().getYaw());
        int oldPlayerPitch = (int) settings.getLocation().getPitch();
        int newPlayerPitch = (int) player.getLocation().getPitch();
        if (hasYawChangedSignificantly(oldPlayerYaw, newPlayerYaw) || hasPitchChangedSignificantly(oldPlayerPitch, newPlayerPitch)) {
            //if (!settings.block.equals(player.getTargetBlock(null, 15))) { return true; } //TODO
            if (!settings.getBlock().equals(getTargetBlock(player, null, 15))) { return true; }
        }
        if (((newPlayerYaw <= 315 && newPlayerYaw >= 225) || (newPlayerYaw <= 135 && newPlayerYaw >= 45)) &&
                ((oldPlayerPosX % newPlayerPosX > 7) || (oldPlayerPosY % newPlayerPosY > 2) || (oldPlayerPosZ % newPlayerPosZ > 2))) { // -X or +X direction
            //if (!settings.block.equals(player.getTargetBlock(null, 15))) { return true; } //TODO
            if (!settings.getBlock().equals(getTargetBlock(player, null, 15))) { return true; }
        }
        if (((newPlayerYaw < 45 || newPlayerYaw > 315) || (newPlayerYaw < 225 && newPlayerYaw > 135)) &&
                ((oldPlayerPosX % newPlayerPosX > 2) || (oldPlayerPosY % newPlayerPosY > 2) || (oldPlayerPosZ % newPlayerPosZ > 7))) { // -Z or +Z direction
            //if (!settings.block.equals(player.getTargetBlock(null, 15))) { return true; } //TODO
            return !settings.getBlock().equals(getTargetBlock(player, null, 15));
        }
        return false;
    }

    /**
     * Gets the block that the living entity has targeted.
     *
     * @param entity
     *            this is the entity to get target block
     * @param transparent
     *            HashSet containing all transparent block Materials (set to
     *            null for only air)
     * @param maxDistance
     *            this is the maximum distance to scan (may be limited by server
     *            by at least 100 blocks, no less)
     * @return block that the living entity has targeted
     */
    private Block getTargetBlock(LivingEntity entity, HashSet<Material> transparent, int maxDistance) {
        Block target = entity.getEyeLocation().getBlock();
        Location eyeLoc = entity.getEyeLocation();
        if (transparent == null){
            transparent = new HashSet<Material>();
            transparent.add(Material.AIR);
        }
        try {
            BlockIterator lineOfSight = new BlockIterator(entity.getWorld(), eyeLoc.toVector(), entity.getLocation().getDirection(), 0, maxDistance);
            while (lineOfSight.hasNext()) {
                Block toTest = lineOfSight.next();
                if (!transparent.contains(toTest.getType()))
                    return target;
                else
                    target = toTest;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return target;
    }

    private boolean hasPitchChangedSignificantly(int oldPlayerPitch, int newPlayerPitch) {
        if (oldPlayerPitch < newPlayerPitch) {
            int temp = oldPlayerPitch;
            oldPlayerPitch = newPlayerPitch;
            newPlayerPitch = temp;
        }
        return (oldPlayerPitch - newPlayerPitch) > 30;
    }

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
