package fr.insynia.craftclan;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Doc on 12/05/2015.
 */
public class PlayerCC implements Loadable {
    private String name;
    private int factionId;
    private int level;
    private UUID uuid;

    public PlayerCC(){}

    public PlayerCC(String name, int factionId, int level, UUID uuid) {
        this.name = name;
        this.factionId = factionId;
        this.level = level;
        this.uuid = uuid;
    }

    public String toString() {
        return name + ", " + factionId + ", " + level;
    }

    public void load(ResultSet rs) {
        try {
            if (rs.next()) {
                name = rs.getString("name");
                factionId = rs.getInt("faction_id");
                level = rs.getInt("level");
                uuid = UUID.fromString(rs.getString("uuid"));
                MapState.getInstance().addPlayer(this);
                Bukkit.getLogger().info("Fetched player: " + this.name + " from the database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UUID getUUID() {
        return uuid;
    }

    public static void create(Player player) {
        PlayerCC playerCC = new PlayerCC(player.getName(), 0, 0, player.getUniqueId());
        MapState.getInstance().addPlayer(playerCC);
        playerCC.save();
        Bukkit.getLogger().info("Created player: " + playerCC.name);
    }

    private void save() {
        SQLManager sqlm = new SQLManager();
        sqlm.execUpdate("INSERT INTO users(name, faction_id, level, uuid) " +
                "VALUES(\"" + name + "\", " + factionId + ", " + level + ", \"" + uuid + "\");");
    }

    public String getName() {
        return name;
    }
}
