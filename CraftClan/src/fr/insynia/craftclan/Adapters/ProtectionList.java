package fr.insynia.craftclan.Adapters;

import fr.insynia.craftclan.Interfaces.Loadable;
import fr.insynia.craftclan.Gameplay.MapState;
import fr.insynia.craftclan.Gameplay.Protection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

/**
 * For CraftClan
 * Created by Doc on 10/06/15 at 19:16.
 */
public class ProtectionList implements Loadable {

    public void load(ResultSet rs) {
        try {
            while (rs.next()) {
                int id = rs.getInt("id");
                int pointId = rs.getInt("point_id");
                Date begin = new Date(rs.getTimestamp("begin").getTime());
                Date end = new Date(rs.getTimestamp("end").getTime());
                String userName = rs.getString("user_name");
                Protection protection = new Protection(id, pointId, begin, end, userName);
                MapState.getInstance().addProtection(protection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
