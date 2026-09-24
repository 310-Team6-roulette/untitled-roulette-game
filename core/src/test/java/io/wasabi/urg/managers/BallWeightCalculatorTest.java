package io.wasabi.urg.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BallWeightCalculatorTest {

    @Test
    void leavesDecelerationFactorUnchangedAtDefaultWeight() {
        assertEquals(0.5f, BallWeightCalculator.adjustDecelerationFactor(0.5f, 1f));
        assertEquals(0.2f, BallWeightCalculator.adjustDecelerationFactor(0.2f, 1f));
    }

    @Test
    void shrinksDecelerationFactorAsWeightIncreases() {
        assertEquals(0.25f, BallWeightCalculator.adjustDecelerationFactor(0.5f, 2f));
        assertEquals(0.1f, BallWeightCalculator.adjustDecelerationFactor(0.2f, 2f));
    }

    @Test
    void growsDecelerationFactorAsWeightDecreases() {
        assertEquals(1f, BallWeightCalculator.adjustDecelerationFactor(0.5f, 0.5f));
    }

    @Test
    void clampsDecelerationFactorToAMinimumForExtremeWeights() {
        assertEquals(0.001f, BallWeightCalculator.adjustDecelerationFactor(0.5f, 1_000f));
    }

    @Test
    void leavesSpeedThresholdUnchangedAtDefaultWeight() {
        assertEquals(400f, BallWeightCalculator.adjustSpeedThreshold(400f, 1f));
    }

    @Test
    void growsSpeedThresholdAsWeightIncreases() {
        assertEquals(600f, BallWeightCalculator.adjustSpeedThreshold(400f, 1.5f));
        assertEquals(800f, BallWeightCalculator.adjustSpeedThreshold(400f, 2f));
        assertEquals(100f, BallWeightCalculator.adjustSpeedThreshold(50f, 2f));
    }

    @Test
    void shrinksSpeedThresholdAsWeightDecreases() {
        assertEquals(200f, BallWeightCalculator.adjustSpeedThreshold(400f, 0.5f));
    }

    @Test
    void leavesDampingPerSecondUnchangedAtDefaultWeight() {
        assertEquals(0.25f, BallWeightCalculator.adjustDampingPerSecond(0.25f, 1f), 1e-6f);
        assertEquals(0.6f, BallWeightCalculator.adjustDampingPerSecond(0.6f, 1f), 1e-6f);
    }

    @Test
    void growsDampingPerSecondAsWeightIncreases() {
        // retained fraction (1 - 0.25) halves from 0.75 to 0.375, so damping rises from 0.25 to 0.625
        assertEquals(0.625f, BallWeightCalculator.adjustDampingPerSecond(0.25f, 2f), 1e-6f);
        assertTrue(BallWeightCalculator.adjustDampingPerSecond(0.25f, 2f)
            > BallWeightCalculator.adjustDampingPerSecond(0.25f, 1f));
    }

    @Test
    void shrinksDampingPerSecondAsWeightDecreases() {
        assertTrue(BallWeightCalculator.adjustDampingPerSecond(0.25f, 0.5f)
            < BallWeightCalculator.adjustDampingPerSecond(0.25f, 1f));
    }

    @Test
    void leavesTimeRequiredUnchangedAtDefaultWeight() {
        assertEquals(0.5f, BallWeightCalculator.adjustTimeRequired(0.5f, 1f));
    }

    @Test
    void shrinksTimeRequiredAsWeightIncreases() {
        assertEquals(0.25f, BallWeightCalculator.adjustTimeRequired(0.5f, 2f));
    }

    @Test
    void growsTimeRequiredAsWeightDecreases() {
        assertEquals(1f, BallWeightCalculator.adjustTimeRequired(0.5f, 0.5f));
    }

    @Test
    void clampsTimeRequiredToAMinimumForExtremeWeights() {
        assertEquals(0.05f, BallWeightCalculator.adjustTimeRequired(0.5f, 1_000f));
    }

    @Test
    void rejectsZeroOrNegativeWeightMultipliers() {
        assertThrows(IllegalArgumentException.class,
            () -> BallWeightCalculator.adjustDecelerationFactor(0.5f, 0f));
        assertThrows(IllegalArgumentException.class,
            () -> BallWeightCalculator.adjustDecelerationFactor(0.5f, -1f));
        assertThrows(IllegalArgumentException.class,
            () -> BallWeightCalculator.adjustSpeedThreshold(400f, 0f));
        assertThrows(IllegalArgumentException.class,
            () -> BallWeightCalculator.adjustSpeedThreshold(400f, -1f));
        assertThrows(IllegalArgumentException.class,
            () -> BallWeightCalculator.adjustDampingPerSecond(0.25f, 0f));
        assertThrows(IllegalArgumentException.class,
            () -> BallWeightCalculator.adjustTimeRequired(0.5f, 0f));
    }
}
