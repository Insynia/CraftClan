package fr.insynia.craftclan.Adapters;

import fr.insynia.craftclan.Gameplay.Faction;
import fr.insynia.craftclan.Interfaces.Loadable;
import fr.insynia.craftclan.Gameplay.MapState;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * For CraftClan
 * Created by Doc on 11/05/2015 at 19:54.
 */
public class FactionList implements Loadable {

    public void load(ResultSet rs) {
        try {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String status = rs.getString("status");
                String leaderName = rs.getString("leader_name");
                String color = rs.getString("color");
                int level = rs.getInt("level");
                Faction faction = new Faction(id, name, color, level, status, leaderName);
                MapState.getInstance().addFaction(faction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
