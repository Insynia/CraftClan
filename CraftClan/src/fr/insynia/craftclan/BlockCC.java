package fr.insynia.craftclan;

import org.bukkit.Location;
import org.bukkit.Material;

/**
 * For CraftClan
 * Created by Doc on 28/05/2015 at 02:15.
 */
public class BlockCC {
    private Location loc;
    private int meta;
    private String type, action;

    public BlockCC(Location loc, String type, int meta, String action) {
        this.loc = loc;
        this.meta = meta;
        this.type = type;
        this.action = action;
    }

    public void restore() {
        if (action.equals("BREAK"))
            BlockSpawner.spawnBlock(loc, type, (byte) meta);
        else
            BlockSpawner.spawnBlock(loc, Material.AIR.toString(), (byte) 0);
    }
}