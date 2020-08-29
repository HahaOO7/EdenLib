package at.haha007.edenlib.utils;

import org.bukkit.craftbukkit.libs.org.apache.commons.codec.DecoderException;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Hex;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

import static at.haha007.edenlib.utils.ReflectionUtils.*;

public class Utils {
	private static final Random rand = new Random();

	public static String combineStrings(int startIndex, int endIndex, String... strings) {
		StringBuilder string = new StringBuilder();
		for (int i = startIndex; i <= endIndex; i++) {
			string.append(" ").append(strings[i]);
		}

		return string.toString().replaceFirst(" ", "");
	}

	public static String getRandomString(int length) {
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		return rand.ints(leftLimit, rightLimit + 1)
			.limit(length)
			.collect(StringBuilder::new,
				StringBuilder::appendCodePoint,
				StringBuilder::append)
			.toString();
	}

	public static UUID getUUID(String name) {
		try {
			JSONObject json = readJsonFromUrl("https://api.mojang.com/users/profiles/minecraft/" + name);
			String uuidString = json.get("id").toString();
			byte[] data = Hex.decodeHex(uuidString.toCharArray());
			return new UUID(ByteBuffer.wrap(data, 0, 8).getLong(), ByteBuffer.wrap(data, 8, 8).getLong());
		} catch (IOException | ParseException | DecoderException e) {
			return null;
		}
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, ParseException {
		try (InputStream is = new URL(url).openStream()) {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			String jsonText = readAll(rd);
			return (JSONObject) new JSONParser().parse(jsonText);
		}
	}

	public static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}

		return sb.toString();
	}

	public static void sendPacket(Player player, Object nmsPacket) {
		Object nmsPlayer = getField(player, "entity");
		if (nmsPlayer == null) return;
		Object nmsPlayerConnection = getField(nmsPlayer, "playerConnection");
		if (nmsPlayerConnection == null) return;
		invokeMethod(nmsPlayerConnection, "sendPacket", nmsPacket);
	}

	public static void guardianBeam(Player player, Vector from, Vector to, int guardianId, int armorstandId) {

		{
			Object packet = newInstance(getNmsPackage() + ".PacketPlayOutSpawnEntityLiving");
			setField(packet, "a", armorstandId);
			setField(packet, "b", UUID.randomUUID());
			// entity type
			setField(packet, "c", 1);
			// pos
			setField(packet, "d", to.getX());
			setField(packet, "e", to.getY());
			setField(packet, "f", to.getZ());
			sendPacket(player, packet);
		}

		guardianBeamExisting(player, from, to, guardianId, armorstandId);
	}

	public static void guardianBeamExisting(Player player, Vector from, Vector to, int guardianId, int targetId) {
		//shoots an existing target
		{
			Object packet = newInstance(getNmsPackage() + ".PacketPlayOutSpawnEntityLiving");
			setField(packet, "a", guardianId);
			setField(packet, "b", UUID.randomUUID());
			// entity type
			setField(packet, "c", 31);
			// pos
			setField(packet, "d", from.getX());
			setField(packet, "e", from.getY());
			setField(packet, "f", from.getZ());
			sendPacket(player, packet);
		}
		{
			Object dataWatcher = newInstance(
				getNmsPackage() + ".DataWatcher",
				new String[]{getNmsPackage() + ".Entity"},
				new Object[]{null});
			if (dataWatcher == null) return;

			invokeMethod(dataWatcher,
				"register",
				newInstance(getNmsPackage() + "DataWatcherObject", 0,
					getStaticField(getNmsPackage() + ".DataWatcherRegistry", "a")),
				(byte) 0b00100000);

			invokeMethod(dataWatcher,
				"register",
				newInstance(getNmsPackage() + "DataWatcherObject", 4,
					getStaticField(getNmsPackage() + ".DataWatcherRegistry", "i")),
				true);

			invokeMethod(dataWatcher,
				"register",
				newInstance(getNmsPackage() + "DataWatcherObject", 5,
					getStaticField(getNmsPackage() + ".DataWatcherRegistry", "i")),
				true);

			invokeMethod(dataWatcher,
				"register",
				newInstance(getNmsPackage() + "DataWatcherObject", 16,
					getStaticField(getNmsPackage() + ".DataWatcherRegistry", "b")),
				targetId);

			Object packet = newInstance(getNmsPackage() + ".PacketPlayOutEntityMetadata", guardianId, dataWatcher, true);
			sendPacket(player, packet);
		}
	}

	public void destroyFakeEntity(Player player, int entityId) {
		Object packet = newInstance(getNmsPackage() + ".PacketPlayOutEntityDestroy", entityId);
		sendPacket(player, packet);
	}
}
