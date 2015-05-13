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
    private static MapState instance = null;
    private List<Point> points;
    private List<PlayerCC> playerCCs;
    private List<Faction> factions;

    protected MapState() {
        points = new ArrayList<Point>();
        playerCCs = new ArrayList<PlayerCC>();
        factions = new ArrayList<Faction>();
    }
    public static MapState getInstance() {
        if(instance == null) {
            instance = new MapState();
        }
        return instance;
    }

    public String stringPoints() {
        //Bukkit.getLogger().info();
        if (points == null) return "No point";
        String test = "";
        Point[] arr = points.toArray(new Point[points.size()]);
        for (Point anArr : arr) {
            test = test + "| Point: " + anArr.toString() + " ";
        }
        return test;
    }

    public String stringFactions() {
        //Bukkit.getLogger().info();
        if (points == null) return "No faction";
        String test = "";
        Faction[] arr = factions.toArray(new Faction[factions.size()]);
        for (Faction anArr : arr) {
            test = test + "| Faction: " + anArr.toString() + " ";
        }
        return test;
    }

    public String stringPlayers() {
        //Bukkit.getLogger().info();
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
        playerCCs.add(playerCC);
    }

    public PlayerCC findPlayer(UUID uuid) {
        for (PlayerCC p : playerCCs) {
            if (p.getUUID().equals(uuid)) {
                return p;
            }
        }
        return null;
    }

    public Point findPoint(String name) {
        for (Point p : points) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
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
}
