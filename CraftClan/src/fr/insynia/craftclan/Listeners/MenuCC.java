package fr.insynia.craftclan.Listeners;

import me.libraryaddict.inventory.ItemBuilder;
import me.libraryaddict.inventory.NamedInventory;
import me.libraryaddict.inventory.Page;
import me.libraryaddict.inventory.events.PagesClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * For CraftClan
 * Created by Doc on 19/06/15 at 04:53.
 */
public class MenuCC implements Listener {
    private NamedInventory menu;
    private final Player p;

    public MenuCC(Player p) {
        this.p = p;
        buildMenu();
    }

    private void buildMenu() {
        ItemStack[] mainItems = new ItemStack[1];
        ItemStack[] tutoItems = new ItemStack[1];

        menu = new NamedInventory("menu", p);

        Page tuto = setTutoPage(tutoItems);
        Page main = setMainPage(mainItems);

        menu.linkPage(mainItems[0], tuto);
        menu.linkPage(tutoItems[0], main);
    }

    public void open() {
        menu.setPage("main");
        menu.openInventory();
    }

    @EventHandler(ignoreCancelled=true)
    public void onInventoryClick(PagesClickEvent ev){
        Bukkit.getLogger().info(ev.getSlot() + "nth slot");
    }

    private Page setMainPage(ItemStack[] mainItems) {
        mainItems[0] = new ItemBuilder(Material.BOOK_AND_QUILL).setTitle("Tutoriel").addLore(ChatColor.AQUA + "Besoin d'aide ?").build();

        Page mainPage = new Page("main", ChatColor.GOLD + "Menu") {

        };
        menu.setPage(mainPage, mainItems);

        return mainPage;
    }

    private Page setTutoPage(ItemStack[] tutoItems) {
        Page tuto = new Page("tuto", ChatColor.GOLD + "Tutoriel");
        tutoItems[0] = new ItemBuilder(Material.EGG).setTitle("En construction").addLore(ChatColor.AQUA + "Patience ;)").build();

        menu.setPage(tuto, tutoItems);
        return tuto;
    }
}
