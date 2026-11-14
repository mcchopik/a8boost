package com.mcchopik.a8boost;

public final class AdaptivePolicy {
    static final int SAMPLE_WINDOW = 40;
    private static final double SMOOTHING = 0.12;

    private double averageFrameNanos;
    private int samples;
    private int level;

    public boolean sample(long frameNanos, int targetFps) {
        if (frameNanos <= 0 || targetFps <= 0) {
            return false;
        }

        if (averageFrameNanos == 0) {
            averageFrameNanos = frameNanos;
        } else {
            averageFrameNanos += (frameNanos - averageFrameNanos) * SMOOTHING;
        }

        if (++samples < SAMPLE_WINDOW) {
            return false;
        }
        samples = 0;

        double targetFrameNanos = 1_000_000_000.0 / targetFps;
        double load = averageFrameNanos / targetFrameNanos;
        int previous = level;
        if (load >= 1.60) {
            level = 2;
        } else if (load >= 1.15) {
            level = Math.max(level, 1);
        } else if (load <= 0.90) {
            level = Math.max(0, level - 1);
        }
        return previous != level;
    }

    public int level() {
        return level;
    }

    public double averageFrameMillis() {
        return averageFrameNanos / 1_000_000.0;
    }
}
