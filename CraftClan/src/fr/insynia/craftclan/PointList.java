package fr.insynia.craftclan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Doc on 11/05/2015.
 */
public class PointList implements Loadable {
    private static final String DEFAULT_WORLD = "world";

    public void load(ResultSet rs) {
        List<Point> pointlist = new ArrayList<Point>();
        try {
            while (rs.next()) {
                String name = rs.getString("name");
                int radius = rs.getInt("radius");
                World world = Bukkit.getWorld(DEFAULT_WORLD);
                Location loc = new Location(world, rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                pointlist.add(new Point(name, radius, loc));
                MapState.getInstance().setPoints(pointlist);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
