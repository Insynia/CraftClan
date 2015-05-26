package fr.insynia.craftclan;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Doc on 13/05/2015.
 */
public class UtilCC {

    public static int distanceBasicFull(Location from, Location to) {
        if (to == null) {
            throw new IllegalArgumentException("Cannot measure distance to a null location");
        } else if(to.getWorld() != null && from.getWorld() != null) {
            if(to.getWorld() != from.getWorld()) {
                throw new IllegalArgumentException("Cannot measure distance between " + from.getWorld().getName() + " and " + to.getWorld().getName());
            } else {
                int xDist = Math.abs((int)(from.getX()) - (int)(to.getX()));
                int zDist = Math.abs((int)(from.getZ()) - (int)(to.getZ()));
                int yDist = Math.abs((int)(from.getY()) - (int)(to.getY()));
                return (xDist > zDist ? (xDist > yDist ? xDist : yDist) : (zDist > yDist ? zDist : yDist));
            }
        } else {
            throw new IllegalArgumentException("Cannot measure distance to a null world");
        }
    }

    public static int distanceBasic(Location from, Location to) {
        if (to == null) {
            throw new IllegalArgumentException("Cannot measure distance to a null location");
        } else if(to.getWorld() != null && from.getWorld() != null) {
            if(to.getWorld() != from.getWorld()) {
                throw new IllegalArgumentException("Cannot measure distance between " + from.getWorld().getName() + " and " + to.getWorld().getName());
            } else {
                int xDist = Math.abs((int)(from.getX()) - (int)(to.getX()));
                int zDist = Math.abs((int)(from.getZ()) - (int)(to.getZ()));
                return (xDist > zDist ? xDist : zDist);
            }
        } else {
            throw new IllegalArgumentException("Cannot measure distance to a null world");
        }
    }

    public static void timeLeftCapture(int time, Player p, PlayerCC pcc) {
        if (time <= 10) p.sendMessage("Il vous reste " + time + " secondes pour capturer le point !");
        else if (time <= 60 && time % 5 == 0) p.sendMessage("Il vous reste " + time + " secondes pour capturer le point !");
        else if (time > 60 && time % 10 == 0) p.sendMessage("Il vous reste " + time + " secondes pour capturer le point !");
    }

    // Récupère la partie entière, du nombre divisé par 2
    public static int halfRound(int toHalfRound) {
        return Math.round(toHalfRound / 2);
    }
}
