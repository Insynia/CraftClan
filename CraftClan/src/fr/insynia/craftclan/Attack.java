package fr.insynia.craftclan;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * For CraftClan
 * Created by Doc on 26/05/2015 at 21:27.
 */
public class Attack implements IDable {

    private int faction_id, target_id, id;
    private String point_name;
    private Date start_time, end_time;
    private boolean active, win;
    private List<PlayerCC> attackers;
    private List<PlayerCC> failers;

    public Attack(int faction_id, int target_id, String point_name) {
        this.faction_id = faction_id;
        this.target_id = target_id;
        this.point_name = point_name;
        attackers = new ArrayList<>();
        failers = new ArrayList<>();
        active = true;
        win = false;
        start_time = new Date();
        if (initAttackers())
            if (!save())
                Bukkit.getLogger().warning("CANNOT CREATE ATTACK: Save failed");
    }

    private boolean initAttackers() {
        boolean hasAttackers = false;
        List<PlayerCC> playerCCs = MapState.getInstance().getPlayerCCs();

        for (PlayerCC p : playerCCs)
            if (p.getFaction().getId() == faction_id) {
                hasAttackers = true;
                addAttacker(p);
            }

        return hasAttackers;
    }

    private boolean playerFailed(PlayerCC p) {
        return failers.contains(p);
    }

    private boolean save() {
        SQLManager sqlm = SQLManager.getInstance();
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String startTime = format.format(start_time);
        String endTime = (end_time != null ? format.format(end_time) : "NULL");


        boolean ret = sqlm.execUpdate("INSERT INTO attacks(faction_id, target_id, active, point_name, win, start_time) VALUES("
                + faction_id + ", "
                + target_id + ", "
                + (active ? 1 : 0) + ", \""
                + point_name + "\", "
                + (win ? 1 : 0) + ", "
                + "\"" + startTime + "\"" + ");", this); // UPDATE ENDTIME IN ENDATTACK
        if (ret) addToMap();
        return ret;
    }

    public void addAttacker(PlayerCC pcc) {
        if (!playerFailed(pcc)) {
            attackers.add(pcc);
            Faction target = MapState.getInstance().findFaction(faction_id);
            Point tarpoint = MapState.getInstance().findPoint(point_name);
            Bukkit.getPlayer(pcc.getUUID()).sendMessage("Vous êtes en mode attaque ! Votre faction contre la faction " + target.getName());
            Bukkit.getPlayer(pcc.getUUID()).sendMessage("Vous devez capturer le point \"" + point_name + "\", il se situe à ces coordonnées :" +
                            "x: " + tarpoint.getLocation().getX() +
                            "y: " + tarpoint.getLocation().getY() +
                            "z: " + tarpoint.getLocation().getZ());
        }
    }

    public void addFailer(PlayerCC pcc) {
        attackers.remove(pcc);
        failers.add(pcc);
        Bukkit.getPlayer(pcc.getUUID()).sendMessage("Looser, votre attaque a échoué !");
    }

    private void addToMap() {
        MapState.getInstance().addAttack(this);
    }

    private void removeFromMap() {
        MapState.getInstance().removeAttack(this);
    }

    private void endAttack() { // HERE BITCH
        active = false;
        end_time = new Date();
        save();
        removeFromMap();
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

    private void restoreAttack() {
        // ---------------------------------
        // TODO !!!!!
        // ---------------------------------
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getFactionId() {
        return faction_id;
    }

    public String getPointName() {
        return point_name;
    }
}
