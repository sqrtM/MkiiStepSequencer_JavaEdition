package org.mason.MkIISeq.sequencer;

import javax.sound.midi.*;
import static java.lang.Math.pow;

public class SequencerBank extends Sequencer implements Receiver {

    StateMachine state = new StateMachine();

    public void initThreads() {
        for(int i = 0; i < 8; i++) {
            int finalI = i;
            state.setSequencerThread(i, new Thread(() -> {
                 while((selectedReceiver != null)) {
                     try {
                         mainLoop(finalI);
                         Thread.sleep(100*finalI);
                     } catch (InterruptedException | InvalidMidiDataException e) {
                         throw new RuntimeException(e);
                     }
                 }
            }));
            state.getSequencerThread(i).start();
        }
    }

    public SequencerBank(Receiver receiver) {
        selectedReceiver = receiver;
    }

    public void mainLoop(int index) throws InvalidMidiDataException {
        if (getActiveMemory() == index) {
            buildMessage(state.getSequencerState(index), state.getBeatState(index));
        }
        state.setBeatState(index,
                (char) ((state.getBeatState(index) < (1 << (bankLength - 1)))
                        ? (state.getBeatState(index) << 1) : 1));
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        byte[] incomingMessage = message.getMessage();
        try {
            handleIncomingMessage(incomingMessage[1]);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleIncomingMessage(byte incomingMessage) throws InvalidMidiDataException {
        if (incomingMessage >= NOTE_OFFSET + bankLength && incomingMessage < NOTE_OFFSET + 16) {
            setActiveMemory(incomingMessage - NOTE_OFFSET - bankLength);
            System.out.println(getActiveMemory());
        } else {
            int incomingBinaryMessage = (int) pow(2, incomingMessage - NOTE_OFFSET);
            state.setSequencerState(getActiveMemory(), (char) (state.getSequencerState(getActiveMemory()) ^ incomingBinaryMessage));
            buildMessage(state.getSequencerState(getActiveMemory()), state.getBeatState(getActiveMemory()));
        }
    }

    public void close() {
    }
}
