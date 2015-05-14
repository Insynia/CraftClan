package fr.insynia.craftclan;

import java.sql.ResultSet;

/**
 * For CraftClan
 * Created by Doc on 11/05/2015 at 19:54.
 */
public interface Loadable {
    void load(ResultSet rs);
}
