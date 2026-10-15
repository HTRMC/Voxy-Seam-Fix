package me.htrmc.voxyseamfix;

import net.fabricmc.api.ClientModInitializer;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoxySeamFix implements ClientModInitializer {
    public static final String MOD_ID = "voxy-seam-fix";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final long BLOCK_MODEL_FLAGS_OFFSET = 4 * 6;

    public static final int FLUID_MODEL_FLAG = 16;

    public static final long SCENE_UNIFORM_FLUID_SURFACE_OFFSET = 92;

    private static SeamFixConfig config;

    @Override
    public void onInitializeClient() {
        config = SeamFixConfig.loadOrCreate();
    }

    public static SeamFixConfig config() {
        if (config == null) {
            config = SeamFixConfig.loadOrCreate();
        }
        return config;
    }

    public static float relativeFluidSurfaceY(int baseSectionY) {
        if (!config().enabled || !(McCompat.seaLevel() instanceof Integer seaLevel)) {
            return Float.NaN;
        }
        int override = config().fluidSurfaceY;
        int surface = override >= 0 ? override : seaLevel;
        return surface - (float) (baseSectionY << 5);
    }

    public static void markFluidModel(Object blockState, Object bakeResult) {
        if (!McCompat.isLiquid(blockState) || !(bakeResult instanceof ModelBufferAccess access)) {
            return;
        }
        long flags = access.voxyseamfix$modelAddress() + BLOCK_MODEL_FLAGS_OFFSET;
        MemoryUtil.memPutInt(flags, MemoryUtil.memGetInt(flags) | FLUID_MODEL_FLAG);
    }
}
