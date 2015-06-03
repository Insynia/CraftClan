package fr.insynia.craftclan;

public class InitPlugin {
    public void init() {
        createTables();
        fetchItems();
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
                " name VARCHAR(255) NOT NULL UNIQUE," +
                " color VARCHAR(255) NOT NULL," +
                " level INT(12) NOT NULL," +
                " PRIMARY KEY (id)," +
                " INDEX (name));");
        sqlm.execUpdate("INSERT INTO factions(name, color, level) VALUES(\"Newbie\", \"GRAY\", 1)");
        sqlm.execUpdate("CREATE TABLE IF NOT EXISTS users (" +
                " id int NOT NULL AUTO_INCREMENT," +
                " name VARCHAR(255) NOT NULL," +
                " faction_id INT(12) NOT NULL," +
                " level INT(12) NOT NULL," +
                " uuid VARCHAR(255) NOT NULL UNIQUE," +
                " PRIMARY KEY (id)," +
                " INDEX (name));");
        sqlm.execUpdate("CREATE TABLE IF NOT EXISTS attacks (" +
                " id int NOT NULL AUTO_INCREMENT," +
                " faction_id INT(12) NOT NULL," +
                " target_id INT(12) NOT NULL," +
                " active tinyint NOT NULL," +
                " point_name VARCHAR(255) NOT NULL," +
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
    }

    private void fetchItems() {
        SQLManager sqlm = SQLManager.getInstance();
        String query = "SELECT * FROM points;";
        sqlm.fetchQuery(query, new PointList());
        query = "SELECT * FROM factions";
        sqlm.fetchQuery(query, new FactionList());
    }
}
