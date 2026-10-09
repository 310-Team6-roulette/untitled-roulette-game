package io.wasabi.urg.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import java.util.Map;

/** Draws tutorial content independently from the main-menu scene and controls. */
public final class TutorialRenderer {
    /** Groups fonts by their role in the tutorial view. */
    public static final class Fonts {
        private final BitmapFont title;
        private final BitmapFont heading;
        private final BitmapFont body;
        private final BitmapFont button;

        public Fonts(BitmapFont title, BitmapFont heading, BitmapFont body, BitmapFont button) {
            this.title = title;
            this.heading = heading;
            this.body = body;
            this.button = button;
        }
    }

    private static final Color PANEL_SHADOW = new Color(0f, 0f, 0f, 0.45f);
    private static final Color PANEL_OUTLINE = Color.WHITE;
    private static final Color PANEL_FILL = new Color(0.10f, 0.10f, 0.13f, 1f);
    private static final Color MODAL_BACKDROP = new Color(0f, 0f, 0f, 0.55f);
    private static final float PANEL_WIDTH = 1180f;
    private static final float PANEL_HEIGHT = 790f;
    private static final float SCREENSHOT_X = 35f;
    private static final float SCREENSHOT_Y = -90f;
    private static final float SCREENSHOT_WIDTH = 500f;
    private static final float SCREENSHOT_HEIGHT = 350f;
    private static final float SCREENSHOT_TEXT_WIDTH = SCREENSHOT_WIDTH - 10f;
    private static final float BODY_FONT_SCALE = 1.1f;
    private static final float BODY_FONT_SCALE_STEP = 0.05f;
    // Paragraph gaps are added between explicit blank-line-separated blocks, not wrapped lines.
    private static final float BODY_PARAGRAPH_GAP = 26f;
    private static final float BODY_MAX_HEIGHT = 490f;
    private static final float MIN_BODY_FONT_SCALE = 0.84f;

    private final SpriteBatch spriteBatch;
    private final Texture backgroundTexture;
    private final Map<TutorialPage, Texture> screenshots;
    private final NinePatch patch;
    private final BitmapFont titleFont;
    private final BitmapFont headingFont;
    private final BitmapFont bodyFont;
    private final BitmapFont buttonFont;
    private final MenuButtonRenderer buttonRenderer;
    // One scale is shared across pages to keep text visually consistent; it is measured once.
    private final float bodyFontScale;
    private final GlyphLayout layout = new GlyphLayout();

    public TutorialRenderer(
        SpriteBatch spriteBatch,
        Texture backgroundTexture,
        Map<TutorialPage, Texture> screenshots,
        NinePatch patch,
        Fonts fonts
    ) {
        this.spriteBatch = spriteBatch;
        this.backgroundTexture = backgroundTexture;
        this.screenshots = screenshots;
        this.patch = patch;
        this.titleFont = fonts.title;
        this.headingFont = fonts.heading;
        this.bodyFont = fonts.body;
        this.buttonFont = fonts.button;
        this.buttonRenderer = new MenuButtonRenderer(spriteBatch, patch, fonts.button);
        this.bodyFontScale = calculateSharedBodyFontScale();
    }

    public void drawPage(TutorialPage page, int pageNumber, int pageCount) {
        float panelX = -PANEL_WIDTH / 2f;
        float panelY = -PANEL_HEIGHT / 2f;

        // Keep the menu recognizable behind the modal while the tutorial owns the foreground.
        drawDimmedMenuBackground();
        Texture screenshot = screenshots.get(page);
        drawPanel(panelX, panelY, screenshot);
        drawPageContent(page, pageNumber, pageCount, panelX, panelY, screenshot != null);
    }

    public void drawButton(MainMenuButton button, MainMenuButton.State state) {
        buttonRenderer.draw(button, state);
    }

    private void drawDimmedMenuBackground() {
        spriteBatch.begin();
        spriteBatch.setColor(MODAL_BACKDROP);
        spriteBatch.draw(backgroundTexture, -800f, -450f, 1600f, 900f);
        spriteBatch.setColor(Color.WHITE);
        spriteBatch.end();
    }

    private void drawPanel(float panelX, float panelY, Texture screenshot) {
        spriteBatch.begin();
        spriteBatch.setColor(PANEL_SHADOW);
        patch.draw(spriteBatch, panelX, panelY - 8f, PANEL_WIDTH, PANEL_HEIGHT);
        spriteBatch.setColor(PANEL_OUTLINE);
        patch.draw(spriteBatch, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);
        spriteBatch.setColor(PANEL_FILL);
        patch.draw(spriteBatch, panelX + 6f, panelY + 6f, PANEL_WIDTH - 12f, PANEL_HEIGHT - 12f);

        if (screenshot != null) {
            // Screenshots have different aspect ratios, so fit them inside a common frame without cropping.
            float scale = Math.min(
                (SCREENSHOT_WIDTH - 10f) / screenshot.getWidth(),
                (SCREENSHOT_HEIGHT - 10f) / screenshot.getHeight()
            );
            float imageWidth = screenshot.getWidth() * scale;
            float imageHeight = screenshot.getHeight() * scale;
            float imageX = SCREENSHOT_X + (SCREENSHOT_WIDTH - imageWidth) / 2f;
            float imageY = SCREENSHOT_Y + (SCREENSHOT_HEIGHT - imageHeight) / 2f;
            spriteBatch.setColor(PANEL_OUTLINE);
            patch.draw(spriteBatch, imageX - 5f, imageY - 5f, imageWidth + 10f, imageHeight + 10f);
            spriteBatch.setColor(Color.WHITE);
            spriteBatch.draw(screenshot, imageX, imageY, imageWidth, imageHeight);
        }
        spriteBatch.end();
    }

    private void drawPageContent(
        TutorialPage page,
        int pageNumber,
        int pageCount,
        float panelX,
        float panelY,
        boolean showScreenshot
    ) {
        // Text shares the panel with a screenshot when present; text-only pages use the full column.
        float descriptionWidth = showScreenshot ? SCREENSHOT_TEXT_WIDTH : PANEL_WIDTH - 104f;

        spriteBatch.begin();
        titleFont.setColor(Color.WHITE);
        titleFont.draw(spriteBatch, "HOW TO PLAY", panelX + 48f, panelY + PANEL_HEIGHT - 50f);

        headingFont.getData().setScale(1f);
        headingFont.setColor(Color.WHITE);
        headingFont.draw(spriteBatch, pageNumber + ". " + page.getTitle(), panelX + 52f, 270f);

        float originalScaleX = bodyFont.getData().scaleX;
        float originalScaleY = bodyFont.getData().scaleY;
        boolean originalMarkupEnabled = bodyFont.getData().markupEnabled;
        // FontManager shares font instances, so restore mutable drawing settings after markup rendering.
        bodyFont.getData().markupEnabled = true;
        bodyFont.getData().setScale(bodyFontScale);
        bodyFont.setColor(Color.WHITE);
        drawBodyParagraphs(page.getDescription(), panelX + 52f, 205f, descriptionWidth);

        buttonFont.draw(
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

        bodyFont.getData().setScale(originalScaleX, originalScaleY);
        bodyFont.getData().markupEnabled = originalMarkupEnabled;
    }

    private float calculateSharedBodyFontScale() {
        float originalScaleX = bodyFont.getData().scaleX;
        float originalScaleY = bodyFont.getData().scaleY;
        boolean originalMarkupEnabled = bodyFont.getData().markupEnabled;
        float scale = BODY_FONT_SCALE;
        try {
            // Use the largest configured scale that fits every page's available text column.
            bodyFont.getData().markupEnabled = true;
            bodyFont.getData().setScale(scale);
            while (!allPagesFit() && scale > MIN_BODY_FONT_SCALE) {
                scale = Math.max(MIN_BODY_FONT_SCALE, scale - BODY_FONT_SCALE_STEP);
                bodyFont.getData().setScale(scale);
            }
            return scale;
        } finally {
            bodyFont.getData().setScale(originalScaleX, originalScaleY);
            bodyFont.getData().markupEnabled = originalMarkupEnabled;
        }
    }

    private boolean allPagesFit() {
        // The fitted size must work for every page, not just whichever page is currently visible.
        for (TutorialPage page : TutorialPage.values()) {
            float width = page.hasScreenshot() ? SCREENSHOT_TEXT_WIDTH : PANEL_WIDTH - 104f;
            if (measureBodyHeight(page.getDescription(), width) > BODY_MAX_HEIGHT) {
                return false;
            }
        }
        return true;
    }

    private float measureBodyHeight(String description, float width) {
        String[] paragraphs = description.split("\\n\\n");
        float height = 0f;
        for (int i = 0; i < paragraphs.length; i++) {
            layout.setText(bodyFont, paragraphs[i], Color.WHITE, width, Align.topLeft, true);
            height += layout.height;
            if (i < paragraphs.length - 1) {
                height += BODY_PARAGRAPH_GAP;
            }
        }
        return height;
    }

    private void drawBodyParagraphs(String description, float x, float topY, float width) {
        String[] paragraphs = description.split("\\n\\n");
        for (int i = 0; i < paragraphs.length; i++) {
            layout.setText(bodyFont, paragraphs[i], Color.WHITE, width, Align.topLeft, true);
            bodyFont.draw(spriteBatch, paragraphs[i], x, topY, width, Align.topLeft, true);
            topY -= layout.height + BODY_PARAGRAPH_GAP;
        }
    }
}
