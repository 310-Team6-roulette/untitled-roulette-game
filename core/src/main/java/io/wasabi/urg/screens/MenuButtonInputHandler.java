package io.wasabi.urg.screens;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

/**
 * Handles hit detection, hover states, and activation for menu-style buttons.
 * Screens provide the buttons and decide what each action does.
 */
public final class MenuButtonInputHandler extends InputAdapter {
    private final OrthographicCamera camera;
    private final MainMenuButton[] buttons;
    private final MainMenuButton.ActionHandler actionHandler;
    private int hoveredIndex = -1;
    private int pressedIndex = -1;

    public MenuButtonInputHandler(
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
        if (!buttons[index].isEnabled()) {
            return MainMenuButton.State.DISABLED;
        }
        if (pressedIndex == index) {
            return MainMenuButton.State.PRESSED;
        }
        if (hoveredIndex == index) {
            return MainMenuButton.State.HOVER;
        }
        return MainMenuButton.State.NORMAL;
    }

    /**
     * Converts screen coordinates into the shared world space so button hitboxes remain
     * aligned when the viewport or window size changes.
     */
    private int findButtonAtScreenCoordinates(int screenX, int screenY) {
        Vector3 world = new Vector3((float) screenX, (float) screenY, 0f);
        camera.unproject(world);

        for (int i = 0; i < buttons.length; i++) {
            MainMenuButton button = buttons[i];
            if (!button.isEnabled()) {
                continue;
            }
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
