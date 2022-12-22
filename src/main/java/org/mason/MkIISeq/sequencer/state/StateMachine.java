package org.mason.MkIISeq.sequencer.state;

import java.util.Arrays;
import java.util.List;

public class StateMachine {

    private final List<StateEntry> state = Arrays.asList(
            new StateEntry(), new StateEntry(), new StateEntry(),
            new StateEntry(), new StateEntry(), new StateEntry(),
            new StateEntry(), new StateEntry()
    );

    public void setBeatState(int index, char newBeatState) {
        this.state.get(index).beatState = newBeatState;
    }

    public void setSequencerState(int index, char newSequencerState) {
        this.state.get(index).sequencerState = newSequencerState;
    }

    public void setSequencerThread(int index, Thread thread) {
        this.state.get(index).threadInstance = thread;
    }

    public char getBeatState(int desiredIndex) {
        return state.get(desiredIndex).beatState;
    }

    public char getSequencerState(int desiredIndex) {
        return state.get(desiredIndex).sequencerState;
    }

    public Thread getSequencerThread(int desiredIndex) {
        return state.get(desiredIndex).threadInstance;
    }


}
