package fr.insynia.craftclan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Doc on 11/05/2015.
 */
public class Point {
    private Location loc;
    private String name;
    private int range; //TODO

    public Point(String name, int range, Location loc) {
        this.name = name;
        this.loc = loc;
        this.range = range;
    }

    public void saveSQL() {
        SQLManager sqlm = new SQLManager();
        sqlm.execUpdate("INSERT INTO points(name, range, x, y, z) " +
                "VALUES(" + name + ", " + range + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ");");
    }

    public static Point fromSQL() {
        return null;
    }
}
