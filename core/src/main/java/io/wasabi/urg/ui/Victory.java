package io.wasabi.urg.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.wasabi.urg.Roulette;
import io.wasabi.urg.elements.card.Card;
import io.wasabi.urg.elements.charm.Charm;
import io.wasabi.urg.elements.game.Tile;
import io.wasabi.urg.managers.FontManager;
import io.wasabi.urg.state.RunState;
import io.wasabi.urg.util.tweens.Tween;

/**
 * Victory screen that slides up over the game when the player completes act 3.
 * Shows the quota, the chips and spins the player had left, how far they got, and lets
 * them either start a new run, return to the main menu, or go into endless mode.
 *
 * It also acts as a modal input layer: registered first in the game screen's input multiplexer,
 * it swallows input events while visible so nothing underneath (cards, charms, tile tooltips,
 * the shop) reacts to the mouse.
 */
public class Victory extends InputAdapter {
    // what the player chose on the victory screen.
    public enum Action {
        ENDLESS_MODE,
        PLAY_AGAIN,
        MAIN_MENU
    }

    private static final float WIDTH = 720f;
    private static final float HEIGHT = 620f;
    private static final float LEFT = -WIDTH / 2f;
    private static final float OFFSCREEN_Y = -1500f;

    // How dark the red screen filter gets once it has fully faded in.
    private static final float GOLD_FILTER_MAX_ALPHA = 0.12f;

    private static final float OUTLINE = 5f;
    private static final float ROW_PADDING = 70f;
    private static final float ROW_START = 210f;
    private static final float ROW_GAP = 52f;

    private static final float BUTTON_WIDTH = 290f;
    private static final float BUTTON_HEIGHT = 72f;
    private static final float BUTTON_GAP = 40f;
    private static final float BUTTON_BOTTOM_PAD = 40f;

    // Panel colours match the main menu and shop so the defeat screen reads as part of the same UI.
    private static final Color PANEL_SHADOW = new Color(0f, 0f, 0f, 0.45f);
    private static final Color PANEL_OUTLINE = Color.WHITE;
    private static final Color PANEL_FILL = new Color(0.10f, 0.10f, 0.13f, 1f);
    private static final Color ACCENT_GOLD = new Color(0.9f, 0.82f, 0.3f, 1f);
    private static final Color LABEL_GREY = new Color(0.70f, 0.70f, 0.75f, 1f);
    private static final Color VALUE_GOLD = new Color(1f, 0.82f, 0.30f, 1f);

    private static final Color ENDLESS_COLOR = new Color(0.2f, 0.5f, 0.7f, 1f);
    private static final Color ENDLESS_HOVER_COLOR = new Color(0.16f, 0.4f, 0.58f, 1f);
    private static final Color PLAY_AGAIN_COLOR = new Color(0.2f, 0.6f, 0.2f, 1f);
    private static final Color PLAY_AGAIN_HOVER_COLOR = new Color(0f, 0.4f, 0f, 1f);
    private static final Color MAIN_MENU_COLOR = new Color(0.25f, 0.25f, 0.32f, 1f);
    private static final Color MAIN_MENU_HOVER_COLOR = new Color(0.16f, 0.16f, 0.21f, 1f);

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch spriteBatch;
    private final Viewport viewport;
    private final Texture patchTexture;
    private final NinePatch patch;
    private final BitmapFont titleFont;
    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private final Matrix4 identity = new Matrix4();
    private final Vector2 touch = new Vector2();

    private final Rectangle endlessButton = new Rectangle();
    private final Rectangle playAgainButton = new Rectangle();
    private final Rectangle mainMenuButton = new Rectangle();
    private Action hovered;
    private Action pressed;

    private Tween panelTween;
    private Tween fadeTween;
    private float y = OFFSCREEN_Y;
    private float goldFade = 0f;
    private boolean visible;
    private boolean hiding;

    private int quota;
    private int chips;
    private int spinsRemaining;
    private int act;
    private int round;

    public Victory(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch, Viewport viewport) {
        this.shapeRenderer = shapeRenderer;
        this.spriteBatch = spriteBatch;
        this.viewport = viewport;
        this.patchTexture = new Texture(Gdx.files.internal("ui/CorneredPatch.png"));
        this.patch = new NinePatch(patchTexture, 10, 10, 10, 10);
        this.titleFont = FontManager.getInstance().getFontByName("Terminus64PXBold");
        this.font = FontManager.getInstance().getFontByName("Terminus32PX");
    }

    /**
     * Slides the defeat panel in with the stats of the run that just ended.
     *
     * @param quota The quota the player failed to reach.
     * @param chips The chips the player had when the run ended.
     * @param spinsRemaining The spins the player had left when the run ended.
     * @param act The act the run ended on.
     * @param round The round the run ended on.
     */
    public void show(int quota, long chips, int spinsRemaining, int act, int round) {
        this.quota = quota;
        this.chips = (int) Math.min(chips, (long) Integer.MAX_VALUE);
        this.spinsRemaining = spinsRemaining;
        this.act = act;
        this.round = round;

        visible = true;
        hiding = false;
        hovered = null;
        pressed = null;
        y = OFFSCREEN_Y;
        goldFade = 0f;
        panelTween = new Tween(1f, OFFSCREEN_Y, 0f, Tween.TweenStyle.QUAD, Tween.TweenDirection.OUT);
        fadeTween = new Tween(1f, 0f, GOLD_FILTER_MAX_ALPHA, Tween.TweenStyle.QUAD, Tween.TweenDirection.OUT);

        hideGameTooltips();
    }

    // Tooltips only hide on mouse move, which this layer now blocks, so clear any that are open.
    private void hideGameTooltips() {
        RunState runState = Roulette.getInstance().getRunState();
        for (Tile tile : runState.getTiles()) {
            if (tile != null) {
                tile.getTooltip().hide();
            }
        }
        for (Card card : runState.getOwnedCards()) {
            card.getTooltip().hide();
        }
        for (Charm charm : runState.getOwnedCharms()) {
            charm.getTooltip().hide();
        }
    }

    public void hide() {
        if (!visible || hiding) {
            return;
        }

        hiding = true;
        hovered = null;
        pressed = null;
        panelTween = new Tween(1f, y, OFFSCREEN_Y, Tween.TweenStyle.QUAD, Tween.TweenDirection.IN);
        fadeTween = new Tween(1f, goldFade, 0f, Tween.TweenStyle.QUAD, Tween.TweenDirection.IN);
    }

    // Hides the panel immediately, for when the game screen is left while it is still up.
    public void hideImmediately() {
        visible = false;
        hiding = false;
        hovered = null;
        pressed = null;
        y = OFFSCREEN_Y;
        goldFade = 0f;
    }

    public void update(float delta) {
        if (!visible) {
            return;
        }

        if (panelTween != null && !panelTween.isComplete()) {
            y = panelTween.update(delta);
        }

        if (fadeTween != null && !fadeTween.isComplete()) {
            goldFade = fadeTween.update(delta);
        }

        if (hiding && panelTween.isComplete() && fadeTween.isComplete()) {
            hideImmediately();
        }
    }

    /**
     * Polls the mouse to update button hover/press state. A button only fires when it is
     * pressed and released while the cursor stays on it, matching the main menu buttons.
     *
     * @return The action the player picked this frame, or null if none.
     */
    public Action handleInput() {
        if (!isInteractive()) {
            return null;
        }

        touch.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(touch);
        hovered = getActionAt(touch.x, touch.y);

        if (Gdx.input.justTouched()) {
            pressed = hovered;
            return null;
        }

        if (pressed != null && !Gdx.input.isTouched()) {
            Action released = pressed == hovered ? pressed : null;
            pressed = null;
            return released;
        }

        return null;
    }

    public void render() {
        if (!visible) {
            return;
        }

        drawRedFilter();

        float bottom = y - HEIGHT / 2f;
        float top = bottom + HEIGHT;
        layoutButtons(bottom);

        spriteBatch.begin();
        spriteBatch.setTransformMatrix(identity);

        drawPanel(bottom, top);
        drawHeader(top);
        drawStats(top);
        drawButton(endlessButton, "ENDLESS MODE", Action.ENDLESS_MODE, ENDLESS_COLOR, ENDLESS_HOVER_COLOR);
        drawButton(playAgainButton, "PLAY AGAIN", Action.PLAY_AGAIN, PLAY_AGAIN_COLOR, PLAY_AGAIN_HOVER_COLOR);
        drawButton(mainMenuButton, "MAIN MENU", Action.MAIN_MENU, MAIN_MENU_COLOR, MAIN_MENU_HOVER_COLOR);

        spriteBatch.setColor(Color.WHITE);
        font.setColor(Color.WHITE);
        titleFont.setColor(Color.WHITE);
        spriteBatch.end();
    }

    private void layoutButtons(float bottom) {
        float buttonY = bottom + BUTTON_BOTTOM_PAD;
        float centerLeft = LEFT + (WIDTH - BUTTON_WIDTH) / 2f;
        endlessButton.set(centerLeft, buttonY + BUTTON_HEIGHT + BUTTON_GAP, BUTTON_WIDTH, BUTTON_HEIGHT);
        float buttonsLeft = LEFT + (WIDTH - (BUTTON_WIDTH * 2f + BUTTON_GAP)) / 2f;
        playAgainButton.set(buttonsLeft, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        mainMenuButton.set(buttonsLeft + BUTTON_WIDTH + BUTTON_GAP, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    private void drawPanel(float bottom, float top) {
        spriteBatch.setColor(PANEL_SHADOW);
        patch.draw(spriteBatch, LEFT, bottom - 8f, WIDTH, HEIGHT);

        spriteBatch.setColor(PANEL_OUTLINE);
        patch.draw(spriteBatch, LEFT, bottom, WIDTH, HEIGHT);

        spriteBatch.setColor(PANEL_FILL);
        patch.draw(spriteBatch, LEFT + OUTLINE, bottom + OUTLINE, WIDTH - OUTLINE * 2f, HEIGHT - OUTLINE * 2f);

        // Gold accent divider between the header and the run stats.
        spriteBatch.setColor(ACCENT_GOLD);
        patch.draw(spriteBatch, LEFT + ROW_PADDING, top - 170f, WIDTH - ROW_PADDING * 2f, 4f);
    }

    private void drawHeader(float top) {
        String title = "VICTORY";
        layout.setText(titleFont, title);
        float titleX = LEFT + (WIDTH - layout.width) / 2f;

        // Offset black copy underneath gives the title the same drop shadow as the main menu.
        titleFont.setColor(Color.BLACK);
        titleFont.draw(spriteBatch, title, titleX + 4f, top - 34f);
        titleFont.setColor(ACCENT_GOLD);
        titleFont.draw(spriteBatch, title, titleX, top - 30f);

        font.setColor(Color.WHITE);
        font.draw(spriteBatch, "Run complete. Well played!", LEFT, top - 118f, WIDTH, Align.center, false);
    }

    private void drawStats(float top) {
        float rowY = top - ROW_START;
        drawStatRow("QUOTA", "$" + quota, VALUE_GOLD, rowY);
        drawStatRow("CHIPS", "$" + chips, VALUE_GOLD, rowY - ROW_GAP);
        drawStatRow("SPINS LEFT", String.valueOf(spinsRemaining), Color.WHITE, rowY - ROW_GAP * 2f);
        drawStatRow("REACHED", "ACT " + act + ", ROUND " + round, Color.WHITE, rowY - ROW_GAP * 3f);
    }

    private void drawStatRow(String label, String value, Color valueColor, float rowY) {
        float rowWidth = WIDTH - ROW_PADDING * 2f;

        font.setColor(LABEL_GREY);
        font.draw(spriteBatch, label, LEFT + ROW_PADDING, rowY, rowWidth, Align.left, false);

        font.setColor(valueColor);
        font.draw(spriteBatch, value, LEFT + ROW_PADDING, rowY, rowWidth, Align.right, false);
    }

    private void drawButton(Rectangle box, String label, Action action, Color color, Color hoverColor) {
        boolean isPressed = pressed == action && hovered == action;
        float pressOffsetY = isPressed ? -4f : 0f;
        float shadowOffset = isPressed ? 4f : 6f;

        spriteBatch.setColor(PANEL_SHADOW);
        patch.draw(spriteBatch, box.x, box.y - shadowOffset + pressOffsetY, box.width, box.height);

        spriteBatch.setColor(PANEL_OUTLINE);
        patch.draw(spriteBatch, box.x, box.y + pressOffsetY, box.width, box.height);

        spriteBatch.setColor(hovered == action ? hoverColor : color);
        patch.draw(spriteBatch, box.x + OUTLINE, box.y + OUTLINE + pressOffsetY,
            box.width - OUTLINE * 2f, box.height - OUTLINE * 2f);

        layout.setText(font, label);
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, label, box.x, box.y + box.height / 2f + layout.height / 2f + pressOffsetY,
            box.width, Align.center, false);
    }

    // Draws a red filter over the entire screen to indicate a game over state.
    private void drawRedFilter() {
        float pad = 200f;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float left = -worldWidth / 2f - pad;
        float bottom = -worldHeight / 2f - pad;

        // ShapeRenderer does not enable alpha blending on its own, so without this the
        // filter's alpha is ignored and it renders fully opaque instead of fading in.
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.9f, 0.82f, 0.3f, goldFade);
        shapeRenderer.rect(left, bottom, worldWidth + pad * 2f, worldHeight + pad * 2f);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private Action getActionAt(float worldX, float worldY) {
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

    // Buttons only respond once the panel has finished sliding in, so a stray click can't skip it.
    private boolean isInteractive() {
        return visible && !hiding && panelTween != null && panelTween.isComplete();
    }

    // Input layer: consume events while visible so they never reach the game underneath.
    // touchUp is let through so a card or charm being dragged when the run ended still gets dropped.

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return visible;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return visible;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return visible;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return visible;
    }

    @Override
    public boolean keyDown(int keycode) {
        return visible;
    }

    @Override
    public boolean keyUp(int keycode) {
        return visible;
    }

    @Override
    public boolean keyTyped(char character) {
        return visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public float getY() {
        return y;
    }

    public void resize(int width, int height) {
    }

    public void dispose() {
        patchTexture.dispose();
    }
}
