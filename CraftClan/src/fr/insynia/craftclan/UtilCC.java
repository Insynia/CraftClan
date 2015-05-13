package fr.insynia.craftclan;

import org.bukkit.Location;
import org.bukkit.util.NumberConversions;

/**
 * Created by Doc on 13/05/2015.
 */
public class UtilCC {
    public static double distance(Location from, Location to) {
        return Math.sqrt(distanceSquared(from, to));
    }

    public static double distanceSquared(Location from, Location to) {
        if (to == null) {
            throw new IllegalArgumentException("Cannot measure distance to a null location");
        } else if(to.getWorld() != null && from.getWorld() != null) {
            if(to.getWorld() != from.getWorld()) {
                throw new IllegalArgumentException("Cannot measure distance between " + from.getWorld().getName() + " and " + to.getWorld().getName());
            } else {
                return NumberConversions.square(from.getX() - to.getX()) + NumberConversions.square(from.getZ() - to.getZ());
            }
        } else {
            throw new IllegalArgumentException("Cannot measure distance to a null world");
        }
    }

    public static double distanceBasic(Location from, Location to) {
        if (to == null) {
            throw new IllegalArgumentException("Cannot measure distance to a null location");
        } else if(to.getWorld() != null && from.getWorld() != null) {
            if(to.getWorld() != from.getWorld()) {
                throw new IllegalArgumentException("Cannot measure distance between " + from.getWorld().getName() + " and " + to.getWorld().getName());
            } else {
                double xDist = Math.abs(from.getX() - to.getX());
                double zDist = Math.abs(from.getZ() - to.getZ());
                return (xDist > zDist ? xDist : zDist);
            }
        } else {
            throw new IllegalArgumentException("Cannot measure distance to a null world");
        }
    }
}
