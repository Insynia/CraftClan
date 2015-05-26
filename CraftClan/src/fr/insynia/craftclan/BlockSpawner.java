package fr.insynia.craftclan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.List;


/**
 * Created by Doc on 12/05/2015.
 * Modified by Sharowin on 18/05/2015
 */
public class BlockSpawner {

    private static final String DEFAULT_FILE = "structures/";
    private static final String DEFAULT_WORLD = "world";

    public static void createBeacon(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        World world = location.getWorld();

            world.getBlockAt(x, y - 2, z).setType(Material.BEACON);
            world.getBlockAt(x, y - 1, z).setType(Material.GLASS);

        for (int xPoint = x - 2 ; xPoint <= x + 2 ; xPoint++) {
            for (int zPoint = z - 2; zPoint <= z + 2; zPoint++) {
                world.getBlockAt(xPoint, y - 3, zPoint).setType(Material.IRON_BLOCK);
            }
        }
    }

    public static void spawnStructure(String filename, Location location) {
        // emptySky(location);
        List<String> blocks;
        List<String> parsed;
            blocks = FileManager.fileReadtoListCC(DEFAULT_FILE, filename);
            int i = 0;
            while (i < blocks.size()) {
                parsed = FileManager.parseLine(blocks.get(i));
                i += 1;
                spawnBlock(location, parsed);
        }
    }

    public static void spawnBlock(Location base, List<String> coords) {
        int x = Integer.parseInt(coords.get(0));
        int y = Integer.parseInt(coords.get(1));
        int z = Integer.parseInt(coords.get(2));
        String mat = coords.get(3);
        byte data = (byte) Integer.parseInt(coords.get(4));

        World world = Bukkit.getWorld(DEFAULT_WORLD);
        Block block = world.getBlockAt(x + (int) base.getX(), y + (int) base.getY(), z + (int) base.getZ());
        block.setType(Material.getMaterial(mat));
        block.setData(data);
    }

    public static void spawnBlock(Location base, int nX, int nY, int nZ, String mat, byte data) {
        World world = Bukkit.getWorld(DEFAULT_WORLD);
        Block block = world.getBlockAt(nX + (int) base.getX(), nY + (int) base.getY(), nZ + (int) base.getZ());
        block.setType(Material.getMaterial(mat));
        block.setData(data);
    }

    public static void saveStructure(String filename, Location from, Location to) {
        int x, y, z, yb, zb, x1, y1, z1;
        World world = Bukkit.getWorld(DEFAULT_WORLD);
        Location delta = new Location(world,
                Math.min(from.getX(), to.getX()),
                Math.min(from.getY(), to.getY()),
                Math.min(from.getZ(), to.getZ()));
        x = (int) from.getX();
        yb = y = (int) from.getY();
        zb = z = (int) from.getZ();
        x1 = (int) to.getX() + (from.getX() > to.getX() ? -1 : 1);
        y1 = (int) to.getY() + (from.getY() > to.getY() ? -1 : 1);
        z1 = (int) to.getZ() + (from.getZ() > to.getZ() ? -1 : 1);

        while (x != x1 || y != y1 || z != z1) {
            y = yb;
            while (y != y1 || z != z1) {
                z = zb;
                while (z != z1) {
                    saveBlock(filename, delta, x, y, z, world.getBlockAt(x, y, z));
                    z += (z > z1 ? -1 : 1);
                }
                y += (y > y1 ? -1 : 1);
            }
            x += (x > x1 ? -1 : 1);
        }
    }

    private static void saveBlock(String filename, Location delta, int x, int y, int z, Block block) {
        String line = ((int)(x - delta.getX())) + "," +
                ((int) (y - delta.getY())) + "," +
                ((int) (z - delta.getZ())) + "," +
                block.getType().toString() + "," +
                ((int) block.getData());
        FileManager.writeLineToFile(DEFAULT_FILE, filename, line);
    }

    public static void emptySky(Location location){
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        World world = location.getWorld();
        // Clearing Y-axis Upper-Zone

        int xPoint = x - 2;
        int yPoint = y + 2;
        int zPoint = z - 2;

        int yMax = world.getMaxHeight();
        while (xPoint <= x + 2) {
            while (zPoint <= z + 2) {
                while (yPoint <= yMax) {
                    world.getBlockAt(xPoint, yPoint, zPoint).setType(Material.AIR);
                    yPoint += 1;
                }
                zPoint += 1;
                yPoint = y + 2;
            }
            xPoint += 1;
            zPoint = z - 2;
        }
    }
    // Creating Beacon+Glass+Diamond Bed
}
