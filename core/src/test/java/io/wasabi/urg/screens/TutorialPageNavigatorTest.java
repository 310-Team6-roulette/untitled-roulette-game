package io.wasabi.urg.screens;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TutorialPageNavigatorTest {
    @Test
    void startsOnTheGoalPage() {
        TutorialPageNavigator navigator = new TutorialPageNavigator();

        assertEquals(TutorialPage.GOAL, navigator.getCurrentPage());
        assertEquals(1, navigator.getCurrentPageNumber());
        assertEquals(TutorialPage.values().length, navigator.getPageCount());
        assertFalse(navigator.canGoToPreviousPage());
        assertTrue(navigator.canGoToNextPage());
    }

    @Test
    void advancesAndGoesBackBetweenPages() {
        TutorialPageNavigator navigator = new TutorialPageNavigator();

        navigator.goToNextPage();
        assertEquals(TutorialPage.CHIPS, navigator.getCurrentPage());
        assertEquals(2, navigator.getCurrentPageNumber());

        navigator.goToPreviousPage();
        assertEquals(TutorialPage.GOAL, navigator.getCurrentPage());
    }

    @Test
    void navigationStopsAtFirstAndLastPages() {
        TutorialPageNavigator navigator = new TutorialPageNavigator();

        navigator.goToPreviousPage();
        assertEquals(TutorialPage.GOAL, navigator.getCurrentPage());

        while (navigator.canGoToNextPage()) {
            navigator.goToNextPage();
        }
        navigator.goToNextPage();

        assertEquals(TutorialPage.PROGRESSION, navigator.getCurrentPage());
        assertEquals(navigator.getPageCount(), navigator.getCurrentPageNumber());
        assertFalse(navigator.canGoToNextPage());
    }
}
