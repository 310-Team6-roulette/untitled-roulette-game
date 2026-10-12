package io.wasabi.urg.ui;

import java.util.EnumSet;
import java.util.Set;

import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.wasabi.urg.ui.Victory.Action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VictoryButtonLayoutTest {
    private static final float PANEL_WIDTH = 720f;
    private static final float PANEL_BOTTOM = -310f;

    private Rectangle endless;
    private Rectangle playAgain;
    private Rectangle mainMenu;

    @BeforeEach
    void setUp() {
        endless = new Rectangle();
        playAgain = new Rectangle();
        mainMenu = new Rectangle();
        Victory.layoutButtons(endless, playAgain, mainMenu, PANEL_WIDTH, PANEL_BOTTOM);
    }

    @Test
    void mapsTheCentreOfEachButtonToItsAction() {
        assertEquals(Action.ENDLESS_MODE, actionAtCentreOf(endless));
        assertEquals(Action.PLAY_AGAIN, actionAtCentreOf(playAgain));
        assertEquals(Action.MAIN_MENU, actionAtCentreOf(mainMenu));
    }

    @Test
    void everyVictoryActionIsReachableFromAButton() {
        Set<Action> reachable = EnumSet.noneOf(Action.class);
        reachable.add(actionAtCentreOf(endless));
        reachable.add(actionAtCentreOf(playAgain));
        reachable.add(actionAtCentreOf(mainMenu));

        assertEquals(EnumSet.allOf(Action.class), reachable);
    }

    @Test
    void returnsNullWhenThePointIsNotOverAnyButton() {
        assertNull(Victory.actionAt(endless, playAgain, mainMenu, 0f, 10_000f));
        assertNull(Victory.actionAt(endless, playAgain, mainMenu, -PANEL_WIDTH, PANEL_BOTTOM));
    }

    @Test
    void placesEndlessModeAboveTheOtherTwoButtons() {
        assertTrue(endless.y > playAgain.y);
        assertTrue(endless.y > mainMenu.y);
    }

    @Test
    void placesPlayAgainAndMainMenuSideBySide() {
        assertEquals(playAgain.y, mainMenu.y, 0.001f);
        assertTrue(playAgain.x < mainMenu.x);
        assertTrue(playAgain.x + playAgain.width <= mainMenu.x);
    }

    @Test
    void centersButtonsHorizontallyWithinThePanel() {
        assertEquals(0f, endless.x + endless.width / 2f, 0.001f);
        float rowCentre = (playAgain.x + mainMenu.x + mainMenu.width) / 2f;
        assertEquals(0f, rowCentre, 0.001f);
    }

    @Test
    void buttonsDoNotOverlap() {
        assertFalse(endless.overlaps(playAgain));
        assertFalse(endless.overlaps(mainMenu));
        assertFalse(playAgain.overlaps(mainMenu));
    }

    @Test
    void keepsButtonsAtTheConfiguredSize() {
        assertEquals(Victory.BUTTON_WIDTH, endless.width, 0.001f);
        assertEquals(Victory.BUTTON_HEIGHT, endless.height, 0.001f);
        assertEquals(Victory.BUTTON_WIDTH, playAgain.width, 0.001f);
        assertEquals(Victory.BUTTON_WIDTH, mainMenu.width, 0.001f);
    }

    private Action actionAtCentreOf(Rectangle button) {
        return Victory.actionAt(
            endless, playAgain, mainMenu,
            button.x + button.width / 2f,
            button.y + button.height / 2f);
    }
}
