package fr.insynia.craftclan.Base;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import fr.insynia.craftclan.Interfaces.IDable;
import fr.insynia.craftclan.Interfaces.Loadable;
import org.bukkit.Bukkit;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLManager {
    private DataSource ds;
    private static SQLManager instance;

    protected SQLManager() {
        try {
            getMySQLDataSource();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static SQLManager getInstance() {
        if(instance == null) {
            instance = new SQLManager();
        }
        return instance;
    }

    private DataSource getMySQLDataSource() throws SQLException {
        if (ds != null)
            return ds;
        Bukkit.getLogger().info("SQL Service is launching");
        MysqlDataSource mysqlDS;
        mysqlDS = new MysqlDataSource();
        if (System.getenv("DB_URL") == null || System.getenv("DB_USERNAME") == null || System.getenv("DB_PASSWORD") == null)
            throw new SQLException("/!\\ ---------- MYSQL ENV VARS NOT CONFIGURED ---------- /!\\");
        mysqlDS.setURL("jdbc:mysql://" + System.getenv("DB_URL"));
        mysqlDS.setUser(System.getenv("DB_USERNAME"));
        mysqlDS.setPassword(System.getenv("DB_PASSWORD"));
        ds = mysqlDS;
        return ds;
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

    public boolean execUpdate(String sql) {
        Connection con = null;
        Statement stmt = null;
        boolean ret;
        try {
            con = ds.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            ret = true;
        } catch (SQLException e) {
            e.printStackTrace();
            ret = false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }


    public boolean execUpdate(String sql, IDable elem) {
        Connection con = null;
        Statement stmt = null;
        boolean ret;
        try {
            con = ds.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ret = true;
            if (elem != null) {
                try {
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    try {
                        if (generatedKeys.next()) {
                            elem.setId(generatedKeys.getInt(1));
                        }
                    } finally {
                        generatedKeys.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    ret = false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ret = false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}
