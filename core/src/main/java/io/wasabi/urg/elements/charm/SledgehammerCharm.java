package io.wasabi.urg.elements.charm;

import java.util.List;

import com.badlogic.gdx.graphics.Color;

import io.wasabi.urg.Roulette;
import io.wasabi.urg.elements.game.Tile;
import io.wasabi.urg.managers.SoundManager;
import io.wasabi.urg.ui.FloatingText;

public class SledgehammerCharm extends Charm {

    public SledgehammerCharm() {
        super("charmBlank");
        tooltip.setTitle("Sledgehammer Charm");
        tooltip.setDescription("Choose one tile and permanently remove it from the wheel.");
    }

    @Override
    public void consume() {
        if (!requirements()) {
            return;
        }

        super.consume();
        List<Tile> selectedTiles = Roulette.getInstance().getRunState().getSelectedTiles();
        Roulette.getInstance().getRunState().removeTile(selectedTiles.get(0));
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
            showError("Select one tile!");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        Roulette.getInstance().getGameScreen().addParticle(new FloatingText(message, getX(), getY(), Color.RED, 1f));
        SoundManager.getInstance().playSound("error");
    }
}
