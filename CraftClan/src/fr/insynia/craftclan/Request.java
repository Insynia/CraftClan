package fr.insynia.craftclan;

/**
 * For CraftClan
 * Created by Doc on 07/06/15 at 18:42.
 */
public class Request implements IDable {
    private int id;
    private int factionId;
    private String playerName;

    public Request(int factionId, String playerName) {
        this.factionId = factionId;
        this.playerName = playerName;
    }

    public Request(int id, int factionId, String playerName) {
        this.id = id;
        this.factionId = factionId;
        this.playerName = playerName;
    }

    public int getId() {
        return id;
    }

    public boolean save() {
        SQLManager sqlm = SQLManager.getInstance();
        boolean ret = sqlm.execUpdate("INSERT INTO requests(id, faction_id, user_name) " +
                "VALUES(" + id + ", " + factionId + ", " + playerName + ");", this);
        if (ret)
            this.addToMap();
        return ret;
    }

    private void addToMap() {
        MapState.getInstance().addRequest(this);
    }

    public int getFactionId() {
        return factionId;
    }

    public void deleteDb() {
        SQLManager sqlm = SQLManager.getInstance();
        sqlm.execUpdate("DELETE FROM requests WHERE id = " + id + ";");
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }
}
