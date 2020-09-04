package at.haha007.edenlib.utils;

import net.minecraft.server.v1_16_R2.ChatComponentText;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.DecoderException;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Hex;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

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
	private static final Class<?> blockClass;
	private static final Class<?> iBlockDataClass;

	private static final Field packetPlayOutSpawnEntityA;
	private static final Field packetPlayOutSpawnEntityB;
	private static final Field packetPlayOutSpawnEntityC;
	private static final Field packetPlayOutSpawnEntityD;
	private static final Field packetPlayOutSpawnEntityE;
	private static final Field packetPlayOutSpawnEntityK;
	private static final Field packetPlayOutSpawnEntityL;

	private static final Field packetPlayOutScoreboardTeamA;
	private static final Field packetPlayOutScoreboardTeamB;
	private static final Field packetPlayOutScoreboardTeamC;
	private static final Field packetPlayOutScoreboardTeamD;
	private static final Field packetPlayOutScoreboardTeamE;
	private static final Field packetPlayOutScoreboardTeamF;
	private static final Field packetPlayOutScoreboardTeamG;
	private static final Field packetPlayOutScoreboardTeamH;
	private static final Field packetPlayOutScoreboardTeamI;
	private static final Field packetPlayOutScoreboardTeamJ;

	private static final Field packetPlayOutSpawnEntityLivingA;
	private static final Field packetPlayOutSpawnEntityLivingB;
	private static final Field packetPlayOutSpawnEntityLivingC;
	private static final Field packetPlayOutSpawnEntityLivingD;
	private static final Field packetPlayOutSpawnEntityLivingE;
	private static final Field packetPlayOutSpawnEntityLivingF;

	private static final Object dataWatcherSerializerA;
	private static final Object dataWatcherSerializerB;
	private static final Object dataWatcherSerializerI;

	private static final Method blockGetCombinedID;
	private static final Method blockGetBlockData;

	private static final Method dataWatcherRegister;


	private static final Object fallingBlockEntityType;

	static {
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
		assert packetPlayOutScoreboardTeamClass != null;

		packetPlayOutSpawnEntityA = getField(packetPlayOutSpawnEntityClass, "a");
		packetPlayOutSpawnEntityB = getField(packetPlayOutSpawnEntityClass, "b");
		packetPlayOutSpawnEntityC = getField(packetPlayOutSpawnEntityClass, "c");
		packetPlayOutSpawnEntityD = getField(packetPlayOutSpawnEntityClass, "d");
		packetPlayOutSpawnEntityE = getField(packetPlayOutSpawnEntityClass, "e");
		packetPlayOutSpawnEntityK = getField(packetPlayOutSpawnEntityClass, "k");
		packetPlayOutSpawnEntityL = getField(packetPlayOutSpawnEntityClass, "l");

		packetPlayOutScoreboardTeamA = getField(packetPlayOutScoreboardTeamClass, "a");
		packetPlayOutScoreboardTeamB = getField(packetPlayOutScoreboardTeamClass, "b");
		packetPlayOutScoreboardTeamC = getField(packetPlayOutScoreboardTeamClass, "c");
		packetPlayOutScoreboardTeamD = getField(packetPlayOutScoreboardTeamClass, "d");
		packetPlayOutScoreboardTeamE = getField(packetPlayOutScoreboardTeamClass, "e");
		packetPlayOutScoreboardTeamF = getField(packetPlayOutScoreboardTeamClass, "f");
		packetPlayOutScoreboardTeamG = getField(packetPlayOutScoreboardTeamClass, "g");
		packetPlayOutScoreboardTeamH = getField(packetPlayOutScoreboardTeamClass, "h");
		packetPlayOutScoreboardTeamI = getField(packetPlayOutScoreboardTeamClass, "i");
		packetPlayOutScoreboardTeamJ = getField(packetPlayOutScoreboardTeamClass, "j");

		packetPlayOutSpawnEntityLivingA = getField(packetPlayOutSpawnEntityLivingClass, "a");
		packetPlayOutSpawnEntityLivingB = getField(packetPlayOutSpawnEntityLivingClass, "b");
		packetPlayOutSpawnEntityLivingC = getField(packetPlayOutSpawnEntityLivingClass, "c");
		packetPlayOutSpawnEntityLivingD = getField(packetPlayOutSpawnEntityLivingClass, "d");
		packetPlayOutSpawnEntityLivingE = getField(packetPlayOutSpawnEntityLivingClass, "e");
		packetPlayOutSpawnEntityLivingF = getField(packetPlayOutSpawnEntityLivingClass, "f");

		blockGetCombinedID = getMethod(blockClass, "getCombinedId", iBlockDataClass);
		blockGetBlockData = getMethod(blockClass, "getBlockData");

		dataWatcherRegister = getMethod(dataWatcherClass, "register", dataWatcherObjectClass, Object.class);

		dataWatcherSerializerA = getStaticFieldValue(dataWatcherRegistryClass, "a");
		dataWatcherSerializerB = getStaticFieldValue(dataWatcherRegistryClass, "b");
		dataWatcherSerializerI = getStaticFieldValue(dataWatcherRegistryClass, "i");


		fallingBlockEntityType = getStaticFieldValue(entityTypesClass, "FALLING_BLOCK");
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
		Object nmsPlayer = invokeMethod(player, "getHandle", new Class[0], new Object[0]);
		if (nmsPlayer == null) return;
		Object nmsPlayerConnection = getFieldValue(nmsPlayer, "playerConnection");
		if (nmsPlayerConnection == null) return;
		invokeMethod(nmsPlayerConnection, "sendPacket", new Class[]{getNmsClass("Packet")}, new Object[]{nmsPacket});
	}

	public static void displayFakeBlock(Player player, Vector location, Object block, int entityId, UUID entityUUID) {
		//block is in nms Blocks
		Object blockData = invokeMethod(block, blockGetBlockData);
		Object packet = newInstance(packetPlayOutSpawnEntityClass, new Class[]{}, new Object[]{});
		if (packet == null) return;
		setFieldValue(packet, packetPlayOutSpawnEntityA, entityId);
		setFieldValue(packet, packetPlayOutSpawnEntityB, entityUUID);
		// pos
		setFieldValue(packet, packetPlayOutSpawnEntityC, location.getX());
		setFieldValue(packet, packetPlayOutSpawnEntityD, location.getY());
		setFieldValue(packet, packetPlayOutSpawnEntityE, location.getZ());
		// entity type
		setFieldValue(packet, packetPlayOutSpawnEntityK, fallingBlockEntityType);
		setFieldValue(packet, packetPlayOutSpawnEntityL, invokeStaticMethod(blockGetCombinedID, blockData));
		sendPacket(player, packet);
	}

	public static void addGlow(Player player, int entityId) {
		Object dataWatcher = newInstance(dataWatcherClass, new Class[]{entityClass}, new Object[]{null});
		registerDataWatcher(dataWatcher, 0, dataWatcherSerializerA, (byte) 0b01000000);
		registerDataWatcher(dataWatcher, 5, dataWatcherSerializerI, true);
		Object packet = newInstance(packetPlayOutEntityMetadataClass, new Class[]{int.class, dataWatcherClass, boolean.class}, new Object[]{entityId, dataWatcher, true});
		sendPacket(player, packet);
	}

	public static void colorGlow(Player player, Object enumChatFormatColor, UUID... entityUUID) {
		Object packetRed = newInstance(packetPlayOutScoreboardTeamClass, new Class[0], new Object[0]);
		setFieldValue(packetRed, packetPlayOutScoreboardTeamA, getRandomString(16)); // name
		setFieldValue(packetRed, packetPlayOutScoreboardTeamB, new ChatComponentText("")); // display name
		setFieldValue(packetRed, packetPlayOutScoreboardTeamC, new ChatComponentText("PRE ")); // prefix
		setFieldValue(packetRed, packetPlayOutScoreboardTeamD, new ChatComponentText(" SUF")); // suffix
		setFieldValue(packetRed, packetPlayOutScoreboardTeamE, "never"); // name tag visible
		setFieldValue(packetRed, packetPlayOutScoreboardTeamF, "never"); // collision rule
		setFieldValue(packetRed, packetPlayOutScoreboardTeamG, enumChatFormatColor); // team color
		setFieldValue(packetRed, packetPlayOutScoreboardTeamH, Arrays.stream(entityUUID).map(UUID::toString).collect(Collectors.toCollection(ArrayList::new))); // entities
		setFieldValue(packetRed, packetPlayOutScoreboardTeamI, 0); // packet type crete team
		setFieldValue(packetRed, packetPlayOutScoreboardTeamJ, entityUUID.length); // entity count?
		sendPacket(player, packetRed);
	}

	public static void guardianBeam(Player player, Vector from, Vector to, int guardianId, int armorstandId) {

		{
			Object packet = newInstance(packetPlayOutSpawnEntityLivingClass, new Class[0], new Object[0]);
			if (packet == null) return;
			setFieldValue(packet, packetPlayOutSpawnEntityLivingA, armorstandId);
			setFieldValue(packet, packetPlayOutSpawnEntityLivingB, UUID.randomUUID());
			// entity type
			setFieldValue(packet, packetPlayOutSpawnEntityLivingC, 1);
			// pos
			setFieldValue(packet, packetPlayOutSpawnEntityLivingD, to.getX());
			setFieldValue(packet, packetPlayOutSpawnEntityLivingE, to.getY());
			setFieldValue(packet, packetPlayOutSpawnEntityLivingF, to.getZ());
			sendPacket(player, packet);
		}
		{
			Object dataWatcher = newInstance(
				dataWatcherClass,
				new Class[]{entityClass},
				new Object[]{null});
			if (dataWatcher == null) return;

			registerDataWatcher(dataWatcher, 0, dataWatcherSerializerA, (byte) 0b00100000);
			registerDataWatcher(dataWatcher, 4, dataWatcherSerializerI, true);
			registerDataWatcher(dataWatcher, 5, dataWatcherSerializerI, true);
			registerDataWatcher(dataWatcher, 14, dataWatcherSerializerA, (byte) 0b00010000);

			Object packet = newInstance(packetPlayOutEntityMetadataClass, new Class[]{int.class, dataWatcherClass, boolean.class}, new Object[]{armorstandId, dataWatcher, true});
			sendPacket(player, packet);
		}

		guardianBeamExisting(player, from, guardianId, armorstandId);
	}

	public static void guardianBeamExisting(Player player, Vector from, int guardianId, int targetId) {
		//shoots an existing target
		{
			Object packet = newInstance(packetPlayOutSpawnEntityLivingClass, new Class[0], new Object[0]);
			if (packet == null) return;
			setFieldValue(packet, packetPlayOutSpawnEntityLivingA, guardianId);
			setFieldValue(packet, packetPlayOutSpawnEntityLivingB, UUID.randomUUID());
			// entity type
			setFieldValue(packet, packetPlayOutSpawnEntityLivingC, 31);
			// pos
			setFieldValue(packet, packetPlayOutSpawnEntityLivingD, from.getX());
			setFieldValue(packet, packetPlayOutSpawnEntityLivingE, from.getY());
			setFieldValue(packet, packetPlayOutSpawnEntityLivingF, from.getZ());
			sendPacket(player, packet);
		}
		{
			Object dataWatcher = newInstance(
				dataWatcherClass,
				new Class[]{entityClass},
				new Object[]{null});
			if (dataWatcher == null) return;

			registerDataWatcher(dataWatcher, 0, dataWatcherSerializerA, (byte) 0b00100000);
			registerDataWatcher(dataWatcher, 4, dataWatcherSerializerI, true);
			registerDataWatcher(dataWatcher, 5, dataWatcherSerializerI, true);
			registerDataWatcher(dataWatcher, 16, dataWatcherSerializerB, targetId);

			Object packet = newInstance(packetPlayOutEntityMetadataClass, new Class[]{int.class, dataWatcherClass, boolean.class}, new Object[]{guardianId, dataWatcher, true});
			sendPacket(player, packet);
		}
	}

	private static void registerDataWatcher(Object dataWatcher, int target, Object dwSerializer, Object value) {
		Object dwObject = newInstance(dataWatcherObjectClass, new Class[]{int.class, dataWatcherSerializerClass}, new Object[]{target, dwSerializer});
		invokeMethod(dataWatcher, dataWatcherRegister, dwObject, value);
	}

	public static void destroyFakeEntity(Player player, int... entityIds) {
		Object packet = newInstance(packetPlayOutEntityDestroyClass, new Class[]{int[].class}, new Object[]{entityIds});
		sendPacket(player, packet);
	}
}
