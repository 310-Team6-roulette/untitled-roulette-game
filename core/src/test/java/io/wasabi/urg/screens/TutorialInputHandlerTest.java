package io.wasabi.urg.screens;

import java.util.concurrent.atomic.AtomicBoolean;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxNativesLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TutorialInputHandlerTest {
    private OrthographicCamera camera;

    @BeforeEach
    void setUp() {
        GdxNativesLoader.load();
        camera = new TestCamera();
    }

    @Test
    void backButtonClosesTheTutorial() {
        AtomicBoolean closed = new AtomicBoolean();
        TutorialInputHandler handler = newHandler(closed);
        int[] backButtonPoint = screenPoint(-400f, -350f);

        handler.touchDown(backButtonPoint[0], backButtonPoint[1], 0, 0);
        handler.touchUp(backButtonPoint[0], backButtonPoint[1], 0, 0);

        assertTrue(closed.get());
    }

    @Test
    void consumesClicksOutsideTheBackButtonWithoutClosing() {
        AtomicBoolean closed = new AtomicBoolean();
        TutorialInputHandler handler = newHandler(closed);
        int[] pagePoint = screenPoint(0f, 0f);

        assertTrue(handler.touchDown(pagePoint[0], pagePoint[1], 0, 0));
        assertTrue(handler.touchUp(pagePoint[0], pagePoint[1], 0, 0));
        assertFalse(closed.get());
    }

    @Test
    void updatesBackButtonHoverState() {
        TutorialInputHandler handler = newHandler(new AtomicBoolean());
        int[] backButtonPoint = screenPoint(-400f, -350f);

        assertTrue(handler.mouseMoved(backButtonPoint[0], backButtonPoint[1]));
        assertEquals(MainMenuButton.State.HOVER, handler.getBackButtonState());
    }

    private TutorialInputHandler newHandler(AtomicBoolean closed) {
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
        return new TutorialInputHandler(
            camera,
            backButton,
            previousButton,
            nextButton,
            action -> closed.set(action == MainMenuButton.Action.BACK_TO_MENU)
        );
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
