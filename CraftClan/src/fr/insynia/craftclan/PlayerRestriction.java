package fr.insynia.craftclan;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
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
        if (!canBreak(playercc, player, event))
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
        if (!canPlace(playercc, player, event))
            event.setCancelled(true);
        else
            handlePlace(playercc, event);
    }

    private boolean canPlace(PlayerCC pcc, Player player, BlockPlaceEvent event) {
        if (isAllowed(event, pcc)) return true; // When the player is at home and not on a point
        if (pcc.isOnPointArea(event.getBlock().getLocation())) return false;

        Point curPoint = MapUtils.getLocationPoint(event.getBlock().getLocation());
        if (curPoint == null) // Is there a point here ?
            return false;
        Attack attack = pcc.isOnAttackOn(curPoint); // Player is Attacking the point ? If yes return the attack
        return (attack != null && event.getBlock().getType().equals(Material.GOLD_BLOCK));  // Checks the attack and the block type to be placed
    }

    private boolean canBreak(PlayerCC pcc, Player player, BlockBreakEvent event) {
        if (isAllowed(event, pcc)) return true;
        if (pcc.isOnPointArea(event.getBlock().getLocation())) return false;

        Point curPoint = MapUtils.getLocationPoint(event.getBlock().getLocation());
        if (curPoint == null)
            return false;

        Attack attack = pcc.isOnAttackOn(curPoint);
        if (attack != null || pcc.willAttack(event.getBlock())) // When player is attacking the point OR the attacks is well created
            return true;
        return false;
    }

    private void handleBreak(PlayerCC pcc, BlockBreakEvent event) {
        Point curPoint = MapUtils.getLocationPoint(event.getBlock().getLocation());
        Attack attack = pcc.isOnAttackOn(curPoint);
        if (attack != null) {
            attack.logBlock(event.getBlock(), "BREAK");
            event.getBlock().setType(Material.AIR);
            event.setCancelled(true);
        }
    }

    private void handlePlace(PlayerCC pcc, BlockPlaceEvent event) {
        Point curPoint = MapUtils.getLocationPoint(event.getBlock().getLocation());
        Attack attack = pcc.isOnAttackOn(curPoint);
        if (attack != null)
            attack.logBlock(event.getBlock(), "PLACE");
    }

    private boolean isAllowed(BlockEvent event, PlayerCC playercc) {
        Location locBlock = event.getBlock().getLocation();
        return playercc.isAtHome(locBlock) && !playercc.isOnPointArea(locBlock);
    }
}
