package io.wasabi.urg.elements.charm;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import io.wasabi.urg.elements.game.Tile;

class SledgehammerCharmTest {

    @Test
    void rejectsAnEmptySelection() {
        assertFalse(SledgehammerCharm.hasExactlyOneSelection(Collections.emptyList()));
    }

    @Test
    void acceptsExactlyOneSelectedTile() {
        assertTrue(SledgehammerCharm.hasExactlyOneSelection(Collections.singletonList((Tile) null)));
    }

    @Test
    void rejectsMultipleSelectedTiles() {
        assertFalse(SledgehammerCharm.hasExactlyOneSelection(Arrays.asList(null, null)));
    }
}