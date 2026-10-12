package io.wasabi.urg.elements.boss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import io.wasabi.urg.elements.card.Card;
import io.wasabi.urg.elements.game.Tile;

class HouseTest {

    @Test
    void exposesFinalBossMetadata() {
        House house = new House();

        assertEquals("The House", house.getName());
        assertEquals("The house always wins...", house.getPhrase());
        assertEquals("The House disrupts the table, payouts, and your hand.", house.getDescription());
    }

    @Test
    void rejectsMissingDifficulty() {
        assertThrows(IllegalArgumentException.class, () -> new House(null));
    }

    @Test
    void findsNoTileWhenThereAreNoCandidates() {
        assertEquals(null, House.findMostUpgradedTile(Collections.<Tile>emptyList()));
        assertEquals(null, House.findMostUpgradedTile(Arrays.asList((Tile) null, null)));
    }

    @Test
    void ignoresNullTilesWhenFindingMostUpgradedTile() {
        assertEquals(null, House.findMostUpgradedTile(Collections.singletonList((Tile) null)));
    }

    @Test
    void doesNotCoverCardsWhenCountIsZeroOrNegative() {
        List<Card> cards = Arrays.asList((Card) null);

        assertDoesNotThrow(() -> House.coverRandomCards(cards, 0, new Random(1)));
        assertDoesNotThrow(() -> House.coverRandomCards(cards, -1, new Random(1)));
    }

    @Test
    void safelyHandlesNullCardsAndExcessiveCoverCount() {
        List<Card> cards = Arrays.asList((Card) null);

        assertDoesNotThrow(() -> House.coverRandomCards(cards, 3, new Random(1)));
    }
}
