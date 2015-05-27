package fr.insynia.craftclan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Doc on 12/05/2015.
 */
public class PlayerCC implements Loadable {
    private final int CAPTURE_DISTANCE = 2;
    private final Material ITEM_FOR_ATTACK = Material.DIAMOND;
    private final int NB_ITEMS_FOR_ATTACK = 10;

    private String name;
    private Faction faction;
    private int level;
    private UUID uuid;
    private int timeToCapture = 10;

    public PlayerCC(){}

    public PlayerCC(String name, int factionId, int level, UUID uuid) {
        this.name = name;
        this.faction = MapState.getInstance().findFaction(factionId);
        this.level = level;
        this.uuid = uuid;
    }

    public String toString() { // Debug
        return name + ", " + faction.getId() + ", " + level;
    }

    public void load(ResultSet rs) {
        try {
            if (rs.next()) {
                name = rs.getString("name");
                faction = MapState.getInstance().findFaction(rs.getInt("faction_id"));
                level = rs.getInt("level");
                uuid = UUID.fromString(rs.getString("uuid"));
                MapState.getInstance().addPlayer(this);
                Bukkit.getLogger().info("Fetched player: " + this.name + " from the database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UUID getUUID() {
        return uuid;
    }

    public Faction getFaction() {
        return faction;
    }

    public String getName() {
        return name;
    }

    public boolean addToFaction(String name) {
        SQLManager sqlm = SQLManager.getInstance();
        Faction f = MapState.getInstance().findFaction(name);
        if (f == null) return false;
        this.faction = f;
        boolean ret = sqlm.execUpdate("UPDATE users SET faction_id = " + f.getId() + " WHERE uuid = \"" + uuid + "\";");
        if (ret) loadFaction();
        return ret;
    }

    public static void create(Player player) {
        PlayerCC playerCC = new PlayerCC(player.getName(), 0, 0, player.getUniqueId());
        MapState.getInstance().addPlayer(playerCC);
        playerCC.save();
        Bukkit.getLogger().info("Created player: " + playerCC.name);
    }

    private void save() {
        int faction_id = 0;
        SQLManager sqlm = SQLManager.getInstance();

        if (faction != null)
            faction_id = faction.getId();

        sqlm.execUpdate("INSERT INTO users(name, faction_id, level, uuid) " +
                "VALUES(\"" + name + "\", " + faction_id + ", " + level + ", \"" + uuid + "\");");
    }

    public boolean isAtHome(Location from) {
        if (faction == null) return false;
        List<Point> points = MapState.getInstance().getFactionPoints(faction.getId());
        for (Point p : points) {
            if (UtilCC.distanceBasic(from, p.getLocation()) <= p.getRadius())
                return true;
        }
        return false;
    }

    public boolean isOnPointArea(Location from) {
        if (faction == null ) return false;
        List<Point> points = MapState.getInstance().getPoints();
        for (Point p : points) {
            if (UtilCC.distanceBasic(from, p.getLocation()) <= CAPTURE_DISTANCE)
                return true;
        }
        return false;
    }

    public void loadFaction() {
        if (faction == null) return;
        Player p = Bukkit.getPlayer(uuid);

        p.setDisplayName(ChatColor.WHITE + "[" + ChatColor.valueOf(faction.getColor()) + faction.getName() + ChatColor.WHITE + "] " + p.getName());
        p.setPlayerListName(ChatColor.WHITE + "[" + ChatColor.valueOf(faction.getColor()) + faction.getName() + ChatColor.WHITE + "] " + p.getName());

        checkAttackStatus();
    }

    private void checkAttackStatus() {
        List<Attack> attacks = MapState.getInstance().getAttacks();

        for (Attack a : attacks) {
            if (a.getFactionId() == faction.getId())
                a.addAttacker(this);
        }
    }

    public Point canCapture(Location from) {
        if (faction == null) return null;
        List<Point> points = MapState.getInstance().getPoints();
        for (Point p : points) {
            if (p.getFactionId() != faction.getId() && UtilCC.distanceBasicFull(from, p.getLocation()) <= CAPTURE_DISTANCE)
                return p;
        }
        return null;
    }

    public void startCapture(final Point point, final Player p) {
        timeToCapture = (int)(10 * Math.pow(point.getLevel(), 2));
        final PlayerCC pcc = this;

        if (isOnAttackOn(point) == null) {
            p.sendMessage("Vous ne pouvez pas capturer ce point car vous n'êtes pas en mode attaque sur ce point");
            p.sendMessage("Vous devez avoir 10 diamants et casser un bloc sur ce point pour activer le mode attaque");
            return;
        }
        Timer captureLoop = new Timer(true);
        captureLoop.schedule(new TimerTask() {
            @Override
            public void run() {
                if (timeToCapture == 0) {
                    this.cancel();
                    Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("CraftClanPlugin"), new Runnable() {
                        @Override
                        public void run() {
                            if (point.addToFaction(pcc.getFaction().getId())) {
                                point.setPointLevel(1);
                                pcc.isOnAttackOn(point).endAttack(true);
                                Bukkit.getLogger().info("Ending attack in capture");
                                p.sendMessage("Vous avez capturé le point !");
                            }
                        }
                    });
                } else if (!checkCapture(point, p)) {
                    this.cancel();
                    p.sendMessage("La capture a échoué");
                } else {
                    UtilCC.timeLeftCapture(timeToCapture, p, pcc);
                    timeToCapture -= 1;
                }
            }
        }, 0, 1000);
    }

    public boolean hasEnough(Material type, int count) {
        ItemStack stack = new ItemStack(type);
        Player p = Bukkit.getPlayer(uuid);

        return p.getInventory().containsAtLeast(stack, count);
    }

    public void decreaseItem(Material type, int count) {
        Player p = Bukkit.getPlayer(uuid);
        PlayerInventory inventory = p.getInventory();
        HashMap<Integer, ? extends ItemStack> items = inventory.all(type);
        int nb = 0;

        for (Map.Entry<Integer, ? extends ItemStack> entry : items.entrySet()) {
            Integer key = entry.getKey();
            ItemStack stack = entry.getValue();

            nb += stack.getAmount();
            inventory.remove(stack);
        }
        nb -= count;
        inventory.addItem(new ItemStack(type, nb));
    }

    public Attack isOnAttackOn(Point point) {
        if (point == null) return null;
        List<Attack> attacks = MapState.getInstance().getAttacks();

        for (Attack a : attacks)
            if (a.getFactionId() == faction.getId() && a.getPointName().equals(point.getName()) && !a.playerFailed(this))
                return a;
        return null;
    }

    public boolean willAttack(Block block) {
        Point point = MapUtils.getLocationPoint(block.getLocation());
        if (point == null)
            return false;
        if (faction == null || faction.getId() == point.getFactionId())
            return false;
        if (hasEnough(ITEM_FOR_ATTACK, NB_ITEMS_FOR_ATTACK)) {
            decreaseItem(ITEM_FOR_ATTACK, NB_ITEMS_FOR_ATTACK);
            new Attack(faction.getId(), point.getFactionId(), point.getName());
            return true;
        } else {
            Bukkit.getPlayer(uuid).sendMessage("Vous n'avez pas assez de diamant pour attaquer ce point");
        }
        return false;
    }

    public boolean checkCapture(Point point, Player p) {
        return point.getFactionId() != faction.getId() &&
                (UtilCC.distanceBasicFull(point.getLocation(), p.getLocation())) <= CAPTURE_DISTANCE &&
                !p.isDead();
    }

    public void failAttacks() {
        List<Attack> attacks = MapState.getInstance().getAttacks();

        for (Attack a : attacks)
            if (a.getFactionId() == faction.getId())
                a.addFailer(this);
        MapState.getInstance().purgeAttacks();
    }
}
