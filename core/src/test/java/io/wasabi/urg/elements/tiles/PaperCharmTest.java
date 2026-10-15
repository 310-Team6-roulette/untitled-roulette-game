package io.wasabi.urg.elements.tiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import io.wasabi.urg.elements.betting.WinBreakdown;

class PaperCharmTest {

    @Test
    void awardsExactlyOneHundredChips() {
        assertEquals(100f, PaperTile.POST_MULTIPLIER_BONUS);
    }

    @Test
    void addsPaperBonusAfterAllMultipliers() {
        WinBreakdown breakdown = new WinBreakdown(
            10, 50, 10, 5f, 0f, 2f, 3f, PaperTile.POST_MULTIPLIER_BONUS, 400, null);

        int multipliedWinnings = Math.round(
            breakdown.getRawPayout()
                * breakdown.getTileMultiplier()
                * breakdown.getGlobalMultiplier());
        int finalWinnings = multipliedWinnings + Math.round(breakdown.getPostMultiplierBonus());

        assertEquals(300, multipliedWinnings);
        assertEquals(400, finalWinnings);
        assertEquals(finalWinnings, breakdown.getFinalTotal());
    }
}