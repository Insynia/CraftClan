package fr.insynia.craftclan;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

/**
 * Created by Doc on 13/05/2015.
 * Modified by Sharowin on 18/05/2015.
 */


public class CommandsCC {
    private static final String DEFAULT_FILE_STRUCTURE = "structures/";
    public static boolean execCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Location loc;
        if (sender instanceof Player) {
            loc = ((Player) sender).getLocation();
        } else {
            sender.sendMessage("You must be a player");
            return false;
        }
        if (cmd.getName().equalsIgnoreCase("cc")) {
            if (args.length == 0) return false ;
            switch (args[0].toLowerCase()) {
                case "capture":
                    int captureReqArgs = 1;
                    String help = "\"capture\": Vous devez être à proximité d'un point ennemi pour pouvoir le capturer.";
                    if (!checkArgs(args, captureReqArgs)) return die(help, sender);
                    return (cmdCapture(sender, loc) || die(help, sender));
                default:
                    sender.sendMessage("Cette commande n'existe pas");
            }
        } else if (cmd.getName().equalsIgnoreCase("cca")) {
            if (!sender.isOp()) {
                return mustBeOp(sender);
            }
            if (args.length == 0) return false;
            // In following lines, cf: "addPointReqArgs" counts addpoint as an argument.
            // That is why there is a - 1 in sent messages, in order to get commands args count minus command name.
            switch (args[0].toLowerCase()) {
                case "addpoint":
                    int addPointReqArgs = 4;
                    String help = "\"addpoint\" command needs " + (addPointReqArgs - 1) + " parameters:\n" +
                            "[PointName] [PointRadius]   [PointLevel]\n" +
                            "<String>    <Integer>       <Integer>";

                    if (!checkArgs(args, addPointReqArgs)) return die(help, sender);
                    return (cmdAddPoint(sender, loc, args) || die(help, sender));

                case "addfaction":
                    int addFactionReqArgs = 4;
                    help = "\"addfaction\" command needs " + (addFactionReqArgs - 1) + " parameters:\n" +
                            "[FactionName]   [FactionColor]  [FactionLevel]\n" +
                            "<String>        <Color>         <Integer>";

                    if (!checkArgs(args, addFactionReqArgs)) return die(help, sender);
                    return (cmdAddFaction(sender, args) || die(help, sender));

                case "setownfaction":
                    int setOwnFactionReqArgs = 2;
                    help = "\"setownfaction\" command needs " + (setOwnFactionReqArgs - 1) + " parameters:\"\n" +
                            "[FactionName]\n" +
                            "<String>";

                    if (!checkArgs(args, setOwnFactionReqArgs)) return die(help, sender);
                    return (cmdSetOwnFaction(sender, args) || die(help, sender));

                case "setpointfaction":
                    int setPointFactionReqArgs = 3;
                    help = "\"setpointfaction\" command needs " + (setPointFactionReqArgs - 1) + " parameters:\n" +
                            "[PointName] [FactionName]\n" +
                            "<String>    <String>";

                    if (!checkArgs(args, setPointFactionReqArgs)) return die(help, sender);
                    return (cmdSetPointFaction(sender, args) || die(help, sender));

                case "select":
                    int selectReqArgs = 1;
                    help = "\"select\" command needs " + (selectReqArgs - 1) + " parameters:\n";

                    if (!checkArgs(args, selectReqArgs)) return die(help, sender);
                    return (cmdSelection(sender) || die(help, sender));

                case "save":
                    int saveReqArgs = 2;
                    help = "\"save\" command needs " + (saveReqArgs - 1) + " parameters:\n" +
                            "[SelectionName]\n" +
                            "<String>";

                    if (!checkArgs(args, saveReqArgs)) return die(help, sender);
                    return (cmdSaveSelection(sender, args) || die(help, sender));

                case "spawnstructure":
                    int spawnStructureReqArgs = 2;
                    help = "\"spawnstructure\" command needs \"" + (spawnStructureReqArgs - 1) + "\" parameters:\n" +
                            "[StructureName]\n" +
                            "<String>";

                    if (!checkArgs(args, spawnStructureReqArgs)) return die(help, sender);
                    return (cmdSpawnStructure(sender, args) || die(help, sender));

                case "setpointlevel":
                    int setPointLevelReqArgs = 3;
                    help = "\"set\" command needs \"" + (setPointLevelReqArgs - 1) + "\" parameters:\n" +
                            "[PointName]    [NewLevel]\n" +
                            "<String>       <Integer>";

                    if (!checkArgs(args, setPointLevelReqArgs)) return die(help, sender);
                    return (cmdSetPointLevel(sender, args) || die(help, sender));
                default:
                    sender.sendMessage("This command does not exist");
            }

            return true;
        }
        sender.sendMessage("Cette commande n'existe pas");
        sender.sendMessage("Tapez /cc help");

        return false;
    }

    // Add a Point.
    private static boolean cmdAddPoint(CommandSender sender, Location loc, String[] args) {
        if (!checkArgIsInteger(args[2]) || !checkArgIsInteger(args[3])) {
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

    // Add a Faction.
    private static boolean cmdAddFaction(CommandSender sender, String[] args) {
        if (!checkColor(args[2])) {
            String msg = "Invalid Color: ";
            for (ChatColor c : ChatColor.values())
                msg += c.name() + " ";
            sender.sendMessage(msg);
            return false;
        }
        if (!checkArgIsInteger(args[3])) return false;
        Faction f = new Faction(0, args[1], args[2], Integer.parseInt(args[3]));
        boolean ret = f.save();

        if (ret)
            sender.sendMessage("Faction \"" + args[1] + "\" saved : " + args[2] + ", level: " + args[3]);
        else
            sender.sendMessage("A faction named \"" + args[1] + "\" already exists");
        return ret;
    }

    // Change own Faction.
    private static boolean cmdSetOwnFaction(CommandSender sender, String[] args) {
        if (checkFactionExists(args[1])) {
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
    private static boolean cmdSetPointFaction(CommandSender sender, String[] args) {
        if (checkPointExists(args[1]) && checkFactionExists(args[2])) {
            Point point = MapState.getInstance().findPoint(args[1]);
            sender.sendMessage("Changed point \"" + args[1] + "\" faction");
            return point.addToFaction(args[2]);
        } else {
            sender.sendMessage("Either the point, or the faction, does not exist");
            return false;
        }
    }

    // Select via Selector.
    private static boolean cmdSelection(CommandSender sender) {
        Player p = ((Player) sender);
        Location tloc = p.getTargetBlock((Set<Material>) null, 10).getLocation();
        Selector.addPoint(tloc.getX(), tloc.getY(), tloc.getZ());
        sender.sendMessage("Selection point added at: " + tloc.getX() + ", " + tloc.getY() + ", " + tloc.getZ());
        return true;
    }

    // Save selection.
    private static boolean cmdSaveSelection(CommandSender sender, String[] args) {
        Selector.saveStructure(args[1]);
        sender.sendMessage("Saved selection, name: \"" + args[1] + "\".");
        return true;
    }

    // Spawn saved structure.
    private static boolean cmdSpawnStructure(CommandSender sender, String[] args) {
        Player p = ((Player) sender);
        Location tloc = p.getTargetBlock((Set<Material>) null, 10).getLocation();
        tloc.setY(tloc.getY() + 1);
        if (!FileManager.checkFileAndFolderExist(DEFAULT_FILE_STRUCTURE, args[1])) {
            sender.sendMessage("The file \"" + args[1] + "\" does not exist");
        } else {
            BlockSpawner.spawnStructure(args[1], tloc);
            sender.sendMessage("Spawned structure: \"" + args[1] + "\"");

        }
        return true;
    }

    // Capture a point
    private static boolean cmdCapture(CommandSender sender, Location loc) {
        Player p = (Player) sender;
        PlayerCC pcc = MapState.getInstance().findPlayer(p.getUniqueId());
        if (pcc == null) return false;
        Point point = pcc.canCapture(loc);
        if (point == null) return false;
        pcc.startCapture(point, p);
        return true;
    }
    // Set a Point's level
    private static boolean cmdSetPointLevel(CommandSender sender, String[] args) {
        if (!checkArgIsInteger(args[2])) return false;
        Point point = MapState.getInstance().findPoint(args[1]);

        if (checkPointExists(args[1])) {
            boolean ret = point.setPointLevel(Integer.parseInt(args[2]));
            if (ret) sender.sendMessage("Changed point \"" + args[1] + "\" level to " + Integer.parseInt(args[2]));
            else sender.sendMessage("This point does not exist");
            return ret;
        }
        return false;
    }

    // Checking args count.
    private static boolean checkArgs(String[] args, int estimatedArgs) {
        return args.length == estimatedArgs;
    }

    private static boolean checkColor(String color) {   // To test.
        for (ChatColor c : ChatColor.values())
            if (c.name().equals(color)) return true;
        return false;
    }

    private static boolean mustBeOp(CommandSender s) {
        s.sendMessage("You must be op !");
        return false;
    }

    private static boolean checkFactionExists(String factionName) {
        List<Faction> factions = MapState.getInstance().getFactions();
        for (Faction f : factions) if (factionName.equalsIgnoreCase(f.getName())) return true;
        return false;
    }

    private static boolean checkPointExists(String pointName) {
        List<Point> points = MapState.getInstance().getPoints();
        for (Point p : points) if (pointName.equalsIgnoreCase(p.getName())) return true;
        return false;
    }

    private static boolean die(String msg, CommandSender sender) {
        sender.sendMessage(msg);
        return false;
    }

    private static boolean checkArgIsInteger(String arg) {
        try {
            Integer.parseInt(arg);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
