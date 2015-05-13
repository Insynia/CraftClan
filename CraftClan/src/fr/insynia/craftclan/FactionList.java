package fr.insynia.craftclan;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Doc on 11/05/2015.
 */
public class FactionList implements Loadable {

    public void load(ResultSet rs) {
        try {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String color = rs.getString("color");
                int level = rs.getInt("level");
                Faction faction = new Faction(id, name, color, level);
                MapState.getInstance().addFaction(faction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
