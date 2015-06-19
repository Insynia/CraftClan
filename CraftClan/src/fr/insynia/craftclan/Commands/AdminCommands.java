package fr.insynia.craftclan.Commands;

import fr.insynia.craftclan.Gameplay.*;
import fr.insynia.craftclan.Utils.FileManagerCC;
import fr.insynia.craftclan.Utils.UtilCC;
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
            for (ChatColor c : UtilCC.getRealColors())
                msg += c.name() + " ";
            return die(msg, sender);
        }

        if (!UtilCC.isInteger(args[3])) return die("Arg 3 is supposed to be an integer", sender);
        if (UtilCC.checkFactionExists(args[1])) return die("This faction already exist", sender);
        Faction f = new Faction(0, args[1], args[2], Integer.parseInt(args[3]), "CLOSED", "Doc_CoBrA");

        boolean ret = f.save();

        if (ret) sender.sendMessage("Faction \"" + args[1] + "\" saved : " + args[2] + ", level: " + args[3]);
        else sender.sendMessage("A faction named \"" + args[1] + "\" already exists");
        return ret;
    }

    // Add a Point.
    public static boolean cmdAddPoint(CommandSender sender, Location loc, String[] args) {
        if (!UtilCC.isInteger(args[2]) || !UtilCC.isInteger(args[3]))
            return die("Args 2 and 3 are supposed to be integers", sender);
        if (UtilCC.checkPointExists(args[1])) return die("This point already exist", sender);

        Point p = new Point(args[1], Integer.parseInt(args[2]), loc, Integer.parseInt(args[3]), -1);

        boolean ret = p.save();

        if (ret) sender.sendMessage("Point \"" + args[1] + "\" saved at " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
        else sender.sendMessage("A point named \"" + args[1] + "\" already exists");
        return ret;
    }

    // Change own Faction.
    public static boolean cmdSetOwnFaction(CommandSender sender, String[] args) {
        if (!UtilCC.checkFactionExists(args[1])) return die("This faction doesn't exist", sender);
        Player p = ((Player) sender);
        PlayerCC pcc = MapState.getInstance().findPlayer(p.getUniqueId());
        sender.sendMessage("Changed faction to: \"" + args[1] + "\"");
        return pcc.addToFaction(args[1]);
    }

    // Change a Point's Faction.
    public static boolean cmdSetPointFaction(CommandSender sender, String[] args) {
        if (!UtilCC.checkPointExists(args[1]) || !UtilCC.checkFactionExists(args[2]))
            return die("Either the point or the faction does not exist", sender);
        Point point = MapState.getInstance().findPoint(args[1]);
        sender.sendMessage("Changed point \"" + args[1] + "\" faction");
        return point.addToFaction(args[2]);
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

        if (!FileManagerCC.checkFileAndFolderExist(BlockSpawner.DEFAULT_FILE, args[1]))
            return die("The file \"" + args[1] + "\" does not exist", sender);

        BlockSpawner.spawnStructure(args[1], tloc);
        sender.sendMessage("Spawned structure: \"" + args[1] + "\"");
        return true;
    }

    // Set a Point's level
    public static boolean cmdSetPointLevel(CommandSender sender, String[] args) {
        if (!UtilCC.isInteger(args[2])) return die("Arg 2 is supposed to be an integer", sender);
        Point point = MapState.getInstance().findPoint(args[1]);
        if (!UtilCC.checkPointExists(args[1])) return die("This point does not exist", sender);

        boolean ret = point.setPointLevel(Integer.parseInt(args[2]));

        if (ret) sender.sendMessage("Changed point \"" + args[1] + "\" level to " + Integer.parseInt(args[2]));
        else sender.sendMessage("This point does not exist");
        return ret;
    }

    // Set a player faction
    public static boolean cmdSetPlayerFaction(CommandSender sender, String[] args) {
        if (MapState.getInstance().findPlayer(args[1]) == null ||
                !UtilCC.checkFactionExists(args[2])) return die("Either the player or the faction does not exist", sender);

        PlayerCC pcc = MapState.getInstance().findPlayer(args[1]);
        sender.sendMessage("Changed player \"" + args[1] + "\" faction to: \"" + args[2] + "\"");
        return pcc.addToFaction(args[2]);
    }

    // TP to point
    public static boolean cmdTPToPoint(CommandSender sender, String[] args) {
        Player p = ((Player) sender);
        if (!UtilCC.checkPointExists(args[1]) ||
                MapState.getInstance().findPlayer(p.getName()) == null) return die("The point does not extist", sender);
        Point point = MapState.getInstance().findPoint(args [1]);
        Location newLoc = point.getLocation().clone();
        newLoc.setX(newLoc.getX() + 0.5);
        newLoc.setZ(newLoc.getZ() + 0.5);
        p.teleport(newLoc);
        return true;
    }

    // Rename Point
    public static boolean cmdRenamePoint(CommandSender sender, String[] args) {
        if (!UtilCC.checkPointExists(args[1])) return die("The point does not exist", sender);
        if (UtilCC.checkPointExists(args[2])) return die("A point name \"" + args[2] + "\" already exists !", sender);
        MapState ms = MapState.getInstance();
        Point p = ms.findPoint(args[1]);
        p.setName(args[2]);
        boolean ret = p.update();
        if (ret) sender.sendMessage("Point \"" + args[1] + "\" has been renamed to \"" + args[2] + "\"");
        UtilCC.serverLogger("Point (ID:" + p.getId() + ") \"" + args[1] + "\" has been renamed to \"" + args[2] +
                "\" by " + sender.getName(), UtilCC.DEFAULT_LOGS_FILE);
        return (ret || die("Unknown error !", sender));
    }

    public static boolean cmdGeneratePoints(CommandSender sender, String[] args) {
        if (!UtilCC.isInteger(args[1])) return die("Arg 1 is supposed to be an integer", sender);

        Generator.generatePoints(Integer.parseInt(args[1]));
        return true;
    }

    public static boolean cmdRegeneratePoints(CommandSender sender) {
        Generator.regenerate();
        return true;
    }

    private static boolean die(String msg, CommandSender sender) {
        sender.sendMessage(msg);
        return false;
    }

}
