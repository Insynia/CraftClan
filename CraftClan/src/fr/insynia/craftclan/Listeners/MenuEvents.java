package fr.insynia.craftclan.Listeners;

import fr.insynia.craftclan.Gameplay.MapState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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
    }
}
