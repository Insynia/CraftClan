package fr.insynia.craftclan;

import org.bukkit.Bukkit;

import java.sql.Array;
import java.util.List;

/**
 * Created by Doc on 11/05/2015.
 */
public class MapState {
    private static MapState instance = null;
    private List<Point> points;
    private List<Faction> factions;

    protected MapState() {}
    public static MapState getInstance() {
        if(instance == null) {
            instance = new MapState();
        }
        return instance;
    }

    public String stringPoints() {
        //Bukkit.getLogger().info();
        if (points == null) return "No point";
        String test = "";
        Point[] arr = points.toArray(new Point[points.size()]);
        for (Point anArr : arr) {
            test = test + "| Point: " + anArr.toString() + " ";
        }
        return test;
    }

    public String stringFactions() {
        //Bukkit.getLogger().info();
        if (points == null) return "No faction";
        String test = "";
        Faction[] arr = factions.toArray(new Faction[factions.size()]);
        for (Faction anArr : arr) {
            test = test + "| Faction: " + anArr.toString() + " ";
        }
        return test;
    }

    public List<Point> getPoints() {
        return points;
    }

    public List<Faction> getFactions() {
        return factions;
    }

    public void setPoints(List<Point> ps) {
        points = ps;
    }

    public void setFactions(List<Faction> fs) {
        factions = fs;
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public void removePoint(Point point) {
        points.remove(point);
    }

    public void addFaction(Faction faction) {
        factions.add(faction);
    }

    public void removeFaction(Faction faction) {
        factions.remove(faction);
    }
}
