package at.haha007.edenlib.utils;

import net.minecraft.server.v1_16_R2.ChatComponentText;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static at.haha007.edenlib.utils.ReflectionUtils.*;

public class Utils {
	private static final Random rand = new Random();
	private static final Class<?> packetPlayOutEntityMetadataClass;
	private static final Class<?> packetPlayOutSpawnEntityLivingClass;
	private static final Class<?> dataWatcherClass;
	private static final Class<?> dataWatcherRegistryClass;
	private static final Class<?> entityClass;
	private static final Class<?> dataWatcherObjectClass;
	private static final Class<?> dataWatcherSerializerClass;
	private static final Class<?> packetPlayOutEntityDestroyClass;
	private static final Class<?> packetPlayOutSpawnEntityClass;
	private static final Class<?> packetPlayOutScoreboardTeamClass;
	private static final Class<?> entityTypesClass;
	private static final Class<?> hexClass;
	private static final Class<?> blockClass;
	private static final Class<?> iBlockDataClass;

	static {
		hexClass = getClassByName("org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Hex");
		packetPlayOutScoreboardTeamClass = getNmsClass("PacketPlayOutScoreboardTeam");
		packetPlayOutEntityDestroyClass = getNmsClass("PacketPlayOutEntityDestroy");
		dataWatcherSerializerClass = getNmsClass("DataWatcherSerializer");
		dataWatcherObjectClass = getNmsClass("DataWatcherObject");
		entityClass = getNmsClass("Entity");
		dataWatcherRegistryClass = getNmsClass("DataWatcherRegistry");
		dataWatcherClass = getNmsClass("DataWatcher");
		packetPlayOutEntityMetadataClass = getNmsClass("PacketPlayOutEntityMetadata");
		packetPlayOutSpawnEntityLivingClass = getNmsClass("PacketPlayOutSpawnEntityLiving");
		packetPlayOutSpawnEntityClass = getNmsClass("PacketPlayOutSpawnEntity");
		entityTypesClass = getNmsClass("EntityTypes");
		blockClass = getNmsClass("Block");
		iBlockDataClass = getNmsClass("IBlockData");
		assert blockClass != null;
		assert iBlockDataClass != null;
		assert entityTypesClass != null;
		assert packetPlayOutEntityMetadataClass != null;
		assert packetPlayOutEntityDestroyClass != null;
		assert dataWatcherSerializerClass != null;
		assert packetPlayOutSpawnEntityLivingClass != null;
		assert dataWatcherClass != null;
		assert dataWatcherObjectClass != null;
		assert entityClass != null;
		assert dataWatcherRegistryClass != null;
		assert packetPlayOutSpawnEntityClass != null;
		assert hexClass != null;
		assert packetPlayOutScoreboardTeamClass != null;
	}

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
			byte[] data = (byte[]) invokeStaticMethod(hexClass,
				"decodeHex",
				new Class[]{char[].class},
				new Object[]{uuidString.toCharArray()});
			return new UUID(ByteBuffer.wrap(data, 0, 8).getLong(), ByteBuffer.wrap(data, 8, 8).getLong());
		} catch (IOException | ParseException e) {
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
		Object nmsPlayer = invokeMethod(player, "getHandle", new Class[0], new Object[0]);
		if (nmsPlayer == null) return;
		Object nmsPlayerConnection = getField(nmsPlayer, "playerConnection");
		if (nmsPlayerConnection == null) return;
		invokeMethod(nmsPlayerConnection, "sendPacket", new Class[]{getNmsClass("Packet")}, new Object[]{nmsPacket});
	}

	public static void displayFakeBlock(Player player, Vector location, Object block, int entityId, UUID entityUUID) {
		//block is in nms Blocks
		Object blockData = invokeMethod(blockClass, block, "getBlockData", new Class[0], new Object[0]);
		Object packet = newInstance(packetPlayOutSpawnEntityClass, new Class[]{}, new Object[]{});
		if (packet == null) return;
		setField(packet, "a", entityId);
		setField(packet, "b", entityUUID);
		// pos
		setField(packet, "c", location.getX());
		setField(packet, "d", location.getY());
		setField(packet, "e", location.getZ());
		// entity type
		setField(packet, "k", getStaticField(entityTypesClass, "FALLING_BLOCK"));
		setField(packet, "l", invokeStaticMethod(blockClass, "getCombinedId", new Class[]{iBlockDataClass}, new Object[]{blockData}));
		sendPacket(player, packet);
	}

	public static void addGlow(Player player, int entityId) {
		Object dataWatcher = newInstance(dataWatcherClass, new Class[]{entityClass}, new Object[]{null});
		registerDataWatcher(dataWatcher, 0, "a", (byte) 0b01000000);
		registerDataWatcher(dataWatcher, 5, "i", true);
		Object packet = newInstance(packetPlayOutEntityMetadataClass, new Class[]{int.class, dataWatcherClass, boolean.class}, new Object[]{entityId, dataWatcher, true});
		sendPacket(player, packet);
	}

	public static void colorClow(Player player, Object enumChatFormatColor, UUID... entityUUID) {
		Object packetRed = newInstance(packetPlayOutScoreboardTeamClass, new Class[0], new Object[0]);
		setField(packetRed, "a", getRandomString(16)); // name
		setField(packetRed, "b", new ChatComponentText("")); // display name
		setField(packetRed, "c", new ChatComponentText("PRE ")); // prefix
		setField(packetRed, "d", new ChatComponentText(" SUF")); // suffix
		setField(packetRed, "e", "never"); // name tag visible
		setField(packetRed, "f", "never"); // collision rule
		setField(packetRed, "g", enumChatFormatColor); // team color
		setField(packetRed, "h", Arrays.stream(entityUUID).map(UUID::toString).collect(HashSet::new, Set::add, Set::addAll)); // entities
		setField(packetRed, "i", 0); // packet type crete team
		setField(packetRed, "j", 1); // entity count?
		sendPacket(player, packetRed);
	}

	public static void guardianBeam(Player player, Vector from, Vector to, int guardianId, int armorstandId) {

		{
			Object packet = newInstance(packetPlayOutSpawnEntityLivingClass, new Class[0], new Object[0]);
			if (packet == null) return;
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
		{
			Object dataWatcher = newInstance(
				dataWatcherClass,
				new Class[]{entityClass},
				new Object[]{null});
			if (dataWatcher == null) return;

			registerDataWatcher(dataWatcher, 0, "a", (byte) 0b00100000);
			registerDataWatcher(dataWatcher, 4, "i", true);
			registerDataWatcher(dataWatcher, 5, "i", true);
			registerDataWatcher(dataWatcher, 14, "a", (byte) 0b00010000);

			Object packet = newInstance(packetPlayOutEntityMetadataClass, new Class[]{int.class, dataWatcherClass, boolean.class}, new Object[]{armorstandId, dataWatcher, true});
			sendPacket(player, packet);
		}

		guardianBeamExisting(player, from, guardianId, armorstandId);
	}

	public static void guardianBeamExisting(Player player, Vector from, int guardianId, int targetId) {
		//shoots an existing target
		{
			Object packet = newInstance(getNmsPackage() + ".PacketPlayOutSpawnEntityLiving");
			if (packet == null) return;
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
				dataWatcherClass,
				new Class[]{entityClass},
				new Object[]{null});
			if (dataWatcher == null) return;

			registerDataWatcher(dataWatcher, 0, "a", (byte) 0b00100000);
			registerDataWatcher(dataWatcher, 4, "i", true);
			registerDataWatcher(dataWatcher, 5, "i", true);
			registerDataWatcher(dataWatcher, 16, "b", targetId);

			Object packet = newInstance(packetPlayOutEntityMetadataClass, new Class[]{int.class, dataWatcherClass, boolean.class}, new Object[]{guardianId, dataWatcher, true});
			sendPacket(player, packet);
		}
	}

	private static void registerDataWatcher(Object dataWatcher, int target, String serializer, Object value) {
		Object dwSerializer = getStaticField(dataWatcherRegistryClass, serializer);
		Object dwObject = newInstance(
			dataWatcherObjectClass,
			new Class[]{int.class, dataWatcherSerializerClass}, new Object[]{target, dwSerializer});
		invokeMethod(
			dataWatcher,
			"register",
			new Class[]{dataWatcherObjectClass, Object.class},
			new Object[]{dwObject, value});
	}

	public static void destroyFakeEntity(Player player, int... entityIds) {
		Object packet = newInstance(packetPlayOutEntityDestroyClass, new Class[]{int[].class}, new Object[]{entityIds});
		sendPacket(player, packet);
	}
}
