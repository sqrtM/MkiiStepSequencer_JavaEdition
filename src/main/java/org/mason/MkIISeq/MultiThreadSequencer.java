package org.mason.MkIISeq;

import org.mason.MkIISeq.sequencer.SequencerBank;

import javax.sound.midi.Receiver;
import java.util.ArrayList;

public class MultiThreadSequencer implements Runnable {

    public static final ArrayList<SequencerBank> bankList = new ArrayList<>();
    int bankID;
    Receiver receiver;

    MultiThreadSequencer(int bankID, Receiver receiver) {
        this.bankID = bankID;
        this.receiver = receiver;
    }

    public void run()
    {
        SequencerBank newSeq = new SequencerBank(bankID, receiver);
        try {
            bankList.add(newSeq);
            newSeq.mainLoop();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new IllegalThreadStateException();
        }
    }
}
