package fr.insynia.craftclan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Doc on 11/05/2015.
 */
public class PointList implements Loadable {
    private static final String DEFAULT_WORLD = "world";

    public void load(ResultSet rs) {
        try {
            while (rs.next()) {
                String name = rs.getString("name");
                int radius = rs.getInt("radius");
                int level = rs.getInt("level");
                int factionId = rs.getInt("faction_id");
                World world = Bukkit.getWorld(DEFAULT_WORLD);
                Location loc = new Location(world, rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                Point point = new Point(name, radius, loc, level, factionId);
                MapState.getInstance().addPoint(point);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
