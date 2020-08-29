package at.haha007.edenlib.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {
	private static final String nmsPackage;

	static {
		nmsPackage = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}

	public static void setField(Object object, String fieldName, Object value) {
		try {
			Class<?> clazz = object.getClass();
			Field field = clazz.getDeclaredField(fieldName);
			boolean a = field.isAccessible();
			field.setAccessible(true);
			field.set(object, value);
			field.setAccessible(a);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public static Class<?> getNmsClass(String name) {
		try {
			return Class.forName(getNmsPackage() + "." + name);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static Object newInstance(String clazzName, Object... constructorArgs) {
		try {
			Class<?>[] parametarTypes = new Class[constructorArgs.length];
			for (int i = 0, constructorArgsLength = constructorArgs.length; i < constructorArgsLength; i++) {
				parametarTypes[i] = constructorArgs[i].getClass();
			}
			Constructor<?> constructor = Class.forName(clazzName).getDeclaredConstructor(parametarTypes);
			constructor.setAccessible(true);
			return constructor.newInstance(constructorArgs);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object newInstance(Class<?> clazz, Class<?>[] paramTypes, Object[] params) {
		try {
			Constructor<?> constructor = clazz.getDeclaredConstructor(paramTypes);
			constructor.setAccessible(true);
			return constructor.newInstance(params);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object newInstance(String clazzName, String[] argTypes, Object[] constructorArgs) {
		try {
			Class<?>[] types = new Class[argTypes.length];
			for (int i = 0; i < argTypes.length; i++) {
				types[i] = Class.forName(argTypes[i]);
			}
			Constructor<?> constructor = Class.forName(clazzName).getDeclaredConstructor(types);
			constructor.setAccessible(true);
			return constructor.newInstance(constructorArgs);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getNmsPackage() {
		return nmsPackage;
	}

	public static Object getField(Object object, String fieldName) {
		try {
			Field field = object.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(object);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			return null;
		}
	}

	public static Object getStaticField(String clazzName, String fieldName) {
		try {
			Class<?> clazz = Class.forName(clazzName);
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(null);
		} catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException e) {
			return null;
		}
	}

	public static Object getStaticField(Class<?> clazz, String fieldName) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(null);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			return null;
		}
	}

	public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object[] params) {
		try {
			Class<?> clazz = object.getClass();
			Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
			method.setAccessible(true);
			return method.invoke(object, params);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object invokeMethod(Object object, String methodName, Object... args) {
		try {
			Class<?> clazz = object.getClass();
			Class<?>[] argTypes = new Class<?>[args.length];
			for (int i = 0; i < args.length; i++) {
				argTypes[i] = args[i].getClass();
			}
			Method method = clazz.getDeclaredMethod(methodName, argTypes);
			method.setAccessible(true);
			return method.invoke(object, args);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object invokeStaticMethod(String clazzName, String methodName, Object... args) {
		try {
			Class<?> clazz = Class.forName(clazzName);
			Class<?>[] argTypes = new Class<?>[args.length];
			for (int i = 0; i < args.length; i++) {
				argTypes[i] = args[i].getClass();
			}
			Method method = clazz.getDeclaredMethod(methodName, argTypes);
			method.setAccessible(true);
			return method.invoke(null, args);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}






































