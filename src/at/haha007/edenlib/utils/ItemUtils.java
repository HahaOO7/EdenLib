package at.haha007.edenlib.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static at.haha007.edenlib.utils.ReflectionUtils.*;

@SuppressWarnings("unused")
public class ItemUtils {
    private static final Class<?> craftItemStackClass;
    private static final Class<?> itemStackClass;
    private static final Class<?> packetPlayOutSetSlotClass;
    private static final Class<?> nbtTagCompoundClass;
    private static final Class<?> nbtTagListClass;
    private static final Class<?> nbtBaseClass;

    private static final Method craftItemStackAsNmsCopy;
    private static final Method craftItemStackAsCraftMirror;

    private static final Method itemStackGetOrCreateTag;

    private static final Method nbtTagCompoundGetString;
    private static final Method nbtTagCompoundSetString;
    private static final Method nbtTagCompoundGetInt;
    private static final Method nbtTagCompoundSetInt;
    private static final Method nbtTagCompoundGetList;
    private static final Method nbtTagCompoundSet;
    private static final Method nbtTagCompoundHasKey;

    private static final Method abstractListAdd;

    private static final Field craftMetaSkullProfile;


    static {
        packetPlayOutSetSlotClass = getNmsClass("PacketPlayOutSetSlot");
        itemStackClass = getNmsClass("ItemStack");
        craftItemStackClass = getCraftBukkitClass("inventory.CraftItemStack");
        nbtTagCompoundClass = getNmsClass("NBTTagCompound");
        nbtTagListClass = getNmsClass("NBTTagList");
        nbtBaseClass = getNmsClass("NBTBase");
        assert nbtBaseClass != null;
        assert craftItemStackClass != null;
        assert nbtTagCompoundClass != null;
        assert packetPlayOutSetSlotClass != null;
        assert itemStackClass != null;
        assert nbtTagListClass != null;

        craftItemStackAsNmsCopy = getMethod(craftItemStackClass, "asNMSCopy", ItemStack.class);
        craftItemStackAsCraftMirror = getMethod(craftItemStackClass, "asCraftMirror", itemStackClass);
        itemStackGetOrCreateTag = getMethod(itemStackClass, "getOrCreateTag");
        nbtTagCompoundGetInt = getMethod(nbtTagCompoundClass, "getInt", String.class);
        nbtTagCompoundSetInt = getMethod(nbtTagCompoundClass, "setInt", String.class, int.class);
        nbtTagCompoundGetString = getMethod(nbtTagCompoundClass, "getString", String.class);
        nbtTagCompoundSetString = getMethod(nbtTagCompoundClass, "setString", String.class, String.class);
        nbtTagCompoundGetList = getMethod(nbtTagCompoundClass, "getList", String.class, int.class);
        nbtTagCompoundSet = getMethod(nbtTagCompoundClass, "set", String.class, nbtBaseClass);
        nbtTagCompoundHasKey = getMethod(nbtTagCompoundClass, "hasKey", String.class);

        abstractListAdd = getMethod(AbstractList.class, "add", Object.class);

        Class<?> craftMetaSkullClass = getCraftBukkitClass("inventory.CraftMetaSkull");
        assert craftMetaSkullClass != null;
        craftMetaSkullProfile = getField(craftMetaSkullClass, "profile");
    }

    public static ItemStack getSkull(String texture) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap properties = profile.getProperties();
        Property property = new Property("textures", texture);
        properties.put("textures", property);
        try {
            craftMetaSkullProfile.setAccessible(true);
            craftMetaSkullProfile.set(itemMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack getItem(Material material, Component name, List<Component> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.lore(lore);
        meta.displayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getItem(Material material, Component name, Component... lore) {
        return getItem(material, name, Arrays.asList(lore));
    }

    public static Object getNmsStack(ItemStack itemStack) {
        return invokeStaticMethod(craftItemStackAsNmsCopy, itemStack);
    }

    public static ItemStack getBukkitStack(Object nmsItemStack) {
        return (ItemStack) invokeStaticMethod(craftItemStackAsCraftMirror, nmsItemStack);
    }

    public static void giveItem(Player player, ItemStack item) {
        HashMap<Integer, ItemStack> remaining = player.getInventory().addItem(item);
        for (Map.Entry<Integer, ItemStack> entry : remaining.entrySet()) {
            Item entityItem = player.getWorld().dropItem(player.getLocation(), entry.getValue());
            entityItem.setOwner(player.getUniqueId());
        }
    }

    public static ItemStack setNbtString(ItemStack item, String name, String value) {
        Object nmsItem = getNmsStack(item);
        if (nmsItem == null) return item;
        Object nbtTagCompound = invokeMethod(nmsItem, itemStackGetOrCreateTag);
        if (nbtTagCompound == null) return item;
        invokeMethod(nbtTagCompound, nbtTagCompoundSetString, name, value);
        return getBukkitStack(nmsItem);
    }

    public static String getNbtString(ItemStack item, String key) {
        Object nmsItem = getNmsStack(item);
        if (nmsItem == null) return null;
        Object tag = invokeMethod(nmsItem, itemStackGetOrCreateTag);
        if (tag == null) return null;
        Object string = invokeMethod(tag, nbtTagCompoundGetString, key);
        if (string == null) return null;
        return string.toString();
    }

    public static ItemStack setNbtInt(ItemStack item, String name, int value) {
        Object nmsItem = getNmsStack(item);
        if (nmsItem == null) return item;
        Object nbtTagCompound = invokeMethod(nmsItem, itemStackGetOrCreateTag);
        if (nbtTagCompound == null) return item;
        invokeMethod(nbtTagCompound, nbtTagCompoundSetInt, name, value);
        return getBukkitStack(nmsItem);
    }

    public static int getNbtInt(ItemStack item, String key) {
        Object nmsItem = getNmsStack(item);
        if (nmsItem == null) return 0;
        Object tag = invokeMethod(nmsItem, itemStackGetOrCreateTag);
        if (tag == null) return 0;
        Object integer = invokeMethod(tag, nbtTagCompoundGetInt, key);
        if (integer == null) return 0;
        return (int) integer;
    }

    public static ItemStack setGlow(ItemStack item, boolean glow) {
        Object nmsItem = getNmsStack(item);
        if (nmsItem == null) return item;
        Object tag = invokeMethod(nmsItem, itemStackGetOrCreateTag);
        if (tag == null) return item;
        Object flag = invokeMethod(tag, nbtTagCompoundGetInt, "HideFlags");
        if (flag == null) return item;
        int f = (int) flag;
        if (f % 2 != 0) f -= 1;
        invokeMethod(tag, nbtTagCompoundSetInt, "HideFlags", glow ? f + 1 : f);
        Object enchantments;
        Object b = invokeMethod(tag, nbtTagCompoundHasKey, "Enchantments");
        if (b == null) return item;
        if ((boolean) b)
            enchantments = invokeMethod(tag, nbtTagCompoundGetList, "Enchantments", 10); // 10 -> TAG_Compound
        else
            enchantments = newInstance(nbtTagListClass, new Class[]{List.class, byte.class}, new Object[]{new ArrayList<>(), (byte) 10});
        if (enchantments == null) return item;
        Object enchantment = newInstance(nbtTagCompoundClass, new Class[0], new Object[0]);
        invokeMethod(enchantments, abstractListAdd, enchantment);
        invokeMethod(tag, nbtTagCompoundSet, "Enchantments", enchantments);
        return getBukkitStack(nmsItem);
    }

    public static void sendFakeItemChange(int slot, Object nmsItemStack, Player player) {
        Utils.sendPacket(player, newInstance(packetPlayOutSetSlotClass, new Class[]{int.class, int.class, itemStackClass}, new Object[]{0, dataSlotToNetworkSlot(slot), nmsItemStack}));
    }


    public static int dataSlotToNetworkSlot(int index) {
        if (index <= 8 && index >= 0)
            index += 36;
        else if (index == 100)
            index = 8;
        else if (index == 101)
            index = 7;
        else if (index == 102)
            index = 6;
        else if (index == 103)
            index = 5;
        else if (index == -106)
            index = 45;
        else if (index >= 80 && index <= 83)
            index -= 79;
        return index;
    }

    public static int networkSlotToDataSlot(int index) {
        if (index == 8)
            index = 100;
        else if (index == 7)
            index = 101;
        else if (index == 6)
            index = 102;
        else if (index == 5)
            index = 103;
        else if (index == 45)
            index = -106;
        else if (index >= 1 && index <= 4)
            index += 79;
        else if (index >= 36)
            index -= 36;
        return index;
    }
}
