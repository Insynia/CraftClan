package fr.insynia.craftclan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by Doc on 12/05/2015.
 */
public class PlayerCC implements Loadable {
    private static final int CAPTURE_DISTANCE = 2;
    private String name;
    private Faction faction;
    private int level;
    private UUID uuid;
    private int timeToCapture = 10;

    public PlayerCC(){}

    public PlayerCC(String name, int factionId, int level, UUID uuid) {
        this.name = name;
        this.faction = MapState.getInstance().findFaction(factionId);
        this.level = level;
        this.uuid = uuid;
    }

    public String toString() { // Debug
        return name + ", " + faction.getId() + ", " + level;
    }

    public void load(ResultSet rs) {
        try {
            if (rs.next()) {
                name = rs.getString("name");
                faction = MapState.getInstance().findFaction(rs.getInt("faction_id"));
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

    public boolean addToFaction(String name) {
        SQLManager sqlm = SQLManager.getInstance();
        Faction f = MapState.getInstance().findFaction(name);
        if (f == null) return false;
        this.faction = f;
        boolean ret = sqlm.execUpdate("UPDATE users SET faction_id = " + f.getId() + " WHERE uuid = \"" + uuid + "\";");
        if (ret) loadFaction();
        return ret;
    }

    public static void create(Player player) {
        PlayerCC playerCC = new PlayerCC(player.getName(), 0, 0, player.getUniqueId());
        MapState.getInstance().addPlayer(playerCC);
        playerCC.save();
        Bukkit.getLogger().info("Created player: " + playerCC.name);
    }

    private void save() {
        int faction_id = 0;
        SQLManager sqlm = SQLManager.getInstance();

        if (faction != null)
            faction_id = faction.getId();

        sqlm.execUpdate("INSERT INTO users(name, faction_id, level, uuid) " +
                "VALUES(\"" + name + "\", " + faction_id + ", " + level + ", \"" + uuid + "\");");
    }

    public String getName() {
        return name;
    }

    public boolean isAtHome(Location from) {
        if (faction == null) return false;
        List<Point> points = MapState.getInstance().getFactionPoints(faction.getId());
        for (Point p : points) {
            if (UtilCC.distanceBasic(from, p.getLocation()) <= p.getRadius())
                return true;
        }
        return false;
    }

    public void loadFaction() {
        if (faction == null) return;
        Player p = Bukkit.getPlayer(uuid);

        p.setDisplayName(ChatColor.WHITE + "[" + ChatColor.valueOf(faction.getColor()) + faction.getName() + ChatColor.WHITE + "] " + p.getName());
        p.setPlayerListName(ChatColor.WHITE + "[" + ChatColor.valueOf(faction.getColor()) + faction.getName() + ChatColor.WHITE + "] " + p.getName());
    }

    public Point canCapture(Location from) {
        if (faction == null) return null;
        List<Point> points = MapState.getInstance().getPoints();
        for (Point p : points) {
            if (p.getFactionId() != faction.getId() && UtilCC.distanceBasic(from, p.getLocation()) <= CAPTURE_DISTANCE)
                return p;
        }
        return null;
    }

    public void startCapture(Point point, final Player p) {
        timeToCapture = 10; // TODO
        Timer captureLoop = new Timer(true);
        captureLoop.schedule(new TimerTask() {
            @Override
            public void run() {
                if (timeToCapture == 0) this.cancel(); // TODO
                p.sendMessage("Il vous reste " + timeToCapture + " secondes pour capturer le point !");
                timeToCapture -= 1;
            }
        }, 0, 1000);
    }
}
