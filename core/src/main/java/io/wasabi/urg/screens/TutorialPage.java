package io.wasabi.urg.screens;

/** Copy for each page in the introductory tutorial. */
public enum TutorialPage {
    GOAL(
        "THE GOAL",
        "Reach the round quota before you run out of spins.\n"
            + "Place bets on the table, then spin the wheel.\n"
            + "Winning rounds earn tickets for the shop."
    ),
    BETTING(
        "PLACE YOUR BETS",
        "Choose a chip from the tray and drag it onto a betting zone.\n"
            + "Different zones cover different pockets and pay out at different rates.\n"
            + "You can place multiple bets before spinning."
    ),
    SPINNING(
        "SPIN AND COLLECT",
        "When your bets are ready, return to the wheel and press SPIN.\n"
            + "The ball lands on a pocket and your bets are resolved.\n"
            + "Your chips must reach the quota to clear the round."
    ),
    SHOP(
        "ROUND REWARDS AND THE SHOP",
        "Clearing a round awards tickets, including a bonus for unused spins.\n"
            + "Spend tickets in the shop to improve your run.\n"
            + "Drag items to the buy or sell squares to manage your collection."
    ),
    PROGRESSION(
        "KEEP PROGRESSING",
        "Each act contains five rounds, with a boss round at the end.\n"
            + "Reach the quota in each round to move forward.\n"
            + "Build your collection and adapt your strategy as the run gets harder."
    );

    private final String title;
    private final String description;

    TutorialPage(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPageCount() {
        return values().length;
    }
}
