package io.wasabi.urg.managers;

import org.junit.jupiter.api.Test;

import io.wasabi.urg.state.RunState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RoundManagerEndlessTest {
    private static final int LAST_ACT = 3;
    private static final int ROUNDS_PER_ACT = 5;
    private static final int FIRST_ENDLESS_ACT = 4;
    private static final int SPINS_PER_ROUND = 5;
    private static final int BASE_TICKET_REWARD = 4;
    private static final int TICKETS_PER_UNUSED_SPIN = 1;

    private RunState runState;
    private RoundManager roundManager;

    private void newRoundManager() {
        runState = new RunState();
        roundManager = new RoundManager(runState);
    }

    @Test
    void startEndlessAdvancesToTheFirstEndlessAct() {
        newRoundManager();

        roundManager.startEndless();

        assertEquals(FIRST_ENDLESS_ACT, roundManager.getAct());
        assertEquals(1, roundManager.getRound());
    }

    @Test
    void startEndlessRestoresASpinsAndClearsCompletionFromAFinishedRun() {
        newRoundManager();
        roundManager.setAct(LAST_ACT);
        roundManager.setRound(ROUNDS_PER_ACT);
        roundManager.setRunComplete(true);
        roundManager.setSpinsRemaining(0);

        roundManager.startEndless();

        assertEquals(FIRST_ENDLESS_ACT, roundManager.getAct());
        assertEquals(1, roundManager.getRound());
        assertFalse(roundManager.isRunComplete());
        assertEquals(SPINS_PER_ROUND, roundManager.getSpinsRemaining());
    }

    @Test
    void awardingBeforeStartEndlessPaysForTheWonRoundsLeftoverSpins() {
        newRoundManager();
        roundManager.setSpinsRemaining(2);

        roundManager.awardTickets();
        roundManager.startEndless();

        assertEquals(BASE_TICKET_REWARD + 2 * TICKETS_PER_UNUSED_SPIN, runState.getTickets());
    }

    @Test
    void awardingAfterStartEndlessPaysForTheResetSpinsCounter() {
        newRoundManager();
        roundManager.setSpinsRemaining(2);

        roundManager.startEndless();
        roundManager.awardTickets();

        assertEquals(BASE_TICKET_REWARD + SPINS_PER_ROUND * TICKETS_PER_UNUSED_SPIN, runState.getTickets());
    }
}
