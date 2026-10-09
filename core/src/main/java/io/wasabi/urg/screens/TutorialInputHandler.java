package io.wasabi.urg.screens;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;

/** Captures input while the tutorial is open and allows the player to return to the menu. */
public final class TutorialInputHandler extends InputAdapter {
    private final MainMenuInputHandler buttonHandler;

    public TutorialInputHandler(
        OrthographicCamera camera,
        MainMenuButton backButton,
        MainMenuButton.ActionHandler actionHandler
    ) {
        buttonHandler = new MainMenuInputHandler(
            camera,
            new MainMenuButton[] {backButton},
            actionHandler
        );
    }

    public MainMenuButton.State getBackButtonState() {
        return buttonHandler.getButtonState(0);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        buttonHandler.touchDown(screenX, screenY, pointer, button);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        buttonHandler.touchUp(screenX, screenY, pointer, button);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        buttonHandler.touchDragged(screenX, screenY, pointer);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        buttonHandler.mouseMoved(screenX, screenY);
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return true;
    }
}
