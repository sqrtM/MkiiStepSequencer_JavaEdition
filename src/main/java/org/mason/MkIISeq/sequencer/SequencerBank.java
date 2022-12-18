package org.mason.MkIISeq.sequencer;


import javax.sound.midi.*;
import java.util.Arrays;

public class SequencerBank extends Sequencer {

    private final int BANK_ID;

    public SequencerBank(int bankID, Receiver receiver) {
        this.BANK_ID = bankID;
        selectedReceiver = receiver;
    }

    // sleep is just for debugging. not permanent.
    public void mainLoop() throws InvalidMidiDataException {
        while (selectedReceiver != null && activeMemory == BANK_ID) {
            try {
                Thread.sleep(500);

                for (int pad = 0; pad < bankLength; pad++) {
                        byte[] newMessage = buildMessage(getTotalMemory()[BANK_ID][pad], pad);
                        sendMessage(newMessage);
                }
            } catch (InterruptedException e) {
                System.out.println("InterruptedException Exception" + e.getMessage());
            }
            beatContainer[BANK_ID]++;
            if (beatContainer[BANK_ID] >= bankLength) {
                beatContainer[BANK_ID] = 0;
            }
        }
    }
}