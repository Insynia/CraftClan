package fr.insynia.craftclan;

import org.bukkit.Bukkit;
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

    private static boolean die(String msg, CommandSender sender) {
        sender.sendMessage(msg);
        return false;
    }

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
}
