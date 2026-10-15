package me.htrmc.voxyseamfix;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class McCompat {
    private McCompat() {}

    private static final ClassLoader LOADER = McCompat.class.getClassLoader();

    static final boolean MOJANG = exists("net.minecraft.client.Minecraft");

    private static final Class<?> LIQUID_BLOCK = cls("net.minecraft.world.level.block.LiquidBlock", "net.minecraft.class_2404");
    private static final Method GET_INSTANCE;
    private static final Field LEVEL;

    static {
        Class<?> minecraft = cls("net.minecraft.client.Minecraft", "net.minecraft.class_310");
        try {
            GET_INSTANCE = minecraft.getMethod(name("getInstance", "method_1551"));
            LEVEL = minecraft.getField(name("level", "field_1687"));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Method getSeaLevel;
    private static Method getBlock;

    public static Integer seaLevel() {
        try {
            Object level = LEVEL.get(GET_INSTANCE.invoke(null));
            if (level == null) {
                return null;
            }
            if (getSeaLevel == null) {
                getSeaLevel = level.getClass().getMethod(name("getSeaLevel", "method_8615"));
            }
            return (Integer) getSeaLevel.invoke(level);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean isLiquid(Object blockState) {
        try {
            if (getBlock == null) {
                getBlock = blockState.getClass().getMethod(name("getBlock", "method_26204"));
            }
            return LIQUID_BLOCK.isInstance(getBlock.invoke(blockState));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    static String name(String mojang, String intermediary) {
        return MOJANG ? mojang : intermediary;
    }

    static Class<?> cls(String mojang, String intermediary) {
        try {
            return Class.forName(name(mojang, intermediary), false, LOADER);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean exists(String name) {
        try {
            Class.forName(name, false, LOADER);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
