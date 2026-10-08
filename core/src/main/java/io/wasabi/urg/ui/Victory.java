package io.wasabi.urg.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Victory screen that slides up over the game when the player completes act 3.
 * Shows the quota, the chips and spins the player had left, how far they got, and lets
 * them either start a new run, return to the main menu, or go into endless mode.
 *
 * It also acts as a modal input layer via {@link ModalEndScreen}: registered first in the
 * game screen's input multiplexer, it swallows input events while visible so nothing
 * underneath (cards, charms, tile tooltips, the shop) reacts to the mouse.
 */
public class Victory extends ModalEndScreen<Victory.Action> {
    // what the player chose on the victory screen.
    public enum Action {
        ENDLESS_MODE,
        PLAY_AGAIN,
        MAIN_MENU
    }

    private static final float WIDTH = 720f;
    private static final float HEIGHT = 620f;
    private static final float OFFSCREEN_Y = -1500f;

    private static final Color ACCENT_GOLD = new Color(0.9f, 0.82f, 0.3f, 1f);

    // How dark the gold screen filter gets once it has fully faded in.
    private static final float GOLD_FILTER_MAX_ALPHA = 0.12f;

    private static final Color ENDLESS_COLOR = new Color(0.2f, 0.5f, 0.7f, 1f);
    private static final Color ENDLESS_HOVER_COLOR = new Color(0.16f, 0.4f, 0.58f, 1f);
    private static final Color PLAY_AGAIN_COLOR = new Color(0.2f, 0.6f, 0.2f, 1f);
    private static final Color PLAY_AGAIN_HOVER_COLOR = new Color(0f, 0.4f, 0f, 1f);
    private static final Color MAIN_MENU_COLOR = new Color(0.25f, 0.25f, 0.32f, 1f);
    private static final Color MAIN_MENU_HOVER_COLOR = new Color(0.16f, 0.16f, 0.21f, 1f);

    private final Rectangle endlessButton = new Rectangle();
    private final Rectangle playAgainButton = new Rectangle();
    private final Rectangle mainMenuButton = new Rectangle();

    public Victory(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch, Viewport viewport) {
        super(shapeRenderer, spriteBatch, viewport);
    }

    @Override
    protected float getWidth() {
        return WIDTH;
    }

    @Override
    protected float getHeight() {
        return HEIGHT;
    }

    @Override
    protected float getOffscreenY() {
        return OFFSCREEN_Y;
    }

    @Override
    protected Color getAccentColor() {
        return ACCENT_GOLD;
    }

    @Override
    protected Color getFilterColor() {
        return ACCENT_GOLD;
    }

    @Override
    protected float getFilterMaxAlpha() {
        return GOLD_FILTER_MAX_ALPHA;
    }

    @Override
    protected String getTitleText() {
        return "VICTORY";
    }

    @Override
    protected String getSubText() {
        return "Run complete. Well played!";
    }

    @Override
    protected Action getActionAt(float worldX, float worldY) {
        if (endlessButton.contains(worldX, worldY)) {
            return Action.ENDLESS_MODE;
        }
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
        float left = -WIDTH / 2f;
        float buttonY = bottom + BUTTON_BOTTOM_PAD;
        float centerLeft = left + (WIDTH - BUTTON_WIDTH) / 2f;
        endlessButton.set(centerLeft, buttonY + BUTTON_HEIGHT + BUTTON_GAP / 2f, BUTTON_WIDTH, BUTTON_HEIGHT);
        float buttonsLeft = left + (WIDTH - (BUTTON_WIDTH * 2f + BUTTON_GAP)) / 2f;
        playAgainButton.set(buttonsLeft, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        mainMenuButton.set(buttonsLeft + BUTTON_WIDTH + BUTTON_GAP, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    @Override
    protected void drawButtons() {
        drawButton(endlessButton, "ENDLESS MODE", Action.ENDLESS_MODE, ENDLESS_COLOR, ENDLESS_HOVER_COLOR);
        drawButton(playAgainButton, "PLAY AGAIN", Action.PLAY_AGAIN, PLAY_AGAIN_COLOR, PLAY_AGAIN_HOVER_COLOR);
        drawButton(mainMenuButton, "MAIN MENU", Action.MAIN_MENU, MAIN_MENU_COLOR, MAIN_MENU_HOVER_COLOR);
    }

    @Override
    protected void drawStats(float top) {
        float rowY = top - ROW_START;
        drawStatRow("QUOTA", "$" + quota, VALUE_GOLD, rowY);
        drawStatRow("CHIPS", "$" + chips, VALUE_GOLD, rowY - ROW_GAP);
        drawStatRow("SPINS LEFT", String.valueOf(spinsRemaining), Color.WHITE, rowY - ROW_GAP * 2f);
        drawStatRow("REACHED", "ACT " + act + ", ROUND " + round, Color.WHITE, rowY - ROW_GAP * 3f);
    }
}
