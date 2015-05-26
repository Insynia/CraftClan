package fr.insynia.craftclan;

import org.bukkit.Location;

import java.util.List;

/**
 * Created by Doc on 11/05/2015.
 * Modified by Sharowin on 18/05/2015.
 */
public class Point {

    private static final String DEFAULT_POINT_STRUCTURE_FOLDER = "structures";
    private static final String DEFAULT_POINT_STRUCTURE = "pointLevel_";
    private static final int DEFAULT_POINT_AREA = 5;

    private Location loc;
    private String name;
    private int radius;
    private int factionId;
    private int level;

    public Point(String name, int radius, Location loc, int level, int factionId) {
        this.name = name;
        this.loc = loc;
        this.radius = radius;
        this.level = level;
        this.factionId = factionId;
    }

    public String toString() {
        return "x: " + loc.getX() + " y: " + loc.getY() + " z: " + loc.getZ();
    }

    public boolean save() {
        SQLManager sqlm = SQLManager.getInstance();
        loc.setX((int) loc.getX());
        loc.setY((int) loc.getY());
        loc.setZ((int) loc.getZ());
        boolean ret = sqlm.execUpdate("INSERT INTO points(name, radius, x, y, z, faction_id, level) " +
                "VALUES(\"" + name + "\", " + radius + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ", " + factionId + ", " + level + ");");
        if (ret){this.addToMap();}
        return ret;
    }

    private void addToMap() {
        MapState.getInstance().addPoint(this);
        BlockSpawner.emptySky(loc);
        BlockSpawner.createBeacon(loc);
        spawnPointStructure(level);
    }

    public boolean addToFaction(int factionId) {
        SQLManager sqlm = SQLManager.getInstance();
        this.factionId = factionId;
        return (sqlm.execUpdate("UPDATE points SET faction_id = " + factionId + " WHERE name = \"" + this.name + "\";"));
    }

    public boolean addToFaction(String name) {

        SQLManager sqlm = SQLManager.getInstance();
        List<Faction> factions = MapState.getInstance().getFactions();
        for (Faction f : factions) {
            if (f.getName().equals(name)) {
                this.factionId = f.getId();
                return (sqlm.execUpdate("UPDATE points SET faction_id = " + factionId + " WHERE name = \"" + this.name + "\";"));
            }
        }
        return false;
    }

    public static Point fromSQL() {
        return null;
    }

    public String getName() {
        return name;
    }

    public int getFactionId() {
        return factionId;
    }

    public Location getLocation() {
        return loc;
    }

    public int getRadius() {
        return radius;
    }

    public int getLevel() {
        return level;
    }

    private void spawnPointStructure(int level){
        Location newloc = this.loc.clone();
        newloc.setX(newloc.getX() - UtilCC.halfRound(DEFAULT_POINT_AREA));
        newloc.setZ(newloc.getZ() - UtilCC.halfRound(DEFAULT_POINT_AREA));
        newloc.setY(newloc.getY() - 1);
        if (FileManager.checkFileAndFolderExist(DEFAULT_POINT_STRUCTURE_FOLDER, DEFAULT_POINT_STRUCTURE + level)) {
            BlockSpawner.emptySky(this.loc);
            BlockSpawner.spawnStructure(DEFAULT_POINT_STRUCTURE + level, newloc);
        }
    }

    // Upgrade point level. ie: +1 Level
    private void upgradePoint() {
        level = level + 1;
        setPointLevel(level);
        updatePointLevel(level);
    }

    // Set a new level to a Point and build the proper structure
    public boolean setPointLevel(int newLevel) {
        this.level = newLevel;
        boolean ret = updatePointLevel(level);
        if (ret) spawnPointStructure(level);
        return ret;
    }

    // Update SQL datas
    public boolean updatePointLevel(int newLevel) {
        SQLManager sqlm = SQLManager.getInstance();
        return (sqlm.execUpdate("UPDATE points SET level = " + newLevel + " WHERE name = \"" + this.name + "\";"));
    }

    public void materialize(){
        // Récupération du niveau du point.
        // Envoi selon le niveau de la table, du path pour le bâtiment.`
        // Va appeler les fonctions existantes avec les bons parametres pour spawn les batiments des differents points selon leur niveau

    }
}
