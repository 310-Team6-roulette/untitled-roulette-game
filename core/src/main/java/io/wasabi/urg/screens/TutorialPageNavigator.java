package io.wasabi.urg.screens;

/** Tracks the current page and enforces the tutorial's first and last page boundaries. */
public final class TutorialPageNavigator {
    private static final TutorialPage[] PAGES = TutorialPage.values();

    private int currentPageIndex;

    public TutorialPage getCurrentPage() {
        return PAGES[currentPageIndex];
    }

    public int getCurrentPageNumber() {
        return currentPageIndex + 1;
    }

    public int getPageCount() {
        return PAGES.length;
    }

    public boolean canGoToPreviousPage() {
        return currentPageIndex > 0;
    }

    public boolean canGoToNextPage() {
        return currentPageIndex < PAGES.length - 1;
    }

    public void goToPreviousPage() {
        if (canGoToPreviousPage()) {
            currentPageIndex--;
        }
    }

    public void goToNextPage() {
        if (canGoToNextPage()) {
            currentPageIndex++;
        }
    }
}
