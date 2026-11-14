package com.mcchopik.a8boost.client.mixin;

import com.mcchopik.a8boost.client.A8BoostClient;
import com.mcchopik.a8boost.client.AdaptiveController;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityRenderDispatcher.class)
abstract class BlockEntityRenderDispatcherMixin {
    @Shadow
    private Vec3 cameraPos;

    @Inject(method = "tryExtractRenderState", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity, S extends BlockEntityRenderState> void a8boost$cullDistantBlockEntities(
            E blockEntity,
            float partialTick,
            ModelFeatureRenderer.CrumblingOverlay breakingOverlay,
            boolean offScreenPass,
            CallbackInfoReturnable<S> callbackInfo) {
        AdaptiveController controller = A8BoostClient.controller();
        if (!offScreenPass
                && controller != null
                && cameraPos != null
                && !controller.allowBlockEntity(blockEntity, cameraPos)) {
            callbackInfo.setReturnValue(null);
        }
    }
}
