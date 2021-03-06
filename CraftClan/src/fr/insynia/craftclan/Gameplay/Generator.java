package fr.insynia.craftclan.Gameplay;

import fr.insynia.craftclan.Utils.FileManagerCC;
import fr.insynia.craftclan.Utils.UtilCC;
import org.bukkit.*;

import java.io.File;
import java.util.Random;

/**
 * For CraftClan
 * Created by Doc on 01/06/2015 at 15:49.
 */
public class Generator {

    public static void regenerate() {
        MapState ms = MapState.getInstance();
        for (Point p : ms.getPoints()) p.materialize();
    }

    public static int generatePoints(int layers) {
        int x, z, nb = 0;
        int curLayer = 1;
        int diameter = 196; // Can't touch this. Well, calculate this. Your go (cf line 25/26), ONLY INCREASE THIS (Base value 118) <- do not remove this
        // Remember that this value is the diameter - 1 (for the center point)
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
        int neutralFactionId = MapState.getInstance().findFaction(Faction.NEUTRAL_FACTION).getId();
        World world = Bukkit.getWorld(MapState.DEFAULT_WORLD);

        while (x <= xCenter + insetRadius + pointRadius + 1) {
            new Point(layer + "_" + nb, pointRadius, new Location(world, hotFix(x), UtilCC.getFloorY(hotFix(x), hotFix(z)), hotFix(z)), 1, neutralFactionId).save();
            x += pointRadius * 2 + 1;
            nb++;
        }

        x = xCenter - insetRadius - pointRadius - 1;
        z = zCenter + insetRadius + pointRadius - 1;
        while (x <= xCenter + insetRadius + pointRadius + 1) {
            new Point(layer + "_" + nb, pointRadius, new Location(world, hotFix(x), UtilCC.getFloorY(hotFix(x), hotFix(z)), hotFix(z)), 1, neutralFactionId).save();
            x += pointRadius * 2 + 1;
            nb++;
        }

        x = xCenter + insetRadius + pointRadius - 1;
        z = zCenter - insetRadius + pointRadius;
        while (z <= zCenter + insetRadius + 1) {
            new Point(layer + "_" + nb, pointRadius, new Location(world, hotFix(x), UtilCC.getFloorY(hotFix(x), hotFix(z)), hotFix(z)), 1, neutralFactionId).save();
            z += pointRadius * 2 + 1;
            nb++;
        }

        x = xCenter - insetRadius - pointRadius - 1;
        z = zCenter - insetRadius + pointRadius;
        while (z <= zCenter + insetRadius + 1) {
            new Point(layer + "_" + nb, pointRadius, new Location(world, hotFix(x), UtilCC.getFloorY(hotFix(x), hotFix(z)), hotFix(z)), 1, neutralFactionId).save();
            z += pointRadius * 2 + 1;
            nb++;
        }
        return nb;
    }

    private static int hotFix(int var) {
        return var < 0 ? var + 1 : var;
    }

    private static void createWorld(String name, World.Environment env) {
        final WorldCreator wc = new WorldCreator(name);
        wc.seed((new Random()).nextLong());
        wc.type(WorldType.NORMAL);
        wc.environment(env);
        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("CraftClan"),
                new Runnable() {
                    @Override
                    public void run() {
                        wc.createWorld();
                    }
                });
    }

    public static void deleteWorld(String name) {
        Bukkit.getLogger().info("The world " + name + " is being reset !");
        World curWorld = Bukkit.getWorld(name);
        if (curWorld != null) {
            File folder = curWorld.getWorldFolder();
            UtilCC.kickPlayersFromWorld(name);
            if (Bukkit.getServer().unloadWorld(name, true))
                FileManagerCC.deleteFileOrFolder(folder);
        }
    }

    public static void resetFarmingZone() {
        deleteWorld(MapState.FARM_WORLD);
        createWorld(MapState.FARM_WORLD, World.Environment.NORMAL);
    }

    public static void resetNether() {
        deleteWorld(MapState.NETHER_WORLD);
        createWorld(MapState.NETHER_WORLD, World.Environment.NETHER);
    }

    public static void resetTheEnd() {
        deleteWorld(MapState.ENDER_WORLD);
        createWorld(MapState.ENDER_WORLD, World.Environment.THE_END);
    }
}
