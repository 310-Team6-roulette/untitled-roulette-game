package io.wasabi.urg.ui;

/**
 * Why a run ended without meeting the quota. The player either went broke mid round
 * or used up every spin before reaching the quota.
 */
public enum DefeatReason {
    OUT_OF_CHIPS("YOU WENT BROKE"),
    OUT_OF_SPINS("YOU RAN OUT OF SPINS");

    private final String message;

    DefeatReason(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Running out of chips takes priority, since a player at $0 cannot keep betting even
     * if they still had spins left.
     */
    public static DefeatReason from(int chips) {
        return chips <= 0 ? OUT_OF_CHIPS : OUT_OF_SPINS;
    }
}
