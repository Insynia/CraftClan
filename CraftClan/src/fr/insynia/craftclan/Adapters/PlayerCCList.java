package fr.insynia.craftclan.Adapters;

import fr.insynia.craftclan.Interfaces.Loadable;
import fr.insynia.craftclan.Gameplay.PlayerCC;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * For CraftClan
 * Created by Doc on 28/05/2015 at 02:07.
 */
public class PlayerCCList implements Loadable {
    private List<PlayerCC> players;

    public PlayerCCList() {
        players = new ArrayList<>();
    }

    public void load(ResultSet rs) {
        try {
            while (rs.next()) {
                String name = rs.getString("name");
                String uuid = rs.getString("uuid");
                int faction_id = rs.getInt("faction_id");
                int level = rs.getInt("level");
                PlayerCC player = new PlayerCC(name, faction_id, level, UUID.fromString(uuid));
                players.add(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PlayerCC> getPlayers() {
        return players;
    }
}
