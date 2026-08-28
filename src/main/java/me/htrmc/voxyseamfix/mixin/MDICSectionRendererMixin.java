package me.htrmc.voxyseamfix.mixin;

import me.cortex.voxy.client.core.gl.GlBuffer;
import me.cortex.voxy.client.core.rendering.section.backend.mdic.MDICSectionRenderer;
import me.cortex.voxy.client.core.rendering.section.backend.mdic.MDICViewport;
import me.cortex.voxy.client.core.rendering.util.UploadStream;
import me.htrmc.voxyseamfix.VoxySeamFix;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MDICSectionRenderer.class)
public class MDICSectionRendererMixin {
    @Shadow @Final private GlBuffer uniform;

    @Inject(method = "uploadUniformBuffer", at = @At("TAIL"))
    private void voxyseamfix$uploadFluidSurface(MDICViewport viewport, CallbackInfo ci) {
        long ptr = UploadStream.INSTANCE.upload(this.uniform, VoxySeamFix.SCENE_UNIFORM_FLUID_SURFACE_OFFSET, 4);
        MemoryUtil.memPutFloat(ptr, VoxySeamFix.relativeFluidSurfaceY(viewport.section.y));
        UploadStream.INSTANCE.commit();
    }
}
