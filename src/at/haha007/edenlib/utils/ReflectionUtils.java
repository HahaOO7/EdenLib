package at.haha007.edenlib.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("unused")
public class ReflectionUtils {
    private static final String nmsPackage;
    private static final String craftBukkitPackage;

    static {
        nmsPackage = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        craftBukkitPackage = "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }

    public static String getNmsPackage() {
        return nmsPackage;
    }

    public static Class<?> getNmsClass(String name) {
        try {
            return Class.forName(getNmsPackage() + "." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static String getCraftBukkitPackage() {
        return craftBukkitPackage;
    }

    public static Class<?> getCraftBukkitClass(String name) {
        try {
            return Class.forName(getCraftBukkitPackage() + "." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static void setFieldValue(Object object, Field field, Object value) {
        try {
            boolean a = field.canAccess(object);
            field.setAccessible(true);
            field.set(object, value);
            field.setAccessible(a);
        } catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getClassByName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Object newInstance(Class<?> clazz, Class<?>[] paramTypes, Object[] params) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(paramTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(params);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getFieldValue(Object object, Field field) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static Object getStaticFieldValue(Field field) {
        try {
            field.setAccessible(true);
            return field.get(null);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Field getField(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object invokeMethod(Object object, Method method, Object... params) {
        try {
            method.setAccessible(true);
            return method.invoke(object, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object invokeStaticMethod(Method method, Object... params) {
        try {
            method.setAccessible(true);
            return method.invoke(null, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

}






































