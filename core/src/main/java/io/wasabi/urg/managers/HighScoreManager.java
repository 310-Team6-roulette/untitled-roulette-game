package io.wasabi.urg.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/** Persists the furthest act and round reached on this device. */
public class HighScoreManager {
    private static final String PREFERENCES_NAME = "untitled-roulette-game";
    private static final String HIGH_SCORE_ACT_KEY = "highScoreAct";
    private static final String HIGH_SCORE_ROUND_KEY = "highScoreRound";

    private final Preferences preferences;
    private int highScoreAct;
    private int highScoreRound;

    /** Loads the saved progress, defaulting to no completed round. */
    public HighScoreManager() {
        preferences = Gdx.app.getPreferences(PREFERENCES_NAME);
        highScoreAct = preferences.getInteger(HIGH_SCORE_ACT_KEY, 0);
        highScoreRound = preferences.getInteger(HIGH_SCORE_ROUND_KEY, 0);
    }

    /** Returns the act from the furthest saved run. */
    public int getHighScoreAct() {
        return highScoreAct;
    }

    /** Returns the round from the furthest saved run. */
    public int getHighScoreRound() {
        return highScoreRound;
    }

    /** Saves the act and round only when they exceed the saved progress. */
    public void recordScore(int act, int round) {
        if (act < highScoreAct || (act == highScoreAct && round <= highScoreRound)) {
            return;
        }

        highScoreAct = act;
        highScoreRound = round;
        preferences.putInteger(HIGH_SCORE_ACT_KEY, highScoreAct);
        preferences.putInteger(HIGH_SCORE_ROUND_KEY, highScoreRound);
        preferences.flush();
    }
}
