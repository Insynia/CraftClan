package fr.insynia.craftclan;

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
        initializer.init();
        server.getPluginManager().registerEvents(new PlayerRestriction(), this);
        server.getPluginManager().registerEvents(new PlayerEvents(), this);
    }

    // Fired when plugin is disabled
    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        CommandsCC.execCommand(sender, cmd, label, args);
        return true;
    }
}