package at.haha007.edenlib;

import at.haha007.edenlib.playerstorage.MySqlPlayerStorage;
import at.haha007.edenlib.playerstorage.PerPlayerStorage;
import at.haha007.edenlib.playerstorage.SqlitePlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PluginTools extends JavaPlugin implements @NotNull Listener {
	private static PerPlayerStorage sqlitePlayerStorage;
	private static PerPlayerStorage mySqlPlayerStorage;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		YamlConfiguration cfg = (YamlConfiguration) getConfig();
		sqlitePlayerStorage = new SqlitePlayerStorage(this);
		if (cfg.getBoolean("storage.mySql.use", false))
			mySqlPlayerStorage = new MySqlPlayerStorage(
				cfg.getString("storage.mySql.host"),
				cfg.getString("storage.mySql.username"),
				cfg.getString("storage.mySql.password"),
				cfg.getString("storage.mySql.database"),
				cfg.getBoolean("storage.mySql.useSSL")
			);
		getLogger().info(" loaded!");

		getServer().getPluginManager().registerEvents(this, this);
	}

	public static PerPlayerStorage getMySqlPlayerStorage() {
		return mySqlPlayerStorage;
	}

	public static PerPlayerStorage getSqlitePlayerStorage() {
		return sqlitePlayerStorage;
	}

	@EventHandler
	void onPlayerLogin(PlayerLoginEvent event) {
		String name = event.getPlayer().getName();
		UUID uuid = event.getPlayer().getUniqueId();
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> sqlitePlayerStorage.saveUUID(name, uuid));
		if (mySqlPlayerStorage != null)
			Bukkit.getScheduler().runTaskAsynchronously(this, () -> mySqlPlayerStorage.saveUUID(name, uuid));
	}
}
