package fr.insynia.craftclan.Listeners;

import fr.insynia.craftclan.Gameplay.Attack;
import fr.insynia.craftclan.Gameplay.MapState;
import fr.insynia.craftclan.Gameplay.PlayerCC;
import fr.insynia.craftclan.Gameplay.Point;
import fr.insynia.craftclan.Utils.MapUtils;
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
        if (!damager.getLocation().getWorld().getName().equals(MapState.DEFAULT_WORLD)) return false;

        Point targetPoint = MapUtils.getLocationPoint(target.getLocation()); // The point the target is on

        if (damagercc.isAtHome(target.getLocation()) || targetPoint == null) // Check if the target is at damager's home, then it is never restricted
            return false;

        Attack attack = damagercc.hasFailedOn(targetPoint);
        if (attack != null) {
            if (attack.playerFailed(damagercc)) { // If player failed the attack, he is neutralized
                damagercc.sendMessage("Vous avez échoué votre attaque, vous ne pouvez pas vous battre");
                return true;
            }
        }

        return targetPoint.getProtection() != null && targetcc.isAtHome(target.getLocation()); // targetPlayer is at home and his point is protected ?
    }
}
