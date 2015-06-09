package fr.insynia.craftclan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Sharowin on 27/05/15.
 */
public class PlayerCommands {

    private static final int DISTANCE_FARM_CMD = 50;

    // Capture a point
    public static boolean cmdCapture(CommandSender sender, Location loc) {
        Player p = (Player) sender;
        PlayerCC pcc = MapState.getInstance().findPlayer(p.getUniqueId());
        if (pcc == null) return false;

        Point point = pcc.canCapture(loc);
        if (point == null) return false;

        pcc.startCapture(point, p);
        return true;
    }

    public static boolean newFaction(CommandSender sender, String[] args) {
        if (!UtilCC.checkColor(args[2])) {
            String msg = "Couleurs valides: ";
            for (ChatColor c : UtilCC.getRealColors())
                msg += c.name() + " ";
            die(msg, sender);
            return true; // Avoid default help
        }

        if (UtilCC.checkFactionExists(args[1])) {
            die("Nom de faction déjà utilisé", sender);
            return true;
        }
        PlayerCC p = MapState.getInstance().findPlayer(((Player) sender).getUniqueId());

        Faction f = new Faction(0, args[1], args[2], 1);

        boolean ret = f.save();

        p.getFaction().broadcastToMembers(p.getName() + " nous quitte pour sa nouvelle faction " + f.getFancyName() + " !");

        p.addToFaction(f.getName());
        if (ret) sender.sendMessage("Bienvenue dans votre nouvelle faction " + f.getFancyName() + " !");
        else sender.sendMessage("A faction named \"" + args[1] + "\" already exists");
        return ret;
    }

    // JOIN f.broadcastToMembers("Nouveau membre ! Bienvenue a " + p.getName());

    public static boolean cmdStopFarm(CommandSender sender, Location loc) {
        Player p = (Player) sender;

        if (p.getLocation().getWorld() != Bukkit.getWorld(MapState.FARM_WORLD))
            return false;
        p.teleport(Bukkit.getWorld(MapState.DEFAULT_WORLD).getSpawnLocation());
        return true;
    }

    public static boolean cmdGoFarm(final CommandSender sender, Location loc) {
        final Player p = (Player) sender;
        PlayerCC pcc = MapState.getInstance().findPlayer(p.getUniqueId());
        if (pcc == null) return false;
        if (pcc.isOnWorld(MapState.FARM_WORLD)) return false;
        if (UtilCC.distanceBasic(Bukkit.getWorld(MapState.DEFAULT_WORLD).getSpawnLocation(), p.getLocation()) >= DISTANCE_FARM_CMD) return false;

        sender.sendMessage("Vous allez être téléporté dans 10s");

        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("CraftClan"), new Runnable() {
            @Override
            public void run() {
                p.teleport(Bukkit.getWorld(MapState.FARM_WORLD).getSpawnLocation());
                sender.sendMessage("Attention, le PvP est activé dans cette zone ! Surveillez vos diamants ;)");
                sender.sendMessage("Tapez /cc stopfarm pour revenir au spawn");
            }
        }, 10 * 20);

        return true;
    }

    //

    public static boolean cmdUpgradePoint(CommandSender sender, Location loc) {
        Player p = (Player) sender;
        PlayerCC pcc = MapState.getInstance().findPlayer(p.getUniqueId());
        if (pcc == null) return false;

        Point point = pcc.canUpgrade(loc);
        if (point == null) return die("Vous ne pouvez pas améliorer ce point \n" +
                "Le point doit appartenir à votre faction et vous devez être placé sur lui \n" +
                "Il doit être de niveau inférieur à " + Point.POINT_MAX_LEVEL, sender);
        pcc.willUpgrade(point);
        point.upgradePoint();
        pcc.sendMessage("Félicitations, vous avez amélioré le point \"" + point.getName() + "\" !\n"+
        "Il est désormais de niveau " + point.getLevel());
        return true;

    }

    private static boolean die(String msg, CommandSender sender) {
        sender.sendMessage(msg);
        return false;
    }
}
