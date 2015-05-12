package fr.insynia.craftclan;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

/**
 * Created by Doc on 12/05/2015.
 */
public class BlockSpawner {
    public static void createBeacon(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        World world = location.getWorld();

        world.getBlockAt(x, y - 2, z).setType(Material.BEACON);
        world.getBlockAt(x, y - 1, z).setType(Material.GLASS);
        for (int xPoint = x-1; xPoint <= x+1 ; xPoint++) {
            for (int zPoint = z-1 ; zPoint <= z+1; zPoint++) {
                world.getBlockAt(xPoint, y-3, zPoint).setType(Material.IRON_BLOCK);
            }
        }
    }
}
