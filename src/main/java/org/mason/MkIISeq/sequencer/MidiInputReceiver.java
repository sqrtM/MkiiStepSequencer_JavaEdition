package org.mason.MkIISeq.sequencer;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import java.util.Arrays;

public class MidiInputReceiver extends Sequencer implements Receiver {

    public void send(MidiMessage msg, long timeStamp) {
        byte[] incomingMessage = msg.getMessage();
        final int PAD_PRESSED = incomingMessage[1];

        if (PAD_PRESSED >= 36 && PAD_PRESSED <= 43) {
            boolean [][] newTotalMemory = getTotalMemory();
            newTotalMemory[0][PAD_PRESSED - NOTE_OFFSET] = !newTotalMemory[0][PAD_PRESSED - NOTE_OFFSET];
            boolean isTargetPadActive = newTotalMemory[0][PAD_PRESSED - NOTE_OFFSET];
            setTotalMemory(newTotalMemory);
            System.out.println("NEW" + Arrays.toString(newTotalMemory[getActiveMemory()]));
            System.out.println("OLD" + Arrays.toString(getTotalMemory()[getActiveMemory()]));
            try {
                byte[] newMessage = buildMessage(isTargetPadActive, PAD_PRESSED - NOTE_OFFSET);
                sendMessage(newMessage);
            } catch (InvalidMidiDataException e) {
                throw new RuntimeException(e);
            }
        } else if (PAD_PRESSED >= 44 && PAD_PRESSED <= 51) {
            setActiveMemory(PAD_PRESSED - 44);
            byte[] MemBankChangeMessage = buildMemBankChangeMessage(PAD_PRESSED);
            try {
                sendMessage(MemBankChangeMessage);
            } catch (InvalidMidiDataException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void close() {
    }
}