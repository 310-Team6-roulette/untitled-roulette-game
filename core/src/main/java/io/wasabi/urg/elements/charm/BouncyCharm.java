package io.wasabi.urg.elements.charm;


import com.badlogic.gdx.graphics.Color;

import io.wasabi.urg.Roulette;
import io.wasabi.urg.elements.game.Ball;
import io.wasabi.urg.managers.SoundManager;
import io.wasabi.urg.ui.FloatingText;

public class BouncyCharm extends Charm {

    private static final String ERROR_SOUND = "error";

    public BouncyCharm() {
        super();
        tooltip.setTitle("Bouncy Charm");
        tooltip.setDescription("Apply to the wheel to make the ball bouncier.");
    }

    @Override
    public void consume() {
        if (requirements()) {
            super.consume();
            Ball ball = Roulette.getInstance().getGameScreen().getBall();
            ball.addBounciness(1);
            removeAndReturnToPool();
            SoundManager.getInstance().playSound("charmConsume");
        }
    }

    @Override
    public boolean requirements() {
        if (Roulette.getInstance().getGameScreen().getWheel().isSpinning()) {
            Roulette.getInstance().getGameScreen().addParticle(new FloatingText(
                    "You cannot use charms while the wheel is spinning!", getX(), getY(), Color.RED, 1f));
            SoundManager.getInstance().playSound(ERROR_SOUND);
            return false;
        }

        // No requirement for tile selection
        return true;
    }
}
