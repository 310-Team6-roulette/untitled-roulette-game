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
    private static final Color BUTTON_HOVER_FILL = new Color(0.16f, 0.16f, 0.20f, 1f);
    private static final Color BUTTON_PRESSED_FILL = new Color(0.08f, 0.08f, 0.11f, 1f);

    private final SpriteBatch spriteBatch;
    private final Texture backgroundTexture;
    private final NinePatch patch;
    private final BitmapFont titleFont;
    private final BitmapFont buttonFont;
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

    public void drawMenuButtons(MainMenuButton[] buttons, MainMenuInputHandler inputHandler) {
        for (int i = 0; i < buttons.length; i++) {
            MainMenuButton button = buttons[i];
            drawButton(
                button.getX(),
                button.getY(),
                button.getWidth(),
                button.getHeight(),
                button.getLabel(),
                inputHandler.getButtonState(i)
            );
        }
    }

    private void drawButton(
        float x,
        float y,
        float width,
        float height,
        String label,
        MainMenuButton.State state
    ) {
        layout.setText(buttonFont, label);
        float shadowOffset = state == MainMenuButton.State.PRESSED ? 4f : 6f;
        float pressOffsetY = state == MainMenuButton.State.PRESSED ? -4f : 0f;

        spriteBatch.begin();

        spriteBatch.setColor(PANEL_SHADOW);
        patch.draw(spriteBatch, x, y - shadowOffset + pressOffsetY, width, height);

        spriteBatch.setColor(PANEL_OUTLINE);
        patch.draw(spriteBatch, x, y + pressOffsetY, width, height);

        // The button shape stays constant across states; only the fill changes to show
        // hover and pressed feedback without changing the layout or hitbox.
        Color fillColor;
        if (state == MainMenuButton.State.NORMAL) {
            fillColor = PANEL_FILL;
        } else if (state == MainMenuButton.State.HOVER) {
            fillColor = BUTTON_HOVER_FILL;
        } else {
            fillColor = BUTTON_PRESSED_FILL;
        }
        spriteBatch.setColor(fillColor);
        patch.draw(spriteBatch, x + 5f, y + 5f + pressOffsetY, width - 10f, height - 10f);

        spriteBatch.setColor(Color.WHITE);
        buttonFont.setColor(Color.WHITE);
        buttonFont.draw(
            spriteBatch,
            label,
            x,
            y + height / 2f + layout.height / 2f + pressOffsetY,
            width,
            Align.center,
            false
        );

        spriteBatch.end();
    }

    /**
     * Draws the placeholder score panel using the same visual language as the gameplay UI
     * so the menu feels consistent with the rest of the project.
     */
    public void drawHighScorePlaceholder(float panelCenterX, float panelY) {
        float panelWidth = 320f;
        float panelHeight = 140f;
        float textPadding = 12f;
        float lineGap = 12f;

        String heading = "HIGH SCORE:";
        String value = "PLACEHOLDER";

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
