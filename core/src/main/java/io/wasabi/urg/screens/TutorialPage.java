package io.wasabi.urg.screens;

/**
 * Content for one tutorial page. Descriptions use BitmapFont markup for highlights and
 * blank lines to separate paragraphs; screenshot paths are relative to the assets directory.
 */
public enum TutorialPage {
    GOAL(
        "THE GOAL",
        "At the start of each round, you have [#FFD700]100 chips[] and [#FFD700]five spins[].\n\n"
            + "Reach the quota shown on screen before you run out of spins.\n\n"
            + "After each spin, winning bets pay out and losing stakes are deducted.\n\n"
            + "Meet the quota to clear the round. If your chips hit 0 or your spins run out first, the run ends.\n\n"
            + "[#FFD700]Tickets[] buy [#FFD700]cards[] and [#FFD700]charms[] in the shop; their effects help shape your strategy.",
        null
    ),
    CHIPS(
        "CHOOSE YOUR CHIPS",
        "On the betting screen, drag a chip from the tray onto a bet area.\n\n"
            + "Chip values are percentages of your current chips: 1%, 5%, 10%, 25%, 50%, or 100%.\n\n"
            + "Place more than one bet if their combined stakes do not exceed your balance.\n\n"
            + "To change a bet, drag its chip to another area; drop it away from the table to remove it.\n\n"
            + "New to roulette? Look up a table guide for more bet placement details.",
        ScreenshotAssets.BETTING_TABLE
    ),
    BET_TYPES(
        "BET TYPES AND PAYOUTS",
        "The number or group you cover must be the winning pocket for your bet to win.\n\n"
            + "[#FFD700]Straight[]: one number, 36x total return.\n\n"
            + "[#FFD700]Split[]: two neighboring numbers that share an edge, 18x.\n\n"
            + "[#FFD700]Street[]: a group of three neighboring numbers, 12x.\n\n"
            + "[#FFD700]Corner[]: four touching numbers, 9x.\n\n"
            + "[#FFD700]Six-line[]: two rows of three, 6x.\n\n"
            + "[#FFD700]Column[] or [#FFD700]dozen[]: a table column or one of the three table sections, 3x.\n\n"
            + "[#FFD700]Red[], [#FFD700]black[], [#FFD700]odd[], [#FFD700]even[], [#FFD700]low[], or [#FFD700]high[]: 2x.\n\n"
            + "Multipliers show the total returned, including your stake: a winning 1-chip straight bet returns 36 chips.",
        null
    ),
    SPINNING(
        "SPIN AND COLLECT",
        "After placing your bets, return to the wheel screen.\n\n"
            + "Click [#FFD700]SPIN[] in the centre of the wheel. You need at least one bet to spin.\n\n"
            + "When the ball lands, bets covering that pocket win; all other stakes are lost.\n\n"
            + "The quota tracks your chips after the result. Keep betting and spinning until you reach it.",
        ScreenshotAssets.WHEEL
    ),
    SHOP(
        "ROUND REWARDS AND THE SHOP",
        "Clearing a round earns four base tickets plus one ticket for each unused spin.\n\n"
            + "In the shop, spend tickets on cards and charms that change your run.\n\n"
            + "Drag an offer to [#32CD32]BUY[] to purchase it, or drag an owned item to [#FF5555]SELL[] to remove it.\n\n"
            + "Select [#FFD700]CONTINUE[] when you are ready for the next round.",
        ScreenshotAssets.SHOP
    ),
    PROGRESSION(
        "KEEP PROGRESSING",
        "A run has three acts, each with five rounds. Every fifth round is a boss round; each boss has a unique effect that changes the rules, so read its description before betting.\n\n"
            + "Clear the quota in every round to advance; the quotas increase as you progress.\n\n"
            + "Use shop items to shape your strategy and help meet later quotas.\n\n"
            + "Fail to reach a round's quota before your spins run out and the run ends.",
        ScreenshotAssets.BOSS
    );

    private static final class ScreenshotAssets {
        private static final String BETTING_TABLE = "ui/TutorialBettingTable.png";
        private static final String WHEEL = "ui/TutorialWheel.png";
        private static final String SHOP = "ui/TutorialShopMenu.png";
        private static final String BOSS = "ui/TutorialBoss.png";

        private ScreenshotAssets() {
        }
    }

    private final String title;
    private final String description;
    private final String screenshotPath;

    TutorialPage(String title, String description, String screenshotPath) {
        this.title = title;
        this.description = description;
        this.screenshotPath = screenshotPath;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasScreenshot() {
        return screenshotPath != null;
    }

    public String getScreenshotPath() {
        return screenshotPath;
    }
}
