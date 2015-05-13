package fr.insynia.craftclan;

import org.bukkit.Location;

/**
 * Created by Doc on 11/05/2015.
 */
public class Point {
    private Location loc;
    private String name;
    private int radius;
    private int factionId;

    public Point(String name, int radius, Location loc, int factionId) {
        this.name = name;
        this.loc = loc;
        this.radius = radius;
        this.factionId = factionId;
    }

    public String toString() {
        return "x: " + loc.getX() + " y: " + loc.getY() + " z: " + loc.getZ();
    }

    public boolean save() {
        SQLManager sqlm = SQLManager.getInstance();
        loc.setX((int) loc.getX());
        loc.setY((int) loc.getY());
        loc.setZ((int) loc.getZ());
        boolean ret = sqlm.execUpdate("INSERT INTO points(name, radius, x, y, z, faction_id) " +
                "VALUES(\"" + name + "\", " + radius + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ", 0);");
        this.addToMap();
        return ret;
    }

    private void addToMap() {
        MapState.getInstance().addPoint(this);
        BlockSpawner.createBeacon(loc);
    }

    public boolean addToFaction(int factionId) {
        SQLManager sqlm = SQLManager.getInstance();
        this.factionId = factionId;
        return (sqlm.execUpdate("UPDATE points SET faction_id = " + factionId + " WHERE name = \"" + name + "\";"));
    }

    public static Point fromSQL() {
        return null;
    }

    public String getName() {
        return name;
    }

    public int getFactionId() {
        return factionId;
    }

    public Location getLocation() {
        return loc;
    }

    public int getRadius() {
        return radius;
    }
}
