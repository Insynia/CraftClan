package fr.insynia.craftclan.Gameplay;

import fr.insynia.craftclan.Base.SQLManager;
import fr.insynia.craftclan.Commands.MenuCC;
import fr.insynia.craftclan.Interfaces.Loadable;
import fr.insynia.craftclan.Utils.EconomyCC;
import fr.insynia.craftclan.Utils.UtilCC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Doc on 12/05/2015.
 */
public class PlayerCC implements Loadable {
    private final Material ITEM_FOR_ATTACK = Material.DIAMOND;
    private final int NB_ITEMS_FOR_ATTACK = 5;

    public static final int BASE_MONEY_UPGRADE = 1000;

    private String name;
    private Faction faction;
    private int level;
    private UUID uuid;
    private int timeToCapture = 10;
    private boolean talkingToFaction = false;
    private MenuCC menu;

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
        failAttacks();
        return ret;
    }

    public static void create(Player player) {
        PlayerCC playerCC = new PlayerCC(player.getName(), MapState.getInstance().findFaction(Faction.BASE_FACTION).getId(), 0, player.getUniqueId());
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
            if (UtilCC.distanceBasic(from, p.getLocation()) <= Point.DEFAULT_AREA)
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
            if (p.getFactionId() != faction.getId() &&
                    UtilCC.distanceBasicFull(from, p.getLocation()) <= Point.DEFAULT_AREA)
                return p;
        }
        return null;
    }

    public void startCapture(final Point point, final Player p) {
        timeToCapture = point.getCaptureTime();
        final PlayerCC pcc = this;

        if (isOnAttackOn(point) == null) {
            p.sendMessage("Vous ne pouvez pas capturer ce point car vous n'êtes pas en mode attaque sur ce point");
            p.sendMessage("Vous devez avoir 10 diamants et casser un bloc sur ce point pour activer le mode attaque");
            return;
        }

        if (point.getProtection() != null) {
            Bukkit.getLogger().warning("SHOULD NOT BE HERE !!! On startCapture, checking the protection after" +
                    " a player is on attack on the point (why is he on attack mode ?)");
            p.sendMessage("Ce point bénéficie d'une protection");
            return;
        }

        Timer captureLoop = new Timer(true);
        //PacketUtils.displayLoadingBar("Capture en cours...", p, timeToCapture, true);
        captureLoop.schedule(new TimerTask() {
            @Override
            public void run() {
                if (timeToCapture == 0) {
                    this.cancel();
                    Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("CraftClan"), new Runnable() {
                        @Override
                        public void run() {
                            if (point.addToFaction(pcc.getFaction().getId())) {
                                point.setPointLevel(1);
                                pcc.isOnAttackOn(point).endAttack(true);
                                MapState.getInstance().purgeAttacks();
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
        if (nb > 0)
            inventory.addItem(new ItemStack(type, nb));
    }

    // Returns the attack of this player for this point if it exists and the player has not failed
    public Attack isOnAttackOn(Point point) {
        if (point == null) return null;
        List<Attack> attacks = MapState.getInstance().getAttacks();

        for (Attack a : attacks)
            if (a.getFactionId() == faction.getId() && a.getPoint().getName().equals(point.getName()) && !a.playerFailed(this))
                return a;
        return null;
    }

    public boolean willAttack(Block block) {
        Player p = Bukkit.getPlayer(uuid);

        if (!p.getWorld().getName().equals(MapState.DEFAULT_WORLD)) return false;
        if (UtilCC.blockAttackBlacklist(block)) return false;
        Point point = MapState.getInstance().findPoint(block.getLocation());
        if (point == null)
            return false;
        if (point.isAttacked()) {
            sendMessage("Ce point subit déjà une attaque");
            return false;
        }
        if (point.getFactionId() == -1 || MapState.getInstance().findFaction(point.getFactionId()) == null)
            return false;
        if (faction == null || faction.getId() == point.getFactionId() || faction.getName().equals(Faction.BASE_FACTION))
            return false;
        Protection protection = point.getProtection();
        if (protection != null) {
            sendMessage("Vous ne pouvez pas attaquer ce point\n" +
                    "Il est protégé jusqu'au: " + UtilCC.dateHumanReadable(protection.getEnd()));
            return false;
        }
        if (hasEnough(ITEM_FOR_ATTACK, NB_ITEMS_FOR_ATTACK * point.getLevel())) {
            decreaseItem(ITEM_FOR_ATTACK, NB_ITEMS_FOR_ATTACK * point.getLevel());
            new Attack(faction.getId(), point.getFactionId(), point.getId());
            return true;
        } else {
            sendMessage("Vous n'avez pas assez de diamants pour attaquer ce point");
        }
        return false;
    }

    public boolean checkCapture(Point point, Player p) {
        return point.getFactionId() != faction.getId() &&
                (UtilCC.distanceBasicFull(point.getLocation(), p.getLocation())) <= Point.DEFAULT_AREA &&
                !p.isDead();
    }

    public void failAttacks() {
        List<Attack> attacks = MapState.getInstance().getAttacks();

        for (Attack a : attacks)
            if (a.getFactionId() == faction.getId())
                a.addFailerWithoutPurge(this);
        MapState.getInstance().purgeAttacks();
    }


    // Check if the player can upgrade a point.
    public Point canUpgrade(Location from) {
        if (faction == null) return null;
        List<Point> points = MapState.getInstance().getPoints();
        for (Point p : points) {
            if (p.getFactionId() == faction.getId() &&
                    (UtilCC.distanceBasicFull(from, p.getLocation()) <= Point.DEFAULT_AREA) &&
                    (p.getLevel() < Point.POINT_MAX_LEVEL)) {
                BigDecimal neededMoney = BigDecimal.valueOf(BASE_MONEY_UPGRADE * p.getLevel());

                if (EconomyCC.has(name, neededMoney)) return p;
                else Bukkit.getPlayer(uuid).sendMessage("Vous devez avoir " + BASE_MONEY_UPGRADE * p.getLevel() + "$ pour améliorer ce point !");
            }
        }
        return null;
    }

    public void willUpgrade(Point point) {
        BigDecimal neededMoney = BigDecimal.valueOf(BASE_MONEY_UPGRADE * point.getLevel());

        if (EconomyCC.has(name, neededMoney))
            EconomyCC.take(name, neededMoney);
    }

    public boolean isOnWorld(String world) {
        return Bukkit.getPlayer(uuid).getLocation().getWorld().getName().equals(world);
    }

    public boolean isOnline() {
        if (Bukkit.getPlayer(uuid) != null)
            return true;
        return false;
    }

    public void sendMessage(String msg) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null)
            p.sendMessage(msg);
    }

    public Point canProtect(Location from) {
        if (faction == null) return null;
        List<Point> points = MapState.getInstance().getPoints();
        for (Point p : points) {
            if (p.getFactionId() == faction.getId() && (UtilCC.distanceBasicFull(from, p.getLocation()) <= Point.DEFAULT_AREA))
                return p;
        }
        return null;
    }

    public boolean isOnPoint(Point point) {
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) return false;
        return (p.getLocation().getZ() <= point.getLocation().getZ() + point.getRadius() &&
                p.getLocation().getZ() >= point.getLocation().getZ() - point.getRadius() &&
                p.getLocation().getX() <= point.getLocation().getX() + point.getRadius() &&
                p.getLocation().getX() >= point.getLocation().getX() - point.getRadius());
    }

    public void goToNearestHome() {
        int distance = -1;
        int curDistance;
        Location target = Bukkit.getWorld(MapState.DEFAULT_WORLD).getSpawnLocation();

        Player p = Bukkit.getPlayer(uuid);
        if (p == null) return;

        for (Point point : MapState.getInstance().getPoints()) {
            if (point.getFactionId() == faction.getId()) {
                curDistance = UtilCC.distanceBasicFull(point.getLocation(), p.getLocation());
                if (distance == -1 || curDistance < distance) {
                    distance = curDistance;
                    target = point.getLocation().clone();
                }
            }
        }
        p.teleport(target);
        p.sendMessage(ChatColor.GREEN + "Vous avez été téléporté au point sûr le plus proche" + ChatColor.RESET);
    }

    public boolean isLeader() {
        return name.equals(faction.getLeaderName());
    }

    public Attack hasFailedOn(Point point) {
        if (point == null) return null;
        List<Attack> attacks = MapState.getInstance().getAttacks();

        for (Attack a : attacks)
            if (a.getFactionId() == faction.getId() && a.getPoint().getName().equals(point.getName()) && a.playerFailed(this))
                return a;
        return null;
    }

    public boolean isTalkingToFaction() {
        return talkingToFaction;
    }

    public void setTalkingToFaction(boolean talkingToFaction) {
        if (talkingToFaction == true)
            sendMessage("Vous parlez maintenant uniquement aux membres de votre faction");
        else
            sendMessage("Vous parlez maintenant à tous les joueurs du serveur");

        this.talkingToFaction = talkingToFaction;
    }

    public MenuCC getMenu() {
        if (menu == null)
            menu = new MenuCC(Bukkit.getPlayer(uuid));
        return menu;
    }

    public void failAttack(Point targetedPoint) {
        List<Attack> attacks = MapState.getInstance().getAttacks();

        for (Attack a : attacks)
            if (a.getFactionId() == faction.getId() && a.getPoint().getId() == targetedPoint.getId()) {
                a.addFailerWithoutPurge(this);
                break;
            }
        MapState.getInstance().purgeAttacks();
    }
}
