package fr.insynia.craftclan;

import org.bukkit.Server;
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
}