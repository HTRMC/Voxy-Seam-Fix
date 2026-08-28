package me.htrmc.voxyseamfix;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class SeamFixConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public int fluidSurfaceY = -1;

    public boolean enabled = true;

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("voxy-seam-fix.json");
    }

    public static SeamFixConfig loadOrCreate() {
        Path path = path();
        try {
            if (Files.exists(path)) {
                SeamFixConfig loaded = GSON.fromJson(Files.readString(path), SeamFixConfig.class);
                if (loaded != null) {
                    return loaded;
                }
            }
        } catch (Exception e) {
            VoxySeamFix.LOGGER.warn("Could not read {}, falling back to defaults", path, e);
        }

        SeamFixConfig config = new SeamFixConfig();
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(config));
        } catch (Exception e) {
            VoxySeamFix.LOGGER.warn("Could not write {}", path, e);
        }
        return config;
    }
}
