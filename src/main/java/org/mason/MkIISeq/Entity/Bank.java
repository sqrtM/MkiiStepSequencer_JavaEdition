package org.mason.MkIISeq.Entity;

import org.mason.MkIISeq.Service.Message;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import java.util.Arrays;

public class Bank {
    private boolean isActive;
    private int beat = 0;
    private final int index;
    private final Thread thread;
    private final Receiver receiver;

    private final int[] INITIAL_SEQUENCER_STATE = {0, 0, 0, 0, 0, 0, 0, 0};
    int[] sequencerState = INITIAL_SEQUENCER_STATE;

    public void setActive(boolean active) {
        isActive = active;
    }

    public Bank(int index, Receiver receiver) {
        this.index = index;
        this.thread = new Thread(() -> {
            try {
                while (receiver != null) {
                    mainLoop();
                    Thread.sleep(1000);
                }
            } catch (InterruptedException | InvalidMidiDataException e) {
                throw new RuntimeException(e);
            }
        });
        this.receiver = receiver;
        thread.start();
    }

    public void mainLoop() throws InvalidMidiDataException {
        if (isActive) {
            Message message = new Message(this.receiver);
            byte[][] outgoingMessage = message.build(sequencerState, beat);
            for (byte[] msg : outgoingMessage) {
                message.send(msg);
            }
        }
        beat = beat < 7 ? beat + 1 : 0;
    }

    public void handleIncomingMessage(int incomingMessage) {
        sequencerState[incomingMessage] = sequencerState[incomingMessage] == 1 ? 0 : 1;
        System.out.println(Arrays.toString(sequencerState));
    }
}