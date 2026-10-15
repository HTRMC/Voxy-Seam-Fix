package me.htrmc.voxyseamfix.mixin;

import me.cortex.voxy.client.core.model.ColourDepthTextureData;
import me.cortex.voxy.client.core.model.ModelFactory;
import me.htrmc.voxyseamfix.VoxySeamFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// voxy 0.2.7-0.2.13, before processTextureBakeResult took a ChunkSectionLayer.
@Mixin(ModelFactory.class)
public class LegacyModelFactoryMixin {
    @Inject(method = "processTextureBakeResult", at = @At("RETURN"))
    private void voxyseamfix$markFluidModels(int blockId,
                                             @Coerce Object blockState,
                                             ColourDepthTextureData[] textureData,
                                             boolean isShaded,
                                             boolean darkenedTinting,
                                             CallbackInfoReturnable<Object> cir) {
        VoxySeamFix.markFluidModel(blockState, cir.getReturnValue());
    }
}
