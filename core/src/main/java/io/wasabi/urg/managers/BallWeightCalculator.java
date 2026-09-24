package io.wasabi.urg.managers;

// How a ball's weight multiplier affects its physics behaviour
public final class BallWeightCalculator {

    private static final float MIN_DECELERATION_FACTOR = 0.001f;
    private static final float MIN_TIME_REQUIRED = 0.05f;

    private BallWeightCalculator() {
    }

    /**
     * A heavier ball loses speed faster, so the per second decay factor shrinks
     * as weightMultiplier grows above 1.
     */
    public static float adjustDecelerationFactor(float baseFactor, float weightMultiplier) {
        validateWeightMultiplier(weightMultiplier);
        return Math.max(MIN_DECELERATION_FACTOR, baseFactor / weightMultiplier);
    }

    /**
     * A heavier ball reaches state-transition speed thresholds (e.g. dropping
     * onto the wheel, settling into a pocket) sooner, so the threshold grows
     * as weightMultiplier grows above 1.
     */
    public static float adjustSpeedThreshold(float baseThreshold, float weightMultiplier) {
        validateWeightMultiplier(weightMultiplier);
        return baseThreshold * weightMultiplier;
    }

    /**
     * A heavier ball bleeds momentum faster every second (e.g. bouncing off
     * frets, settling into a pocket), so the per second damping fraction grows
     * towards 1 as weightMultiplier grows above 1.
     */
    public static float adjustDampingPerSecond(float baseDampingPerSecond, float weightMultiplier) {
        float retainedFraction = adjustDecelerationFactor(1f - baseDampingPerSecond, weightMultiplier);
        return 1f - retainedFraction;
    }

    /**
     * A heavier ball needs less time spent below a speed threshold before it's
     * considered settled, so the required time shrinks as weightMultiplier
     * grows above 1.
     */
    public static float adjustTimeRequired(float baseTimeRequired, float weightMultiplier) {
        validateWeightMultiplier(weightMultiplier);
        return Math.max(MIN_TIME_REQUIRED, baseTimeRequired / weightMultiplier);
    }

    private static void validateWeightMultiplier(float weightMultiplier) {
        if (weightMultiplier <= 0f) {
            throw new IllegalArgumentException("weightMultiplier must be greater than 0");
        }
    }
}
