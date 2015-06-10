package fr.insynia.craftclan;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * For CraftClan
 * Created by Doc on 27/05/2015 at 18:52.
 */
public class PvPRestriction implements Listener {

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player && e.getDamager() instanceof Player)) return;
        e.setCancelled(checkPvPRestricted((Player) e.getDamager(), (Player) e.getEntity()));
    }

    private boolean checkPvPRestricted(Player damager, Player target) {
        PlayerCC damagercc = MapState.getInstance().findPlayer(damager.getUniqueId());
        PlayerCC targetcc = MapState.getInstance().findPlayer(damager.getUniqueId());
        Point targetPoint = MapUtils.getLocationPoint(target.getLocation()); // The point the target is on

        if (damagercc.isAtHome(target.getLocation()) || targetPoint == null) // Check if the target is at damager's home, then it is never restricted
            return false;

        Attack attack = damagercc.isOnAttackOn(targetPoint);
        if (attack != null)
            if (attack.playerFailed(damagercc)) // If player failed the attack, he is neutralized
                return true;

        return targetPoint.getProtection() != null && targetcc.isAtHome(target.getLocation()); // targetPlayer is at home and his point is protected ?
    }
}
