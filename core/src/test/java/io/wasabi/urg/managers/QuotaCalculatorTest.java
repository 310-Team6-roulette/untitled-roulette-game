package io.wasabi.urg.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuotaCalculatorTest {

    @Test
    void usesConfiguredBaseQuotasForFirstThreeActs() {
        assertEquals(125, QuotaCalculator.calculate(1, 1));
        assertEquals(450, QuotaCalculator.calculate(2, 2));
        assertEquals(3_000, QuotaCalculator.calculate(3, 5));
    }

    @Test
    void appliesEachRoundMultiplier() {
        int[] expectedQuotas = {125, 150, 200, 275, 300};

        for (int round = 1; round <= expectedQuotas.length; round++) {
            assertEquals(expectedQuotas[round - 1], QuotaCalculator.calculate(1, round));
        }
    }

    @Test
    void scalesEndlessActsFromTheThirdActQuota() {
        assertEquals(3_750, QuotaCalculator.calculate(4, 1));
        assertEquals(11_250, QuotaCalculator.calculate(5, 1));
        assertEquals(33_750, QuotaCalculator.calculate(6, 1));
    }

    @Test
    void capsVeryLargeQuotasAtIntegerMaximum() {
        assertEquals(Integer.MAX_VALUE, QuotaCalculator.calculate(17, 5));
        assertEquals(Integer.MAX_VALUE, QuotaCalculator.calculate(Integer.MAX_VALUE, 1));
    }

    @Test
    void rejectsInvalidActs() {
        assertThrows(IllegalArgumentException.class, () -> QuotaCalculator.calculate(0, 1));
        assertThrows(IllegalArgumentException.class, () -> QuotaCalculator.calculate(-1, 1));
    }

    @Test
    void rejectsInvalidRounds() {
        assertThrows(IllegalArgumentException.class, () -> QuotaCalculator.calculate(1, 0));
        assertThrows(IllegalArgumentException.class, () -> QuotaCalculator.calculate(1, 6));
    }
}