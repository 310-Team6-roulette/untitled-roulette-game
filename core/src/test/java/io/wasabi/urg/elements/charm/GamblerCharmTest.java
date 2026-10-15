package io.wasabi.urg.elements.charm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GamblerCharmTest {
    @Test
    void calculatesHalfCurrentBalanceWithRounding() {
        assertEquals(50, GamblerCharm.calculateLandingPenalty(100));
        assertEquals(51, GamblerCharm.calculateLandingPenalty(101));
        assertEquals(0, GamblerCharm.calculateLandingPenalty(0));
    }

    @Test
    void rejectsNegativeBalance() {
        assertThrows(IllegalArgumentException.class, () -> GamblerCharm.calculateLandingPenalty(-1));
    }
}
