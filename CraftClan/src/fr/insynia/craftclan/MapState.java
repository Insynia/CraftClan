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

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> ps) {
        points = ps;
    }
}
