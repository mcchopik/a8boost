package com.mcchopik.a8boost.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcchopik.a8boost.A8Boost;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class A8BoostConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public int configVersion = 1;
    public boolean applyHardwareProfile = true;
    public boolean profileApplied = false;
    public boolean adaptiveTuning = true;
    public int targetFps = 45;
    public int normalParticleBudget = 512;
    public int pressureParticleBudget = 256;
    public int criticalParticleBudget = 128;
    public int normalParticleDistance = 32;
    public int pressureParticleDistance = 24;
    public int criticalParticleDistance = 16;
    public int normalBlockEntityDistance = 48;
    public int pressureBlockEntityDistance = 32;
    public int criticalBlockEntityDistance = 24;

    private transient Path file;

    public static A8BoostConfig load(Path file) {
        A8BoostConfig config = null;
        if (Files.isRegularFile(file)) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                config = GSON.fromJson(reader, A8BoostConfig.class);
            } catch (Exception exception) {
                A8Boost.LOGGER.warn("Could not read {}, using safe defaults", file, exception);
            }
        }
        if (config == null) {
            config = new A8BoostConfig();
        }
        config.file = file;
        config.validate();
        return config;
    }

    public void save() {
        try {
            Files.createDirectories(file.getParent());
            Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
            }
            try {
                Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            A8Boost.LOGGER.warn("Could not save {}", file, exception);
        }
    }

    private void validate() {
        configVersion = 1;
        targetFps = clamp(targetFps, 20, 120);
        normalParticleBudget = clamp(normalParticleBudget, 64, 4096);
        pressureParticleBudget = clamp(pressureParticleBudget, 32, normalParticleBudget);
        criticalParticleBudget = clamp(criticalParticleBudget, 16, pressureParticleBudget);
        normalParticleDistance = clamp(normalParticleDistance, 8, 96);
        pressureParticleDistance = clamp(pressureParticleDistance, 8, normalParticleDistance);
        criticalParticleDistance = clamp(criticalParticleDistance, 6, pressureParticleDistance);
        normalBlockEntityDistance = clamp(normalBlockEntityDistance, 16, 128);
        pressureBlockEntityDistance = clamp(pressureBlockEntityDistance, 12, normalBlockEntityDistance);
        criticalBlockEntityDistance = clamp(criticalBlockEntityDistance, 8, pressureBlockEntityDistance);
    }

    private static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }
}
