package fr.insynia.craftclan;

import org.bukkit.Location;

import java.util.List;

/**
 * For CraftClan
 * Created by Doc on 26/05/2015 at 23:17.
 */
public class MapUtils {
    public static Point getLocationPoint(Location loc) {
        List<Point> points = MapState.getInstance().getPoints();

        for (Point p : points) {
            if (UtilCC.distanceBasic(loc, p.getLocation()) <= p.getRadius())
                return p;
        }
        return null;
    }
}
