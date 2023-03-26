package org.mason.MkIISeq.Entity;

import org.mason.MkIISeq.GUI.Window;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

public class Sequencer implements Receiver {

    public final Bank[] banks;

    private final Receiver selectedReceiver;
    // bankLength is temporarily final.
    // it will be made mutable in the future.
    private final int bankLength = 8;
    private int activeMemory = 0;

    protected final int NOTE_OFFSET = 36;

    public Sequencer(Receiver receiver) {
        this.banks = new Bank[]{
                new Bank(0, receiver), new Bank(1, receiver), new Bank(2, receiver), new Bank(3, receiver),
                new Bank(4, receiver), new Bank(5, receiver), new Bank(6, receiver), new Bank(7, receiver)
        };
        selectedReceiver = receiver;
        banks[0].setActive(true);
    }

    //public Window GUI = new Window();

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
        banks[activeMemory].handleIncomingMessage(incomingMessage - NOTE_OFFSET);
        //
        // GUI.setInfo(banks[activeMemory].getSequencerState());
    }

    public void close() {
    }
}
