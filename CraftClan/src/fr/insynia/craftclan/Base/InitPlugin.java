package fr.insynia.craftclan.Base;

import fr.insynia.craftclan.Adapters.FactionList;
import fr.insynia.craftclan.Adapters.FactionRequestList;
import fr.insynia.craftclan.Adapters.PointList;
import fr.insynia.craftclan.Adapters.ProtectionList;
import fr.insynia.craftclan.Gameplay.Faction;
import fr.insynia.craftclan.Gameplay.Generator;
import fr.insynia.craftclan.Gameplay.MapState;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class InitPlugin {
    static Plugin p = null;

    public void init(Plugin plugin) {
        createTables();
        fetchItems();
        Generator.resetFarmingZone();
        prepareFarmTimer(plugin);
        preparePayDay(plugin);
        prepareAreas(plugin);
    }

    private void prepareAreas(Plugin plugin) {
        p = plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(p,
                new Runnable() {
                    @Override
                    public void run() {
                        MaperCC.generateAreas();
                    }
                }, 15 * 20); // 15 seconds

    }

    private void preparePayDay(Plugin plugin) {
        p = plugin;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(p,
                new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage("Jour de paie !");
                        MapState.getInstance().payDay();
                    }
                }, 60 * 60 * 20, 60 * 60 * 20); // 1 hour
    }

    private void prepareFarmTimer(Plugin plugin) {
        p = plugin;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(p,
                new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage("La zone de farm va être réinitialisée dans 5 minutes !!! Hop hop hop, on sort !");

                        Bukkit.getScheduler().scheduleSyncDelayedTask(p,
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        Bukkit.getServer().broadcastMessage("La zone de farm se réinitialise !");
                                        Generator.resetFarmingZone();
                                    }
                                }, 60 * 5 * 20); // 5 minutes
                    }
                }, 60 * 20, 60 * 60 * 20 * 2); // 2 hours
    }

    private void createTables() {
        SQLManager sqlm = SQLManager.getInstance();
        sqlm.execUpdate("CREATE TABLE IF NOT EXISTS points (" +
                " id int NOT NULL AUTO_INCREMENT," +
                " name VARCHAR(255) UNIQUE NOT NULL, radius INT(12) NOT NULL," +
                " x INT(32) NOT NULL, y INT(32) NOT NULL, z INT(32) NOT NULL," +
                " level INT(12) NOT NULL," +
                " faction_id INT(12) NOT NULL," +
                " PRIMARY KEY (id)," +
                " INDEX (name));");
        sqlm.execUpdate("CREATE TABLE IF NOT EXISTS factions (" +
                " id int NOT NULL AUTO_INCREMENT," +
                " status ENUM('OPEN', 'CLOSED', 'RESTRICTED') NOT NULL DEFAULT 'RESTRICTED'," +
                " leader_name VARCHAR(255) NOT NULL," +
                " name VARCHAR(255) NOT NULL UNIQUE," +
                " color VARCHAR(255) NOT NULL," +
                " level INT(12) NOT NULL," +
                " PRIMARY KEY (id)," +
                " INDEX (name));");
        sqlm.execUpdate("INSERT IGNORE INTO factions(name, color, level, status, leader_name) VALUES(\"" + Faction.BASE_FACTION+ "\", \"GRAY\", 1, 'OPEN', \"\")");
        sqlm.execUpdate("INSERT IGNORE INTO factions(name, color, level, status, leader_name) VALUES(\"" + Faction.NEUTRAL_FACTION + "\", \"GRAY\", 1, 'CLOSED', \"\")");
        sqlm.execUpdate("CREATE TABLE IF NOT EXISTS users (" +
                " id int NOT NULL AUTO_INCREMENT," +
                " name VARCHAR(255) NOT NULL," +
                " faction_id INT(12) NOT NULL," +
                " level INT(12) NOT NULL," +
                " uuid VARCHAR(255) NOT NULL UNIQUE," +
                " started_farm_at DATETIME DEFAULT NULL," +
                " PRIMARY KEY (id)," +
                " INDEX (name));");
        sqlm.execUpdate("CREATE TABLE IF NOT EXISTS attacks (" +
                " id int NOT NULL AUTO_INCREMENT," +
                " faction_id INT(12) NOT NULL," +
                " target_id INT(12) NOT NULL," +
                " active tinyint NOT NULL," +
                " point_id INT(12) NOT NULL," +
                " win tinyint NOT NULL," +
                " start_time DATETIME NOT NULL," +
                " end_time DATETIME DEFAULT NULL," +
                " PRIMARY KEY (id));");
        sqlm.execUpdate("CREATE TABLE IF NOT EXISTS attack_logs (" +
                " id int NOT NULL AUTO_INCREMENT," +
                " attack_id INT(12) NOT NULL," +
                " x INT(32) NOT NULL, y INT(32) NOT NULL, z INT(32) NOT NULL," +
                " block VARCHAR(255) NOT NULL," +
                " action ENUM('PLACE', 'BREAK') NOT NULL," +
                " meta tinyint NOT NULL," +
                " PRIMARY KEY (id));");
        sqlm.execUpdate("CREATE TABLE IF NOT EXISTS faction_requests (" +
                " id int NOT NULL AUTO_INCREMENT," +
                " faction_id INT(12) NOT NULL," +
                " user_name VARCHAR(255) NOT NULL," +
                " PRIMARY KEY (id));");
        sqlm.execUpdate("CREATE TABLE IF NOT EXISTS protections (" +
                " id int NOT NULL AUTO_INCREMENT," +
                " point_id INT(12) NOT NULL," +
                " begin DATETIME NOT NULL," +
                " end DATETIME NOT NULL," +
                " user_name VARCHAR(255) NOT NULL," +
                " PRIMARY KEY (id));");
    }

    private void fetchItems() {
        SQLManager sqlm = SQLManager.getInstance();
        String query = "SELECT * FROM points;";
        sqlm.fetchQuery(query, new PointList());
        query = "SELECT * FROM factions";
        sqlm.fetchQuery(query, new FactionList());
        query = "SELECT * FROM protections";
        sqlm.fetchQuery(query, new ProtectionList());
        query = "SELECT * FROM faction_requests";
        sqlm.fetchQuery(query, new FactionRequestList());
    }
}
