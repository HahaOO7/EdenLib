package at.haha007.edenlib.playerstorage;

import at.haha007.edenlib.database.SqlDatabase;
import at.haha007.edenlib.database.SqliteDatabase;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SqlitePlayerStorage implements PerPlayerStorage {
	private final SqlDatabase database;

	public SqlitePlayerStorage(JavaPlugin plugin) {
		database = new SqliteDatabase(plugin, "players");
		try {
			database.prepareStatement("CREATE TABLE IF NOT EXISTS `UuidNames` (UUID VARCHAR(36), Name VARCHAR(16), PRIMARY KEY(Name, UUID))").executeUpdate();
			database.prepareStatement("CREATE TABLE IF NOT EXISTS `PerPlayerStorage` (UUID VARCHAR(36), Config BLOB, PRIMARY KEY(UUID))").executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public @NotNull YamlConfiguration loadConfig(@NotNull UUID player) {
		try {
			PreparedStatement statement = database.prepareStatement("SELECT `Config` FROM `PerPlayerStorage` WHERE UUID = ?");
			statement.setString(1, player.toString());
			ResultSet result = statement.executeQuery();
			if (result.next())
				return YamlConfiguration.loadConfiguration(new StringReader(decompress(result.getBlob(1).getBinaryStream())));
		} catch (SQLException ignore) {
		}
		return new YamlConfiguration();
	}

	@Override
	public void saveConfig(@Nullable YamlConfiguration cfg, @NotNull UUID player) {
		if (cfg == null) cfg = new YamlConfiguration();
		try {
			PreparedStatement statement = database.prepareStatement("REPLACE INTO `PerPlayerStorage` VALUES(?, ?)");
			statement.setString(1, player.toString());
			statement.setBlob(2, new ByteArrayInputStream(compress(cfg.saveToString())));
			statement.executeUpdate();
		} catch (SQLException ignore) {
		}
	}

	@Override
	public @Nullable UUID getUUID(@NotNull String playerName) {
		try {
			PreparedStatement statement = database.prepareStatement("SELECT `UUID` FROM `UuidNames` WHERE Name = ?");
			statement.setString(1, playerName);
			ResultSet result = statement.executeQuery();
			if (result.next())
				return UUID.fromString(result.getString(1));
		} catch (SQLException ignore) {
		}
		return null;
	}

	@Override
	public void saveUUID(@NotNull String name, @NotNull UUID uuid) {
		try {
			PreparedStatement statement = database.prepareStatement("REPLACE INTO `UuidNames` VALUES(?, ?)");
			statement.setString(1, uuid.toString());
			statement.setString(2, name);
			statement.executeUpdate();
		} catch (SQLException ignore) {
		}
	}

	private byte[] compress(String string) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
			byte[] data = string.getBytes(StandardCharsets.UTF_8);
			gzipOutputStream.write(data);
			gzipOutputStream.close();
			return byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String decompress(InputStream data) {
		StringBuilder sb = new StringBuilder();
		try {
			new BufferedReader(new InputStreamReader(new GZIPInputStream(data))).lines().forEachOrdered(sb::append);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
