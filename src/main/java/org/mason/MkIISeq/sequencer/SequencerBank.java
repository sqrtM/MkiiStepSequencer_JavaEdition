package org.mason.MkIISeq.sequencer;

import javax.sound.midi.*;

public class SequencerBank extends Sequencer {

    public SequencerBank(int bankID) {
        this.BANK_ID = bankID;
    }

    private int BANK_ID;

    public void setSelectedReceiver(Receiver receiver) {
        selectedReceiver = receiver;
    }

    public SequencerBank(byte inactiveOffColor, byte inactiveOnColor, byte activeOnColor, byte activeOffColor, byte bankColor) {
        this.inactiveOffColor = inactiveOffColor;
        this.inactiveOnColor = inactiveOnColor;
        this.activeOnColor = activeOnColor;
        this.activeOffColor = activeOffColor;
        this.bankColor = bankColor;
    }

    // sleep is just for debugging. not permanent.
    public void mainLoop() throws InvalidMidiDataException {
        while (selectedReceiver != null) {
            try {
                Thread.sleep(500);
                for (int pad = 0; pad < bankLength; pad++) {
                    byte[] newMessage = buildMessage(totalMemory[BANK_ID][pad], pad);
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