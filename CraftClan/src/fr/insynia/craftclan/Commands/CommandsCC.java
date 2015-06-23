package fr.insynia.craftclan.Commands;

import fr.insynia.craftclan.Gameplay.MapState;
import fr.insynia.craftclan.Gameplay.PlayerCC;
import fr.insynia.craftclan.Utils.UtilCC;
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
        String help;

        if (sender instanceof Player) {
            loc = ((Player) sender).getLocation();
        } else {
            sender.sendMessage("You must be a player");
            return false;
        }

        if (cmd.getName().equalsIgnoreCase("cc")) {
            if (args.length == 0) return false;
            switch (args[0].toLowerCase()) {
                case "menu":
                    PlayerCC pcc = MapState.getInstance().findPlayer(((Player) sender).getUniqueId());
                    pcc.getMenu().open();
                    return true;
                case "f":
                    help = "Votre faction ne peut pas communiquer en privé";
                    return (PlayerCommands.cmdTalkToFaction(sender, loc) || die(help, sender));
                case "capture":
                    help = "\"capture\": Vous devez être à proximité d'un point ennemi pour pouvoir le capturer";
                    return (PlayerCommands.cmdCapture((Player) sender) || die(help, sender));
                case "farm":
                    help = "\"farm\": Vous êtes trop loin du spawn";
                    return (PlayerCommands.cmdGoFarm((Player) sender) || die(help, sender));
                case "stopfarm":
                    help = "\"stopfarm\": Vous n'êtes pas dans la zone de farm";
                    return (PlayerCommands.cmdStopFarm((Player) sender) || die(help, sender));
                case "newfaction":
                    help = "La commande \"newfaction\" requiert 2 paramètres:\n" +
                            "[Nom] [Couleur]";

                    if (!UtilCC.checkArgsChatCommand(args, 2)) return die(help, sender);
                    return (PlayerCommands.newFaction(sender, args) || die(help, sender));
                case "join":
                    help = "La commande \"joinfaction\" requiert 1 paramètre:\n" +
                            "[Nom]";

                    if (!UtilCC.checkArgsChatCommand(args, 1)) return die(help, sender);
                    return (PlayerCommands.joinFaction(sender, args) || die(help, sender));
                case "accept":
                    help = "La commande \"accept\" requiert 1 paramètre:\n" +
                            "[Nom]";

                    if (!UtilCC.checkArgsChatCommand(args, 1)) return die(help, sender);
                    return (PlayerCommands.acceptMember(sender, args) || die(help, sender));
                case "refuse":
                    help = "La commande \"refuse\" requiert 1 paramètre:\n" +
                            "[Nom]";

                    if (!UtilCC.checkArgsChatCommand(args, 1)) return die(help, sender);
                    return (PlayerCommands.refuseMember(sender, args) || die(help, sender));
                case "setleader":
                    help = "La commande \"setleader\" requiert 1 paramètre:\n" +
                            "[Nom]";

                    if (!UtilCC.checkArgsChatCommand(args, 1)) return die(help, sender);
                    return (PlayerCommands.setLeader(sender, args) || die(help, sender));
                case "leave":
                    return (PlayerCommands.leaveFaction(sender, args));
                case "kick":
                    help = "La commande \"kick\" requiert 1 paramètre:\n" +
                            "[Nom]";
                    return (PlayerCommands.kickMember(sender, args) || die(help, sender));
                case "listmembers":
                    return PlayerCommands.listFactionMembers(sender);
                case "listfactions":
                    return PlayerCommands.listFactions(sender);
                case "status":
                    help = "La commande \"status\" requiert 1 paramètre:\n" +
                            "[Statut]";
                    if (!UtilCC.checkArgsChatCommand(args, 1)) return die(help, sender);
                    return (PlayerCommands.setFactionStatus(sender, args) || die(help, sender));
                case "cancelrequest":
                    return (PlayerCommands.cancelRequest(sender));
                case "help":
                    sender.sendMessage("Vous pouvez consulter les règles, conditions d'utilisation et commandes disponibles sur notre forum: http://forum.craftclan.fr");
                    return true;
                case "upgrade":
                    help = "\"upgrade\": Vous ne pouvez pas améliorer ce point";
                    return (PlayerCommands.cmdUpgradePoint(sender, loc) || die(help, sender));
                case "protect":
                    help = "La commande \"protect\" requiert 2 paramètres:\n" +
                            "[Quantité] [Unité temporelle]\n" +
                            "Unités temporelles valides: hour (heure), day (jour), week (semaine)";
                    if (!UtilCC.checkArgsChatCommand(args, 2)) return die(help, sender);
                    return (PlayerCommands.protectPoint(sender, args) || die(help, sender));
                default:
                    sender.sendMessage("Cette commande n'existe pas");
            }
// Admin Commands
        } else if (cmd.getName().equalsIgnoreCase("cca")) {
            if (!sender.isOp()) return checkOp(sender);
            if (args.length == 0) return false;

            switch (args[0].toLowerCase()) {
//                case "mount":
//                    return (AdminCommands.cmdMount(sender, args));

                case "addpoint":
                    int addPointReqArgs = 3;
                    help = "\"addpoint\" command needs " + (addPointReqArgs) + " parameters:\n" +
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
                    return AdminCommands.cmdSelection(sender);

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
                    help = "\"setpointlevel\" command needs \"" + (setPointLevelReqArgs) + "\" parameters:\n" +
                            "[PointName]    [NewLevel]\n" +
                            "<String>       <Integer>";

                    if (!UtilCC.checkArgsChatCommand(args, setPointLevelReqArgs)) return die(help, sender);
                    return (AdminCommands.cmdSetPointLevel(sender, args) || die(help, sender));

                case "setplayerfaction":
                    int setPlayerFactionReqArgs = 2;
                    help = "\"setplayerfaction\" command needs \"" + (setPlayerFactionReqArgs) + "\" parameters:\n" +
                            "[PlayerName]    [FactionName]\n" +
                            "<String>       <String>";

                    if (!UtilCC.checkArgsChatCommand(args, setPlayerFactionReqArgs)) return die(help, sender);
                    return (AdminCommands.cmdSetPlayerFaction(sender, args) || die(help, sender));
                case "tptopoint":
                    int tpToPointReqArgs = 1;
                    help = "\"tptopoint\" command needs \"" + (tpToPointReqArgs) + "\" parameters:\n" +
                            "[PointName]\n" +
                            "<String>";

                    if (!UtilCC.checkArgsChatCommand(args, tpToPointReqArgs)) return die(help, sender);
                    return (AdminCommands.cmdTPToPoint(sender, args) || die(help, sender));
                case "genpoints":
                    int genPointsReqArgs = 1;
                    help = "\"genpoints\" command needs \"" + (genPointsReqArgs) + "\" parameters:\n" +
                            "[LayersCount]\n" +
                            "<Integer>";

                    if (!UtilCC.checkArgsChatCommand(args, genPointsReqArgs)) return die(help, sender);
                    return (AdminCommands.cmdGeneratePoints(sender, args) || die(help, sender));
                case "renamepoint":
                    int renamePointReqArgs = 2;
                    help = "\"renamepoint\" command needs \"" + (renamePointReqArgs) + "\" parameters:\n" +
                            "[PointName]    [NewName]\n" +
                            "<String>       <String>";

                    if (!UtilCC.checkArgsChatCommand(args, renamePointReqArgs)) return die(help, sender);
                    return (AdminCommands.cmdRenamePoint(sender, args) || die(help, sender));
                case "renamefaction":
                    int renameFactionReqArgs = 2;
                    help = "\"renamefaction\" command needs \"" + (renameFactionReqArgs) + "\" parameters:\n" +
                            "[FactionName]    [NewName]\n" +
                            "<String>       <String>";
                    if (!UtilCC.checkArgsChatCommand(args, renameFactionReqArgs)) return die(help, sender);
                    return (AdminCommands.cmdRenameFaction(sender, args) || die(help, sender));
                case "regenpoints":
                    return (AdminCommands.cmdRegeneratePoints(sender));
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
