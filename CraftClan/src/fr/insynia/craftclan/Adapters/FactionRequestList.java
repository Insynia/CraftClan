package fr.insynia.craftclan.Adapters;

import fr.insynia.craftclan.Gameplay.MapState;
import fr.insynia.craftclan.Gameplay.Request;
import fr.insynia.craftclan.Interfaces.Loadable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * For CraftClan
 * Created by Doc on 11/05/2015 at 19:54.
 */
public class FactionRequestList implements Loadable {

    public void load(ResultSet rs) {
        try {
            while (rs.next()) {
                int id = rs.getInt("id");
                int faction_id = rs.getInt("faction_id");
                String user_name = rs.getString("user_name");
                Request request = new Request(id, faction_id, user_name);
                MapState.getInstance().addRequest(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
