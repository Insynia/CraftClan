package fr.insynia.craftclan;

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
                Date begin = rs.getDate("start_time");
                Date end = rs.getDate("end_time");
                String userName = rs.getString("user_name");
                Protection protection = new Protection(id, pointId, begin, end, userName);
                MapState.getInstance().addProtection(protection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
