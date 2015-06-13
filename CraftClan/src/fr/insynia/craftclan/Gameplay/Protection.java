package fr.insynia.craftclan.Gameplay;

import fr.insynia.craftclan.Base.SQLManager;
import fr.insynia.craftclan.Interfaces.IDable;

import java.util.Date;

/**
 * For CraftClan
 * Created by Doc on 10/06/15 at 19:14.
 */
public class Protection implements IDable {

    public static final int BASE_AMOUNT = 100;
    public static final int HOUR_COEF = 1;
    public static final int WEEK_COEF = 50;
    public static final int DAY_COEF = 10;

    private int id;
    private final int pointId;
    private final Date begin;
    private final Date end;
    private final String userName;

    public Protection(int id, int pointId, Date begin, Date end, String userName) {
        this.id = id;
        this.pointId = pointId;
        this.begin = begin;
        this.end = end;
        this.userName = userName;
    }

    public Protection(int pointId, Date begin, Date end, String userName) {
        this.pointId = pointId;
        this.begin = begin;
        this.end = end;
        this.userName = userName;
    }

    public boolean save() {
        SQLManager sqlm = SQLManager.getInstance();

        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String beginTime = format.format(begin);
        String endTime = format.format(end);

        boolean ret = sqlm.execUpdate("INSERT INTO protections(point_id, begin, end, user_name) " +
                "VALUES(\"" + pointId + "\", \"" + beginTime + "\", \"" + endTime + "\", \"" + userName + "\" );", this);
        if (ret)
            this.addToMap();
        return ret;
    }

    public void addToMap() {
        MapState.getInstance().addProtection(this);
    }

    public int getId() {
        return id;
    }

    public int getPointId() {
        return pointId;
    }

    public Date getBegin() {
        return begin;
    }

    public Date getEnd() {
        return end;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
}
