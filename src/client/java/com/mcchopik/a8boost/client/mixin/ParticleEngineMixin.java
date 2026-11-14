package com.mcchopik.a8boost.client.mixin;

import com.mcchopik.a8boost.client.A8BoostClient;
import com.mcchopik.a8boost.client.AdaptiveController;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleEngine.class)
abstract class ParticleEngineMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void a8boost$beginParticleTick(CallbackInfo callbackInfo) {
        AdaptiveController controller = A8BoostClient.controller();
        if (controller != null) {
            controller.beginParticleTick();
        }
    }

    @Inject(method = "createParticle", at = @At("HEAD"), cancellable = true)
    private void a8boost$limitParticleCreation(
            ParticleOptions options,
            double x,
            double y,
            double z,
            double velocityX,
            double velocityY,
            double velocityZ,
            CallbackInfoReturnable<Particle> callbackInfo) {
        AdaptiveController controller = A8BoostClient.controller();
        if (controller != null && !controller.allowParticle(x, y, z)) {
            callbackInfo.setReturnValue(null);
        }
    }
}
