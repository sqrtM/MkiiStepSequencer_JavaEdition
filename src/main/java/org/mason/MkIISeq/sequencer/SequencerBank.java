package org.mason.MkIISeq.sequencer;

import javax.sound.midi.*;

import static java.lang.Math.*;

public class SequencerBank extends Sequencer {

    private final int BANK_ID;
    private final char INITIAL_STATE = 0b1000_0000_0000_0000;

    private char sequencerMemory = INITIAL_STATE;
    private int beatLocation = INITIAL_STATE;

    private final Receiver virtualReceiver;

    protected char getSequencerMemory() {
        return sequencerMemory;
    }

    protected void setSequencerMemory(char newMemory) {
        this.sequencerMemory = newMemory;
    }

    public SequencerBank(int bankID, Receiver receiver, Receiver virtualReceiver) {
        this.BANK_ID = bankID;
        selectedReceiver = receiver;
        this.virtualReceiver = virtualReceiver;
    }

    public void mainLoop() throws InvalidMidiDataException {
        while ((selectedReceiver != null) && (getActiveMemory() == BANK_ID)) {
            try {
                Thread.sleep(1000);
                for (int pad = 0; pad < bankLength; pad++) {
                    byte[] newMessage = buildMessage(sequencerMemory, beatLocation);
                    sendMessage(newMessage);
                }
            } catch (InterruptedException e) {
                System.out.println("InterruptedException Exception" + e.getMessage());
            }
            beatLocation = (beatLocation > (INITIAL_STATE >> (bankLength - 1))) ? (beatLocation >> 1) : INITIAL_STATE;
        }
    }
}
