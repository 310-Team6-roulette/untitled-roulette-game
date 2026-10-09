package io.wasabi.urg.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

/** Draws tutorial content independently from the main-menu scene and controls. */
public final class TutorialRenderer {
    private static final Color PANEL_SHADOW = new Color(0f, 0f, 0f, 0.45f);
    private static final Color PANEL_OUTLINE = Color.WHITE;
    private static final Color PANEL_FILL = new Color(0.10f, 0.10f, 0.13f, 1f);
    private static final Color MODAL_BACKDROP = new Color(0f, 0f, 0f, 0.55f);
    private static final Color SCREENSHOT_FILL = new Color(0.07f, 0.07f, 0.09f, 1f);

    private static final float PANEL_WIDTH = 1180f;
    private static final float PANEL_HEIGHT = 790f;
    private static final float SCREENSHOT_X = 35f;
    private static final float SCREENSHOT_Y = -90f;
    private static final float SCREENSHOT_WIDTH = 500f;
    private static final float SCREENSHOT_HEIGHT = 350f;

    private final SpriteBatch spriteBatch;
    private final Texture backgroundTexture;
    private final NinePatch patch;
    private final BitmapFont titleFont;
    private final BitmapFont bodyFont;
    private final MenuButtonRenderer buttonRenderer;
    private final GlyphLayout layout = new GlyphLayout();

    public TutorialRenderer(
        SpriteBatch spriteBatch,
        Texture backgroundTexture,
        NinePatch patch,
        BitmapFont titleFont,
        BitmapFont bodyFont
    ) {
        this.spriteBatch = spriteBatch;
        this.backgroundTexture = backgroundTexture;
        this.patch = patch;
        this.titleFont = titleFont;
        this.bodyFont = bodyFont;
        this.buttonRenderer = new MenuButtonRenderer(spriteBatch, patch, bodyFont);
    }

    public void drawPage(
        TutorialPage page,
        int pageNumber,
        int pageCount,
        MainMenuButton backButton,
        MainMenuButton.State backButtonState,
        MainMenuButton previousButton,
        MainMenuButton.State previousButtonState,
        MainMenuButton nextButton,
        MainMenuButton.State nextButtonState
    ) {
        float panelX = -PANEL_WIDTH / 2f;
        float panelY = -PANEL_HEIGHT / 2f;

        drawDimmedMenuBackground();
        drawPanel(panelX, panelY);
        drawPageContent(page, pageNumber, pageCount, panelX, panelY);
        buttonRenderer.draw(backButton, backButtonState);
        buttonRenderer.draw(previousButton, previousButtonState);
        buttonRenderer.draw(nextButton, nextButtonState);
    }

    private void drawDimmedMenuBackground() {
        spriteBatch.begin();
        spriteBatch.setColor(MODAL_BACKDROP);
        spriteBatch.draw(backgroundTexture, -800f, -450f, 1600f, 900f);
        spriteBatch.setColor(Color.WHITE);
        spriteBatch.end();
    }

    private void drawPanel(float panelX, float panelY) {
        spriteBatch.begin();
        spriteBatch.setColor(PANEL_SHADOW);
        patch.draw(spriteBatch, panelX, panelY - 8f, PANEL_WIDTH, PANEL_HEIGHT);
        spriteBatch.setColor(PANEL_OUTLINE);
        patch.draw(spriteBatch, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);
        spriteBatch.setColor(PANEL_FILL);
        patch.draw(spriteBatch, panelX + 6f, panelY + 6f, PANEL_WIDTH - 12f, PANEL_HEIGHT - 12f);

        spriteBatch.setColor(PANEL_OUTLINE);
        patch.draw(spriteBatch, SCREENSHOT_X, SCREENSHOT_Y, SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT);
        spriteBatch.setColor(SCREENSHOT_FILL);
        patch.draw(
            spriteBatch,
            SCREENSHOT_X + 5f,
            SCREENSHOT_Y + 5f,
            SCREENSHOT_WIDTH - 10f,
            SCREENSHOT_HEIGHT - 10f
        );
        spriteBatch.end();
    }

    private void drawPageContent(
        TutorialPage page,
        int pageNumber,
        int pageCount,
        float panelX,
        float panelY
    ) {
        spriteBatch.begin();
        titleFont.setColor(Color.WHITE);
        titleFont.draw(spriteBatch, "HOW TO PLAY", panelX + 48f, panelY + PANEL_HEIGHT - 50f);

        bodyFont.setColor(Color.WHITE);
        bodyFont.draw(spriteBatch, pageNumber + ". " + page.getTitle(), panelX + 52f, 270f);
        bodyFont.draw(
            spriteBatch,
            page.getDescription(),
            panelX + 52f,
            205f,
            490f,
            Align.topLeft,
            true
        );

        layout.setText(bodyFont, "GAMEPLAY SCREENSHOT");
        bodyFont.draw(
            spriteBatch,
            "GAMEPLAY SCREENSHOT",
            SCREENSHOT_X,
            SCREENSHOT_Y + SCREENSHOT_HEIGHT / 2f + layout.height / 2f,
            SCREENSHOT_WIDTH,
            Align.center,
            false
        );
        bodyFont.draw(
            spriteBatch,
            pageNumber + " / " + pageCount,
            160f,
            panelY + 60f,
            200f,
            Align.center,
            false
        );
        spriteBatch.setColor(Color.WHITE);
        spriteBatch.end();
    }
}
