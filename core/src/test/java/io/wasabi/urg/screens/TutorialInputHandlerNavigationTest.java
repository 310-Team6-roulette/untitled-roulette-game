package io.wasabi.urg.screens;

import java.util.concurrent.atomic.AtomicReference;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxNativesLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TutorialInputHandlerNavigationTest {
    private OrthographicCamera camera;

    @BeforeEach
    void setUp() {
        GdxNativesLoader.load();
        camera = new TestCamera();
    }

    @Test
    void navigationButtonsHaveIndependentHoverStates() {
        TutorialInputHandler handler = newHandler(action -> { });
        int[] nextButtonPoint = screenPoint(400f, -335f);

        handler.mouseMoved(nextButtonPoint[0], nextButtonPoint[1]);

        assertEquals(MainMenuButton.State.NORMAL, handler.getPreviousButtonState());
        assertEquals(MainMenuButton.State.HOVER, handler.getNextButtonState());
    }

    @Test
    void disabledNavigationButtonDoesNotActivate() {
        AtomicReference<MainMenuButton.Action> selectedAction = new AtomicReference<>();
        TutorialInputHandler handler = newHandler(selectedAction::set);
        handler.setNavigationEnabled(false, true);
        int[] previousButtonPoint = screenPoint(60f, -335f);

        handler.mouseMoved(previousButtonPoint[0], previousButtonPoint[1]);
        handler.touchDown(previousButtonPoint[0], previousButtonPoint[1], 0, 0);
        handler.touchUp(previousButtonPoint[0], previousButtonPoint[1], 0, 0);

        assertEquals(MainMenuButton.State.DISABLED, handler.getPreviousButtonState());
        assertNull(selectedAction.get());
    }

    private TutorialInputHandler newHandler(MainMenuButton.ActionHandler actionHandler) {
        MainMenuButton backButton = new MainMenuButton(
            MainMenuButton.Action.BACK_TO_MENU,
            "BACK TO MENU",
            -542f,
            -371f,
            450f,
            72f
        );
        MainMenuButton previousButton = new MainMenuButton(
            MainMenuButton.Action.PREVIOUS_TUTORIAL_PAGE,
            "PREVIOUS",
            -40f,
            -371f,
            200f,
            72f
        );
        MainMenuButton nextButton = new MainMenuButton(
            MainMenuButton.Action.NEXT_TUTORIAL_PAGE,
            "NEXT",
            360f,
            -371f,
            200f,
            72f
        );
        return new TutorialInputHandler(camera, backButton, previousButton, nextButton, actionHandler);
    }

    private int[] screenPoint(float worldX, float worldY) {
        return new int[] {
            Math.round(worldX + 800f),
            Math.round(450f - worldY)
        };
    }

    private static final class TestCamera extends OrthographicCamera {
        private TestCamera() {
            super(1600f, 900f);
        }

        @Override
        public Vector3 unproject(Vector3 screenCoordinates) {
            screenCoordinates.x -= 800f;
            screenCoordinates.y = 450f - screenCoordinates.y;
            return screenCoordinates;
        }
    }
}
