package io.wasabi.urg.elements.boss;

import io.wasabi.urg.Roulette;

public class Taxman extends Boss {

    private static final int FEE_PER_SPIN = 20;

    public Taxman() {
        super("The Taxman", "He always gets his cut.", "After every spin, a flat house fee is skimmed from your chips.");
    }

    @Override
    public void afterSpinEffect() {
        int chips = Roulette.getInstance().getRunState().getChips();
        Roulette.getInstance().getRunState().spendChips(calculateTax(chips, FEE_PER_SPIN));
    }

    /**
     * Calculates the flat house fee a spin should cost, never taxing more than
     * the player currently holds so chips can never go negative.
     *
     * @param chips The player's current chip count.
     * @param fee The flat fee the Taxman charges per spin.
     * @return The amount to remove from the player's chips.
     */
    public static int calculateTax(int chips, int fee) {
        if (chips < 0 || fee < 0) {
            throw new IllegalArgumentException("chips and fee cannot be negative");
        }
        return Math.min(chips, fee);
    }
}
