package io.wasabi.urg.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Defeat screen that slides up over the game when the player fails to meet the quota.
 * Shows the quota, the chips and spins the player had left, how far they got, and lets
 * them either start a new run or return to the main menu.
 *
 * It also acts as a modal input layer via {@link ModalEndScreen}: registered first in the
 * game screen's input multiplexer, it swallows input events while visible so nothing
 * underneath (cards, charms, tile tooltips, the shop) reacts to the mouse.
 */
public class GameOver extends ModalEndScreen<GameOver.Action> {
    // what the player chose on the defeat screen.
    public enum Action {
        PLAY_AGAIN,
        MAIN_MENU
    }

    private static final Color ACCENT_RED = new Color(0.85f, 0.24f, 0.24f, 1f);
    private static final Color RED_FILTER = new Color(0.68f, 0.10f, 0.10f, 1f);

    // How dark the red screen filter gets once it has fully faded in.
    private static final float RED_FILTER_MAX_ALPHA = 0.25f;

    private static final Color PLAY_AGAIN_COLOR = new Color(0.2f, 0.6f, 0.2f, 1f);
    private static final Color PLAY_AGAIN_HOVER_COLOR = new Color(0f, 0.4f, 0f, 1f);
    private static final Color MAIN_MENU_COLOR = new Color(0.25f, 0.25f, 0.32f, 1f);
    private static final Color MAIN_MENU_HOVER_COLOR = new Color(0.16f, 0.16f, 0.21f, 1f);

    private final Rectangle playAgainButton = new Rectangle();
    private final Rectangle mainMenuButton = new Rectangle();

    private DefeatReason reason = DefeatReason.OUT_OF_SPINS;

    public GameOver(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch, Viewport viewport) {
        super(shapeRenderer, spriteBatch, viewport, 720f, 560f);
    }

    @Override
    protected void onShow() {
        reason = DefeatReason.from((int) Math.min(chips, Integer.MAX_VALUE));
    }

    @Override
    protected Color getAccentColor() {
        return ACCENT_RED;
    }

    @Override
    protected Color getFilterColor() {
        return RED_FILTER;
    }

    @Override
    protected float getFilterMaxAlpha() {
        return RED_FILTER_MAX_ALPHA;
    }

    @Override
    protected String getTitleText() {
        return "DEFEAT";
    }

    @Override
    protected String getSubText() {
        return reason.getMessage();
    }

    @Override
    protected Action getActionAt(float worldX, float worldY) {
        if (playAgainButton.contains(worldX, worldY)) {
            return Action.PLAY_AGAIN;
        }
        if (mainMenuButton.contains(worldX, worldY)) {
            return Action.MAIN_MENU;
        }
        return null;
    }

    @Override
    protected void layoutButtons(float bottom) {
        float left = -width / 2f;
        float buttonsLeft = left + (width - (BUTTON_WIDTH * 2f + BUTTON_GAP)) / 2f;
        float buttonY = bottom + BUTTON_BOTTOM_PAD;
        playAgainButton.set(buttonsLeft, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        mainMenuButton.set(buttonsLeft + BUTTON_WIDTH + BUTTON_GAP, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    @Override
    protected void drawButtons() {
        drawButton(playAgainButton, "PLAY AGAIN", Action.PLAY_AGAIN, PLAY_AGAIN_COLOR, PLAY_AGAIN_HOVER_COLOR);
        drawButton(mainMenuButton, "MAIN MENU", Action.MAIN_MENU, MAIN_MENU_COLOR, MAIN_MENU_HOVER_COLOR);
    }

    @Override
    protected void drawStats(float top) {
        float rowY = top - ROW_START;
        drawStatRow("QUOTA", "$" + quota, VALUE_GOLD, rowY);
        drawStatRow("CHIPS", "$" + chips, ACCENT_RED, rowY - ROW_GAP);
        drawStatRow("SPINS LEFT", String.valueOf(spinsRemaining),
            reason == DefeatReason.OUT_OF_SPINS ? ACCENT_RED : Color.WHITE, rowY - ROW_GAP * 2f);
        drawStatRow("REACHED", "ACT " + act + ", ROUND " + round, Color.WHITE, rowY - ROW_GAP * 3f);
    }
}
