package me.chrommob.cheques;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {
    private final FileConfiguration pluginConfig = Cheques.getInstance().getConfig();
    private HikariDataSource hikari;

    public DatabaseManager() {
        Cheques.getInstance().getLogger().info("DatabaseManager initialized!");
        setupPool();
        createTable();
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(
                "jdbc:mysql://" +
                        pluginConfig.getString("mysql.ip") +
                        ":" +
                        pluginConfig.getString("mysql.port") +
                        "/" +
                        pluginConfig.getString("mysql.database") +
                        "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(pluginConfig.getString("mysql.username"));
        config.setPassword(pluginConfig.getString("mysql.password"));
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(100);
        config.setConnectionTimeout(2000);
        config.setLeakDetectionThreshold(60000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        // config.setConnectionTestQuery(testQuery);
        hikari = new HikariDataSource(config);
    }

    private void close(Connection conn, PreparedStatement ps, ResultSet res) {
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        if (ps != null)
            try {
                ps.close();
            } catch (SQLException ignored) {
            }
        if (res != null)
            try {
                res.close();
            } catch (SQLException ignored) {
            }
    }

    private void createTable() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS cheques"
                    + "(id INT NOT NULL AUTO_INCREMENT,"
                    + "uuid VARCHAR(36) NOT NULL,"
                    + "amount DOUBLE NOT NULL,"
                    + "claimedBy VARCHAR(36) NULL,"
                    + "PRIMARY KEY (id))");

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, null);
        }
    }

    public void addCheque(String uuid, double amount) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("INSERT INTO cheques (uuid, amount, claimedBy) VALUES (?, ?, ?)");
            ps.setString(1, uuid);
            ps.setDouble(2, amount);
            ps.setString(3, null);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, null);
        }
    }

    public boolean isChequeValid(String uuid, Double amount) {
        boolean valid = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("SELECT * FROM cheques WHERE uuid = ? AND amount = ? AND claimedBy IS NULL");
            ps.setString(1, uuid);
            ps.setDouble(2, amount);
            res = ps.executeQuery();
            valid = res.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, res);
        }
        return valid;
    }

    public void setClaimed(String uuid, Player player) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("UPDATE cheques SET claimedBy = ? WHERE uuid = ?");
            ps.setString(1, player.getDisplayName());
            ps.setString(2, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, null);
        }
    }

    public String getChequeClaimer(String uuid) {
        String claimedBy = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("SELECT * FROM cheques WHERE uuid = ?");
            ps.setString(1, uuid);
            res = ps.executeQuery();
            if (res.next()) {
                claimedBy = res.getString("claimedBy");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, res);
        }
        return claimedBy;
    }

    public int getTotalAmountClaimed() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        int total = 0;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("SELECT amount FROM cheques WHERE claimedBy IS NOT NULL");
            res = ps.executeQuery();
            while (res.next()) {
                total += res.getDouble("amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public int getTotalAmountNotClaimed() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        int total = 0;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("SELECT amount FROM cheques WHERE claimedBy IS NULL");
            res = ps.executeQuery();
            while (res.next()) {
                total += res.getDouble("amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public int getTotalAmountDistributed() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        int total = 0;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("SELECT amount FROM cheques");
            res = ps.executeQuery();
            while (res.next()) {
                total += res.getDouble("amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
}
