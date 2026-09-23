package io.wasabi.urg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Align;

import io.wasabi.urg.Roulette;
import io.wasabi.urg.managers.FontManager;
import io.wasabi.urg.managers.RendererManager;

public final class MainMenuScreen extends ScreenAdapter {
    private static final float TITLE_SCALE = 1.25f;

    // These anchors are in the shared 1600 x 900 world and align with MainMenu.png.
    private static final float CONTENT_CENTER_X = -255f;

    private static final float WOOD_CENTER_X = 575f;
    private static final float HIGH_SCORE_PANEL_WIDTH = 320f;
    private static final float HIGH_SCORE_PANEL_HEIGHT = 140f;

    private static final Color PANEL_SHADOW = new Color(0f, 0f, 0f, 0.45f);
    private static final Color PANEL_OUTLINE = Color.WHITE;
    private static final Color PANEL_FILL = new Color(0.10f, 0.10f, 0.13f, 1f);

    // Shared renderers and menu assets
    private final Roulette game;
    private final SpriteBatch spriteBatch;
    private final Texture backgroundTexture;
    private final Texture patchTexture;
    private final NinePatch patch;

    private final BitmapFont titleFont;
    private final BitmapFont buttonFont;
    private final GlyphLayout layout = new GlyphLayout();
    private final GlyphLayout scoreHeadingLayout = new GlyphLayout();
    private final GlyphLayout scoreValueLayout = new GlyphLayout();

    /** Loads resources used throughout the menu; shared renderers are owned by RendererManager. */
    public MainMenuScreen(final Roulette game) {
        this.game = game;

        RendererManager rendererManager = RendererManager.getInstance();
        this.spriteBatch = rendererManager.getSpriteBatch();

        this.backgroundTexture =
            new Texture(Gdx.files.internal("ui/MainMenu.png"));
        this.patchTexture =
            new Texture(Gdx.files.internal("ui/CorneredPatch.png"));
        this.patch = new NinePatch(patchTexture, 10, 10, 10, 10);

        FontManager fontManager = FontManager.getInstance();
        this.titleFont =
            fontManager.getFontByName("Terminus64PXBold");
        this.buttonFont =
            fontManager.getFontByName("Terminus32PX");
    }

    /** Renders the menu in layer order so the background stays behind all UI. */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.17f, 0.20f, 0.12f, 1f);

        drawBackground();
        drawTitle();
        drawMenuButtons();
        drawHighScorePlaceholder();
    }

    private void drawBackground() {
        spriteBatch.begin();

        // MainMenu.png contains the complete felt-and-wood composition.
        spriteBatch.draw(
            backgroundTexture,
            -game.getWorldWidth() / 2f,
            -game.getWorldHeight() / 2f,
            game.getWorldWidth(),
            game.getWorldHeight()
        );

        spriteBatch.end();
    }

    /**
     * Draws the title around the same horizontal anchor as the menu buttons.
     * The shadow improves contrast against the textured felt background.
     */
    private void drawTitle() {
        String firstLine = "UNTITLED";
        String secondLine = "ROULETTE GAME";

        titleFont.getData().setScale(TITLE_SCALE);

        layout.setText(titleFont, firstLine);
        float firstLineX = CONTENT_CENTER_X - layout.width / 2f;

        layout.setText(titleFont, secondLine);
        float secondLineX = CONTENT_CENTER_X - layout.width / 2f;

        spriteBatch.begin();

        titleFont.setColor(Color.BLACK);
        titleFont.draw(spriteBatch, firstLine, firstLineX + 4f, 354f);
        titleFont.draw(spriteBatch, secondLine, secondLineX + 4f, 274f);

        titleFont.setColor(Color.WHITE);
        titleFont.draw(spriteBatch, firstLine, firstLineX, 358f);
        titleFont.draw(spriteBatch, secondLine, secondLineX, 278f);

        spriteBatch.end();

        // FontManager shares this BitmapFont with other screens, so restore its scale.
        titleFont.getData().setScale(1f);
    }

    private void drawMenuButtons() {
        // Keep placeholder actions visible until their screen transitions are implemented.
        drawButton(-450f, 100f, 390f, 72f, "PLAY");
        drawButton(-450f, 0f, 390f, 72f, "TUTORIAL");
        drawButton(-450f, -100f, 390f, 72f, "SETTINGS");
        drawButton(-450f, -200f, 390f, 72f, "COLLECTIONS");
        drawButton(-450f, -300f, 390f, 72f, "QUIT");
    }

    /** Draws a gameplay-style panel and centres its label within the supplied bounds. */
    private void drawButton(
        float x,
        float y,
        float width,
        float height,
        String label
    ) {
        layout.setText(buttonFont, label);

        spriteBatch.begin();

        // Match the shadow, outline, and inset fill used by the gameplay panels.
        spriteBatch.setColor(PANEL_SHADOW);
        patch.draw(spriteBatch, x, y - 6f, width, height);

        spriteBatch.setColor(PANEL_OUTLINE);
        patch.draw(spriteBatch, x, y, width, height);

        spriteBatch.setColor(PANEL_FILL);
        patch.draw(spriteBatch, x + 5f, y + 5f, width - 10f, height - 10f);

        spriteBatch.setColor(Color.WHITE);
        buttonFont.setColor(Color.WHITE);
        buttonFont.draw(
            spriteBatch,
            label,
            x,
            y + height / 2f + layout.height / 2f,
            width,
            Align.center,
            false
        );

        spriteBatch.end();
    }

    /**
     * Draws the placeholder score as a centred two-line group rather than
     * positioning each line independently within the panel.
     */
    private void drawHighScorePlaceholder() {
        // The panel uses world coordinates so it remains aligned with the background artwork.
        float panelX = WOOD_CENTER_X - HIGH_SCORE_PANEL_WIDTH / 2f;
        float panelY = 235f;

        String heading = "HIGH SCORE:";
        String value = "PLACEHOLDER";
        float lineGap = 12f;

        scoreHeadingLayout.setText(buttonFont, heading);
        scoreValueLayout.setText(buttonFont, value);
        float panelCenterY = panelY + HIGH_SCORE_PANEL_HEIGHT / 2f;
        float firstLineY = panelCenterY
            + (lineGap + scoreValueLayout.height) / 2f;
        float secondLineY = panelCenterY
            - (lineGap + scoreHeadingLayout.height) / 2f;

        float textPadding = 12f;

        spriteBatch.begin();

        spriteBatch.setColor(PANEL_SHADOW);
        patch.draw(
            spriteBatch,
            panelX,
            panelY - 6f,
            HIGH_SCORE_PANEL_WIDTH,
            HIGH_SCORE_PANEL_HEIGHT
        );

        spriteBatch.setColor(PANEL_OUTLINE);
        patch.draw(
            spriteBatch,
            panelX,
            panelY,
            HIGH_SCORE_PANEL_WIDTH,
            HIGH_SCORE_PANEL_HEIGHT
        );

        spriteBatch.setColor(PANEL_FILL);
        patch.draw(
            spriteBatch,
            panelX + 5f,
            panelY + 5f,
            HIGH_SCORE_PANEL_WIDTH - 10f,
            HIGH_SCORE_PANEL_HEIGHT - 10f
        );

        spriteBatch.setColor(Color.WHITE);
        buttonFont.setColor(Color.WHITE);
        buttonFont.draw(
            spriteBatch,
            heading,
            panelX + textPadding,
            firstLineY,
            HIGH_SCORE_PANEL_WIDTH - textPadding * 2f,
            Align.center,
            false
        );
        buttonFont.draw(
            spriteBatch,
            value,
            panelX + textPadding,
            secondLineY,
            HIGH_SCORE_PANEL_WIDTH - textPadding * 2f,
            Align.center,
            false
        );

        spriteBatch.setColor(Color.WHITE);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        // These textures are owned by this screen and must be released with it.
        backgroundTexture.dispose();
        patchTexture.dispose();
    }
}