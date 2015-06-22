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
    private final int NB_TUTO = 1;
    private final int NB_MAIN = 9;
    private final int LINK_TUTO = 0;
    private final int CMD_ATTACK = 1;
    private final int CMD_CAPTURE = 2;
    private final int CMD_FARM = 3;
    private final int CMD_UPGRADE = 4;
    private final int CMD_MEMBERS = 5;
    private final int CMD_FACTIONS = 6;
    private final int CMD_HELP = 7;
    private final int CMD_F = 8;
    private NamedInventory menu;
    private Player p;
    private PlayerCC pcc;
    private ItemStack[] mainItems = new ItemStack[NB_MAIN];
    private ItemStack[] tutoItems = new ItemStack[NB_TUTO];
    private Page tuto, main;
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
        setMainPage();

        menu.linkPage(mainItems[0], tuto);
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
        this.main = new Page("main", ChatColor.DARK_PURPLE + "Menu");
        menu.setPage(main, mainItems);
    }

    private void setMainPage() {
        mainItems[LINK_TUTO] = new ItemBuilder(Material.BOOK_AND_QUILL).setTitle("Tutoriel").addLore(ChatColor.AQUA + "Besoin d'aide ?").build();
        mainItems[CMD_ATTACK] = new ItemBuilder(Material.DIAMOND_SWORD).setTitle("Attaque").addLore(ChatColor.RED + "Veuillez vous approcher d'un point ennemi").build();
        mainItems[CMD_CAPTURE] = new ItemBuilder(Material.BANNER).setTitle("Capture").addLore(ChatColor.RED + "Vous devez être en mode attaque").build();

        this.main = new Page("main", ChatColor.DARK_PURPLE + "Menu");
        menu.setPage(main, mainItems);
    }

    private void setTutoPage() {
        Page tuto = new Page("tuto", ChatColor.GOLD + "Tutoriel");
        tutoItems[0] = new ItemBuilder(Material.EGG).setTitle("En construction").addLore(ChatColor.AQUA + "Patience ;)").build();

        menu.setPage(tuto, tutoItems);
        this.tuto = tuto;
    }

    public void actionEvent(int slot, String name) {
        switch (ChatColor.stripColor(name)) {
            case "Menu":
                handleMenuAction(slot);
                break;
        }
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
        }
    }

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
