package io.wasabi.urg.screens;

/**
 * Content for one tutorial page. Descriptions use BitmapFont markup for highlights and
 * blank lines to separate paragraphs; screenshot paths are relative to the assets directory.
 */
public enum TutorialPage {
    GOAL(
        "THE GOAL",
        "At the start of each round, you have " + TextStyle.GOLD + "100 chips[] and "
            + TextStyle.GOLD + "five spins[].\n\n"
            + TextStyle.GOLD + "Cards[] give passive effects. " + TextStyle.GOLD
            + "Charms[] are consumables: drag one to the wheel to use it; some require selecting pockets.\n\n"
            + "Reach the quota shown on screen before you run out of spins.\n\n"
            + "After each spin, winning bets pay out and losing stakes are deducted.\n\n"
            + "Meet the quota to clear the round. If your chips hit 0 or your spins run out first, the run ends.\n\n"
            + TextStyle.GOLD + "Tickets[] buy " + TextStyle.GOLD + "cards[] and "
            + TextStyle.GOLD + "charms[] in the shop to shape your strategy.",
        null
    ),
    CHIPS(
        "CHOOSE YOUR CHIPS",
        "On the betting screen, drag a chip from the tray onto a bet area.\n\n"
            + "Chip values are percentages of your current chips: 1%, 5%, 10%, 25%, 50%, or 100%.\n\n"
            + "Place more than one bet if their combined stakes do not exceed your balance.\n\n"
            + "To change a bet, drag its chip to another area; drop it away from the table to remove it.\n\n"
            + TextStyle.GOLD + "New to roulette?[] Look up a table guide for more bet placement details.",
        ScreenshotAssets.BETTING_TABLE
    ),
    BET_TYPES(
        "BET TYPES AND PAYOUTS",
        "The number or group you cover must be the winning pocket for your bet to win.\n\n"
            + TextStyle.GOLD + "Straight[]: one number, " + TextStyle.MULTIPLIER + "36x[] total return.\n\n"
            + TextStyle.GOLD + "Split[]: two neighboring numbers that share an edge, "
            + TextStyle.MULTIPLIER + "18x[].\n\n"
            + TextStyle.GOLD + "Street[]: a group of three neighboring numbers, "
            + TextStyle.MULTIPLIER + "12x[].\n\n"
            + TextStyle.GOLD + "Corner[]: four touching numbers, " + TextStyle.MULTIPLIER + "9x[].\n\n"
            + TextStyle.GOLD + "Six-line[]: two rows of three, " + TextStyle.MULTIPLIER + "6x[].\n\n"
            + TextStyle.GOLD + "Column[] or " + TextStyle.GOLD
            + "dozen[]: a table column or one of the three table sections, "
            + TextStyle.MULTIPLIER + "3x[].\n\n"
            + TextStyle.GOLD + "Red[], " + TextStyle.GOLD + "black[], " + TextStyle.GOLD
            + "odd[], " + TextStyle.GOLD + "even[], " + TextStyle.GOLD + "low[], or "
            + TextStyle.GOLD + "high[]: " + TextStyle.MULTIPLIER + "2x[].\n\n"
            + "Multipliers show the total returned, including your stake: a winning 1-chip straight bet returns 36 chips.",
        null
    ),
    SPINNING(
        "SPIN AND COLLECT",
        "After placing your bets, return to the wheel screen.\n\n"
            + "Click " + TextStyle.GOLD + "SPIN[] in the centre of the wheel. You need at least one bet to spin.\n\n"
            + "When the ball lands, bets covering that pocket win; all other stakes are lost.\n\n"
            + "The quota tracks your chips after the result. Keep betting and spinning until you reach it.",
        ScreenshotAssets.WHEEL
    ),
    SHOP(
        "ROUND REWARDS AND THE SHOP",
        "Clearing a round earns four base tickets plus one ticket for each unused spin.\n\n"
            + "In the shop, spend tickets on cards and charms that change your run.\n\n"
            + "Drag an offer to " + TextStyle.BUY + "BUY[] to purchase it, or drag an owned item to "
            + TextStyle.SELL + "SELL[] to remove it.\n\n"
            + "Select " + TextStyle.GOLD + "CONTINUE[] when you are ready for the next round.",
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

    private static final class TextStyle {
        private static final String GOLD = "[#FFD700]";
        private static final String MULTIPLIER = "[#55DDEE]";
        private static final String BUY = "[#32CD32]";
        private static final String SELL = "[#FF5555]";

        private TextStyle() {
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
