package fr.insynia.craftclan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * Created by Doc on 12/05/2015.
 * Last modified by Sharowin on 13/05/2015_6:00
 */
//        Bukkit.getLogger().info("yMax: " + yMax);
public class BlockSpawner {
    private static final String DELIMITER = ",";
    private static final String DEFAULT_WORLD = "world";

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

    public static void testBeaconBase(Location location) {
        emptySky(location);

        String filepath = "test_file.ccs";
        BufferedReader br;
        String curline;
        try {
            br = new BufferedReader(new FileReader(filepath));

            while ((curline = br.readLine()) != null) {
                handleLine(curline);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleLine(String curline) {
        String[] coords = curline.split(DELIMITER);
        Bukkit.getLogger().info(curline);
        Bukkit.getLogger().info(coords[0]);
        int x = Integer.parseInt(coords[0]);
        int y = Integer.parseInt(coords[1]);
        int z = Integer.parseInt(coords[2]);
        String mat = coords[3];

        World world = Bukkit.getWorld(DEFAULT_WORLD);
        Bukkit.getLogger().info(world.getBlockAt(x, y - 2, z).getType().toString());
        world.getBlockAt(x, y, z).setType(Material.getMaterial(mat)); // Debug
    }


    private static void emptySky(Location location){
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
