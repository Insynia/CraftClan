package fr.insynia.craftclan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
		initializer.createPoints();
		getServer().getPluginManager().registerEvents(new PlayerRestriction(), this);
	}
	// Fired when plugin is disabled
	@Override
	public void onDisable() {

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("addpoint")) {
			Location loc = Bukkit.getPlayer(sender.getName()).getLocation();
			Point p = new Point(args[0], Integer.parseInt(args[1]), loc);
			p.saveSQL();
			sender.sendMessage("Point saved at " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
			return true;
		}
		return false;
	}
}