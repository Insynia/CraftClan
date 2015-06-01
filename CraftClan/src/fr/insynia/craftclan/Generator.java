package fr.insynia.craftclan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * For CraftClan
 * Created by Doc on 01/06/2015 at 15:49.
 */
public class Generator {
    private static final int BASE_POINT_RADIUS = 20;
    private static final int POINT_RADIUS_MODIFIER = 10;

    public static int generatePoints(int layers) {
        int x, z, nb = 0;
        int curLayer = 1;
        int curInset = MapState.SPAWN_RADIUS;
        int pointRadius;
        Location spawnLoc = Bukkit.getWorld(MapState.DEFAULT_WORLD).getSpawnLocation();

        x = (int) spawnLoc.getX();
        z = (int) spawnLoc.getZ();

        while (layers != 0) {
            pointRadius = BASE_POINT_RADIUS + ((curLayer - 1) * POINT_RADIUS_MODIFIER);
            nb += genSquarePoints(pointRadius, x, z, curInset, curLayer);
            layers--;
            curInset += pointRadius + curLayer * POINT_RADIUS_MODIFIER;
            curLayer++;
        }
        return nb;
    }

    private static int genSquarePoints(int pointRadius, int xCenter, int zCenter, int insetRadius, int layer) {
        int nb = 0;
        int x = xCenter - insetRadius - pointRadius;
        int z = zCenter - insetRadius - pointRadius;
        World world = Bukkit.getWorld(MapState.DEFAULT_WORLD);

        while (x <= xCenter + insetRadius + pointRadius + 1) {
            new Point(layer + "_" + nb, pointRadius - 1, new Location(world, x, UtilCC.getFloorY(x, z), z), 1, 1).save();
            x += pointRadius * 2;
            nb++;
        }

        x = xCenter - insetRadius - pointRadius;
        z = zCenter + insetRadius + pointRadius;
        while (x <= xCenter + insetRadius + pointRadius + 1) {
            new Point(layer + "_" + nb, pointRadius - 1, new Location(world, x, UtilCC.getFloorY(x, z), z), 1, 1).save();
            x += pointRadius * 2;
            nb++;
        }

        x = xCenter + insetRadius + pointRadius;
        z = zCenter - insetRadius + pointRadius;
        while (z <= zCenter + insetRadius) {
            new Point(layer + "_" + nb, pointRadius - 1, new Location(world, x, UtilCC.getFloorY(x, z), z), 1, 1).save();
            z += pointRadius * 2;
            nb++;
        }

        x = xCenter - insetRadius - pointRadius;
        z = zCenter - insetRadius + pointRadius;
        while (z <= zCenter + insetRadius) {
            new Point(layer + "_" + nb, pointRadius, new Location(world, x, UtilCC.getFloorY(x, z), z), 1, 1).save();
            z += pointRadius * 2;
            nb++;
        }
        return nb;
    }
}
