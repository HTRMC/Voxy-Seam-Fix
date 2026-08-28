package me.htrmc.voxyseamfix.mixin;

import me.cortex.voxy.common.util.MemoryBuffer;
import me.htrmc.voxyseamfix.ModelBufferAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "me.cortex.voxy.client.core.model.ModelFactory$ModelBakeResultUpload")
public abstract class ModelBakeResultUploadMixin implements ModelBufferAccess {
    @Shadow @Final private MemoryBuffer model;

    @Override
    public long voxyseamfix$modelAddress() {
        return this.model.address;
    }
}
