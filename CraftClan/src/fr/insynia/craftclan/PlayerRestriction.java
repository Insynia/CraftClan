package fr.insynia.craftclan;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerRestriction implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        event.getPlayer().sendMessage(MapState.getInstance().stringPoints());
        //event.setCancelled(true);
    }
}
