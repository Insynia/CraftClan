package fr.insynia.craftclan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Date;

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
        if (point == null) {
            die("Vous ne pouvez pas capturer ce point !", sender);
            return true;
        }

        pcc.startCapture(point, p);
        return true;
    }

    public static boolean newFaction(CommandSender sender, String[] args) {
        PlayerCC p = MapState.getInstance().findPlayer(((Player) sender).getUniqueId());

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

        if (MapState.getInstance().findRequestByPlayer(p.getName()) != null) {
            die("Vous avez une requête en cours, impossible de créer une faction. Tapez \"/cc cancelrequest\" pour annuler votre requête", sender);
            return true;
        }

        Faction f = new Faction(0, args[1], args[2], 1, "RESTRICTED", p.getName());

        boolean ret = f.save();

        p.getFaction().broadcastToMembers(p.getName() + " nous quitte pour sa nouvelle faction " + f.getFancyName() + " !");

        p.addToFaction(f.getName());
        if (ret) sender.sendMessage("Bienvenue dans votre nouvelle faction " + f.getFancyName() + " !");
        else sender.sendMessage("A faction named \"" + args[1] + "\" already exists");
        return ret;
    }

    // JOIN f.broadcastToMembers("Nouveau membre ! Bienvenue a " + p.getName());

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

    public static boolean joinFaction(CommandSender sender, String[] args) {
        MapState ms = MapState.getInstance();
        PlayerCC p = ms.findPlayer(((Player) sender).getUniqueId());

        Faction playerFaction = p.getFaction();
        Faction targetFaction = ms.findFaction(args[1]);

        if (targetFaction == null) {
            die("Cette faction n'existe pas", sender);
            return true;
        }
        if (playerFaction.getLeaderName().equals(p.getName())) {
            die("Vous êtes le leader de votre faction ! Pas question de déserter :(", sender);
            return true;
        }
        if (ms.findRequestByPlayer(p.getName()) != null) {
            die("Chaque chose en son temps, vous avez déjà une requête en cours", sender);
            return true;
        }
        if (targetFaction.getStatus().equals("CLOSED")) {
            die("Cette faction est fermée", sender);
            return true;
        }
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

        if (!targetFaction.getLeaderName().equals(p.getName())) {
            die("Vous n'êtes pas le leader de la faction ;)", sender);
            return true;
        }
        String target = args[1];
        PlayerCC onlinePlayerCC = MapState.getInstance().findPlayer(target);
        if (ms.findRequestByPlayer(args[1]) == null) {
            die("Aucune requête de la part du joueur", sender);
            return true;
        }
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

        if (!targetFaction.getLeaderName().equals(p.getName())) {
            die("Vous n'êtes pas le leader de la faction ;)", sender);
            return true;
        }
        if (ms.findRequestByPlayer(args[1]) == null) {
            die("Aucune requête de la part du joueur", sender);
            return true;
        }
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

    public static boolean setLeader(CommandSender sender, String[] args) {
        MapState ms = MapState.getInstance();
        PlayerCC p = ms.findPlayer(((Player) sender).getUniqueId());

        Faction targetFaction = p.getFaction();

        if (!targetFaction.getLeaderName().equals(p.getName())) {
            die("Vous n'êtes pas le leader de la faction ;)", sender);
            return true;
        }
        String target = args[1];
        PlayerCC onlinePlayerCC = MapState.getInstance().findPlayer(target);
        if (onlinePlayerCC != null) {
            targetFaction.setLeaderName(onlinePlayerCC.getName());
            onlinePlayerCC.sendMessage("Vous êtes maintenant leader de " + targetFaction.getFancyName());
        } else {
            die("Le joueur doit être en ligne pour devenir leader !", sender);
        }
        return true;
    }

    public static boolean leaveFaction(CommandSender sender, String[] args) {
        MapState ms = MapState.getInstance();
        PlayerCC p = ms.findPlayer(((Player) sender).getUniqueId());

        Faction targetFaction = p.getFaction();

        if (targetFaction.getLeaderName().equals(p.getName())) {
            die("Vous êtes le leader de votre faction ! Pas question de déserter :(", sender);
            return true;
        }
        p.addToFaction("Newbie");
        p.sendMessage("Vous retournez dans la faction " + p.getFaction().getFancyName());
        return true;
    }

    public static boolean kickMember(CommandSender sender, String[] args) {
        MapState ms = MapState.getInstance();
        PlayerCC p = ms.findPlayer(((Player) sender).getUniqueId());

        Faction targetFaction = p.getFaction();
        String target = args[1];
        PlayerCC targetPlayer = MapState.getInstance().findPlayer(target);
        if (!targetFaction.getLeaderName().equals(p.getName())) {
            die("Vous n'êtes pas le leader de la faction ;)", sender);
            return true;
        }
        if (targetPlayer != null) {
            targetPlayer.addToFaction("Newbie");
            targetPlayer.sendMessage("Vous avez été kick de votre faction :(");
            targetFaction.broadcastToMembers(p.getName() + " a kické " + targetPlayer.getName() + " !");
        } else {
            SQLManager sqlm = SQLManager.getInstance();
            for (PlayerCC pcc : targetFaction.getMembers()) {
                if (pcc.getName().equalsIgnoreCase(target)) {
                    if (sqlm.execUpdate("UPDATE users SET faction_id = " + MapState.getInstance().findFaction("Newbie").getId() + " WHERE name = \"" + pcc.getName() + "\"")) {
                        targetPlayer = MapState.getInstance().findPlayer(pcc.getName());
                        if (targetPlayer != null) {
                            targetPlayer.addToFaction("Newbie");
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

        if (!targetFaction.getLeaderName().equals(p.getName())) {
            die("Vous n'êtes pas le leader de la faction ;)", sender);
            return true;
        }
        if (!args[1].equalsIgnoreCase("closed") && !args[1].equalsIgnoreCase("restricted") && !args[1].equalsIgnoreCase("open")) {
            die("Statuts valides: closed, open, restricted", sender);
            return true;
        }
        targetFaction.setStatus(args[1].toUpperCase());
        return targetFaction.update();
    }

    public static boolean cancelRequest(CommandSender sender, String[] args) {
        MapState ms = MapState.getInstance();
        PlayerCC p = ms.findPlayer(((Player) sender).getUniqueId());

        Request r = ms.findRequestByPlayer(p.getName());
        if (r == null) {
            die("Aucune requête en cours", sender);
            return true;
        }
        ms.removeRequest(r.getId());
        return true;
    }

    public static boolean protectPoint(CommandSender sender, String[] args) {
        Protection protection;
        Player p = (Player) sender;
        PlayerCC pcc = MapState.getInstance().findPlayer(p.getUniqueId());

        Date startTime = new Date();
        Date endTime = (Date) startTime.clone();

        if (!UtilCC.isInteger(args[1])) {
            die("Merci de mettre une quantité valable, exemple: /cc protect 2 hour", sender);
            return true;
        }

        int amount = Integer.parseInt(args[1]);

        int moneyNeeded = Protection.BASE_AMOUNT;

        switch (args[2]) {
            case "hour":
                endTime.setTime(endTime.getTime() + amount * 3600 * 1000); // hour = 3600 seconds * 1000 milliseconds
                moneyNeeded *= Protection.HOUR_COEF;
                break;
            case "day":
                endTime.setTime(endTime.getTime() + amount * 3600 * 1000 * 24); // day 24 * hour
                moneyNeeded *= Protection.DAY_COEF;
                break;
            case "week":
                endTime.setTime(endTime.getTime() + amount * 3600 * 1000 * 24 * 7); // week day * 7
                moneyNeeded *= Protection.WEEK_COEF;
                break;
            default:
                die("L'unité de temps n'est pas valide, utilisez hour, day ou week", sender);
                return true;
        }

        Point point = pcc.canProtect(p.getLocation());

        if (point == null) {
            die("Vous devez être à proximité d'un point de votre faction pour effectuer cette commande", sender);
            return true;
        }

        if (pcc.getFaction().getId() != point.getFactionId()) {
            die("Ce point ne vous appartient pas", sender);
            return true;
        }

        if (point.getProtection() != null) {
            die("Merci d'attendre que la protection précédente expire", sender);
            return true;
        }

        if (point.isAttacked()) {
            die("Vous ne pouvez pas ajouter une protection lorsque votre point subit une attaque\n" +
                    "Merci d'attendre que l'attaque soit terminée", sender);
            return true;
        }

        BigDecimal money = BigDecimal.valueOf(moneyNeeded);

        if (!EconomyCC.has(pcc.getName(), money)) {
            die("Vous n'avez pas assez d'argent, il vous faut " + money + "$\n" +
                    "Tapez /balance pour savoir combien vous avez !", sender);
            return true;
        }

        EconomyCC.take(pcc.getName(), money);
        protection = new Protection(point.getId(), startTime, endTime, pcc.getName());
        protection.save();
        return true;
    }
}
