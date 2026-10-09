package io.wasabi.urg.screens;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Keeps pointer input within the tutorial overlay and delegates button interaction to the
 * shared menu-button handler.
 */
public final class TutorialInputHandler extends InputAdapter {
    private static final int BACK_BUTTON_INDEX = 0;
    private static final int PREVIOUS_BUTTON_INDEX = 1;
    private static final int NEXT_BUTTON_INDEX = 2;

    private final MenuButtonInputHandler buttonHandler;
    private final MainMenuButton previousButton;
    private final MainMenuButton nextButton;

    public TutorialInputHandler(
        OrthographicCamera camera,
        MainMenuButton backButton,
        MainMenuButton previousButton,
        MainMenuButton nextButton,
        MainMenuButton.ActionHandler actionHandler
    ) {
        this.previousButton = previousButton;
        this.nextButton = nextButton;
        buttonHandler = new MenuButtonInputHandler(
            camera,
            new MainMenuButton[] {backButton, previousButton, nextButton},
            actionHandler
        );
    }

    public MainMenuButton.State getBackButtonState() {
        return buttonHandler.getButtonState(BACK_BUTTON_INDEX);
    }

    public MainMenuButton.State getPreviousButtonState() {
        return buttonHandler.getButtonState(PREVIOUS_BUTTON_INDEX);
    }

    public MainMenuButton.State getNextButtonState() {
        return buttonHandler.getButtonState(NEXT_BUTTON_INDEX);
    }

    public void setNavigationEnabled(boolean canGoToPreviousPage, boolean canGoToNextPage) {
        previousButton.setEnabled(canGoToPreviousPage);
        nextButton.setEnabled(canGoToNextPage);
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
