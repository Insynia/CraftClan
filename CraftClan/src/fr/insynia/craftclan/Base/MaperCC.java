package fr.insynia.craftclan.Base;

import fr.insynia.craftclan.Gameplay.Faction;
import fr.insynia.craftclan.Gameplay.MapState;
import fr.insynia.craftclan.Gameplay.Point;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerSet;

/**
 * For CraftClan
 * Created by Doc on 15/06/15 at 01:42.
 */
public class MaperCC {
    private static final String POINTS_MARKER_ID = "points";
    private static final String LABEL_POINTS_MARKER = "Points";
    private static DynmapAPI api;

    private static void initMarker() {
        Plugin dynmap = Bukkit.getPluginManager().getPlugin("dynmap");
        if (dynmap == null) {
            Bukkit.getLogger().warning("Cannot find dynmap!");
            return;
        }
        api = (DynmapAPI) dynmap;

        MarkerSet pointSet = api.getMarkerAPI().getMarkerSet(POINTS_MARKER_ID);

        if (pointSet == null) {
            api.getMarkerAPI().createMarkerSet(POINTS_MARKER_ID, LABEL_POINTS_MARKER, null, true);
        }
    }

    public static void generateAreas() {
        initMarker();
        MarkerSet pointSet = api.getMarkerAPI().getMarkerSet(POINTS_MARKER_ID);
        for (AreaMarker marker : pointSet.getAreaMarkers())
            marker.deleteMarker();
        pointSet.setHideByDefault(false);
        for (Point point : MapState.getInstance().getPoints()) {
            double[] xVals = { point.getLocation().getX() - point.getRadius(), point.getLocation().getX() + point.getRadius() };
            double[] zVals = { point.getLocation().getZ() - point.getRadius(), point.getLocation().getZ() + point.getRadius() };

            AreaMarker am = pointSet.createAreaMarker("point_" + point.getId(), point.getName(), false, MapState.DEFAULT_WORLD, xVals, zVals, false);
            if (am != null) {
                Faction f = MapState.getInstance().findFaction(point.getFactionId());
                am.setLabel(point.getName());
                if (f == null) {
                    am.setFillStyle(0.3, 0xAAAAAA);
                    am.setLineStyle(1, 0.8, 0xAAAAAA);
                    am.setDescription("Nom du point: " + point.getName() + "<br>Faction: inconnue"
                            + "<br>Taille: " + point.getRadius() + "x" + point.getRadius());
                } else {
                    am.setLineStyle(1, 0.8, f.getColorHex());
                    am.setFillStyle(0.1, f.getColorHex());
                    am.setDescription("Nom du point: " + point.getName() + "<br>Faction: " + f.getName()
                            + "<br>Taille: " + point.getRadius() + "x" + point.getRadius());
                }
                am.setRangeY(point.getLocation().getY(), point.getLocation().getY() + 1);
            } else {
                Bukkit.getLogger().info("Error during point area generation for Dynmap");
            }
        }
    }

    public static void updatePointArea(Point point) {
        initMarker();
        MarkerSet pointSet = api.getMarkerAPI().getMarkerSet(POINTS_MARKER_ID);
        AreaMarker am = null;

        for (AreaMarker marker : pointSet.getAreaMarkers())
            if (marker.getMarkerID().equals("point_" + point.getId()))
                am = marker;

        if (am == null) {
            Bukkit.getLogger().warning("Tried to change a non existent area");
            return;
        }

        Faction f = MapState.getInstance().findFaction(point.getFactionId());
        am.setLabel(point.getName());
        if (f == null) {
            am.setFillStyle(0.3, 0xAAAAAA);
            am.setLineStyle(1, 0.8, 0xAAAAAA);
            am.setDescription("Nom du point: " + point.getName() + "<br>Faction: inconnue"
                    + "<br>Taille: " + point.getRadius() + "x" + point.getRadius());
        } else {
            am.setLineStyle(1, 0.8, f.getColorHex());
            am.setFillStyle(0.1, f.getColorHex());
            am.setDescription("Nom du point: " + point.getName() + "<br>Faction: " + f.getName()
                    + "<br>Taille: " + point.getRadius() + "x" + point.getRadius());
        }
        am.setRangeY(point.getLocation().getY(), point.getLocation().getY() + 1);
    }
}
