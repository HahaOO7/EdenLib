package at.haha007.edenlib.database;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

abstract public class SqlDatabase {
    @NotNull
    public static SqlDatabase fromYamlSection(ConfigurationSection cfg, JavaPlugin plugin) {
        if (!cfg.getBoolean("use", false)) return new SqliteDatabase(plugin, "data.db");
        return new MySqlDatabase(cfg.getString("host"), cfg.getString("username"), cfg.getString("password"), cfg.getString("database"), cfg.getString("datasource"), cfg.getBoolean("useSSL"));
    }

    protected Connection connection;

    public PreparedStatement prepareStatement(String statement) throws SQLException {
        return connection.prepareStatement(statement);
    }

    abstract public void connect() throws SQLException;

    public void disconnect() throws SQLException {
        if (isConnected()) connection.close();
    }

    public boolean isConnected() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    public Connection getConnection() {
        return connection;
    }
}
