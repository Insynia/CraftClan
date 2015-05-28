package fr.insynia.craftclan;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * For CraftClan
 * Created by Doc on 28/05/2015 at 02:07.
 */
public class BlockList implements Loadable {
    private List<BlockCC> blocks;

    public BlockList() {
        blocks = new ArrayList<>();
    }

    public void load(ResultSet rs) {
        try {
            while (rs.next()) {
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                String type = rs.getString("block");
                int meta = rs.getInt("meta");
                String action = rs.getString("action");
                Location loc = new Location(Bukkit.getWorld(MapState.DEFAULT_WORLD), x, y, z);
                BlockCC block = new BlockCC(loc, type, meta, action);
                blocks.add(block);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void restore() {
        for (BlockCC b : blocks)
            b.restore();
    }
}
