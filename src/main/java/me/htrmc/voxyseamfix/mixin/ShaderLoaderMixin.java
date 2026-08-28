package me.htrmc.voxyseamfix.mixin;

import me.cortex.voxy.client.core.gl.shader.ShaderLoader;
import me.htrmc.voxyseamfix.ShaderPatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShaderLoader.class)
public class ShaderLoaderMixin {
    @Inject(method = "parse", at = @At("RETURN"), cancellable = true)
    private static void voxyseamfix$patch(String id, CallbackInfoReturnable<String> cir) {
        String source = cir.getReturnValue();
        if (source == null) {
            return;
        }
        String patched = ShaderPatcher.patch(id, source);
        if (!patched.equals(source)) {
            cir.setReturnValue(patched);
        }
    }
}
