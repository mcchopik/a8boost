package com.mcchopik.a8boost.client;

import com.mcchopik.a8boost.A8Boost;
import com.mcchopik.a8boost.AdaptivePolicy;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public final class AdaptiveController {
    private static final double CLOSE_PARTICLE_DISTANCE_SQUARED = 8.0 * 8.0;
    private static final int CLOSE_PARTICLE_RESERVE = 32;

    private final A8BoostConfig config;
    private final AdaptivePolicy policy = new AdaptivePolicy();
    private int particlesThisTick;

    AdaptiveController(A8BoostConfig config) {
        this.config = config;
    }

    public void sampleFrame(Minecraft client) {
        if (!config.adaptiveTuning || client.level == null) {
            return;
        }
        if (policy.sample(client.getFrameTimeNs(), config.targetFps)) {
            A8Boost.LOGGER.info(
                    "Adaptive level changed to {} (average frame {} ms)",
                    policy.level(),
                    String.format("%.1f", policy.averageFrameMillis()));
        }
    }

    public void beginParticleTick() {
        particlesThisTick = 0;
    }

    public boolean allowParticle(double x, double y, double z) {
        Minecraft client = Minecraft.getInstance();
        Entity camera = client.getCameraEntity();
        if (camera == null) {
            return true;
        }

        int level = effectiveLevel();
        int distance = switch (level) {
            case 2 -> config.criticalParticleDistance;
            case 1 -> config.pressureParticleDistance;
            default -> config.normalParticleDistance;
        };
        double distanceSquared = camera.distanceToSqr(x, y, z);
        if (distanceSquared > (double) distance * distance) {
            return false;
        }

        int budget = switch (level) {
            case 2 -> config.criticalParticleBudget;
            case 1 -> config.pressureParticleBudget;
            default -> config.normalParticleBudget;
        };
        particlesThisTick++;
        return particlesThisTick <= budget
                || (distanceSquared <= CLOSE_PARTICLE_DISTANCE_SQUARED
                        && particlesThisTick <= budget + CLOSE_PARTICLE_RESERVE);
    }

    public boolean allowBlockEntity(BlockEntity blockEntity, Vec3 cameraPosition) {
        int distance = switch (effectiveLevel()) {
            case 2 -> config.criticalBlockEntityDistance;
            case 1 -> config.pressureBlockEntityDistance;
            default -> config.normalBlockEntityDistance;
        };
        Vec3 center = Vec3.atCenterOf(blockEntity.getBlockPos());
        return cameraPosition.distanceToSqr(center) <= (double) distance * distance;
    }

    private int effectiveLevel() {
        return config.adaptiveTuning ? policy.level() : 0;
    }
}
