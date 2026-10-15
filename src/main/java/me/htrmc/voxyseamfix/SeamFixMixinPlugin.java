package me.htrmc.voxyseamfix;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class SeamFixMixinPlugin implements IMixinConfigPlugin {
    // voxy 0.2.14 added a ChunkSectionLayer parameter to processTextureBakeResult.
    private static final boolean LAYER_ARG = voxyAtLeast("0.2.14-alpha");

    private static boolean voxyAtLeast(String version) {
        try {
            return FabricLoader.getInstance().getModContainer("voxy").orElseThrow()
                    .getMetadata().getVersion().compareTo(SemanticVersion.parse(version)) >= 0;
        } catch (VersionParsingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith(".ModelFactoryMixin")) {
            return LAYER_ARG;
        }
        if (mixinClassName.endsWith(".LegacyModelFactoryMixin")) {
            return !LAYER_ARG;
        }
        return true;
    }

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
