package org.mason.MkIISeq.sequencer.state;

class StateEntry {

    private final char INITIAL_BEAT_STATE      = 0b0000_0000_0000_0001;
    private final char INITIAL_SEQUENCER_STATE = 0b0000_0000_0000_0000;

    char beatState      = INITIAL_BEAT_STATE;
    char sequencerState = INITIAL_SEQUENCER_STATE;
    Thread threadInstance;
}
