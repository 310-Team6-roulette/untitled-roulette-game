package io.wasabi.urg.state;

/** Represents events in the game that can trigger effects. */
public enum EffectType {
    ROUND_START,
    BEFORE_SPIN,
    AFTER_SPIN,
    ROUND_END,
    CHARM_CONSUMED,    
}
