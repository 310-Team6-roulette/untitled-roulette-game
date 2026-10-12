package io.wasabi.urg.elements.boss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.wasabi.urg.Roulette;
import io.wasabi.urg.elements.betting.BetType;
import io.wasabi.urg.elements.card.Card;
import io.wasabi.urg.elements.game.Tile;
import io.wasabi.urg.elements.game.Wheel;
import io.wasabi.urg.state.RunState;

/** Final-act boss that temporarily takes control of the table and hand. */
public final class House extends Boss {
    public enum Difficulty {
        NORMAL(1, 0.8f),
        HARD(2, 0.8f),
        FINAL(3, 0.8f);

        private final int coveredCards;
        private final float evenMoneyPayout;

        Difficulty(int coveredCards, float evenMoneyPayout) {
            this.coveredCards = coveredCards;
            this.evenMoneyPayout = evenMoneyPayout;
        }
    }

    private static final float MIN_TILE_SIZE = 0.5f;
    private final Difficulty difficulty;
    private final Map<BetType, Float> originalPayouts = new EnumMap<BetType, Float>(BetType.class);
    private final List<Card> coveredCards = new ArrayList<Card>();
    private Wheel.State wheelStateBeforeBoss;

    public House() {
        this(Difficulty.FINAL);
    }

    public House(Difficulty difficulty) {
        super("The House", "The house always wins...", "The House disrupts the table, payouts, and your hand.");
        if (difficulty == null) {
            throw new IllegalArgumentException("difficulty cannot be null");
        }
        this.difficulty = difficulty;
    }

    @Override
    public void roundStartEffect() {
        RunState state = Roulette.getInstance().getRunState();
        Wheel wheel = Roulette.getInstance().getGameScreen().getWheel();

        wheelStateBeforeBoss = wheel.captureState();
        shrinkMostUpgradedTile(state.getTiles());
        wheel.resetNumbersPreservingColours();
        restorePayouts();
        reduceEvenMoneyPayouts();
        List<Card> previouslyCovered = new ArrayList<Card>();
        for (Card card : state.getOwnedCards()) {
            if (card != null && card.isCovered()) {
                previouslyCovered.add(card);
            }
        }
        coverRandomCards(state.getOwnedCards(), difficulty.coveredCards, new Random());
        coveredCards.clear();
        for (Card card : state.getOwnedCards()) {
            if (card != null && card.isCovered() && !previouslyCovered.contains(card)) {
                coveredCards.add(card);
            }
        }
    }

    @Override
    public void roundEndEffect() {
        restorePayouts();
        if (wheelStateBeforeBoss != null) {
            Roulette.getInstance().getGameScreen().getWheel().restoreState(wheelStateBeforeBoss);
            wheelStateBeforeBoss = null;
        }
        for (Card card : coveredCards) {
            card.setCovered(false);
        }
        coveredCards.clear();
    }

    private void shrinkMostUpgradedTile(List<Tile> tiles) {
        Tile mostUpgraded = findMostUpgradedTile(tiles);
        if (mostUpgraded != null) {
            mostUpgraded.setSize(Math.max(MIN_TILE_SIZE, mostUpgraded.getSize() * 0.5f));
        }
    }

    static Tile findMostUpgradedTile(List<Tile> tiles) {
        Tile mostUpgraded = null;
        for (Tile tile : tiles) {
            if (tile != null && (mostUpgraded == null
                    || tile.getCharmEffectCount() > mostUpgraded.getCharmEffectCount())) {
                mostUpgraded = tile;
            }
        }
        return mostUpgraded;
    }

    private void reduceEvenMoneyPayouts() {
        for (BetType type : new BetType[] { BetType.RED, BetType.BLACK, BetType.ODD, BetType.EVEN, BetType.HIGH, BetType.LOW }) {
            originalPayouts.put(type, type.payoutMultiplier);
            type.payoutMultiplier = difficulty.evenMoneyPayout;
        }
    }

    private void restorePayouts() {
        for (Map.Entry<BetType, Float> entry : originalPayouts.entrySet()) {
            entry.getKey().payoutMultiplier = entry.getValue();
        }
        originalPayouts.clear();
    }

    static void coverRandomCards(List<Card> cards, int count, Random random) {
        List<Card> available = new ArrayList<Card>();
        for (Card card : cards) {
            if (card != null && !card.isCovered()) {
                available.add(card);
            }
        }
        Collections.shuffle(available, random);
        int cardsToCover = Math.min(Math.max(count, 0), available.size());
        for (int i = 0; i < cardsToCover; i++) {
            available.get(i).setCovered(true);
        }
    }
}
