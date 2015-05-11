package fr.insynia.craftclan;

import java.sql.ResultSet;

/**
 * Created by Doc on 11/05/2015.
 */
public interface Loadable {
    void load(ResultSet rs);
}
