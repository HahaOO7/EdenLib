package at.haha007.edenlib;

import at.haha007.edenlib.playerstorage.MySqlPlayerStorage;
import at.haha007.edenlib.playerstorage.PerPlayerStorage;
import at.haha007.edenlib.playerstorage.SqlitePlayerStorage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginTools extends JavaPlugin {
	private static PerPlayerStorage sqlitePlayerStorage;
	private static PerPlayerStorage mySqlPlayerStorage;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		YamlConfiguration cfg = (YamlConfiguration) getConfig();
		sqlitePlayerStorage = new SqlitePlayerStorage(this);
		if (cfg.getBoolean("storage.mySql.use", false))
			mySqlPlayerStorage = new MySqlPlayerStorage(
				cfg.getString("host"),
				cfg.getString("username"),
				cfg.getString("password"),
				cfg.getString("database"),
				cfg.getBoolean("useSSL")
			);
		getLogger().info(" loaded!");
	}

	public static PerPlayerStorage getMySqlPlayerStorage() {
		return mySqlPlayerStorage;
	}

	public static PerPlayerStorage getSqlitePlayerStorage() {
		return sqlitePlayerStorage;
	}
}
