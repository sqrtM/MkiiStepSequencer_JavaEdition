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
        while (selectedReceiver != null && getActiveMemory() == BANK_ID) {
            try {
                System.out.println(Arrays.toString(getTotalMemory()[getActiveMemory()]));
                Thread.sleep(1000);
                boolean[][] currentTotalMemory = getTotalMemory();
                for (int pad = 0; pad < bankLength; pad++) {
                        byte[] newMessage = buildMessage(currentTotalMemory[BANK_ID][pad], pad);
                        sendMessage(newMessage);
                }
            } catch (InterruptedException e) {
                System.out.println("InterruptedException Exception" + e.getMessage());
            }
            int[] newBeatContainer = getBeatContainer();
            newBeatContainer[BANK_ID]++;
            if (newBeatContainer[BANK_ID] >= bankLength) {
                newBeatContainer[BANK_ID] = 0;
            }
            setBeatContainer(newBeatContainer);
        }
    }
}