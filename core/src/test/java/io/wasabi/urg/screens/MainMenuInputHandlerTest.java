package io.wasabi.urg.screens;

import java.util.concurrent.atomic.AtomicReference;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxNativesLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainMenuInputHandlerTest {
    private static final MainMenuButton[] BUTTONS = {
        new MainMenuButton(MainMenuButton.Action.PLAY, "PLAY", -450f, 100f, 390f, 72f),
        new MainMenuButton(MainMenuButton.Action.QUIT, "QUIT", -450f, -300f, 390f, 72f)
    };

    private OrthographicCamera camera;

    @BeforeEach
    void setUp() {
        GdxNativesLoader.load();
        camera = new TestCamera();
    }

    @Test
    void reportsNormalStateBeforeInteraction() {
        MainMenuInputHandler handler = newHandler(action -> { });

        assertEquals(MainMenuButton.State.NORMAL, handler.getButtonState(0));
    }

    @Test
    void reportsPressedStateWhenButtonIsTouched() {
        MainMenuInputHandler handler = newHandler(action -> { });
        int[] playPoint = screenPoint(-300f, 130f);

        assertTrue(handler.touchDown(playPoint[0], playPoint[1], 0, 0));
        assertEquals(MainMenuButton.State.PRESSED, handler.getButtonState(0));
    }

    @Test
    void dispatchesPlayActionWhenReleasedInsidePlayButton() {
        AtomicReference<MainMenuButton.Action> selectedAction = new AtomicReference<>();
        MainMenuInputHandler handler = newHandler(selectedAction::set);
        int[] playPoint = screenPoint(-300f, 130f);

        handler.touchDown(playPoint[0], playPoint[1], 0, 0);
        handler.touchUp(playPoint[0], playPoint[1], 0, 0);

        assertEquals(MainMenuButton.Action.PLAY, selectedAction.get());
    }

    @Test
    void dispatchesQuitActionWhenReleasedInsideQuitButton() {
        AtomicReference<MainMenuButton.Action> selectedAction = new AtomicReference<>();
        MainMenuInputHandler handler = newHandler(selectedAction::set);
        int[] quitPoint = screenPoint(-300f, -260f);

        handler.touchDown(quitPoint[0], quitPoint[1], 0, 0);
        handler.touchUp(quitPoint[0], quitPoint[1], 0, 0);

        assertEquals(MainMenuButton.Action.QUIT, selectedAction.get());
    }

    @Test
    void doesNotDispatchActionWhenReleasedOutsidePressedButton() {
        AtomicReference<MainMenuButton.Action> selectedAction = new AtomicReference<>();
        MainMenuInputHandler handler = newHandler(selectedAction::set);
        int[] playPoint = screenPoint(-300f, 130f);
        int[] outsidePoint = screenPoint(300f, 130f);

        handler.touchDown(playPoint[0], playPoint[1], 0, 0);
        handler.touchUp(outsidePoint[0], outsidePoint[1], 0, 0);

        assertNull(selectedAction.get());
    }

    private MainMenuInputHandler newHandler(MainMenuButton.ActionHandler actionHandler) {
        return new MainMenuInputHandler(camera, BUTTONS, actionHandler);
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
