package fr.insynia.craftclan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by Doc on 11/05/2015.
 */
public class MapState {
    public static final int SPAWN_RADIUS = 196;
    private static final int BASE_PAYDAY = 5;
    public static String DEFAULT_WORLD = "world";
    public static String FARM_WORLD = "world_farm";
    private static MapState instance = null;
    private List<Point> points;
    private List<PlayerCC> playerCCs;
    private List<Request> requests;
    private List<Protection> protections;
    private List<Faction> factions;
    private List<Attack> attacks;

    protected MapState() {
        points = new ArrayList<Point>();
        protections = new ArrayList<>();
        requests = new ArrayList<Request>();
        playerCCs = new ArrayList<PlayerCC>();
        factions = new ArrayList<Faction>();
        attacks = new ArrayList<Attack>();
        launchReminders(Bukkit.getPluginManager().getPlugin("CraftClan"));
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

    public Point findPoint(int id) {
        for (Point p : points) if (p.getId() == id) return p;
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

    public void removePoint(int id) {
        Iterator<Point> itr = points.iterator();
        while (itr.hasNext()) {
            Point point = itr.next();
            if (point.getId() == id) {
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
            if (a.getAttackers().size() == 0 || a.isWon()) {
                Bukkit.getLogger().info("Attack purged");
                itr.remove();
            }
        }
    }

    public void removeRequest(int id) {
        Iterator<Request> itr = requests.iterator();
        while (itr.hasNext()) {
            Request request = itr.next();
            if (request.getId() == id) {
                request.deleteDb();
                itr.remove();
            }
        }
    }

    public void addRequest(Request request) {
        requests.add(request);
    }

    private void reminders() {
        Faction faction;
        PlayerCC pcc;
        PlayerCC leader;
        for (Request r : requests) {
            faction = findFaction(r.getFactionId());
            pcc = findPlayer(r.getPlayerName());
            leader = findPlayer(faction.getLeaderName());
            if (leader != null)
                leader.sendMessage("Le joueur " + pcc.getName() + " veut rejoindre votre faction !\n" +
                        "Tapez /cc accept " + pcc.getName() + " ou /cc refuse " + pcc.getName());
        }
    }

    private void launchReminders(Plugin plugin) {
        Plugin p = Bukkit.getPluginManager().getPlugin("CraftClan");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(p,
                new Runnable() {
                    @Override
                    public void run() {
                        reminders();
                    }
                }, 60 * 5 * 20, 60 * 5 * 20); // 5 minutes
    }

    public Request findRequestByPlayer(String playerName) {
        for (Request r : requests) if (r.getPlayerName().equals(playerName)) return r;
        return null;
    }

    public void addProtection(Protection protection) {
        protections.add(protection);
    }

    public Protection findProtectionForPoint(int pointId) {
        for (Protection p : protections)
            if (p.getPointId() == pointId)
                return (p);
        return null;
    }

    public void removeProtection(int id) {
        Iterator<Request> itr = requests.iterator();
        while (itr.hasNext()) {
            Request request = itr.next();
            if (request.getId() == id) {
                request.deleteDb();
                itr.remove();
            }
        }
    }

    public Attack findAttackByPointId(int pointId) {
        for (Attack a : attacks) {
            if (a.getPoint().getId() == pointId)
                return a;
        }
        return null;
    }

    public void payDay() {
        for (PlayerCC pcc : playerCCs) {
            for (Point p : points) {
                if (p.getFactionId() == pcc.getFaction().getId()) {
                    EconomyCC.give(pcc.getName(), BigDecimal.valueOf(p.getLevel() * BASE_PAYDAY));
                    pcc.sendMessage(ChatColor.GREEN + "+" + p.getLevel() * BASE_PAYDAY + "$");
                }
            }
        }
    }
}
