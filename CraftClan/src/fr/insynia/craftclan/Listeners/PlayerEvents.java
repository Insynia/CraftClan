package fr.insynia.craftclan.Listeners;

import fr.insynia.craftclan.Base.SQLManager;
import fr.insynia.craftclan.Commands.MenuCC;
import fr.insynia.craftclan.Gameplay.MapState;
import fr.insynia.craftclan.Gameplay.PlayerCC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

public class PlayerEvents implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        SQLManager sqlm = SQLManager.getInstance();
        Player p = event.getPlayer();
        sqlm.fetchQuery("SELECT * FROM users WHERE uuid = \"" + p.getUniqueId() + "\";", new PlayerCC());

        PlayerCC player = MapState.getInstance().findPlayer(p.getUniqueId());
        if (player == null) {
            PlayerCC.create(p);
        } else {
            MapState.getInstance().addPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();

        if (!p.getInventory().contains(MenuCC.getMenu())) {
            if (p.getInventory().getItem(8) != null)
                p.getWorld().dropItem(p.getLocation(), p.getInventory().getItem(8));
            p.getInventory().setItem(8, MenuCC.getMenu());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        PlayerCC pcc = MapState.getInstance().findPlayer(p.getUniqueId());
        pcc.loadFaction();

        if (!p.getInventory().contains(MenuCC.getMenu())) {
            if (p.getInventory().getItem(8) != null)
                p.getWorld().dropItem(p.getLocation(), p.getInventory().getItem(8));
            p.getInventory().setItem(8, MenuCC.getMenu());
        }
        if (!p.hasPlayedBefore())
            p.sendMessage("Le simple fait de jouer sur ce serveur vous engage à respecter nos" +
                    " conditions d'utilisation présentes sur le forum: http://forum.craftclan.fr");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        PlayerCC pcc = MapState.getInstance().findPlayer(event.getEntity().getUniqueId());
        pcc.failAttacks();
        event.getDrops().remove(MenuCC.getMenu());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerCC pcc = MapState.getInstance().findPlayer(event.getPlayer().getUniqueId());
        if (pcc == null) {
            Bukkit.getLogger().warning("TRIED TO REMOVE A NON EXISTENT PLAYER FROM MAPSTATE");
        } else {
            pcc.failAttacks();
            MapState.getInstance().removePlayer(pcc);
        }
    }

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        final PlayerCC pcc = MapState.getInstance().findPlayer(p.getUniqueId());
        final String msg = event.getMessage();

        if (pcc.isTalkingToFaction()) {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    pcc.getFaction().msgFromPlayerToFaction(pcc, msg);
                }
            };
            Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("CraftClan"), task);
            event.setCancelled(true);
        }
    }
}
