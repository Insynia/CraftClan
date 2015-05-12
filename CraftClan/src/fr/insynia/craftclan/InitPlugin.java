package fr.insynia.craftclan;

public class InitPlugin {
    public void init() {
        createTables();
        fetchItems();
    }

    private void createTables() {
        SQLManager sqlm = new SQLManager();
        sqlm.execUpdate("CREATE TABLE IF NOT EXISTS points (" +
                " id int NOT NULL AUTO_INCREMENT," +
                " name VARCHAR(255), radius INT(12)," +
                " x INT(32), y INT(32), z INT(32)," +
                " level INT(12)," +
                " owner INT(12)," +
                " PRIMARY KEY (id));");
        sqlm.execUpdate("CREATE TABLE IF NOT EXISTS factions (" +
                " id int NOT NULL AUTO_INCREMENT," +
                " name VARCHAR(255)," +
                " color VARCHAR(255)," +
                " level INT(12)," +
                " PRIMARY KEY (id));");
        sqlm.execUpdate("CREATE TABLE IF NOT EXISTS users (" +
                " id int NOT NULL AUTO_INCREMENT," +
                " name VARCHAR(255)," +
                " faction_id INT(12)," +
                " level INT(12)," +
                " uuid VARCHAR(255)," +
                " PRIMARY KEY (id));");
    }
    private void fetchItems() {
        SQLManager sqlm = new SQLManager();
        String query = "SELECT * FROM points;";
        sqlm.fetchQuery(query, new PointList());
        query = "SELECT * FROM factions";
        sqlm.fetchQuery(query, new FactionList());
    }
}
