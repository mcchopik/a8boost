package com.mcchopik.a8boost.client;

import com.mcchopik.a8boost.A8Boost;
import java.nio.file.Path;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;

public final class A8BoostClient implements ClientModInitializer {
    private static AdaptiveController controller;

    @Override
    public void onInitializeClient() {
        Path configFile = FabricLoader.getInstance().getConfigDir().resolve("a8boost.json");
        A8BoostConfig config = A8BoostConfig.load(configFile);
        controller = new AdaptiveController(config);

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            if (config.applyHardwareProfile && !config.profileApplied) {
                PerformanceProfile.apply(client);
                config.profileApplied = true;
                config.save();
            }
            A8Boost.LOGGER.info("Adaptive tuning: {}, target: {} FPS", config.adaptiveTuning, config.targetFps);
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> controller.sampleFrame(client));
    }

    public static AdaptiveController controller() {
        return controller;
    }
}
