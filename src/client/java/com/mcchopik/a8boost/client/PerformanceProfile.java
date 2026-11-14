package com.mcchopik.a8boost.client;

import com.mcchopik.a8boost.A8Boost;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsPreset;
import net.minecraft.client.InactivityFpsLimit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

final class PerformanceProfile {
    private PerformanceProfile() {
    }

    static void apply(Minecraft client) {
        Options options = client.options;
        options.applyGraphicsPreset(GraphicsPreset.FAST);
        options.renderDistance().set(7);
        options.simulationDistance().set(5);
        options.entityDistanceScaling().set(0.60);
        options.framerateLimit().set(60);
        options.enableVsync().set(false);
        options.cloudStatus().set(CloudStatus.OFF);
        options.biomeBlendRadius().set(0);
        options.menuBackgroundBlurriness().set(0);
        options.vignette().set(false);
        options.chunkSectionFadeInTime().set(0.0);
        options.inactivityFpsLimit().set(InactivityFpsLimit.MINIMIZED);
        options.save();
        A8Boost.LOGGER.info("Applied the one-time AMD A8/Radeon R7 performance profile");
    }
}
