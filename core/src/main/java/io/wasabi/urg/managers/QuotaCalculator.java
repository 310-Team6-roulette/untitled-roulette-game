package io.wasabi.urg.managers;

public final class QuotaCalculator {

    private static final int ROUNDS_PER_ACT = 5;

    private static final int[] ACT_BASE_QUOTAS = {100, 300, 1000}; // Base quotas for first 3 acts (non-endless)
    private static final float[] ROUND_MULTIPLIERS = {1.25f, 1.5f, 2.0f, 2.75f, 3.0f}; // Multipliers for each round
    
    private static final int ENDLESS_ACT_SCALE = 3; // Base quota multiplier for endless acts (acts beyond the first 3)
    
    private QuotaCalculator() {
    }

    public static int calculate(int act, int round) {
        if (act < 1 || round < 1 || round > ROUNDS_PER_ACT) {
            throw new IllegalArgumentException("act and round must describe a valid round");
        }

        int baseQuota = getBaseQuota(act);

        return Math.min(Integer.MAX_VALUE, Math.round(baseQuota * ROUND_MULTIPLIERS[round - 1]));
    }

    private static int getBaseQuota(int act) {
        if (act <= ACT_BASE_QUOTAS.length) {
            return ACT_BASE_QUOTAS[act - 1];
        }

        long quota = ACT_BASE_QUOTAS[ACT_BASE_QUOTAS.length - 1];
        int endlessAct = act - ACT_BASE_QUOTAS.length;

        // Long is used for quota to prevent overflow during multiplication. 
        for (int i = 0; i < endlessAct; i++) {
            if (quota > Integer.MAX_VALUE / ENDLESS_ACT_SCALE) {
                return Integer.MAX_VALUE;
            }
            quota *= ENDLESS_ACT_SCALE;
        }

        return (int) quota;
    }
}
