package fr.insynia.craftclan;

import org.bukkit.*;

import java.io.File;
import java.util.Random;

/**
 * For CraftClan
 * Created by Doc on 01/06/2015 at 15:49.
 */
public class Generator {

    public static void resetFarmingZone() {
        Bukkit.getLogger().info("The farming zone is being reset !");
        World farmingZone = Bukkit.getWorld(MapState.FARM_WORLD);
        if (farmingZone != null) {
            File folder = farmingZone.getWorldFolder();
            UtilCC.kickPlayersFromWorld(MapState.FARM_WORLD);
            if (Bukkit.getServer().unloadWorld(MapState.FARM_WORLD, true))
                FileManager.deleteFileOrFolder(folder);
        }
        createFarmWorld();
    }

    private static void createFarmWorld() {
        WorldCreator wc = new WorldCreator(MapState.FARM_WORLD);
        wc.seed((new Random()).nextLong());
        wc.type(WorldType.AMPLIFIED);
        wc.createWorld();
    }

    public static int generatePoints(int layers) {
        int x, z, nb = 0;
        int curLayer = 1;
        int diameter = 118; // Can't touch this. Well, calculate this. Your go (cf line 25/26), ONLY INCREASE THIS (Base value 118) <- do not remove this
        int pointRadius = choseRadius(diameter); // Hammer time
        Location spawnLoc = Bukkit.getWorld(MapState.DEFAULT_WORLD).getSpawnLocation();

        x = (int) spawnLoc.getX();
        z = (int) spawnLoc.getZ();

        while (layers != 0) {
            Bukkit.getLogger().info("Point radius: " + pointRadius + ", diameter: " + diameter + ", layer: " + curLayer + ", center x: " + x + ", z: " + z);
            nb += genSquarePoints(pointRadius, x, z, diameter, curLayer);
            diameter += 2 * (pointRadius * 2 + 1);
            pointRadius = choseRadius(diameter);
            layers--;
            curLayer++;
        }
        return nb;
    }

    private static int choseRadius(int diameter) {
        int maxRadius = 150; // Max point radius
        int nbPoints = 2;
        int radius;

        diameter--;

        while (nbPoints <= 50) {
            nbPoints++;
            if ((diameter - nbPoints) % nbPoints == 0) {
                radius = ((diameter - nbPoints) / nbPoints) / 2;
                if (radius > 5 && radius <= maxRadius) return radius;
            }
        }

        return 0;
    }

    private static int genSquarePoints(int pointRadius, int xCenter, int zCenter, int diameter, int layer) {
        int nb = 0;
        int insetRadius = (diameter / 2);
        int x = xCenter - insetRadius - pointRadius - 1;
        int z = zCenter - insetRadius - pointRadius - 1;
        int neutralFactionId = MapState.getInstance().findFaction("Neutre").getId();
        World world = Bukkit.getWorld(MapState.DEFAULT_WORLD);

        while (x <= xCenter + insetRadius + pointRadius + 1) {
            new Point(layer + "_" + nb, pointRadius, new Location(world, x, UtilCC.getFloorY(x, z), z), 1, neutralFactionId).save();
            x += pointRadius * 2 + 1;
            nb++;
        }

        x = xCenter - insetRadius - pointRadius - 1;
        z = zCenter + insetRadius + pointRadius - 1;
        while (x <= xCenter + insetRadius + pointRadius + 1) {
            new Point(layer + "_" + nb, pointRadius, new Location(world, x, UtilCC.getFloorY(x, z), z), 1, neutralFactionId).save();
            x += pointRadius * 2 + 1;
            nb++;
        }

        x = xCenter + insetRadius + pointRadius - 1;
        z = zCenter - insetRadius + pointRadius;
        while (z <= zCenter + insetRadius + 1) {
            new Point(layer + "_" + nb, pointRadius, new Location(world, x, UtilCC.getFloorY(x, z), z), 1, neutralFactionId).save();
            z += pointRadius * 2 + 1;
            nb++;
        }

        x = xCenter - insetRadius - pointRadius - 1;
        z = zCenter - insetRadius + pointRadius;
        while (z <= zCenter + insetRadius + 1) {
            new Point(layer + "_" + nb, pointRadius, new Location(world, x, UtilCC.getFloorY(x, z), z), 1, neutralFactionId).save();
            z += pointRadius * 2 + 1;
            nb++;
        }
        return nb;
    }
}
