package io.wasabi.urg.screens;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import io.wasabi.urg.Roulette;

/**
 * Handles only the menu interaction concerns: hit detection, hover states and
 * activation. Keeping this separate from the screen makes the menu easier to reason
 * about and simpler to extend with additional buttons later.
 */
public final class MainMenuInputHandler extends InputAdapter {
    private final OrthographicCamera camera;
    private final MainMenuButton[] buttons;
    private final MainMenuButton.ActionHandler actionHandler;
    private int hoveredIndex = -1;
    private int pressedIndex = -1;

    public MainMenuInputHandler(
        Roulette game,
        MainMenuButton[] buttons,
        MainMenuButton.ActionHandler actionHandler
    ) {
        this(game.getCamera(), buttons, actionHandler);
    }

    public MainMenuInputHandler(
        OrthographicCamera camera,
        MainMenuButton[] buttons,
        MainMenuButton.ActionHandler actionHandler
    ) {
        this.camera = camera;
        this.buttons = buttons;
        this.actionHandler = actionHandler;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        int index = findButtonAtScreenCoordinates(screenX, screenY);
        if (index == -1) {
            return false;
        }

        pressedIndex = index;
        hoveredIndex = index;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        int index = findButtonAtScreenCoordinates(screenX, screenY);
        if (index >= 0 && pressedIndex >= 0) {
            hoveredIndex = index;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        int index = findButtonAtScreenCoordinates(screenX, screenY);
        hoveredIndex = index;
        return index >= 0;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        int index = findButtonAtScreenCoordinates(screenX, screenY);
        if (pressedIndex < 0) {
            return false;
        }

        int pressed = pressedIndex;
        pressedIndex = -1;
        hoveredIndex = index;

        if (pressed == index) {
            actionHandler.handle(buttons[pressed].getAction());
        }
        return true;
    }

    public MainMenuButton.State getButtonState(int index) {
        if (pressedIndex == index) {
            return MainMenuButton.State.PRESSED;
        }
        if (hoveredIndex == index) {
            return MainMenuButton.State.HOVER;
        }
        return MainMenuButton.State.NORMAL;
    }

    /**
     * Converts screen coordinates to the fixed 1600x900 world space used by the menu.
     * This keeps button hitboxes aligned with the background art even when the window is
     * resized and the viewport changes.
     */
    private int findButtonAtScreenCoordinates(int screenX, int screenY) {
        Vector3 world = new Vector3(screenX, screenY, 0f);
        camera.unproject(world);

        for (int i = 0; i < buttons.length; i++) {
            MainMenuButton button = buttons[i];
            boolean isInside =
                world.x >= button.getX() && world.x <= button.getX() + button.getWidth()
                    && world.y >= button.getY() && world.y <= button.getY() + button.getHeight();
            if (isInside) {
                return i;
            }
        }

        return -1;
    }
}
