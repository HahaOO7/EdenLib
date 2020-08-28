package at.haha007.edenlib.utils;

import org.bukkit.craftbukkit.libs.org.apache.commons.codec.DecoderException;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Hex;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

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
		Object nmsPlayer = ReflectionUtils.getField(player, "entity");
		if (nmsPlayer == null) return;
		Object nmsPlayerConnection = ReflectionUtils.getField(nmsPlayer, "playerConnection");
		if (nmsPlayerConnection == null) return;
		ReflectionUtils.invokeMethod(nmsPlayerConnection, "sendPacket", nmsPacket);
	}
}
