package fr.insynia.craftclan;

/**
 * Created by Doc on 11/05/2015.
 */
public class Faction {
    private String name;
    private String color;
    private int level;

    public Faction(String name, String color, int level) {
        this.name = name;
        this.color = color;
        this.level = level;
    }

    public String toString() {
        return "name: " + name + " color: " + color + " level: " + level;
    }

    public boolean save() {
        SQLManager sqlm = new SQLManager();
        boolean ret = sqlm.execUpdate("INSERT INTO factions(name, color, level) " +
                "VALUES(\"" + name + "\", \"" + color + "\", " + level + ");");
        if (ret)
            this.addToMap();
        return ret;
    }

    private void addToMap() {
        MapState.getInstance().addFaction(this);
    }

    public static Faction fromSQL() {
        return null;
    }

    public String getName() {
        return name;
    }
}
