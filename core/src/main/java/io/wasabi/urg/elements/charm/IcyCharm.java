package io.wasabi.urg.elements.charm;

import com.badlogic.gdx.graphics.Color;

import io.wasabi.urg.Roulette;
import io.wasabi.urg.elements.game.Ball;
import io.wasabi.urg.managers.SoundManager;
import io.wasabi.urg.ui.FloatingText;

public class IcyCharm extends Charm {

    private static final float FRICTION_MULTIPLIER = 0.5f;

    public IcyCharm() {
        super();
        tooltip.setTitle("Icy Charm");
        tooltip.setDescription("Reduce ball friction, allowing the ball to move for longer.");
    }

    @Override
    public void consume() {
        if (requirements()) {
            super.consume();

            Ball ball = Roulette.getInstance().getGameScreen().getBall();
            ball.setFrictionMultiplier(FRICTION_MULTIPLIER);

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
                    "You cannot use charms while the wheel is spinning!",
                    getX(),
                    getY(),
                    Color.RED,
                    1f
                )
            );

            SoundManager.getInstance().playSound("error");
            return false;
        }

        return true;
    }
}
