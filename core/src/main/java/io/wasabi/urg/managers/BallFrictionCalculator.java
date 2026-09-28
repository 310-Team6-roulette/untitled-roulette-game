package io.wasabi.urg.managers;

public final class BallFrictionCalculator {

    private BallFrictionCalculator() {
    }

    /**
     * Adjusts a deceleration factor based on the ball's friction multiplier.
     * Lower friction causes the ball to retain more of its speed.
     *
     * @param baseFactor the original deceleration factor
     * @param frictionMultiplier the multiplier applied to friction
     * @return the adjusted deceleration factor
     * @throws IllegalArgumentException if frictionMultiplier is zero or negative
     */
    public static float adjustDecelerationFactor(float baseFactor, float frictionMultiplier) {

        if (frictionMultiplier <= 0f) {
            throw new IllegalArgumentException("frictionMultiplier must be greater than 0");
        }

        return 1f - ((1f - baseFactor) * frictionMultiplier);
    }

    /**
     * Adjusts the amount of damping applied per second based on the ball's
     * friction multiplier. Lower friction results in less damping.
     *
     * @param baseDamping the original damping applied per second
     * @param frictionMultiplier the multiplier applied to friction
     * @return the adjusted damping per second
     * @throws IllegalArgumentException if frictionMultiplier is zero or negative
     */
    public static float adjustDampingPerSecond(float baseDamping, float frictionMultiplier) {

        if (frictionMultiplier <= 0f) {
            throw new IllegalArgumentException("frictionMultiplier must be greater than 0");
        }

        return baseDamping * frictionMultiplier;
    }

    /**
     * Adjusts a speed threshold based on the ball's friction multiplier.
     * Lower friction increases the threshold used when determining when the
     * ball can begin settling.
     *
     * @param baseThreshold the original speed threshold
     * @param frictionMultiplier the multiplier applied to friction
     * @return the adjusted speed threshold
     * @throws IllegalArgumentException if frictionMultiplier is zero or negative
     */
    public static float adjustSpeedThreshold(float baseThreshold, float frictionMultiplier) {

        if (frictionMultiplier <= 0f) {
            throw new IllegalArgumentException("frictionMultiplier must be greater than 0");
        }

        return baseThreshold / frictionMultiplier;
    }

    /**
     * Adjusts the amount of time required for the ball to settle based on its
     * friction multiplier. Lower friction causes the ball to take longer to settle.
     *
     * @param baseTime the original settling time
     * @param frictionMultiplier the multiplier applied to friction
     * @return the adjusted time required
     * @throws IllegalArgumentException if frictionMultiplier is zero or negative
     */
    public static float adjustTimeRequired(float baseTime, float frictionMultiplier) {

        if (frictionMultiplier <= 0f) {
            throw new IllegalArgumentException("frictionMultiplier must be greater than 0");
        }

        return baseTime / frictionMultiplier;
    }
}
