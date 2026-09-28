package io.wasabi.urg.elements.charm;

import java.util.List;

import com.badlogic.gdx.graphics.Color;

import io.wasabi.urg.Roulette;
import io.wasabi.urg.elements.game.Tile;
import io.wasabi.urg.elements.tiles.PaperTile;
import io.wasabi.urg.elements.tiles.TileType;
import io.wasabi.urg.managers.SoundManager;
import io.wasabi.urg.ui.FloatingText;

public class PaperCharm extends Charm {

    public PaperCharm() {
        super();
        tooltip.setTitle("Paper Charm");
        tooltip.setDescription("Choose one tile and enchant it with Paper. Gain an extra 100 chips when scored; Paper breaks after scoring.");
    }

    @Override
    public void consume() {
        if (requirements()) {
            super.consume();
            List<Tile> selectedTiles = Roulette.getInstance().getRunState().getSelectedTiles();
            Tile tile = selectedTiles.get(0);
            TileType paperType = new PaperTile(tile.getType(), tile);
            tile.setTemporaryType(paperType);
            Roulette.getInstance().getRunState().clearSelectedTiles();
            removeAndReturnToPool();
            SoundManager.getInstance().playSound("charmConsume");
        }
    }

    @Override
    public boolean requirements() {
        if (Roulette.getInstance().getGameScreen().getWheel().isSpinning()) {
            showError("You cannot use charms while the wheel is spinning!");
            return false;
        }

        List<Tile> selectedTiles = Roulette.getInstance().getRunState().getSelectedTiles();
        if (selectedTiles.isEmpty()) {
            showError("Select one tile!");
        } else if (selectedTiles.size() > 1) {
            showError("You can only select one tile!");
        } else if (selectedTiles.get(0).getType() instanceof PaperTile) {
            showError("This tile is already paper!");
        } else {
            return true;
        }
        return false;
    }

    private void showError(String message) {
        Roulette.getInstance().getGameScreen().addParticle(new FloatingText(message, getX(), getY(), Color.RED, 1f));
        SoundManager.getInstance().playSound("error");
    }
}