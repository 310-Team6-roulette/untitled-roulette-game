package io.wasabi.urg.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.wasabi.urg.elements.boss.Bartender;
import io.wasabi.urg.elements.boss.Boss;
import io.wasabi.urg.elements.boss.Gamer;
import io.wasabi.urg.elements.boss.House;
import io.wasabi.urg.elements.boss.Taxman;
import io.wasabi.urg.state.RunState;

class RoundManagerBossSelectionTest {

    @Test
    void campaignActsUseTheirAssignedBosses() {
        RoundManager roundManager = new RoundManager(new RunState());

        assertTrue(roundManager.selectBossForAct(1) instanceof Taxman);
        assertTrue(roundManager.selectBossForAct(2) instanceof Gamer);
        assertTrue(roundManager.selectBossForAct(3) instanceof House);
    }

    @Test
    void endlessPoolContainsEveryBossAndReplenishesAfterBeingExhausted() {
        RoundManager roundManager = new RoundManager(new RunState());
        Set<Class<? extends Boss>> firstCycle = new HashSet<Class<? extends Boss>>();
        Set<Class<? extends Boss>> secondCycle = new HashSet<Class<? extends Boss>>();

        for (int i = 0; i < 4; i++) {
            firstCycle.add(roundManager.selectBossForAct(4).getClass());
        }
        for (int i = 0; i < 4; i++) {
            secondCycle.add(roundManager.selectBossForAct(4).getClass());
        }

        assertEquals(
            Set.of(Bartender.class, Gamer.class, Taxman.class, House.class),
            firstCycle
        );
        assertEquals(firstCycle, secondCycle);
    }
}
