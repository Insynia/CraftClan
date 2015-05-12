package fr.insynia.craftclan;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        SQLManager sqlm = new SQLManager();
        Player p = event.getPlayer();
        sqlm.fetchQuery("SELECT * FROM users WHERE uuid = " + p.getUniqueId() + ";", new PlayerCC());
        event.getPlayer().sendMessage("Hello");
        PlayerCC player = MapState.getInstance().findPlayer(p.getUniqueId());
        if (player == null) {
            PlayerCC.create(p);
        } else {
            MapState.getInstance().addPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        PlayerCC player = MapState.getInstance().findPlayer(event.getPlayer().getUniqueId());
        if (player == null) {
            Bukkit.getLogger().warning("TRIED TO REMOVE A NON EXISTENT PLAYER FROM MAPSTATE");
        } else {
            MapState.getInstance().removePlayer(player);
        }
    }
}
