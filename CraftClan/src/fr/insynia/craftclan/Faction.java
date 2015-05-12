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
        return "name: " + name + " color: " + color + " z: " + level;
    }

    public void save() {
        SQLManager sqlm = new SQLManager();
        sqlm.execUpdate("INSERT INTO factions(name, color, level) " +
                "VALUES(\"" + name + "\", " + color + ", " + level + ");");
    }

    public static Faction fromSQL() {
        return null;
    }
}
