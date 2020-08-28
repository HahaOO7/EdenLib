package at.haha007.edenlib.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public class ItemUtils {

	public static ItemStack getSkull(String texture) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);

		SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		profile.getProperties().put("textures", new Property("textures", texture));
		try {
			Field profileField = itemMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(itemMeta, profile);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack getItem(Material material, String name, List<String> lore) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getItem(Material material, String name, String... lore) {
		return getItem(material, name, Arrays.asList(lore));
	}

	public static Object getNmsStack(ItemStack itemStack) {
		return ReflectionUtils.invokeStaticMethod(ReflectionUtils.getNmsPackage() + ".CraftItemStack", "asNMSCopy", itemStack);
	}

	public static void giveItem(Player player, ItemStack item) {
		HashMap<Integer, ItemStack> remaining = player.getInventory().addItem(item);
		for (Map.Entry<Integer, ItemStack> entry : remaining.entrySet()) {
			Item entityItem = player.getWorld().dropItem(player.getLocation(), entry.getValue());
			entityItem.setOwner(player.getUniqueId());
		}
	}

	public static ItemStack setNbtString(ItemStack item, String name, String value) {
		Object nmsItem = ReflectionUtils.invokeStaticMethod(ReflectionUtils.getNmsPackage() + ".CraftItemStack", "asNMSCopy", item);
		if (nmsItem == null) return item;
		Object nbtTagCompound = ReflectionUtils.invokeMethod(nmsItem, "getOrCreateTag");
		if (nbtTagCompound == null) return item;
		ReflectionUtils.invokeMethod(nbtTagCompound, "setString", name, value);
		return (ItemStack) ReflectionUtils.invokeStaticMethod(ReflectionUtils.getNmsPackage() + ".CraftItemStack", "asCraftMirror", nmsItem);
	}

	public static String getNbtString(ItemStack item, String key) {
		Object nmsItem = ReflectionUtils.invokeStaticMethod(ReflectionUtils.getNmsPackage() + ".CraftItemStack", "asNMSCopy", item);
		if (nmsItem == null) return null;
		Object tag = ReflectionUtils.invokeMethod(nmsItem, "getOrCreateTag");
		if (tag == null) return null;
		Object string = ReflectionUtils.invokeMethod(tag, "getString", key);
		if (string == null) return null;
		return string.toString();
	}

	public static ItemStack setNbtInt(ItemStack item, String name, int value) {
		Object nmsItem = ReflectionUtils.invokeStaticMethod(ReflectionUtils.getNmsPackage() + ".CraftItemStack", "asNMSCopy", item);
		if (nmsItem == null) return item;
		Object nbtTagCompound = ReflectionUtils.invokeMethod(nmsItem, "getOrCreateTag");
		if (nbtTagCompound == null) return item;
		ReflectionUtils.invokeMethod(nbtTagCompound, "setInt", name, value);
		return (ItemStack) ReflectionUtils.invokeStaticMethod(ReflectionUtils.getNmsPackage() + ".CraftItemStack", "asCraftMirror", nmsItem);
	}

	public static int getNbtInt(ItemStack item, String key) {
		Object nmsItem = ReflectionUtils.invokeStaticMethod(ReflectionUtils.getNmsPackage() + ".CraftItemStack", "asNMSCopy", item);
		if (nmsItem == null) return 0;
		Object tag = ReflectionUtils.invokeMethod(nmsItem, "getOrCreateTag");
		if (tag == null) return 0;
		Object integer = ReflectionUtils.invokeMethod(tag, "getInt", key);
		if (integer == null) return 0;
		return (int) integer;
	}

	public static ItemStack addGlow(ItemStack item) {
		Object nmsItem = ReflectionUtils.invokeStaticMethod(ReflectionUtils.getNmsPackage() + ".CraftItemStack", "asNMSCopy", item);
		if (nmsItem == null) return item;
		Object tag = ReflectionUtils.invokeMethod(nmsItem, "getOrCreateTag");
		if (tag == null) return item;
		Object flag = ReflectionUtils.invokeMethod(tag, "getInt", "HideFlags");
		if (flag == null) return item;
		ReflectionUtils.invokeMethod(tag, "setInt", "HideFlags", (int) flag % 2 == 0 ? (int) flag + 1 : (int) flag - 1);

		//TODO
		return (ItemStack) ReflectionUtils.invokeStaticMethod(ReflectionUtils.getNmsPackage() + ".CraftItemStack", "asCraftMirror", nmsItem);
	}

	public static void sendFakeItemChange(int slot, Object nmsItemStack, Player player) {
		Utils.sendPacket(player, ReflectionUtils.newInstance(ReflectionUtils.getNmsPackage() + ".PacketPlayOutSetSlot", 0, dataSlotToNetworkSlot(0), nmsItemStack));
	}

	public static int dataSlotToNetworkSlot(int index) {
		if (index <= 8)
			index += 36;
		else if (index == 100)
			index = 8;
		else if (index == 101)
			index = 7;
		else if (index == 102)
			index = 6;
		else if (index == 103)
			index = 5;
		else if (index >= 80 && index <= 83)
			index -= 79;
		return index;
	}
}
