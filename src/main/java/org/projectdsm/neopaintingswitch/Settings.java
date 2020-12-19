package org.projectdsm.neopaintingswitch;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Stores the metadata of the current block the player is interacting with
 */
public class Settings {

    private boolean clicked;
    private Block block;
    private Painting painting;
    private Painting previousPainting;
    private Location location;

    public Settings() {
        clicked = false;
        block = null;
        painting = null;
        previousPainting = null;
        location = null;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Painting getPainting() {
        return painting;
    }

    public void setPainting(Painting painting) {
        this.painting = painting;
    }

    public Painting getPreviousPainting() {
        return previousPainting;
    }

    public void setPreviousPainting(Painting previousPainting) {
        this.previousPainting = previousPainting;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
