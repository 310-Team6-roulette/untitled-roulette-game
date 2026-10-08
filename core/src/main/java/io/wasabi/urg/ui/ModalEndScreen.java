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
 * Shared modal end screen that slides up over the game when a run ends (defeat, victory).
 * Owns the panel chrome, the slide/fade animation, the stats layout, button drawing, and
 * the modal input layer; subclasses supply the per-screen title, colours, stats and buttons.
 *
 * While visible it is registered first in the game screen's input multiplexer and swallows
 * input events so nothing underneath (cards, charms, tile tooltips, the shop) reacts to the mouse.
 *
 * @param <T> the enum of actions the concrete screen's buttons can return.
 */
public abstract class ModalEndScreen<T extends Enum<T>> extends InputAdapter {
    protected static final float OUTLINE = 5f;
    protected static final float ROW_PADDING = 70f;
    protected static final float ROW_START = 210f;
    protected static final float ROW_GAP = 52f;
    protected static final float BUTTON_WIDTH = 290f;
    protected static final float BUTTON_HEIGHT = 72f;
    protected static final float BUTTON_GAP = 40f;
    protected static final float BUTTON_BOTTOM_PAD = 40f;

    protected static final Color PANEL_SHADOW = new Color(0f, 0f, 0f, 0.45f);
    protected static final Color PANEL_OUTLINE = Color.WHITE;
    protected static final Color PANEL_FILL = new Color(0.10f, 0.10f, 0.13f, 1f);
    protected static final Color LABEL_GREY = new Color(0.70f, 0.70f, 0.75f, 1f);
    protected static final Color VALUE_GOLD = new Color(1f, 0.82f, 0.30f, 1f);

    protected final ShapeRenderer shapeRenderer;
    protected final SpriteBatch spriteBatch;
    protected final Viewport viewport;
    protected final Texture patchTexture;
    protected final NinePatch patch;
    protected final BitmapFont titleFont;
    protected final BitmapFont font;
    protected final GlyphLayout layout = new GlyphLayout();
    protected final Matrix4 identity = new Matrix4();
    protected final Vector2 touch = new Vector2();

    protected Tween panelTween;
    protected Tween fadeTween;
    protected float y = -1500f;
    protected float fadeAlpha = 0f;
    protected boolean visible;
    protected boolean hiding;
    protected T hovered;
    protected T pressed;

    protected int quota;
    protected long chips;
    protected int spinsRemaining;
    protected int act;
    protected int round;

    public ModalEndScreen(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch, Viewport viewport) {
        this.shapeRenderer = shapeRenderer;
        this.spriteBatch = spriteBatch;
        this.viewport = viewport;
        this.patchTexture = new Texture(Gdx.files.internal("ui/CorneredPatch.png"));
        this.patch = new NinePatch(patchTexture, 10, 10, 10, 10);
        this.titleFont = FontManager.getInstance().getFontByName("Terminus64PXBold");
        this.font = FontManager.getInstance().getFontByName("Terminus32PX");
    }

    /**
     * Slides the panel in with the stats of the run that just ended.
     *
     * @param quota The quota for the run.
     * @param chips The chips the player had when the run ended.
     * @param spinsRemaining The spins the player had left when the run ended.
     * @param act The act the run ended on.
     * @param round The round the run ended on.
     */
    public void show(int quota, long chips, int spinsRemaining, int act, int round) {
        this.quota = quota;
        this.chips = chips;
        this.spinsRemaining = spinsRemaining;
        this.act = act;
        this.round = round;

        visible = true;
        hiding = false;
        hovered = null;
        pressed = null;
        y = getOffscreenY();
        fadeAlpha = 0f;
        panelTween = new Tween(1f, getOffscreenY(), 0f, Tween.TweenStyle.QUAD, Tween.TweenDirection.OUT);
        fadeTween = new Tween(1f, 0f, getFilterMaxAlpha(), Tween.TweenStyle.QUAD, Tween.TweenDirection.OUT);

        hideGameTooltips();
        onShow();
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
        panelTween = new Tween(1f, y, getOffscreenY(), Tween.TweenStyle.QUAD, Tween.TweenDirection.IN);
        fadeTween = new Tween(1f, fadeAlpha, 0f, Tween.TweenStyle.QUAD, Tween.TweenDirection.IN);
    }

    // Hides the panel immediately, for when the game screen is left while it is still up.
    public void hideImmediately() {
        visible = false;
        hiding = false;
        hovered = null;
        pressed = null;
        y = getOffscreenY();
        fadeAlpha = 0f;
    }

    public void update(float delta) {
        if (!visible) {
            return;
        }

        if (panelTween != null && !panelTween.isComplete()) {
            y = panelTween.update(delta);
        }

        if (fadeTween != null && !fadeTween.isComplete()) {
            fadeAlpha = fadeTween.update(delta);
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
    public T handleInput() {
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
            T released = pressed == hovered ? pressed : null;
            pressed = null;
            return released;
        }

        return null;
    }

    public void render() {
        if (!visible) {
            return;
        }

        drawFilter();

        float bottom = y - getHeight() / 2f;
        float top = bottom + getHeight();
        layoutButtons(bottom);

        spriteBatch.begin();
        spriteBatch.setTransformMatrix(identity);

        drawPanel(bottom, top);
        drawHeader(top);
        drawStats(top);
        drawButtons();

        spriteBatch.setColor(Color.WHITE);
        font.setColor(Color.WHITE);
        titleFont.setColor(Color.WHITE);
        spriteBatch.end();
    }

    protected void drawPanel(float bottom, float top) {
        float left = -getWidth() / 2f;

        spriteBatch.setColor(PANEL_SHADOW);
        patch.draw(spriteBatch, left, bottom - 8f, getWidth(), getHeight());

        spriteBatch.setColor(PANEL_OUTLINE);
        patch.draw(spriteBatch, left, bottom, getWidth(), getHeight());

        spriteBatch.setColor(PANEL_FILL);
        patch.draw(spriteBatch, left + OUTLINE, bottom + OUTLINE,
            getWidth() - OUTLINE * 2f, getHeight() - OUTLINE * 2f);

        // Accent divider between the header and the run stats.
        spriteBatch.setColor(getAccentColor());
        patch.draw(spriteBatch, left + ROW_PADDING, top - 170f, getWidth() - ROW_PADDING * 2f, 4f);
    }

    protected void drawHeader(float top) {
        float left = -getWidth() / 2f;
        String title = getTitleText();
        layout.setText(titleFont, title);
        float titleX = left + (getWidth() - layout.width) / 2f;

        // Offset black copy underneath gives the title the same drop shadow as the main menu.
        titleFont.setColor(Color.BLACK);
        titleFont.draw(spriteBatch, title, titleX + 4f, top - 34f);
        titleFont.setColor(getAccentColor());
        titleFont.draw(spriteBatch, title, titleX, top - 30f);

        font.setColor(Color.WHITE);
        font.draw(spriteBatch, getSubText(), left, top - 118f, getWidth(), Align.center, false);
    }

    protected void drawStatRow(String label, String value, Color valueColor, float rowY) {
        float left = -getWidth() / 2f;
        float rowWidth = getWidth() - ROW_PADDING * 2f;

        font.setColor(LABEL_GREY);
        font.draw(spriteBatch, label, left + ROW_PADDING, rowY, rowWidth, Align.left, false);

        font.setColor(valueColor);
        font.draw(spriteBatch, value, left + ROW_PADDING, rowY, rowWidth, Align.right, false);
    }

    protected void drawButton(Rectangle box, String label, T action, Color color, Color hoverColor) {
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

    // Draws a filter over the entire screen to indicate the run has ended.
    private void drawFilter() {
        float pad = 200f;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float left = -worldWidth / 2f - pad;
        float bottom = -worldHeight / 2f - pad;

        // ShapeRenderer does not enable alpha blending on its own, so without this the
        // filter's alpha is ignored and it renders fully opaque instead of fading in.
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        Color filterColor = getFilterColor();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(filterColor.r, filterColor.g, filterColor.b, fadeAlpha);
        shapeRenderer.rect(left, bottom, worldWidth + pad * 2f, worldHeight + pad * 2f);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    // Buttons only respond once the panel has finished sliding in, so a stray click can't skip it.
    protected boolean isInteractive() {
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

    /** Panel width of this screen. */
    protected abstract float getWidth();

    /** Panel height of this screen. */
    protected abstract float getHeight();

    /** The y position the panel starts and ends at while off screen. */
    protected abstract float getOffscreenY();

    /** Divider/title accent colour. */
    protected abstract Color getAccentColor();

    /** Full screen filter colour. */
    protected abstract Color getFilterColor();

    /** How dark the full screen filter gets once it has fully faded in. */
    protected abstract float getFilterMaxAlpha();

    /** The big title drawn at the top of the panel. */
    protected abstract String getTitleText();

    /** The subtitle drawn under the title. */
    protected abstract String getSubText();

    /** The action under the given world-space point, or null if there is none. */
    protected abstract T getActionAt(float worldX, float worldY);

    /** Positions the screen's buttons relative to the panel's bottom edge. */
    protected abstract void layoutButtons(float bottom);

    /** Draws the screen's buttons. */
    protected abstract void drawButtons();

    /** Draws the run stats rows between the header and the buttons. */
    protected abstract void drawStats(float top);

    /** Hook for per-screen setup once the run stats have been stored and the panel starts sliding in. */
    protected void onShow() {
    }
}
