package io.wasabi.urg.elements.boss;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaxmanTest {

    @Test
    void constructorSetsExpectedMetadata() {
        Taxman taxman = new Taxman();
        assertEquals("The Taxman", taxman.getName());
        assertEquals("He always gets his cut.", taxman.getPhrase());
        assertEquals("After every spin, a flat house fee is skimmed from your chips.", taxman.getDescription());
    }

    @Test
    void takesFlatFeeWhenChipsAreSufficient() {
        assertEquals(20, Taxman.calculateTax(100, 20));
        assertEquals(20, Taxman.calculateTax(20, 20));
    }

    @Test
    void takesOnlyWhatRemainsWhenChipsAreLow() {
        assertEquals(7, Taxman.calculateTax(7, 20));
        assertEquals(1, Taxman.calculateTax(1, 20));
    }

    @Test
    void takesNothingWhenChipsAreZero() {
        assertEquals(0, Taxman.calculateTax(0, 20));
    }

    @Test
    void zeroFeeTakesNothing() {
        assertEquals(0, Taxman.calculateTax(100, 0));
        assertEquals(0, Taxman.calculateTax(0, 0));
    }

    @Test
    void rejectsNegativeValues() {
        assertThrows(IllegalArgumentException.class, () -> Taxman.calculateTax(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> Taxman.calculateTax(100, -1));
        assertThrows(IllegalArgumentException.class, () -> Taxman.calculateTax(-1, -1));
    }
}
