package fr.insynia.craftclan;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Doc on 11/05/2015.
 */
public class FactionList implements Loadable {

    public void load(ResultSet rs) {
        List<Faction> FactionList = new ArrayList<Faction>();
        try {
            while (rs.next()) {
                String name = rs.getString("name");
                String color = rs.getString("color");
                int level = rs.getInt("level");
                FactionList.add(new Faction(name, color, level));
                MapState.getInstance().setFactions(FactionList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
