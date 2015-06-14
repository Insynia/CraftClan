package fr.insynia.craftclan.Gameplay;

import fr.insynia.craftclan.Adapters.PlayerCCList;
import fr.insynia.craftclan.Base.SQLManager;
import fr.insynia.craftclan.Interfaces.IDable;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * For CraftClan
 * Created by Doc on 11/05/2015 at 19:54.
 */
public class Faction implements IDable {
    private String name;
    private String color;
    private String status;
    private int factionId;
    private String leaderName;
    private int level;

    public final static String BASE_FACTION = "Newbie";
    public final static String BASE_NEUTRAL = "Neutre";

    public Faction(int factionId, String name, String color, int level, String status, String leaderName) {
        this.factionId = factionId;
        this.name = name;
        this.color = color;
        this.level = level;
        this.status = status;
        if (leaderName == null)
            leaderName = "";
        this.leaderName = leaderName;
    }

    public String toString() {
        return "name: " + name + " color: " + color + " level: " + level;
    }

    public boolean save() {
        boolean colorOk = false;
        SQLManager sqlm = SQLManager.getInstance();
        for (ChatColor chatColor : ChatColor.values()) {
            if (chatColor.name().equals(color)) {
                colorOk = true;
                break;
            }
        }
        if (!colorOk) return false;
        boolean ret = sqlm.execUpdate("INSERT INTO factions(name, color, level, status, leader_name) " +
                "VALUES(\"" + name + "\", \"" + color + "\", " + level + ", \"" + status + "\", \"" + leaderName + "\" );", this);
        if (ret)
            this.addToMap();
        return ret;
    }

    public void broadcastToMembers(String msg) {
        List<PlayerCC> members = getOnlineMembers();

        msg = "[" + ChatColor.BOLD + ChatColor.GOLD + name + ChatColor.RESET + "]: " + msg;

        for (PlayerCC member : members)
            member.sendMessage(msg);
    }

    private void addToMap() {
        MapState.getInstance().addFaction(this);
    }

    public String getName() {
        return name;
    }

    public List<PlayerCC> getOnlineMembers() {
        List<PlayerCC> members = new ArrayList<>();

        for (PlayerCC pcc : MapState.getInstance().getPlayerCCs()) {
            if (pcc.getFaction().getId() == factionId)
                members.add(pcc);
        }
        return members;
    }

    public List<PlayerCC> getMembers() {
        PlayerCCList playerList = new PlayerCCList();
        SQLManager sqlm = SQLManager.getInstance();
        sqlm.fetchQuery("SELECT * from users WHERE faction_id = " + factionId + ";", playerList);

        return playerList.getPlayers();
    }

    public int getId() {
        return factionId;
    }

    public String getColor() {
        return color;
    }

    @Override
    public void setId(int id) {
        this.factionId = id;
    }

    public String getFancyName() {
        return ChatColor.WHITE + "[" + ChatColor.valueOf(getColor()) + getName() + ChatColor.WHITE + "]";
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void requestedBy(PlayerCC p) {
        Request request = new Request(factionId, p.getName());
        request.save();
        broadcastToMembers("Le joueur " + p.getName() + " veut rejoindre votre faction !");
    }

    public boolean update() {
        SQLManager sqlm = SQLManager.getInstance();
        return sqlm.execUpdate("UPDATE factions SET " +
                "name = \"" + name + "\", " +
                "color = \"" + color + "\", " +
                "status = \"" + status + "\", " +
                "leader_name = \"" + leaderName + "\", " +
                "level = " + level + " " +
                "WHERE id = " + factionId + ";", this);
    }

    public boolean neutralize() {
        List<Point> factionPoints = MapState.getInstance().getFactionPoints(factionId);
        if (factionPoints == null) return false;
        for (Point p : factionPoints) {
            p.addToFaction(BASE_NEUTRAL);
            p.setPointLevel(1);
        }
        return true;
    }

    public String listMembers() {
        String members = "";
        List<PlayerCC> memberList = getMembers();

        if (memberList.size() == 0) return members;
        for (PlayerCC p : memberList) {
            members += (p.isLeader() ? ChatColor.BLUE : ChatColor.RESET) + p.getName() + ChatColor.RESET + (p.equals(memberList.get(memberList.size() - 1)) ? "." : ", ");
        }
        return members;
    }
}
