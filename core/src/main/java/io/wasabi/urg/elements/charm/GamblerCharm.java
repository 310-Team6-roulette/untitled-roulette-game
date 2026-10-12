package io.wasabi.urg.elements.charm;

import java.util.List;

import com.badlogic.gdx.graphics.Color;

import io.wasabi.urg.Roulette;
import io.wasabi.urg.elements.game.Tile;
import io.wasabi.urg.managers.SoundManager;
import io.wasabi.urg.ui.FloatingText;

/**
 * Marks one wheel pocket as evil. The current balance values are deliberately
 * kept here so they can be tuned without changing payout or state management.
 */
public class GamblerCharm extends Charm {
    public static final float GAIN_MULTIPLIER = 1.25f;
    public static final float LANDING_PENALTY_PERCENT = 0.50f;

    public static int calculateLandingPenalty(int chips) {
        if (chips < 0) {
            throw new IllegalArgumentException("chips cannot be negative");
        }
        return Math.round(chips * LANDING_PENALTY_PERCENT);
    }

    public GamblerCharm() {
        super("GamblerCharm");
        tooltip.setTitle("Gambler's Charm");
        tooltip.setDescription("Choose one tile. Gains are increased by 25%, but landing on it loses 50% of your current chips.");
    }

    @Override
    public void consume() {
        if (!requirements()) {
            return;
        }

        super.consume();
        Tile selectedTile = Roulette.getInstance().getRunState().getSelectedTiles().get(0);
        Roulette.getInstance().getRunState().setEvilTile(selectedTile);
        Roulette.getInstance().getRunState().clearSelectedTiles();
        removeAndReturnToPool();
        SoundManager.getInstance().playSound("charmConsume");
    }

    @Override
    public boolean requirements() {
        if (Roulette.getInstance().getGameScreen().getWheel().isSpinning()) {
            showError("You cannot use charms while the wheel is spinning!");
            return false;
        }

        List<Tile> selectedTiles = Roulette.getInstance().getRunState().getSelectedTiles();
        if (selectedTiles.size() != 1) {
            showError("Select exactly one tile!");
            return false;
        }

        if (Roulette.getInstance().getRunState().getEvilTile() != null) {
            showError("An Evil tile already exists!");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        Roulette.getInstance().getGameScreen().addParticle(
            new FloatingText(message, getX(), getY(), Color.RED, 1f)
        );
        SoundManager.getInstance().playSound("error");
    }
}
