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
    private int radius; //TODO

    public Point(String name, int radius, Location loc) {
        this.name = name;
        this.loc = loc;
        this.radius = radius;
    }

    public String toString() {
        return "x: " + loc.getX() + " y: " + loc.getY() + " z: " + loc.getZ();
    }

    public void saveSQL() {
        SQLManager sqlm = new SQLManager();
        sqlm.execUpdate("INSERT INTO points(name, radius, x, y, z) " +
                "VALUES(\"" + name + "\", " + radius + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ");");
    }

    public static Point fromSQL() {
        return null;
    }
}
