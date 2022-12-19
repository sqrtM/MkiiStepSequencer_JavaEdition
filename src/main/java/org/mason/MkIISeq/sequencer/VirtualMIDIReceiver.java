package org.mason.MkIISeq.sequencer;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

public class VirtualMIDIReceiver implements Receiver {
    protected int lastMessage = 0;
    protected void setLastMessage(int newMessage) {
        lastMessage = newMessage;
    }
    public void send(MidiMessage msg, long timeStamp) {
        byte[] incomingMessage = msg.getMessage();
        setLastMessage(incomingMessage[1]);
    }
    public void close() {
    }
}