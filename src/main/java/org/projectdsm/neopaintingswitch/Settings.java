package org.projectdsm.neopaintingswitch;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Painting;

/**
 * Caches the metadata of the current instance of a Painting placed by a Player
 */
public class Settings {

    private boolean clicked;
    private Block block;
    private Painting painting;
    private Painting previousPainting;
    private Location location;

    /**
     * Create a default instance of Settings
     */
    public Settings() {
        clicked = false;
        block = null;
        painting = null;
        previousPainting = null;
        location = null;
    }

    /**
     * Get the locked or unlocked state of the Painting
     * @return true if the Painting has been clicked (in edit mode) or false for un-clicked (NOT in edit mode)
     */
    public boolean isClicked() {
        return clicked;
    }

    /**
     * Set whether a Painting has been clicked or unclicked
     * @param clicked - true for clicked (in edit mode) or false for un-clicked (NOT in edit mode)
     */
    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    /**
     * Get the given Block the Painting is placed on
     * @return the block the Painting is placed on
     */
    public Block getBlock() {
        return block;
    }

    /**
     * Record the Block that the Painting is placed on
     * @param block - the specified Block object
     */
    public void setBlock(Block block) {
        this.block = block;
    }

    /**
     * Get the current Painting object
     * @return a Painting object
     */
    public Painting getPainting() {
        return painting;
    }

    /**
     * Set the current Painting object
     * @param painting - the specified Painting
     */
    public void setPainting(Painting painting) {
        this.painting = painting;
    }

    /**
     * Get the same Painting that the Player previously placed
     * @return the previous Painting placed by the Player
     */
    public Painting getPreviousPainting() {
        return previousPainting;
    }

    /**
     * Record the Painting that the Player just placed
     * @param previousPainting - the current Painting placed by the Player
     */
    public void setPreviousPainting(Painting previousPainting) {
        this.previousPainting = previousPainting;
    }

    /**
     * Get the current location of the Player as they placed the Painting
     * @return the location of the Player at placement
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Set the location of the Player as they placed the Painting
     * @param location - the specified Location object
     */
    public void setLocation(Location location) {
        this.location = location;
    }
}
