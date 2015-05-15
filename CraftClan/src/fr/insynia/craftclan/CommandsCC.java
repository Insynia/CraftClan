package fr.insynia.craftclan;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Created by Doc on 13/05/2015.
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
        if (cmd.getName().equalsIgnoreCase("addpoint")) { //debug
            if (!sender.isOp())
                return mustBeOp(sender);
            Point p = new Point(args[0], Integer.parseInt(args[1]), loc, Integer.parseInt(args[2]), -1);
            boolean ret = p.save();
            if (ret)
                sender.sendMessage("Point saved at " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
            else
                sender.sendMessage("A point named " + args[0] + " already exists");
            return ret;
        } else if (cmd.getName().equalsIgnoreCase("addfaction")) { //debug
            if (!sender.isOp())
                return mustBeOp(sender);
            Faction f = new Faction(0, args[0], args[1], Integer.parseInt(args[2]));
            boolean ret = f.save();
            if (ret)
                sender.sendMessage("Faction saved : " + args[0] + ", " + args[1] + ", " + args[2]);
            else
                sender.sendMessage("A faction named " + args[0] + " already exists");
            return ret;
        } else if (cmd.getName().equalsIgnoreCase("gofaction")) { //debug
            Player p = ((Player) sender);
            PlayerCC pcc = MapState.getInstance().findPlayer(p.getUniqueId());
            return pcc.addToFaction(args[0]);
        } else if (cmd.getName().equalsIgnoreCase("pointfaction")) { //debug
            Point point = MapState.getInstance().findPoint(args[0]);
            return point.addToFaction(Integer.parseInt(args[1]));
        } else if (cmd.getName().equalsIgnoreCase("ccselect")) {
            Player p = ((Player) sender);
            Set<Material> mat = null;
            Location tloc = p.getTargetBlock(mat, 10).getLocation();
            Selector.addPoint(tloc.getX(), tloc.getY(), tloc.getZ());
            sender.sendMessage("Point added: " + tloc.getX() + ", " + tloc.getY() + ", " + tloc.getZ());
            return true;
        } else if (cmd.getName().equalsIgnoreCase("spawnstructure")) {
            Player p = ((Player) sender);
            Set<Material> mat = null;
            Location tloc = p.getTargetBlock(mat, 10).getLocation();
            tloc.setY(tloc.getY() + 1);
            BlockSpawner.spawnStructure(args[0], tloc);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("ccsave")) {
            Selector.saveStructure(args[0]);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("ccread")) {
            FilesManagerCC.fileReadtoArrayCC(args[0], args[1]);
            return true;
        }
        return false;
    }

    private static boolean mustBeOp(CommandSender s) {
        s.sendMessage("You must be op !");
        return false;
    }
}
