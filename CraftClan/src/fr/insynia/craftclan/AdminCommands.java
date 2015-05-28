package fr.insynia.craftclan;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Created by Sharowin on 27/05/15.
 */
public class AdminCommands {

    // Add a Faction.
    public static boolean cmdAddFaction(CommandSender sender, String[] args) {
        if (!UtilCC.checkColor(args[2])) {
            String msg = "Invalid Color: ";
            for (ChatColor c : ChatColor.values())
                msg += c.name() + " ";
            sender.sendMessage(msg);
            return false;
        }

        if (!UtilCC.checkArgIsInteger(args[3])) return false;
        Faction f = new Faction(0, args[1], args[2], Integer.parseInt(args[3]));
        boolean ret = f.save();

        if (ret)
            sender.sendMessage("Faction \"" + args[1] + "\" saved : " + args[2] + ", level: " + args[3]);
        else
            sender.sendMessage("A faction named \"" + args[1] + "\" already exists");
        return ret;
    }

    // Add a Point.
    public static boolean cmdAddPoint(CommandSender sender, Location loc, String[] args) {
        if (!UtilCC.checkArgIsInteger(args[2]) || !UtilCC.checkArgIsInteger(args[3])) {
            return false;
        } else {
            Point p = new Point(args[1], Integer.parseInt(args[2]), loc, Integer.parseInt(args[3]), -1);

            boolean ret = p.save();
            if (ret)
                sender.sendMessage("Point \"" + args[1] + "\" saved at " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
            else
                sender.sendMessage("A point named \"" + args[1] + "\" already exists");
            return ret;
        }
    }

    // Change own Faction.
    public static boolean cmdSetOwnFaction(CommandSender sender, String[] args) {
        if (UtilCC.checkFactionExists(args[1])) {
            Player p = ((Player) sender);
            PlayerCC pcc = MapState.getInstance().findPlayer(p.getUniqueId());
            sender.sendMessage("Changed faction to: \"" + args[1] + "\"");
            return pcc.addToFaction(args[1]);
        } else {
            sender.sendMessage("This faction does not exist");
            return false;
        }
    }

    // Change a Point's Faction.
    public static boolean cmdSetPointFaction(CommandSender sender, String[] args) {
        if (UtilCC.checkPointExists(args[1]) && UtilCC.checkFactionExists(args[2])) {
            Point point = MapState.getInstance().findPoint(args[1]);
            sender.sendMessage("Changed point \"" + args[1] + "\" faction");
            return point.addToFaction(args[2]);
        } else {
            sender.sendMessage("Either the point, or the faction, does not exist");
            return false;
        }
    }

    // Select via Selector.
    public static boolean cmdSelection(CommandSender sender) {
        Player p = ((Player) sender);
        Location tloc = p.getTargetBlock((Set<Material>) null, 10).getLocation();
        Selector.addPoint(tloc.getX(), tloc.getY(), tloc.getZ());
        sender.sendMessage("Selection point added at: " + tloc.getX() + ", " + tloc.getY() + ", " + tloc.getZ());
        return true;
    }

    // Save selection.
    public static boolean cmdSaveSelection(CommandSender sender, String[] args) {
        boolean ret = Selector.saveStructure(args[1]);
        if (ret) sender.sendMessage("Saved selection, name: \"" + args[1] + "\".");
        else sender.sendMessage("This file already exists can't overwrite");
        return ret;
    }

    // Spawn saved structure.
    public static boolean cmdSpawnStructure(CommandSender sender, String[] args) {
        Player p = ((Player) sender);
        Location tloc = p.getTargetBlock((Set<Material>) null, 10).getLocation();
        tloc.setY(tloc.getY() + 1);
        if (!FileManager.checkFileAndFolderExist(BlockSpawner.DEFAULT_FILE, args[1])) {
            sender.sendMessage("The file \"" + args[1] + "\" does not exist");
        } else {
            BlockSpawner.spawnStructure(args[1], tloc);
            sender.sendMessage("Spawned structure: \"" + args[1] + "\"");
        }
        return true;
    }

    // Set a Point's level
    public static boolean cmdSetPointLevel(CommandSender sender, String[] args) {
        if (!UtilCC.checkArgIsInteger(args[2])) return false;
        Point point = MapState.getInstance().findPoint(args[1]);

        if (UtilCC.checkPointExists(args[1])) {
            boolean ret = point.setPointLevel(Integer.parseInt(args[2]));
            if (ret) sender.sendMessage("Changed point \"" + args[1] + "\" level to " + Integer.parseInt(args[2]));
            else sender.sendMessage("This point does not exist");
            return ret;
        }
        return false;
    }
}
