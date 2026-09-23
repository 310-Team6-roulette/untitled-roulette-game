package io.wasabi.urg.elements.card;

import io.wasabi.urg.Roulette;
import io.wasabi.urg.elements.game.Ball;

public class OverweightSticker extends Card {
    private static final float WEIGHT_MULTIPLIER = 2f;

    public OverweightSticker() {
        super(Rarity.COMMON);
        this.price = 4;
        this.sellPrice = 2;
        tooltip.setTitle("Overweight Sticker");
        tooltip.setDescription(
            "The ball is [RED]twice as heavy[BLACK], decelerating faster and dropping onto the wheel sooner");
    }

    @Override
    public void roundStartEffect() {
        setBallWeightMultiplier(WEIGHT_MULTIPLIER);
    }

    @Override
    public void roundEndEffect() {
        setBallWeightMultiplier(1f);
    }

    @Override
    public void removedEffect() {
        setBallWeightMultiplier(1f);
    }

    private void setBallWeightMultiplier(float multiplier) {
        Ball ball = Roulette.getInstance().getGameScreen().getBall();
        if (ball != null) {
            ball.setWeightMultiplier(multiplier);
        }
    }
}
