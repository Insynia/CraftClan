package fr.insynia.craftclan;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by Doc on 11/05/2015.
 */
public class MapState {
    public static final int SPAWN_RADIUS = 61;
    public static String DEFAULT_WORLD = "world";
    public static String FARM_WORLD = "world_farm";
    private static MapState instance = null;
    private List<Point> points;
    private List<PlayerCC> playerCCs;
    private List<Faction> factions;
    private List<Attack> attacks;

    protected MapState() {
        points = new ArrayList<Point>();
        playerCCs = new ArrayList<PlayerCC>();
        factions = new ArrayList<Faction>();
        attacks = new ArrayList<Attack>();
    }
    public static MapState getInstance() {
        if (instance == null) {
            instance = new MapState();
        }
        return instance;
    }

    public String stringPoints() {
        if (points == null) return "No point";
        String test = "";
        Point[] arr = points.toArray(new Point[points.size()]);
        for (Point anArr : arr) {
            test = test + "| Point: " + anArr.toString() + " ";
        }
        return test;
    }

    public String stringAttacks() {
        if (attacks == null) return "No attack";
        String test = "";
        Attack[] arr = attacks.toArray(new Attack[attacks.size()]);
        for (Attack anArr : arr) {
            test = test + "| Attack: " + anArr.toString() + " ";
        }
        return test;
    }

    public String stringFactions() {
        if (points == null) return "No faction";
        String test = "";
        Faction[] arr = factions.toArray(new Faction[factions.size()]);
        for (Faction anArr : arr) {
            test = test + "| Faction: " + anArr.toString() + " ";
        }
        return test;
    }

    public String stringPlayers() {
        if (points == null) return "No player";
        String test = "";
        PlayerCC[] arr = playerCCs.toArray(new PlayerCC[playerCCs.size()]);
        for (PlayerCC anArr : arr) {
            test = test + "| PlayerCC: " + anArr.toString() + " ";
        }
        return test;
    }

    public List<Point> getPoints() {
        return points;
    }

    public List<Faction> getFactions() {
        return factions;
    }

    public List<PlayerCC> getPlayerCCs() {
        return playerCCs;
    }

    public void setPoints(List<Point> ps) {
        points = ps;
    }

    public void setFactions(List<Faction> fs) {
        factions = fs;
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public void addPlayer(PlayerCC playerCC) {
        if (findPlayer(playerCC.getUUID()) == null)
            playerCCs.add(playerCC);
    }

    public PlayerCC findPlayer(UUID uuid) {
        for (PlayerCC p : playerCCs) if (p.getUUID().equals(uuid)) return p;
        return null;
    }

    public PlayerCC findPlayer(String name) {
        for (PlayerCC p : playerCCs) if (p.getName().equals(name)) return p;
        return null;
    }

    public Point findPoint(String name) {
        for (Point p : points) if (p.getName().equals(name)) return p;
        return null;
    }

    public Faction findFaction(int id) {
        for (Faction f : factions) if (f.getId() == id) return f;
        return null;
    }

    public Faction findFaction(String name) {
        for (Faction f : factions) if (f.getName().equals(name)) return f;
        return null;
    }

    public void removePlayer(PlayerCC playerCC) {
        String msg = "Players left: ";
        Iterator<PlayerCC> itr = playerCCs.iterator();
        while (itr.hasNext()) {
            PlayerCC player = itr.next();
            if (player.getUUID().equals(playerCC.getUUID())) {
                itr.remove();
            }
        }
        for (PlayerCC p : playerCCs) {
            if (p.getUUID().equals(playerCC.getUUID())) {
                msg = msg + p.getName() + " ";
            }
        }
        Bukkit.getLogger().info(msg);
    }

    public void removePoint(String name) {
        Iterator<Point> itr = points.iterator();
        while (itr.hasNext()) {
            Point point = itr.next();
            if (point.getName().equals(name)) {
                itr.remove();
            }
        }
    }

    public void addFaction(Faction faction) {
        factions.add(faction);
    }

    public void removeFaction(String name) {
        Iterator<Faction> itr = factions.iterator();
        while (itr.hasNext()) {
            Faction faction = itr.next();
            if (faction.getName().equals(name)) {
                itr.remove();
            }
        }
    }

    public List<Point> getFactionPoints(int factionId) {
        List<Point> pointlist = new ArrayList<Point>();
        for (Point point : points) {
            if (point.getFactionId() == factionId)
                pointlist.add(point);
        }
        return pointlist;
    }

    public void addAttack(Attack attack) {
        attacks.add(attack);
        Bukkit.getLogger().info("---------- Attacks -----------");
        Bukkit.getLogger().info(stringAttacks());
        Bukkit.getLogger().info("---------- ------- -----------");
    }

    public List<Attack> getAttacks() {
        return attacks;
    }

    public void purgeAttacks() {
        Iterator<Attack> itr = attacks.iterator();
        while (itr.hasNext()) {
            Attack a = itr.next();
            if (a.getAttackers().size() == 0 || a.isWon())
                itr.remove();
        }
    }

}
