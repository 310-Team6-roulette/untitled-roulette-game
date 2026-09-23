package io.wasabi.urg.screens;

/**
 * Represents one menu entry and the specific action it should trigger when activated.
 * Keeping the action separate from the visible label avoids fragile comparisons against
 * literal UI text such as "PLAY".
 */
public final class MainMenuButton {
    @FunctionalInterface
    public interface ActionHandler {
        void handle(Action action);
    }

    public enum Action {
        PLAY,
        TUTORIAL,
        SETTINGS,
        COLLECTIONS,
        QUIT
    }

    public enum State {
        NORMAL,
        HOVER,
        PRESSED
    }

    private final Action action;
    private final String label;
    private final float x;
    private final float y;
    private final float width;
    private final float height;

    public MainMenuButton(
        Action action,
        String label,
        float x,
        float y,
        float width,
        float height
    ) {
        this.action = action;
        this.label = label;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Action getAction() {
        return action;
    }

    public String getLabel() {
        return label;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
