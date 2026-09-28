package io.wasabi.urg.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BallFrictionCalculatorTest {

    @Test
    void leavesDecelerationFactorUnchangedAtDefaultFriction() {
        assertEquals(0.5f, BallFrictionCalculator.adjustDecelerationFactor(0.5f, 1f), 1e-6f);
        assertEquals(0.2f, BallFrictionCalculator.adjustDecelerationFactor(0.2f, 1f), 1e-6f);
    }

    @Test
    void increasesDecelerationFactorWhenFrictionIsReduced() {
        assertEquals(0.75f,BallFrictionCalculator.adjustDecelerationFactor(0.5f, 0.5f));
        assertEquals(0.6f,BallFrictionCalculator.adjustDecelerationFactor(0.2f, 0.5f));
    }

    @Test
    void reducedFrictionRetainsMoreSpeed() {
        float normal =BallFrictionCalculator.adjustDecelerationFactor(0.5f, 1f);
        float icy =BallFrictionCalculator.adjustDecelerationFactor(0.5f, 0.5f);
        assertTrue(icy > normal);
    }

    @Test
    void leavesDampingUnchangedAtDefaultFriction() {
        assertEquals(0.25f,BallFrictionCalculator.adjustDampingPerSecond(0.25f, 1f));

        assertEquals(0.6f, BallFrictionCalculator.adjustDampingPerSecond(0.6f, 1f));
    }

    @Test
    void reducesDampingWhenFrictionIsReduced() {
        assertEquals(0.125f, BallFrictionCalculator.adjustDampingPerSecond(0.25f, 0.5f));
        assertEquals(0.3f, BallFrictionCalculator.adjustDampingPerSecond(0.6f, 0.5f));
    }

    @Test
    void increasesSettleThresholdWhenFrictionIsReduced() {
        assertEquals(100f, BallFrictionCalculator.adjustSpeedThreshold(50f, 0.5f));
    }

    @Test
    void increasesSettleTimeWhenFrictionIsReduced() {
        assertEquals(1f, BallFrictionCalculator.adjustTimeRequired(0.5f, 0.5f));
    }

    @Test
    void rejectsZeroOrNegativeFrictionMultipliers() {
        assertThrows(IllegalArgumentException.class,
            () -> BallFrictionCalculator.adjustDecelerationFactor(0.5f, 0f));

        assertThrows(IllegalArgumentException.class,
            () -> BallFrictionCalculator.adjustDecelerationFactor(0.5f, -1f));

        assertThrows(IllegalArgumentException.class,
            () -> BallFrictionCalculator.adjustDampingPerSecond(0.25f, 0f));

        assertThrows(IllegalArgumentException.class,
            () -> BallFrictionCalculator.adjustSpeedThreshold(50f, 0f));

        assertThrows(IllegalArgumentException.class,
            () -> BallFrictionCalculator.adjustTimeRequired(0.5f, 0f));
    }
}
