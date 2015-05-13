package fr.insynia.craftclan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftClanPlugin extends JavaPlugin {
    private Server server = null;

    public CraftClanPlugin(){
        server = getServer();
    }

    // Fired when plugin is first enabled
    @Override
    public void onEnable() {
        InitPlugin initializer = new InitPlugin();
        initializer.init();
        getServer().getPluginManager().registerEvents(new PlayerRestriction(), this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
    }
    // Fired when plugin is disabled
    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("addpoint")) { //debug
            if (!sender.isOp())
                return mustBeOp(sender);
            Location loc = Bukkit.getPlayer(sender.getName()).getLocation();
            Point p = new Point(args[0], Integer.parseInt(args[1]), loc, -1);
            boolean ret = p.save();
            if (ret)
                sender.sendMessage("Point saved at " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
            else
                sender.sendMessage("A point named " + args[0] + " already exists");
            return ret;
        } else if (cmd.getName().equalsIgnoreCase("addfaction")) { //debug
            if (!sender.isOp())
                return mustBeOp(sender);
            Faction f = new Faction(args[0], args[1], Integer.parseInt(args[2]));
            boolean ret = f.save();
            if (ret)
                sender.sendMessage("Faction saved : " + args[0] + ", " + args[1] + ", " + args[2]);
            else
                sender.sendMessage("A faction named " + args[0] + " already exists");
            return ret;
        } else if (cmd.getName().equalsIgnoreCase("gofaction")) { //debug
            Player p = Bukkit.getPlayer(args[0]);
            PlayerCC pcc = MapState.getInstance().findPlayer(p.getUniqueId());
            return pcc.addToFaction(Integer.parseInt(args[1]));
        } else if (cmd.getName().equalsIgnoreCase("pointfaction")) { //debug
            Point point = MapState.getInstance().findPoint(args[0]);
            return point.addToFaction(Integer.parseInt(args[1]));
        }
        return false;
    }

    private boolean mustBeOp(CommandSender s) {
        s.sendMessage("You must be op !");
        return false;
    }
}