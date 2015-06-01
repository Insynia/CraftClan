package fr.insynia.craftclan;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Sharowin on 27/05/15.
 */
public class PlayerCommands {

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
}
