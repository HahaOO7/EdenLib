package at.haha007.edenlib.playerstorage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PerPlayerStorage {
	@NotNull YamlConfiguration loadConfig(@NotNull UUID player);

	void saveConfig(@Nullable YamlConfiguration cfg, @NotNull UUID player);

	@Nullable UUID getUUID(@NotNull String playerName);

	void saveUUID(@NotNull String name, @NotNull UUID uuid);
}
