package fr.insynia.craftclan;

import org.bukkit.ChatColor;

/**
 * For CraftClan
 * Created by Doc on 11/05/2015 at 19:54.
 */
public class Faction implements IDable {
    private String name;
    private String color;
    private int factionId;
    private int level;

    public Faction(int factionId, String name, String color, int level) {
        this.factionId = factionId;
        this.name = name;
        this.color = color;
        this.level = level;
    }

    public String toString() {
        return "name: " + name + " color: " + color + " level: " + level;
    }

    public boolean save() {
        boolean colorOk = false;
        SQLManager sqlm = SQLManager.getInstance();
        for (ChatColor chatColor : ChatColor.values()) {
            if (chatColor.name().equals(color)) {
                colorOk = true;
                break;
            }
        }
        if (!colorOk) return false;
        boolean ret = sqlm.execUpdate("INSERT INTO factions(name, color, level) " +
                "VALUES(\"" + name + "\", \"" + color + "\", " + level + ");", this);
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

    public int getId() {
        return factionId;
    }

    public String getColor() {
        return color;
    }

    @Override
    public void setId(int id) {
        this.factionId = id;
    }

    public String getFancyName() {
        return ChatColor.WHITE + "[" + ChatColor.valueOf(getColor()) + getName() + ChatColor.WHITE + "]";
    }
}
