package fr.insynia.craftclan.Listeners;

import fr.insynia.craftclan.Gameplay.MapState;
import fr.insynia.craftclan.Gameplay.PlayerCC;
import fr.insynia.craftclan.Base.SQLManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        SQLManager sqlm = SQLManager.getInstance();
        Player p = event.getPlayer();
        sqlm.fetchQuery("SELECT * FROM users WHERE uuid = \"" + p.getUniqueId() + "\";", new PlayerCC());

        if (!p.hasPlayedBefore())
            p.sendMessage("Le simple fait de jouer sur ce serveur vous engage à respecter nos" +
                    " conditions d'utilisation présentes sur le forum: http://forum.craftclan.fr");

        PlayerCC player = MapState.getInstance().findPlayer(p.getUniqueId());
        if (player == null) {
            PlayerCC.create(p);
        } else {
            MapState.getInstance().addPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        PlayerCC player = MapState.getInstance().findPlayer(p.getUniqueId());
        player.loadFaction();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        PlayerCC pcc = MapState.getInstance().findPlayer(event.getEntity().getUniqueId());
        pcc.failAttacks();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        PlayerCC pcc = MapState.getInstance().findPlayer(event.getPlayer().getUniqueId());
        if (pcc == null) {
            Bukkit.getLogger().warning("TRIED TO REMOVE A NON EXISTENT PLAYER FROM MAPSTATE");
        } else {
            pcc.failAttacks();
            MapState.getInstance().removePlayer(pcc);
        }
    }
}
