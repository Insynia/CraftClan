package fr.insynia.craftclan.Gameplay;

import fr.insynia.craftclan.Adapters.BlockList;
import fr.insynia.craftclan.Base.SQLManager;
import fr.insynia.craftclan.Interfaces.IDable;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * For CraftClan
 * Created by Doc on 26/05/2015 at 21:27.
 */
public class Attack implements IDable {

    private int faction_id, target_id, id;
    private Point point;
    private Date start_time, end_time;
    private boolean active, win;
    private List<PlayerCC> attackers;
    private List<PlayerCC> failers;
    private BlockList blockLog;

    public Attack(int faction_id, int target_id, int point_id) {
        this.faction_id = faction_id;
        this.target_id = target_id;
        this.point = MapState.getInstance().findPoint(point_id);
        blockLog = new BlockList();
        attackers = new ArrayList<>();
        failers = new ArrayList<>();
        active = true;
        win = false;
        start_time = new Date();
        if (initAttackers())
            if (!save())
                Bukkit.getLogger().warning("CANNOT CREATE ATTACK: Save failed");
            else
                Bukkit.broadcastMessage("La faction " + MapState.getInstance().findFaction(faction_id).getFancyName() +
                        " à lancer une attaque sur le point \"" + point.getName() + "\" de la faction " +  MapState.getInstance().findFaction(target_id).getFancyName());
    }

    private boolean initAttackers() {
        boolean hasAttackers = false;
        List<PlayerCC> playerCCs = MapState.getInstance().getPlayerCCs();

        for (PlayerCC p : playerCCs) {
            if (p.getFaction().getId() == faction_id) {
                hasAttackers = true;
                addAttacker(p);
            }
        }
        return hasAttackers;
    }

    public boolean playerFailed(PlayerCC p) {
        return failers.contains(p);
    }

    private boolean save() {
        SQLManager sqlm = SQLManager.getInstance();
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String startTime = format.format(start_time);

        boolean ret = sqlm.execUpdate("INSERT INTO attacks(faction_id, target_id, active, point_id, win, start_time) VALUES("
                + faction_id + ", "
                + target_id + ", "
                + (active ? 1 : 0) + ", "
                + point.getId() + ", "
                + (win ? 1 : 0) + ", "
                + "\"" + startTime + "\"" + ");", this);
        if (ret) addToMap();
        return ret;
    }

    private boolean update() {
        SQLManager sqlm = SQLManager.getInstance();
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String endTime = format.format(end_time);

        return sqlm.execUpdate("UPDATE attacks SET " +
                "active = " + (active ? 1 : 0) + ", " +
                "win = " + (win ? 1 : 0) + ", " +
                "end_time = \"" + endTime + "\" " +
                "WHERE id = " + id + ";", this);
    }

    public void addAttacker(PlayerCC pcc) {
        if (!playerFailed(pcc) && !attackers.contains(pcc)) {
            attackers.add(pcc);
            Faction target = MapState.getInstance().findFaction(target_id);
            pcc.sendMessage("Vous êtes en mode attaque contre la faction " + target.getFancyName());
            pcc.sendMessage("Vous devez capturer le point \"" + point.getName() + "\" aux coordonnées :" +
                    " x: " + point.getLocation().getX() +
                    " y: " + point.getLocation().getY() +
                    " z: " + point.getLocation().getZ());
        }
    }

    public void addFailerWithoutPurge(PlayerCC pcc) {
        removeAttacker(pcc);
        failers.add(pcc);
        pcc.sendMessage("Vous ne participez plus à l'attaque du point " + point.getName() + " !");
        if (attackers.size() == 0) {
            endAttack(false);
            Bukkit.getLogger().info("Ending attack in addFailerWithoutPurge");
        }
        else
            for (PlayerCC p : attackers)
                Bukkit.getLogger().info("Player still attacking: " + p.getName() + "\n");
    }

    private void removeAttacker(PlayerCC pcc) {
        Iterator<PlayerCC> itr = attackers.iterator();
        while (itr.hasNext()) {
            PlayerCC p = itr.next();
            if (p.getUUID() == pcc.getUUID()) {
                itr.remove();
            }
        }
    }

    private void addToMap() {
        MapState.getInstance().addAttack(this);
    }

    public void endAttack(boolean isWon) {
        active = false;
        win = isWon;
        end_time = new Date();
        update();
        Faction hostileFaction = MapState.getInstance().findFaction(faction_id);
        Faction targetFaction = MapState.getInstance().findFaction(target_id);
        if (!isWon) {
            Bukkit.broadcastMessage("La faction " + hostileFaction.getFancyName() +
                    " a raté son attaque contre la faction " + targetFaction.getFancyName());
            for (PlayerCC pcc : hostileFaction.getOnlineMembers()) {
                if (pcc.isOnPoint(point))
                    pcc.goToNearestHome();
            }
        } else {
            Bukkit.broadcastMessage("La faction " + hostileFaction.getFancyName() +
                    " a capturé le point \"" + point.getName() + "\" à la faction " + targetFaction.getFancyName());
            for (PlayerCC pcc : targetFaction.getOnlineMembers()) {
                if (pcc.isOnPoint(point))
                    pcc.goToNearestHome();
            }
        }
        restoreBlocks();
    }

    public boolean logBlock(Block block, String action) {
        SQLManager sqlm = SQLManager.getInstance();
        return sqlm.execUpdate("INSERT INTO attack_logs(attack_id, x, y, z, block, action, meta) VALUES("
                + id + ", "
                + block.getLocation().getX() + ", "
                + block.getLocation().getY() + ", "
                + block.getLocation().getZ() + ", "
                + "\"" + block.getType().toString() + "\"" + ", "
                + "\"" + action + "\"" + ", "
                + block.getData() + ");");
    }

    private void restoreBlocks() {
        SQLManager sqlm = SQLManager.getInstance();
        sqlm.fetchQuery("SELECT * FROM attack_logs WHERE attack_id = " + id + " ORDER BY id DESC;", blockLog);
        blockLog.restore();
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getFactionId() {
        return faction_id;
    }

    public Point getPoint() {
        return point;
    }

    public int getId() {
        return id;
    }

    public List<PlayerCC> getFailers() {
        return failers;
    }

    public List<PlayerCC> getAttackers() {
        return attackers;
    }

    public boolean isWon() {
        return win;
    }
}
