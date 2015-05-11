package fr.insynia.craftclan;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLManager {
    private DataSource ds;

    public SQLManager() {
        try {
            ds = getMySQLDataSource();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static DataSource getMySQLDataSource() throws SQLException {
        MysqlDataSource mysqlDS;
        mysqlDS = new MysqlDataSource();
        if (System.getenv("DB_URL") == null || System.getenv("DB_USERNAME") == null || System.getenv("DB_PASSWORD") == null)
            throw new SQLException("/!\\ ---------- MYSQL ENV VARS NOT CONFIGURED ---------- /!\\");
        mysqlDS.setURL("jdbc:mysql://" + System.getenv("DB_URL"));
        mysqlDS.setUser(System.getenv("DB_USERNAME"));
        mysqlDS.setPassword(System.getenv("DB_PASSWORD"));
        return mysqlDS;
    }

    public void fetchQuery(String sql, Loadable elem) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = ds.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            elem.load(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void execUpdate(String sql) {
        Connection con = null;
        Statement stmt = null;
        try {
            con = ds.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
