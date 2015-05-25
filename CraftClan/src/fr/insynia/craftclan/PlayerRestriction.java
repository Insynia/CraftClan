package fr.insynia.craftclan;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerRestriction implements Listener {
    private static final String DEFAULT_WORLD = "world";

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (!event.getBlock().getLocation().getWorld().getName().equals(DEFAULT_WORLD))
            return;
        Player player = event.getPlayer();
        PlayerCC playercc = MapState.getInstance().findPlayer(player.getUniqueId());
        event.setCancelled(placeAndBreakRestriction(event, playercc));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if (!event.getBlock().getLocation().getWorld().getName().equals(DEFAULT_WORLD))
            return;
        Player player = event.getPlayer();
        PlayerCC playercc = MapState.getInstance().findPlayer(player.getUniqueId());
        event.setCancelled(placeAndBreakRestriction(event, playercc));
    }

    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) { //debug
        event.getPlayer().sendMessage(MapState.getInstance().stringPoints());
        event.getPlayer().sendMessage(MapState.getInstance().stringFactions());
        event.getPlayer().sendMessage(MapState.getInstance().stringPlayers());
    }

    private boolean placeAndBreakRestriction(BlockEvent event, PlayerCC playercc) {
        Location locBlock = event.getBlock().getLocation();
        return !playercc.isAtHome(locBlock) || playercc.isOnPointArea(locBlock);
    }
}
