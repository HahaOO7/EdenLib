package at.haha007.edenlib.database;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteDatabase extends SqlDatabase {

	private final String path;

	public SqliteDatabase(JavaPlugin plugin, String path) {
		File file = new File(plugin.getDataFolder(), path);
		this.path = file.getAbsolutePath();
	}

	public void connect() throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		connection = DriverManager.getConnection(String.format("jdbc:sqlite:plugins/%s.db", path));
	}

}
