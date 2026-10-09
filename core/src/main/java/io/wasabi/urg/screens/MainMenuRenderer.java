package io.wasabi.urg.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

/**
 * Responsible only for drawing the menu scene. Keeping the rendering code separate
 * prevents the screen class from becoming a large rendering container.
 */
public final class MainMenuRenderer {
    private static final Color PANEL_SHADOW = new Color(0f, 0f, 0f, 0.45f);
    private static final Color PANEL_OUTLINE = Color.WHITE;
    private static final Color PANEL_FILL = new Color(0.10f, 0.10f, 0.13f, 1f);
    private final SpriteBatch spriteBatch;
    private final Texture backgroundTexture;
    private final NinePatch patch;
    private final BitmapFont titleFont;
    private final BitmapFont buttonFont;
    private final MenuButtonRenderer buttonRenderer;
    private final GlyphLayout layout = new GlyphLayout();
    private final GlyphLayout scoreHeadingLayout = new GlyphLayout();
    private final GlyphLayout scoreValueLayout = new GlyphLayout();

    public MainMenuRenderer(
        SpriteBatch spriteBatch,
        Texture backgroundTexture,
        NinePatch patch,
        BitmapFont titleFont,
        BitmapFont buttonFont
    ) {
        this.spriteBatch = spriteBatch;
        this.backgroundTexture = backgroundTexture;
        this.patch = patch;
        this.titleFont = titleFont;
        this.buttonFont = buttonFont;
        this.buttonRenderer = new MenuButtonRenderer(spriteBatch, patch, buttonFont);
    }

    public void drawBackground(float worldWidth, float worldHeight) {
        spriteBatch.begin();
        spriteBatch.draw(
            backgroundTexture,
            -worldWidth / 2f,
            -worldHeight / 2f,
            worldWidth,
            worldHeight
        );
        spriteBatch.end();
    }

    public void drawTitle(float contentCenterX, float titleScale) {
        String firstLine = "UNTITLED";
        String secondLine = "ROULETTE GAME";

        titleFont.getData().setScale(titleScale);

        layout.setText(titleFont, firstLine);
        float firstLineX = contentCenterX - layout.width / 2f;

        layout.setText(titleFont, secondLine);
        float secondLineX = contentCenterX - layout.width / 2f;

        spriteBatch.begin();

        titleFont.setColor(Color.BLACK);
        titleFont.draw(spriteBatch, firstLine, firstLineX + 4f, 354f);
        titleFont.draw(spriteBatch, secondLine, secondLineX + 4f, 274f);

        titleFont.setColor(Color.WHITE);
        titleFont.draw(spriteBatch, firstLine, firstLineX, 358f);
        titleFont.draw(spriteBatch, secondLine, secondLineX, 278f);

        spriteBatch.end();
        titleFont.getData().setScale(1f);
    }

    public void drawMenuButtons(MainMenuButton[] buttons, MenuButtonInputHandler inputHandler) {
        for (int i = 0; i < buttons.length; i++) {
            buttonRenderer.draw(buttons[i], inputHandler.getButtonState(i));
        }
    }

    /** Draws the saved best act and round using the menu panel style. */
    public void drawHighScore(float panelCenterX, float panelY, int bestAct, int bestRound) {
        float panelWidth = 320f;
        float panelHeight = 140f;
        float textPadding = 12f;
        float lineGap = 12f;

        String heading = "BEST ROUND:";
        String value = "Act " + bestAct + ", Round " + bestRound;

        scoreHeadingLayout.setText(buttonFont, heading);
        scoreValueLayout.setText(buttonFont, value);

        float panelX = panelCenterX - panelWidth / 2f;
        float panelCenterY = panelY + panelHeight / 2f;
        float firstLineY = panelCenterY + (lineGap + scoreValueLayout.height) / 2f;
        float secondLineY = panelCenterY - (lineGap + scoreHeadingLayout.height) / 2f;

        spriteBatch.begin();

        spriteBatch.setColor(PANEL_SHADOW);
        patch.draw(spriteBatch, panelX, panelY - 6f, panelWidth, panelHeight);

        spriteBatch.setColor(PANEL_OUTLINE);
        patch.draw(spriteBatch, panelX, panelY, panelWidth, panelHeight);

        spriteBatch.setColor(PANEL_FILL);
        patch.draw(spriteBatch, panelX + 5f, panelY + 5f, panelWidth - 10f, panelHeight - 10f);

        spriteBatch.setColor(Color.WHITE);
        buttonFont.setColor(Color.WHITE);
        buttonFont.draw(
            spriteBatch,
            heading,
            panelX + textPadding,
            firstLineY,
            panelWidth - textPadding * 2f,
            Align.center,
            false
        );
        buttonFont.draw(
            spriteBatch,
            value,
            panelX + textPadding,
            secondLineY,
            panelWidth - textPadding * 2f,
            Align.center,
            false
        );

        spriteBatch.setColor(Color.WHITE);
        spriteBatch.end();
    }
}
