package com.mcchopik.a8boost;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AdaptivePolicyTest {
    @Test
    void ignoresInvalidSamples() {
        AdaptivePolicy policy = new AdaptivePolicy();

        assertFalse(policy.sample(0, 45));
        assertFalse(policy.sample(20_000_000, 0));
        assertEquals(0, policy.level());
    }

    @Test
    void entersPressureAndCriticalLevels() {
        AdaptivePolicy policy = new AdaptivePolicy();

        feedWindow(policy, 28_000_000, 45);
        assertEquals(1, policy.level());

        feedWindow(policy, 45_000_000, 45);
        assertEquals(2, policy.level());
    }

    @Test
    void recoversOneLevelPerFastWindow() {
        AdaptivePolicy policy = new AdaptivePolicy();
        feedWindow(policy, 45_000_000, 45);
        assertEquals(2, policy.level());

        assertTrue(feedUntilChanged(policy, 10_000_000, 45, 4));
        assertEquals(1, policy.level());
        assertTrue(feedUntilChanged(policy, 10_000_000, 45, 4));
        assertEquals(0, policy.level());
    }

    private static void feedWindow(AdaptivePolicy policy, long frameNanos, int targetFps) {
        for (int i = 0; i < AdaptivePolicy.SAMPLE_WINDOW; i++) {
            policy.sample(frameNanos, targetFps);
        }
    }

    private static boolean feedUntilChanged(
            AdaptivePolicy policy, long frameNanos, int targetFps, int maximumWindows) {
        for (int window = 0; window < maximumWindows; window++) {
            for (int i = 0; i < AdaptivePolicy.SAMPLE_WINDOW; i++) {
                if (policy.sample(frameNanos, targetFps)) {
                    return true;
                }
            }
        }
        return false;
    }
}
