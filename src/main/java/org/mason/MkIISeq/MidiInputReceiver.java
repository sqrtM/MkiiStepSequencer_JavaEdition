package org.mason.MkIISeq;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

public class MidiInputReceiver implements Receiver {

    public String name;
    public MidiInputReceiver(String name) {
         this.name = name;
     }
    public MidiInputReceiver() {
        this("default receiver");
    }

    public void send(MidiMessage msg, long timeStamp) {
        byte[] aMsg = msg.getMessage();
        // take the MidiMessage msg and store it in a byte array

        // msg.getLength() returns the length of the message in bytes
        for(int i = 0; i < msg.getLength(); i++){
            System.out.println(aMsg[i]);
            if (aMsg[1] >= 36 && aMsg[1] <= 44) {
                try {
                    SequencerBank.parseIncomingMessage(aMsg[1]);
                } catch (InvalidMidiDataException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public void close() {};
}
