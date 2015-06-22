package fr.insynia.craftclan.Base;

import fr.insynia.craftclan.Commands.CommandsCC;
import fr.insynia.craftclan.Listeners.MenuEvents;
import fr.insynia.craftclan.Listeners.PlayerEvents;
import fr.insynia.craftclan.Listeners.PlayerRestriction;
import fr.insynia.craftclan.Listeners.PvPRestriction;
import fr.insynia.craftclan.Utils.MenuEnchant;
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
        initializer.init(this);
        server.getPluginManager().registerEvents(new PlayerRestriction(), this);
        server.getPluginManager().registerEvents(new PlayerEvents(), this);
        server.getPluginManager().registerEvents(new PvPRestriction(), this);
        server.getPluginManager().registerEvents(new MenuEvents(), this);
        MenuEnchant.registerMe();
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