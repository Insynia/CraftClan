package fr.insynia.craftclan.Listeners;

import fr.insynia.craftclan.Commands.MenuCC;
import fr.insynia.craftclan.Gameplay.MapState;
import fr.insynia.craftclan.Gameplay.PlayerCC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

/**
 * For CraftClan
 * Created by Doc on 19/06/15 at 21:37.
 */
public class MenuEvents implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        MapState.getInstance().menuAction(player, event.getSlot(), event.getInventory().getName());
        Bukkit.getLogger().info("Inventory clicked: " + inventory.getName() + " on slot: " + event.getSlot()); // DEBUG to remove
        if (event.getCurrentItem().equals(MenuCC.getMenu())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().equals(MenuCC.getMenu()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent event) {
        if (event.getOldCursor().equals(MenuCC.getMenu()))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerUse(PlayerInteractEvent event){
        Player p = event.getPlayer();

        if(event.getItem().equals(MenuCC.getMenu())){
            PlayerCC pcc = MapState.getInstance().findPlayer(p.getUniqueId());
            if (pcc != null)
                pcc.getMenu().open();
            event.setCancelled(true);
        }
    }
}
