package fr.insynia.craftclan.Commands;

import fr.insynia.craftclan.Base.SQLManager;
import fr.insynia.craftclan.Gameplay.*;
import fr.insynia.craftclan.Utils.EconomyCC;
import fr.insynia.craftclan.Utils.UtilCC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Sharowin on 27/05/15.
 */
public class PlayerCommands {

    private static final int DISTANCE_FARM_CMD = 50;
    private static final int MAX_MEMBERS = 20;

    // Capture a point
    public static boolean cmdCapture(CommandSender sender, Location loc) {
        Player p = (Player) sender;
        PlayerCC pcc = MapState.getInstance().findPlayer(p.getUniqueId());
        if (pcc == null) return false;

        Point point = pcc.canCapture(loc);
        if (point == null) return live("Vous ne pouvez pas capturer ce point !", sender);

        pcc.startCapture(point, p);
        return true;
    }

    public static boolean newFaction(CommandSender sender, String[] args) {
        PlayerCC p = MapState.getInstance().findPlayer(((Player) sender).getUniqueId());
        Faction  playerFaction = p.getFaction();
        int factionMembers = playerFaction.getMembers().size();

        if (p.isLeader() && factionMembers > 1) return live("Vous êtes le leader de votre faction !\n" +
                "Vous devez être seul dans la faction, ou désigner un autre leader en tapant /cc setleader [Nom]\n" +
                "Voici les membres de votre faction: " + playerFaction.listMembers(), sender);

        if (!UtilCC.validatorName(args[1]))
            return live("Le nom est trop long (Maximum: " + UtilCC.MAX_NAME_CHAR_LENGTH + " caractères), ou comporte des caractères interdits", sender);

        if (!UtilCC.checkColor(args[2])) {
            String msg = "Couleurs valides: ";
            for (ChatColor c : UtilCC.getRealColors())
                msg += c.name() + " ";
            return live(msg, sender);
        }

        if (UtilCC.checkFactionExists(args[1])) return live("Nom de faction déjà utilisé", sender);

        if (MapState.getInstance().findRequestByPlayer(p.getName()) != null)
            return live("Vous avez une requête en cours, impossible de créer une faction. Tapez \"/cc cancelrequest\" pour annuler votre requête", sender);

        Faction f = new Faction(0, args[1], args[2], 1, "RESTRICTED", p.getName());

        boolean ret = f.save();

        p.getFaction().broadcastToMembers(p.getName() + " nous quitte pour sa nouvelle faction " + f.getFancyName() + " !");

        p.addToFaction(f.getName());
        if (ret) sender.sendMessage("Bienvenue dans votre nouvelle faction " + f.getFancyName() + " !");
        else sender.sendMessage("A faction named \"" + args[1] + "\" already exists");
        return ret;
    }

    public static boolean cmdStopFarm(CommandSender sender, Location loc) {
        final Player p = (Player) sender;

        sender.sendMessage("Vous allez être téléporté dans 10s");

        if (p.getLocation().getWorld() != Bukkit.getWorld(MapState.FARM_WORLD))
            return false;
        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("CraftClan"), new Runnable() {
            @Override
            public void run() {
                p.teleport(Bukkit.getWorld(MapState.DEFAULT_WORLD).getSpawnLocation());
            }
        }, 10 * 20);
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

    public static boolean cmdUpgradePoint(CommandSender sender, Location loc) {
        Player p = (Player) sender;
        PlayerCC pcc = MapState.getInstance().findPlayer(p.getUniqueId());
        if (pcc == null) return false;

        Point point = pcc.canUpgrade(loc);
        if (point == null) return live("Vous ne pouvez pas améliorer ce point \n" +
                "Le point doit appartenir à votre faction et vous devez être placé sur lui \n" +
                "Il doit être de niveau inférieur à " + Point.POINT_MAX_LEVEL, sender);
        pcc.willUpgrade(point);
        point.upgradePoint();
        pcc.sendMessage("Félicitations, vous avez amélioré le point \"" + point.getName() + "\" !\n" +
                "Il est désormais de niveau " + point.getLevel());
        return true;

    }

    // Faction Organization

    public static boolean listFactionMembers(CommandSender sender) {
        PlayerCC pcc = MapState.getInstance().findPlayer(sender.getName());

        if (pcc.getFaction() == null) return live("Vous n'appartenez à aucune faction", sender);

        Faction f = pcc.getFaction();
        String members = "";
        List<PlayerCC> memberList = f.getMembers();

        if (memberList.size() == 0) return live("La faction est vide", sender);
        for (PlayerCC p : memberList) {
            members += (p.isLeader() ? ChatColor.BLUE : ChatColor.RESET) + p.getName() + ChatColor.RESET + (p.equals(memberList.get(memberList.size() - 1)) ? "." : ", ");
        }
        sender.sendMessage("Votre faction comporte " + memberList.size() +
                (memberList.size() > 1 ? " membres: " : " membre:") + "\n" + members);
        return true;
    }

    public static boolean joinFaction(CommandSender sender, String[] args) {
        MapState ms = MapState.getInstance();
        PlayerCC p = ms.findPlayer(((Player) sender).getUniqueId());

        Faction playerFaction = p.getFaction();
        Faction targetFaction = ms.findFaction(args[1]);
        int factionMembers = playerFaction.getMembers().size();

        if (p.isLeader() && factionMembers > 1) return live("Vous êtes le leader de votre faction !\n" +
                "Vous devez être seul dans la faction, ou désigner un autre leader en tapant /cc setleader [Nom]\n" +
                "Voici les membres de votre faction: " + playerFaction.listMembers(), sender);
        if (targetFaction == null) return live("Cette faction n'existe pas", sender);

        if (playerFaction.getLeaderName().equals(p.getName()))
            return live("Vous êtes le leader de votre faction ! Pas question de déserter :(", sender);

        if (ms.findRequestByPlayer(p.getName()) != null)
            return live("Chaque chose en son temps, vous avez déjà une requête en cours", sender);

        if (targetFaction.getStatus().equals("CLOSED")) return live("Cette faction est fermée", sender);

        if (targetFaction.getMembers().size() >= MAX_MEMBERS) return live("Cette faction est pleine !", sender);

        if (targetFaction.getStatus().equals("OPEN")) {
            if (!p.addToFaction(targetFaction.getName()))
                return false;
            targetFaction.broadcastToMembers(p.getName() + " fait maintenant partie de la faction :) ! Bienvenuuuuue !");
        } else {
            targetFaction.requestedBy(p);
            sender.sendMessage("Votre requête est envoyée au leader !");
        }
        return true;
    }

    public static boolean acceptMember(CommandSender sender, String[] args) {
        MapState ms = MapState.getInstance();
        PlayerCC p = ms.findPlayer(((Player) sender).getUniqueId());

        Faction targetFaction = p.getFaction();

        if (!targetFaction.getLeaderName().equals(p.getName())) return live("Vous n'êtes pas le leader de la faction ;)", sender);

        String target = args[1];
        PlayerCC onlinePlayerCC = MapState.getInstance().findPlayer(target);

        if (ms.findRequestByPlayer(args[1]) == null) return live("Aucune requête de la part du joueur", sender);

        if (onlinePlayerCC != null) {
            onlinePlayerCC.addToFaction(targetFaction.getName());
            ms.removeRequest(ms.findRequestByPlayer(args[1]).getId());
            targetFaction.broadcastToMembers(onlinePlayerCC.getName() + " fait maintenant partie de la faction :) ! Bienvenuuuuue !");
        } else {
            SQLManager sqlm = SQLManager.getInstance();
            sqlm.execUpdate("UPDATE users SET faction_id = " + targetFaction.getId() + " WHERE name = \"" + target + "\"");
            ms.removeRequest(ms.findRequestByPlayer(args[1]).getId());
            targetFaction.broadcastToMembers(p.getName() + " fait maintenant partie de la faction :) ! Pensez a lui souhaiter la bienvenue lorsqu'il sera connecté");
        }
        return true;
    }

    public static boolean refuseMember(CommandSender sender, String[] args) {
        MapState ms = MapState.getInstance();
        PlayerCC p = ms.findPlayer(((Player) sender).getUniqueId());

        Faction targetFaction = p.getFaction();

        if (!targetFaction.getLeaderName().equals(p.getName())) return live("Vous n'êtes pas le leader de la faction ;)", sender);

        if (ms.findRequestByPlayer(args[1]) == null) return live("Aucune requête de la part du joueur", sender);

        String target = args[1];
        PlayerCC onlinePlayerCC = MapState.getInstance().findPlayer(target);
        if (onlinePlayerCC != null) {
            ms.removeRequest(ms.findRequestByPlayer(args[1]).getId());
            onlinePlayerCC.sendMessage("Vous avez été refusé par le leader de " + targetFaction.getFancyName());
            p.sendMessage("Joueur refusé !");
        } else {
            ms.removeRequest(ms.findRequestByPlayer(args[1]).getId());
        }
        return true;
    }

    // Mettre en place même si le gars est déconnecté
    // Broadcast à la faction
    public static boolean setLeader(CommandSender sender, String[] args) {
        MapState ms = MapState.getInstance();
        PlayerCC p = ms.findPlayer(((Player) sender).getUniqueId());

        Faction targetFaction = p.getFaction();

        if (!targetFaction.getLeaderName().equals(p.getName())) return live("Vous n'êtes pas le leader de la faction ;)", sender);

        String target = args[1];
        PlayerCC onlinePlayerCC = MapState.getInstance().findPlayer(target);
        if (onlinePlayerCC != null && onlinePlayerCC.getFaction().getId() == p.getFaction().getId()) {
            targetFaction.setLeaderName(onlinePlayerCC.getName());
            if (targetFaction.update()) onlinePlayerCC.sendMessage("Vous êtes maintenant leader de " + targetFaction.getFancyName());
            else live("Erreur inconnue", sender);
        } else {
            die("Le joueur doit être en ligne et être dans votre faction pour devenir leader !", sender);
        }
        return true;
    }

    public static boolean leaveFaction(CommandSender sender, String[] args) {
        MapState ms = MapState.getInstance();
        PlayerCC p = ms.findPlayer(((Player) sender).getUniqueId());

        Faction targetFaction = p.getFaction();
        int factionMembers = targetFaction.getMembers().size();
        if (p.isLeader() && factionMembers > 1) return live("Vous êtes le leader de votre faction !\n" +
                "Vous devez être seul dans la faction, ou désigner un autre leader en tapant /cc setleader [Nom]\n" +
                "Voici les membres de votre faction: " + targetFaction.listMembers(), sender);
        if (factionMembers == 1) targetFaction.neutralize();
        p.addToFaction(Faction.BASE_FACTION);
        p.sendMessage("Vous retournez dans la faction " + p.getFaction().getFancyName());
        return true;
    }

    public static boolean kickMember(CommandSender sender, String[] args) {
        MapState ms = MapState.getInstance();
        PlayerCC p = ms.findPlayer(((Player) sender).getUniqueId());

        Faction targetFaction = p.getFaction();
        String target = args[1];
        PlayerCC targetPlayer = MapState.getInstance().findPlayer(target);
        if (!targetFaction.getLeaderName().equals(p.getName())) return live("Vous n'êtes pas le leader de la faction ;)", sender);

        if (targetPlayer != null) {
            targetPlayer.addToFaction(Faction.BASE_FACTION);
            targetPlayer.sendMessage("Vous avez été kick de votre faction :(");
            targetFaction.broadcastToMembers(p.getName() + " a kické " + targetPlayer.getName() + " !");
        } else {
            SQLManager sqlm = SQLManager.getInstance();
            for (PlayerCC pcc : targetFaction.getMembers()) {
                if (pcc.getName().equalsIgnoreCase(target)) {
                    if (sqlm.execUpdate("UPDATE users SET faction_id = " + MapState.getInstance().findFaction(Faction.BASE_FACTION).getId() + " WHERE name = \"" + pcc.getName() + "\"")) {
                        targetPlayer = MapState.getInstance().findPlayer(pcc.getName());
                        if (targetPlayer != null) {
                            targetPlayer.addToFaction(Faction.BASE_FACTION);
                            targetPlayer.sendMessage("Vous avez été kick de votre faction :(");
                        }
                        targetFaction.broadcastToMembers(p.getName() + " a kické " + pcc.getName() + " !");
                    }
                    return true;
                }
            }
            die("Ce joueur n'est pas dans votre faction", sender);
        }
        return true;
    }

    public static boolean setFactionStatus(CommandSender sender, String[] args) {
        MapState ms = MapState.getInstance();
        PlayerCC p = ms.findPlayer(((Player) sender).getUniqueId());

        Faction targetFaction = p.getFaction();

        if (!targetFaction.getLeaderName().equals(p.getName())) return live("Vous n'êtes pas le leader de la faction ;)", sender);

        if (!args[1].equalsIgnoreCase("closed") && !args[1].equalsIgnoreCase("restricted") && !args[1].equalsIgnoreCase("open"))
            return live("Statuts valides: closed, open, restricted", sender);

        targetFaction.setStatus(args[1].toUpperCase());
        targetFaction.broadcastToMembers(p.getName() + " a modifié le statut de la faction en " + args[1].toUpperCase());
        return targetFaction.update();
    }

    public static boolean cancelRequest(CommandSender sender, String[] args) {
        MapState ms = MapState.getInstance();
        PlayerCC p = ms.findPlayer(((Player) sender).getUniqueId());

        Request r = ms.findRequestByPlayer(p.getName());
        if (r == null) return live("Aucune requête en cours", sender);

        ms.removeRequest(r.getId());
        return true;
    }

    public static boolean protectPoint(CommandSender sender, String[] args) {
        Protection protection;
        Player p = (Player) sender;
        PlayerCC pcc = MapState.getInstance().findPlayer(p.getUniqueId());

        Date startTime = new Date();
        Date endTime = (Date) startTime.clone();

        if (!UtilCC.isInteger(args[1])) return live("Merci de mettre une quantité valable, exemple: /cc protect 2 hour", sender);

        int amount = Integer.parseInt(args[1]);

        if (amount < 1 || amount > 24)
            die("Quantité invalide !", sender);

        int moneyNeeded = Protection.BASE_AMOUNT;

        switch (args[2]) {
            case "hour":
                endTime.setTime(endTime.getTime() + amount * 3600 * 1000); // hour = 3600 seconds * 1000 milliseconds
                moneyNeeded *= Protection.HOUR_COEF * amount;
                break;
            case "day":
                endTime.setTime(endTime.getTime() + amount * 3600 * 1000 * 24); // day 24 * hour
                moneyNeeded *= Protection.DAY_COEF * amount;
                break;
            case "week":
                endTime.setTime(endTime.getTime() + amount * 3600 * 1000 * 24 * 7); // week day * 7
                moneyNeeded *= Protection.WEEK_COEF * amount;
                break;
            default:
                return live("L'unité de temps n'est pas valide, utilisez hour, day ou week", sender);
        }

        Point point = pcc.canProtect(p.getLocation());

        if (point == null)
            return live("Vous devez être à proximité d'un point de votre faction pour effectuer cette commande", sender);

        if (pcc.getFaction().getId() != point.getFactionId()) return live("Ce point ne vous appartient pas", sender);

        if (point.getProtection() != null) return live("Merci d'attendre que la protection précédente expire", sender);

        if (point.isAttacked())
            return live("Vous ne pouvez pas ajouter une protection lorsque votre point subit une attaque\n" +
                    "Merci d'attendre que l'attaque soit terminée", sender);

        BigDecimal money = BigDecimal.valueOf(moneyNeeded * point.getLevel()); // 1 day = 100 * 10 * Point level

        if (!EconomyCC.has(pcc.getName(), money))
            return live("Vous n'avez pas assez d'argent, il vous faut " + money + "$\n" +
                    "Tapez /balance pour savoir combien vous avez !", sender);

        EconomyCC.take(pcc.getName(), money);
        protection = new Protection(point.getId(), startTime, endTime, pcc.getName());
        if (protection.save()) {
            Bukkit.broadcastMessage(pcc.getName() + " a ajouté une protection sur son point: " + point.getName() + "\n" +
                    "La protection sera active jusqu'au " + UtilCC.dateHumanReadable(endTime));
        } else {
            p.sendMessage("Erreur inconnue, votre commande a échoué");
        }
        return true;
    }

    private static boolean die(String msg, CommandSender sender) {
        sender.sendMessage(msg);
        return false;
    }

    private static boolean live(String msg, CommandSender sender) {
        sender.sendMessage(ChatColor.RED + msg + ChatColor.RESET);
        return true;
    }
}
