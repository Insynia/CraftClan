package fr.insynia.craftclan;

import org.bukkit.Bukkit;

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

    public void displayPoints() {
        Bukkit.getLogger().info(points.toString());
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> ps) {
        points = ps;
    }
}
