package io.wasabi.urg.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

/** Draws menu-style buttons consistently across the main menu and tutorial. */
public final class MenuButtonRenderer {
    private static final Color PANEL_SHADOW = new Color(0f, 0f, 0f, 0.45f);
    private static final Color PANEL_OUTLINE = Color.WHITE;
    private static final Color PANEL_FILL = new Color(0.10f, 0.10f, 0.13f, 1f);
    private static final Color BUTTON_HOVER_FILL = new Color(0.16f, 0.16f, 0.20f, 1f);
    private static final Color BUTTON_PRESSED_FILL = new Color(0.08f, 0.08f, 0.11f, 1f);
    private static final Color BUTTON_DISABLED_FILL = new Color(0.08f, 0.08f, 0.11f, 1f);
    private static final Color BUTTON_DISABLED_TEXT = new Color(0.55f, 0.55f, 0.58f, 1f);

    private final SpriteBatch spriteBatch;
    private final NinePatch patch;
    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    public MenuButtonRenderer(SpriteBatch spriteBatch, NinePatch patch, BitmapFont font) {
        this.spriteBatch = spriteBatch;
        this.patch = patch;
        this.font = font;
    }

    public void draw(MainMenuButton button, MainMenuButton.State state) {
        layout.setText(font, button.getLabel());
        float shadowOffset = state == MainMenuButton.State.PRESSED ? 4f : 6f;
        float pressOffsetY = state == MainMenuButton.State.PRESSED ? -4f : 0f;

        spriteBatch.begin();
        spriteBatch.setColor(PANEL_SHADOW);
        patch.draw(
            spriteBatch,
            button.getX(),
            button.getY() - shadowOffset + pressOffsetY,
            button.getWidth(),
            button.getHeight()
        );

        spriteBatch.setColor(PANEL_OUTLINE);
        patch.draw(
            spriteBatch,
            button.getX(),
            button.getY() + pressOffsetY,
            button.getWidth(),
            button.getHeight()
        );

        Color fillColor = getFillColor(state);
        spriteBatch.setColor(fillColor);
        patch.draw(
            spriteBatch,
            button.getX() + 5f,
            button.getY() + 5f + pressOffsetY,
            button.getWidth() - 10f,
            button.getHeight() - 10f
        );

        spriteBatch.setColor(Color.WHITE);
        font.setColor(state == MainMenuButton.State.DISABLED ? BUTTON_DISABLED_TEXT : Color.WHITE);
        font.draw(
            spriteBatch,
            button.getLabel(),
            button.getX(),
            button.getY() + button.getHeight() / 2f + layout.height / 2f + pressOffsetY,
            button.getWidth(),
            Align.center,
            false
        );
        spriteBatch.end();
    }

    private Color getFillColor(MainMenuButton.State state) {
        if (state == MainMenuButton.State.HOVER) {
            return BUTTON_HOVER_FILL;
        }
        if (state == MainMenuButton.State.PRESSED) {
            return BUTTON_PRESSED_FILL;
        }
        if (state == MainMenuButton.State.DISABLED) {
            return BUTTON_DISABLED_FILL;
        }
        return PANEL_FILL;
    }
}
