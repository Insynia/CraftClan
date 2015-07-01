package fr.insynia.craftclan.Commands;

import fr.insynia.craftclan.Gameplay.MapState;
import fr.insynia.craftclan.Gameplay.PlayerCC;
import fr.insynia.craftclan.Gameplay.Point;
import fr.insynia.craftclan.Utils.MenuEnchant;
import fr.insynia.craftclan.Utils.UtilCC;
import me.libraryaddict.inventory.ItemBuilder;
import me.libraryaddict.inventory.NamedInventory;
import me.libraryaddict.inventory.Page;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Set;

/**
 * For CraftClan
 * Created by Doc on 19/06/15 at 04:53.
 */
public class MenuCC {
    private final int NB_TUTO = 9;
    private final int NB_MAIN = 9;
    private final int NB_CLAN = 9;
    private final int LINK_TUTO = 0;
    private final int CMD_ATTACK = 1;
    private final int CMD_CAPTURE = 2;
    private final int CMD_FARM = 3;
    private final int CMD_F = 4;
    private final int LINK_CLAN = 5;
    private NamedInventory menu;
    private Player p;
    private PlayerCC pcc;
    private ItemStack[] mainItems = new ItemStack[NB_MAIN];
    private ItemStack[] tutoItems = new ItemStack[NB_TUTO];
    private ItemStack[] clanItems = new ItemStack[NB_CLAN];
    private Page tuto, clan, main;
    private Block tmpBlock;

    public MenuCC(Player p) {
        this.p = p;
        pcc = MapState.getInstance().findPlayer(p.getUniqueId());
        buildMenu();
    }

    private void buildMenu() {
        if (menu == null)
            menu = new NamedInventory("menu", p);

        setTutoPage();
        setFactionPage();
        setMainPage();

        menu.linkPage(mainItems[LINK_TUTO], tuto);
        menu.linkPage(mainItems[LINK_CLAN], clan);
        menu.linkPage(tutoItems[0], main);
        menu.setPage(main);
    }

    public void open() {
        menu.openInventory();
        resetMainPage();
    }

    private void resetMainPage() {
        resetAttackBtn();
        resetCaptureBtn();
        resetFBtn();
        resetFarmBtn();
        this.main = new Page("main", ChatColor.DARK_PURPLE + "Menu");
        menu.setPage(main, mainItems);
    }

    //////// Pages initialization v v v

    private void setMainPage() {
        mainItems[LINK_TUTO] = new ItemBuilder(Material.BOOK_AND_QUILL).setTitle("Tutoriel").addLore(ChatColor.AQUA + "Besoin d'aide ?").build();
        mainItems[CMD_ATTACK] = new ItemBuilder(Material.DIAMOND_SWORD).setTitle("Attaque").addLore(ChatColor.RED + "Veuillez vous approcher d'un point ennemi").build();
        mainItems[CMD_CAPTURE] = new ItemBuilder(Material.BANNER).setTitle("Capture").addLore(ChatColor.RED + "Vous devez être en mode attaque").build();
        mainItems[CMD_FARM] = new ItemBuilder(Material.DIAMOND).setTitle("Zone de farm").addLore(ChatColor.RED + "Vous devez être à proximité du spawn").build();
        mainItems[CMD_F] = new ItemBuilder(Material.SIGN).setTitle("Chat de clan").addLore("Chat général activé").build();
        mainItems[LINK_CLAN] = new ItemBuilder(Material.BOOK_AND_QUILL).setTitle("Commandes de clan").addLore(ChatColor.AQUA + "Commandes en rapport avec les clans").build();

        this.main = new Page("main", ChatColor.DARK_PURPLE + "Menu");
        menu.setPage(main, mainItems);
    }

    private void setTutoPage() {
        Page tuto = new Page("tuto", ChatColor.DARK_PURPLE + "Tutoriel");
        tutoItems[0] = new ItemBuilder(Material.EGG).setTitle("En construction").addLore(ChatColor.AQUA + "Patience ;)").build();

        menu.setPage(tuto, tutoItems);
        this.tuto = tuto;
    }

    private void setFactionPage() {
        Page faction = new Page("clan", ChatColor.DARK_PURPLE + "Clan");
        clanItems[0] = new ItemBuilder(Material.EGG).setTitle("En construction").addLore(ChatColor.AQUA + "Patience ;)").build();

        menu.setPage(faction, clanItems);
        this.clan = faction;
    }

    //////// Pages initialization ^ ^ ^

    //////// Handlers v v v

    public void actionEvent(int slot, String name) {
        switch (ChatColor.stripColor(name)) {
            case "Menu":
                handleMenuAction(slot);
                break;
            case "Clan":
                handleClanAction(slot);
                break;
        }
    }
    private void handleClanAction(int slot) {
//        switch (slot) {
//            case CMD_ATTACK:
//                if (!checkMainWorld()) {
//                    menu.closeInventory();
//                    break;
//                }
//        }
    }

    private void handleMenuAction(int slot) {
        switch (slot) {
            case CMD_ATTACK:
                if (!checkMainWorld()) {
                    menu.closeInventory();
                    break;
                }
                if (tmpBlock != null) {
                    Point targetedPoint = MapState.getInstance().findPoint(tmpBlock.getLocation());

                    if (pcc.isOnAttackOn(targetedPoint) != null) {
                        PlayerCommands.surrender(pcc, targetedPoint);
                    } else {
                        if (!pcc.willAttack(tmpBlock))
                            p.sendMessage(ChatColor.RED + "Vous ne pouvez pas attaquer");
                    }
                }
                menu.closeInventory();
                break;
            case CMD_CAPTURE:
                if (!checkMainWorld()) {
                    menu.closeInventory();
                    break;
                }
                PlayerCommands.cmdCapture(p);
                menu.closeInventory();
                break;
            case CMD_FARM:
                String worldName = p.getLocation().getWorld().getName();
                if (!(worldName.equals(MapState.DEFAULT_WORLD) || worldName.equals(MapState.FARM_WORLD))) {
                    menu.closeInventory();
                    break;
                }
                if (worldName.equals(MapState.DEFAULT_WORLD))
                    PlayerCommands.cmdGoFarm(p);
                else
                    PlayerCommands.cmdStopFarm(p);
                menu.closeInventory();
                break;
            case CMD_F:
                pcc.setTalkingToFaction(!pcc.isTalkingToFaction());
                menu.closeInventory();
                break;
        }
    }

    //////// Handlers ^ ^ ^

    private boolean checkMainWorld() {
        if (p.getLocation().getWorld().getName().equals(MapState.DEFAULT_WORLD))
            return true;
        return false;
    }

    private void resetAttackBtn() {
        String attackMsg;
        PlayerCC pcc = MapState.getInstance().findPlayer(p.getUniqueId());
        Point targetedPoint;
        Material mat = Material.DIAMOND_SWORD;
        String title = "Attaque";
        ChatColor colorValid = null;
        ItemBuilder ib;

        Block targetedBlock = p.getTargetBlock((Set<Material>) null, 30);
        if (!checkMainWorld()) {
            attackMsg = ChatColor.RED + "Impossible dans ce monde !";
        } else {
            if (targetedBlock == null) {
                attackMsg = ChatColor.RED + "Vous devez regarder un des blocs d'un point ennemi !";
            } else {
                targetedPoint = MapState.getInstance().findPoint(targetedBlock.getLocation());
                tmpBlock = targetedBlock;

                if (targetedPoint == null) {
                    Bukkit.getLogger().warning("No point found in open menu ! A block with no point was found");
                    attackMsg = ChatColor.RED + "Vous devez regarder un des blocs d'un point ennemi !";
                } else {
                    if (pcc.isOnAttackOn(targetedPoint) != null) {
                        mat = Material.YELLOW_FLOWER;
                        title = "Capituler";
                        attackMsg = ChatColor.AQUA + "Se rendre";
                    } else {
                        if (targetedPoint.getFactionId() == pcc.getFaction().getId()) {
                            attackMsg = ChatColor.AQUA + "Vous ciblez un point qui vous appartient !";
                        } else {
                            colorValid = (pcc.hasEnough(Material.DIAMOND, targetedPoint.getLevel() * 5) ? ChatColor.GREEN : ChatColor.RED);
                            attackMsg = "L'attaque vous coûtera: " + colorValid + targetedPoint.getLevel() * 5 + " diamants" + ChatColor.RESET;
                        }
                    }
                }
            }
        }


        ib = new ItemBuilder(mat);
        ib.setTitle(title).addLore(attackMsg);

        if (colorValid == ChatColor.RED)
            ib.addLore(colorValid + "Vous n'en avez pas assez !");

        mainItems[CMD_ATTACK] = ib.build();
    }

    private void resetCaptureBtn() {
        PlayerCC pcc = MapState.getInstance().findPlayer(p.getUniqueId());
        Point targetedPoint;
        String msg;

        if (!checkMainWorld()) {
            msg = ChatColor.RED + "Impossible dans ce monde !";
        } else {
            targetedPoint = MapState.getInstance().findPoint(p.getLocation());

            if (pcc.isOnAttackOn(targetedPoint) == null)
                msg = ChatColor.RED + "Vous n'êtes pas sur un point que vous attaquez";
            else {
                if (!pcc.isOnPointArea(p.getLocation()))
                    msg = ChatColor.RED + "Vous devez vous rapprocher du centre du point";
                else
                    msg = ChatColor.GREEN + "Capturer ce point vous prendra: " + UtilCC.formatTime(targetedPoint.getCaptureTime());
            }
        }

        ItemBuilder ib = new ItemBuilder(Material.BANNER);
        ib.setTitle("Capture").addLore(msg);
        mainItems[CMD_CAPTURE] = ib.build();
    }

    private void resetFBtn() {
        String msg = (pcc.isTalkingToFaction() ? "Chat de clan activé" : "Chat général activé");

        ItemBuilder ib = new ItemBuilder(Material.SIGN);
        ib.setTitle(pcc.isTalkingToFaction() ? "Chat général" : "Chat de clan").addLore(msg);
        mainItems[CMD_F] = ib.build();
    }

    private void resetFarmBtn() {
        String msg, worldName, title = "Zone de farm";

        worldName = p.getLocation().getWorld().getName();

        if (worldName.equals(MapState.DEFAULT_WORLD)) {
            if (UtilCC.distanceBasic(Bukkit.getWorld(MapState.DEFAULT_WORLD).getSpawnLocation(), p.getLocation()) >= PlayerCommands.DISTANCE_FARM_CMD)
                msg = ChatColor.RED + "Vous devez être à proximité du spawn";
            else
                msg = ChatColor.AQUA + "Tp en zone de farm";
            title = "Zone de farm";

        } else if (worldName.equals(MapState.FARM_WORLD)) {
            msg = ChatColor.AQUA + "Tp dans le monde normal";
            title = "Retour au monde normal";
        } else
            msg = ChatColor.RED + "Impossible dans ce monde !";

        ItemBuilder ib = new ItemBuilder(Material.DIAMOND);
        ib.setTitle(title).addLore(msg);
        mainItems[CMD_FARM] = ib.build();
    }

    public static ItemStack getMenu() {
        ItemStack ret = new ItemStack(Material.BOOK);
        ItemMeta im;

        ret.addEnchantment(new MenuEnchant(), 0);
        im = ret.getItemMeta();
        im.setDisplayName(ChatColor.DARK_PURPLE + "Menu");
        ret.setItemMeta(im);
        return ret;
    }
}
