package org.mason.MkIISeq.sequencer;

import javax.sound.midi.*;

import static java.lang.Math.pow;

public class SequencerBank extends Sequencer implements Receiver {

    private final int BANK_ID;
    private final char INITIAL_BEAT_STATE     = 0b0000_0000_0000_0001;
    private final int INITIAL_SEQUENCER_STATE = 0b0000_0000_0000_0000;

    private int beatLocation = INITIAL_BEAT_STATE;
    private int sequencerMemory = INITIAL_SEQUENCER_STATE;

    public void setSequencerMemory(int newSequencerMemory) {
        this.sequencerMemory = newSequencerMemory;
    }

    public SequencerBank(int bankID, Receiver receiver) {
        this.BANK_ID = bankID;
        selectedReceiver = receiver;
    }

    public void mainLoop() throws InvalidMidiDataException {
        while ((selectedReceiver != null) && (getActiveMemory() == BANK_ID)) {
            try {
                Thread.sleep(1000);
                buildMessage(sequencerMemory, beatLocation);
            } catch (InterruptedException e) {
                System.out.println("InterruptedException Exception" + e.getMessage());
            }
            beatLocation = (beatLocation < (INITIAL_BEAT_STATE << (bankLength - 1))) ? (beatLocation << 1) : 1;
        }
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
        int incomingBinaryMessage = (int) pow(2, incomingMessage - NOTE_OFFSET);
        setSequencerMemory(sequencerMemory ^ incomingBinaryMessage);
        buildMessage(sequencerMemory, beatLocation);
    }

    public void close() {
    }
}
