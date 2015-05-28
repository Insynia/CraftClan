package fr.insynia.craftclan;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Doc on 13/05/2015.
 * Modified by Sharowin on 18/05/2015.
 */


public class CommandsCC {
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
                    if (!UtilCC.checkArgsChatCommand(args, captureReqArgs)) return die(help, sender);
                    return (PlayerCommands.cmdCapture(sender, loc) || die(help, sender));
                default:
                    sender.sendMessage("Cette commande n'existe pas");
            }
        } else if (cmd.getName().equalsIgnoreCase("cca")) {
            if (!sender.isOp()) {
                return checkOp(sender);
            }
            if (args.length == 0) return false;
            // In following lines, cf: "addPointReqArgs" counts addpoint as an argument.
            // That is why there is a - 1 in sent messages, in order to get commands args count minus command name.
            switch (args[0].toLowerCase()) {
                case "addpoint":
                    int addPointReqArgs = 3;
                    String help = "\"addpoint\" command needs " + (addPointReqArgs) + " parameters:\n" +
                            "[PointName] [PointRadius]   [PointLevel]\n" +
                            "<String>    <Integer>       <Integer>";

                    if (!UtilCC.checkArgsChatCommand(args, addPointReqArgs)) return die(help, sender);
                    return (AdminCommands.cmdAddPoint(sender, loc, args) || die(help, sender));

                case "addfaction":
                    int addFactionReqArgs = 3;
                    help = "\"addfaction\" command needs " + (addFactionReqArgs) + " parameters:\n" +
                            "[FactionName]   [FactionColor]  [FactionLevel]\n" +
                            "<String>        <Color>         <Integer>";

                    if (!UtilCC.checkArgsChatCommand(args, addFactionReqArgs)) return die(help, sender);
                    return (AdminCommands.cmdAddFaction(sender, args) || die(help, sender));

                case "setownfaction":
                    int setOwnFactionReqArgs = 1;
                    help = "\"setownfaction\" command needs " + (setOwnFactionReqArgs) + " parameters:\"\n" +
                            "[FactionName]\n" +
                            "<String>";

                    if (!UtilCC.checkArgsChatCommand(args, setOwnFactionReqArgs)) return die(help, sender);
                    return (AdminCommands.cmdSetOwnFaction(sender, args) || die(help, sender));

                case "setpointfaction":
                    int setPointFactionReqArgs = 2;
                    help = "\"setpointfaction\" command needs " + (setPointFactionReqArgs) + " parameters:\n" +
                            "[PointName] [FactionName]\n" +
                            "<String>    <String>";

                    if (!UtilCC.checkArgsChatCommand(args, setPointFactionReqArgs)) return die(help, sender);
                    return (AdminCommands.cmdSetPointFaction(sender, args) || die(help, sender));

                case "select":
                    int selectReqArgs = 0;
                    help = "\"select\" command needs " + (selectReqArgs) + " parameters:\n";

                    if (!UtilCC.checkArgsChatCommand(args, selectReqArgs)) return die(help, sender);
                    return (AdminCommands.cmdSelection(sender) || die(help, sender));

                case "save":
                    int saveReqArgs = 1;
                    help = "\"save\" command needs " + (saveReqArgs) + " parameters:\n" +
                            "[SelectionName]\n" +
                            "<String>";

                    if (!UtilCC.checkArgsChatCommand(args, saveReqArgs)) return die(help, sender);
                    return (AdminCommands.cmdSaveSelection(sender, args) || die(help, sender));

                case "spawnstructure":
                    int spawnStructureReqArgs = 1;
                    help = "\"spawnstructure\" command needs \"" + (spawnStructureReqArgs) + "\" parameters:\n" +
                            "[StructureName]\n" +
                            "<String>";

                    if (!UtilCC.checkArgsChatCommand(args, spawnStructureReqArgs)) return die(help, sender);
                    return (AdminCommands.cmdSpawnStructure(sender, args) || die(help, sender));

                case "setpointlevel":
                    int setPointLevelReqArgs = 2;
                    help = "\"set\" command needs \"" + (setPointLevelReqArgs) + "\" parameters:\n" +
                            "[PointName]    [NewLevel]\n" +
                            "<String>       <Integer>";

                    if (!UtilCC.checkArgsChatCommand(args, setPointLevelReqArgs)) return die(help, sender);
                    return (AdminCommands.cmdSetPointLevel(sender, args) || die(help, sender));
                default:
                    sender.sendMessage("This command does not exist");
            }

            return true;
        }
        sender.sendMessage("Cette commande n'existe pas");
        sender.sendMessage("Tapez /cc help");

        return false;
    }

    private static boolean checkOp(CommandSender s) {
        s.sendMessage("You must be op !");
        return false;
    }

    private static boolean die(String msg, CommandSender sender) {
        sender.sendMessage(msg);
        return false;
    }
}
