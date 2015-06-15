package fr.insynia.craftclan.Gameplay;

import fr.insynia.craftclan.Base.MaperCC;
import fr.insynia.craftclan.Base.SQLManager;
import fr.insynia.craftclan.Interfaces.IDable;
import fr.insynia.craftclan.Utils.FileManagerCC;
import fr.insynia.craftclan.Utils.MapUtils;
import fr.insynia.craftclan.Utils.UtilCC;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Date;
import java.util.List;

/**
 * Created by Doc on 11/05/2015.
 * Modified by Sharowin on 18/05/2015.
 */
public class Point implements IDable {

    private static final String DEFAULT_POINT_STRUCTURE_FOLDER = "structures";
    private static final String DEFAULT_POINT_STRUCTURE = "pointLevel_";
    public static final int DEFAULT_AREA = 3; // Area radius

    public static final int POINT_MAX_LEVEL = 10;

    private Location loc;
    private String name;
    private int radius;
    private int factionId;
    private int level;
    private int id;


    public Point(String name, int radius, Location loc, int level, int factionId) {
        this.name = name;
        this.loc = loc;
        this.radius = radius;
        this.level = level;
        this.factionId = factionId;
        this.id = 0;
    }

    public Point(int id, String name, int radius, Location loc, int level, int factionId) {
        this.name = name;
        this.loc = loc;
        this.radius = radius;
        this.level = level;
        this.factionId = factionId;
        this.id = id;
    }

    public String toString() {
        return "x: " + loc.getX() + " y: " + loc.getY() + " z: " + loc.getZ();
    }

    public boolean save() {
        if (MapUtils.getLocationPoint(loc) != null) {
            Bukkit.getLogger().warning("----- Cannot create an overlapping point !!! -----");
            return false;
        }
        SQLManager sqlm = SQLManager.getInstance();
        loc.setX(UtilCC.getInt(loc.getX()));
        loc.setY((int) loc.getY());
        loc.setZ(UtilCC.getInt(loc.getZ()));
        boolean ret = sqlm.execUpdate("INSERT INTO points(name, radius, x, y, z, faction_id, level) " +
                "VALUES(\"" + name + "\", " + radius + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ", " + factionId + ", " + level + ");", this);
        if (ret){this.addToMap();}
        return ret;
    }

    private void addToMap() {
        MapState.getInstance().addPoint(this);
        BlockSpawner.emptySky(loc);
        BlockSpawner.floorPointIsland(this.loc);
        BlockSpawner.createBeacon(loc);
        // UtilCC.debugPoint(loc, radius); // Debug function
        spawnPointStructure(level);
    }

    public boolean addToFaction(int factionId) {
        this.factionId = factionId;
        changePointFaction(this.factionId);
        return update();
    }

    public boolean addToFaction(String name) {
        List<Faction> factions = MapState.getInstance().getFactions();

        for (Faction f : factions) {
            if (f.getName().equals(name)) {
                this.factionId = f.getId();
                changePointFaction(this.factionId);
                return update();
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

    private void spawnPointStructure(int level) {
        Location newloc = this.loc.clone();
        newloc.setX(newloc.getX() - DEFAULT_AREA);
        newloc.setZ(newloc.getZ() - DEFAULT_AREA);
        newloc.setY(newloc.getY() - 1);
        if (FileManagerCC.checkFileAndFolderExist(DEFAULT_POINT_STRUCTURE_FOLDER, DEFAULT_POINT_STRUCTURE + level)) {
            BlockSpawner.spawnStructure(DEFAULT_POINT_STRUCTURE + level, newloc);
            setPointBeam();
        }
    }

    // Upgrade point level. ie: +1 Level
    public void upgradePoint() {
        level = level + 1;
        setPointLevel(level);
    }

    // Set a new level to a Point and build the proper structure
    public boolean setPointLevel(int newLevel) {
        this.level = newLevel;
        boolean ret = update();
        if (ret) spawnPointStructure(level);
        return ret;
    }

    // Modify glass block color to match with point faction
    private void setPointBeam() {
        int faction_id = this.factionId;
        byte gMeta = 0;
        String blockType;

        Faction faction = MapState.getInstance().findFaction(faction_id);
        if (faction == null) gMeta = -1;
        else gMeta = (byte)(UtilCC.getGlassMetadataColor(faction.getColor()));
        if (gMeta == -1) {
            blockType = "GLASS";
            gMeta = 0;
        } else blockType = "STAINED_GLASS";
        Location newLoc = this.loc.clone();
        newLoc.setY(newLoc.getY() - 1);
        BlockSpawner.spawnBlock(newLoc, blockType, gMeta);
    }

    // Changing the faction of a point.
    public void changePointFaction(int faction_id) {
        this.factionId = faction_id;
        setPointBeam();
    }

    // Update SQL with the point name

    private boolean update() {
        SQLManager sqlm = SQLManager.getInstance();
        MaperCC.updatePointArea(this);
        return (sqlm.execUpdate("UPDATE points SET name = \"" + name +
                "\", radius = \"" + radius +
                "\", x = \"" + loc.getX() +
                "\", y = \"" + loc.getY() +
                "\", z = \"" + loc.getZ() +
                "\", level = \"" + level +
                "\", faction_id = \"" + factionId +
                "\" WHERE id = \"" + this.id + "\";"));
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Protection getProtection() {
        Protection protection = MapState.getInstance().findProtectionForPoint(this.id);
        if (protection != null) {
            Date now = new Date();
            if (protection.getEnd().getTime() < now.getTime()) {
                MapState.getInstance().removeProtection(protection.getId());
                protection = null;
            }
        }

        return protection;
    }

    public boolean isAttacked() {
        if (MapState.getInstance().findAttackByPointId(id) != null)
            return true;
        return false;
    }
}