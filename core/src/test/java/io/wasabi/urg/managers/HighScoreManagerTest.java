package io.wasabi.urg.managers;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Preferences;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HighScoreManagerTest {

    @Test
    void savesHigherActAndRound() {
        InMemoryPreferences preferences = new InMemoryPreferences();
        HighScoreManager manager = new HighScoreManager(preferences);

        manager.recordScore(2, 3);

        assertEquals(2, manager.getHighScoreAct());
        assertEquals(3, manager.getHighScoreRound());
        assertEquals(2, preferences.getInteger("highScoreAct"));
        assertEquals(3, preferences.getInteger("highScoreRound"));
    }

    @ParameterizedTest
    @CsvSource({
        "1, 5, 2, 3",
        "2, 3, 2, 3",
        "2, 4, 2, 4"
    })
    void comparesNewProgressWithSavedProgress(
        int newAct,
        int newRound,
        int expectedAct,
        int expectedRound
    ) {
        HighScoreManager manager = new HighScoreManager(new InMemoryPreferences());

        manager.recordScore(2, 3);
        manager.recordScore(newAct, newRound);

        assertEquals(expectedAct, manager.getHighScoreAct());
        assertEquals(expectedRound, manager.getHighScoreRound());
    }

    private static final class InMemoryPreferences implements Preferences {
        private final Map<String, Object> values = new HashMap<>();

        @Override
        public Preferences putBoolean(String key, boolean value) {
            values.put(key, value);
            return this;
        }

        @Override
        public Preferences putInteger(String key, int value) {
            values.put(key, value);
            return this;
        }

        @Override
        public Preferences putLong(String key, long value) {
            values.put(key, value);
            return this;
        }

        @Override
        public Preferences putFloat(String key, float value) {
            values.put(key, value);
            return this;
        }

        @Override
        public Preferences putString(String key, String value) {
            values.put(key, value);
            return this;
        }

        @Override
        public Preferences put(Map<String, ?> values) {
            this.values.putAll(values);
            return this;
        }

        @Override
        public boolean getBoolean(String key) {
            return getBoolean(key, false);
        }

        @Override
        public boolean getBoolean(String key, boolean defaultValue) {
            return (boolean) values.getOrDefault(key, defaultValue);
        }

        @Override
        public int getInteger(String key) {
            return getInteger(key, 0);
        }

        @Override
        public int getInteger(String key, int defaultValue) {
            return (int) values.getOrDefault(key, defaultValue);
        }

        @Override
        public long getLong(String key) {
            return getLong(key, 0L);
        }

        @Override
        public long getLong(String key, long defaultValue) {
            return (long) values.getOrDefault(key, defaultValue);
        }

        @Override
        public float getFloat(String key) {
            return getFloat(key, 0f);
        }

        @Override
        public float getFloat(String key, float defaultValue) {
            return (float) values.getOrDefault(key, defaultValue);
        }

        @Override
        public String getString(String key) {
            return getString(key, "");
        }

        @Override
        public String getString(String key, String defaultValue) {
            return (String) values.getOrDefault(key, defaultValue);
        }

        @Override
        public boolean contains(String key) {
            return values.containsKey(key);
        }

        @Override
        public void clear() {
            values.clear();
        }

        @Override
        public void remove(String key) {
            values.remove(key);
        }

        @Override
        public void flush() {
            // This in-memory test double has no external storage to synchronize.
        }

        @Override
        public Map<String, ?> get() {
            return new HashMap<>(values);
        }
    }
}
