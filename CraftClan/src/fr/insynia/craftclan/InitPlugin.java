package fr.insynia.craftclan;

public class InitPlugin {
	public void createPoints() {
        SQLManager sqlm = new SQLManager();
        String query = "SELECT * FROM points;";
        sqlm.fetchQuery(query, new PointList());
	}
}
