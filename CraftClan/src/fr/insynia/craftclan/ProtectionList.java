package fr.insynia.craftclan;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * For CraftClan
 * Created by Doc on 10/06/15 at 19:16.
 */
public class ProtectionList implements Loadable {

    public void load(ResultSet rs) {
        try {
            while (rs.next()) {
                int id = rs.getInt("id");
                int point_id = rs.getInt("point_id");
                Date begin = rs.getDate("start_time");
                Date end = rs.getDate("start_time");
                // Protection protection = new Protection(id, point_id, begin, end);
                // MapState.getInstance().addProtection(protection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
