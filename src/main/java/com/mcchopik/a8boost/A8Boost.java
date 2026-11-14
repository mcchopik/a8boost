package com.mcchopik.a8boost;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class A8Boost implements ModInitializer {
    public static final String MOD_ID = "a8boost";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("A8Boost initialized");
    }
}
