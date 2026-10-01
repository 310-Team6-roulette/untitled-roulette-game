package io.wasabi.urg.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefeatReasonTest {

    @Test
    void reportsOutOfChipsWhenPlayerIsBroke() {
        assertEquals(DefeatReason.OUT_OF_CHIPS, DefeatReason.from(0));
    }

    @Test
    void treatsNegativeChipsAsBroke() {
        assertEquals(DefeatReason.OUT_OF_CHIPS, DefeatReason.from(-25));
    }

    @Test
    void reportsOutOfSpinsWhenPlayerStillHasChips() {
        assertEquals(DefeatReason.OUT_OF_SPINS, DefeatReason.from(1));
        assertEquals(DefeatReason.OUT_OF_SPINS, DefeatReason.from(90));
    }
}
