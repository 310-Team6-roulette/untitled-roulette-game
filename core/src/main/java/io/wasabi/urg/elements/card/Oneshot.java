package io.wasabi.urg.elements.card;

import io.wasabi.urg.Roulette;
import io.wasabi.urg.state.EffectType;

public class Oneshot extends Card {
    public Oneshot() {
        super(Rarity.RARE);
        this.price = 15;
        this.sellPrice = 7;
        tooltip.setTitle("Oneshot");
        tooltip.setMinWidth(200f);
        tooltip.setDescription("Set your spins to [RED]1\n[BLACK]All card effects trigger [RED]TWICE\n[#888888](overrides other cards' effects on spin count)");
    }

    @Override
    public int getAdditionalEffectTriggers() {
        return 1;
    }

    @Override
    public void afterCardEffects(EffectType effectType) {
        if (effectType == EffectType.ROUND_START) {
            Roulette.getInstance().getRoundManager().setSpinsRemaining(1);
        }
        triggerDisplay();
    }
}
