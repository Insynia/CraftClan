package fr.insynia.craftclan;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerRestriction implements Listener {
    private static final String DEFAULT_WORLD = "world";

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (!event.getBlock().getLocation().getWorld().getName().equals(DEFAULT_WORLD))
            return;
        Player player = event.getPlayer();
        PlayerCC playercc = MapState.getInstance().findPlayer(player.getUniqueId());
        Location locBlock = event.getBlock().getLocation();
        if (!canBreak(playercc, player, event) && !playercc.isAtHome(locBlock))
            event.setCancelled(true);
        else
            handleBreak(playercc, event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if (!event.getBlock().getLocation().getWorld().getName().equals(DEFAULT_WORLD))
            return;
        Player player = event.getPlayer();
        PlayerCC playercc = MapState.getInstance().findPlayer(player.getUniqueId());
        Location locBlock = event.getBlock().getLocation();
        if (!canPlace(playercc, player, event) && !playercc.isAtHome(locBlock))
            event.setCancelled(true);
        else
            handlePlace(playercc, event);
    }

    private boolean canPlace(PlayerCC pcc, Player player, BlockPlaceEvent event) {
        Point curPoint = MapUtils.getLocationPoint(event.getBlock().getLocation());
        Attack attack = pcc.isOnAttackOn(curPoint);

        if (curPoint == null)
            return false;
        return attack != null && event.getBlock().getType().equals(Material.GOLD_BLOCK);
    }

    private boolean canBreak(PlayerCC pcc, Player player, BlockBreakEvent event) {
        Point curPoint = MapUtils.getLocationPoint(event.getBlock().getLocation());
        Attack attack = pcc.isOnAttackOn(curPoint);

        if (curPoint == null)
            return false;
        if (attack != null)
            return true;
        if (pcc.willAttack(event.getBlock()))
            return true;
        return false;
    }

    private void handleBreak(PlayerCC pcc, BlockBreakEvent event) {
        Point curPoint = MapUtils.getLocationPoint(event.getBlock().getLocation());
        Attack attack = pcc.isOnAttackOn(curPoint);
        if (attack != null)
            attack.logBlock(event.getBlock(), "BREAK");
    }

    private void handlePlace(PlayerCC pcc, BlockPlaceEvent event) {
        Point curPoint = MapUtils.getLocationPoint(event.getBlock().getLocation());
        Attack attack = pcc.isOnAttackOn(curPoint);
        attack.logBlock(event.getBlock(), "PLACE");
    }
}
