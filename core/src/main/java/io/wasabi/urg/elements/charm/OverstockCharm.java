package io.wasabi.urg.elements.charm;

import com.badlogic.gdx.graphics.Color;

import io.wasabi.urg.Roulette;
import io.wasabi.urg.elements.game.Ball;
import io.wasabi.urg.managers.SoundManager;
import io.wasabi.urg.ui.FloatingText;

public class OverstockCharm extends Charm {

    public OverstockCharm() {
        super();
        tooltip.setTitle("Overstock Charm");
        tooltip.setDescription("Increase the number of items in the next shop.");
    }

    @Override
    public void consume() {
        if (requirements()) {
            super.consume();
            Roulette.getInstance().getGameScreen().getShop().addNextShopStock(1, 2);
            removeAndReturnToPool();
            SoundManager.getInstance().playSound("charmConsume");
        }
    }

    @Override
    public boolean requirements() {
        Ball ball = Roulette.getInstance().getGameScreen().getBall();

        if (ball.getState() != Ball.State.STOPPED
            || Roulette.getInstance().getGameScreen().getWheel().isSpinning()) {
            Roulette.getInstance().getGameScreen().addParticle(
                new FloatingText(
                    "You cannot use charms while the wheel is spinning!", getX(), getY(), Color.RED, 1f
                )
            );
            SoundManager.getInstance().playSound("error");
            return false;
        }

        return true;
    }
}
