package me.htrmc.voxyseamfix.mixin;

import me.cortex.voxy.client.core.model.ColourDepthTextureData;
import me.cortex.voxy.client.core.model.ModelFactory;
import me.htrmc.voxyseamfix.ModelBufferAccess;
import me.htrmc.voxyseamfix.VoxySeamFix;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelFactory.class)
public class ModelFactoryMixin {
    @Inject(method = "processTextureBakeResult", at = @At("RETURN"))
    private void voxyseamfix$markFluidModels(int blockId,
                                             BlockState blockState,
                                             ColourDepthTextureData[] textureData,
                                             boolean isShaded,
                                             boolean darkenedTinting,
                                             ChunkSectionLayer layer,
                                             CallbackInfoReturnable<Object> cir) {
        if (!(blockState.getBlock() instanceof LiquidBlock)) {
            return;
        }
        if (!(cir.getReturnValue() instanceof ModelBufferAccess access)) {
            return;
        }
        long flags = access.voxyseamfix$modelAddress() + VoxySeamFix.BLOCK_MODEL_FLAGS_OFFSET;
        MemoryUtil.memPutInt(flags, MemoryUtil.memGetInt(flags) | VoxySeamFix.FLUID_MODEL_FLAG);
    }
}
