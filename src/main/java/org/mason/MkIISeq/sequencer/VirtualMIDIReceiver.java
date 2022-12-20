package org.mason.MkIISeq.sequencer;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

public class VirtualMIDIReceiver extends Sequencer implements Receiver {
    protected int lastMessage = 0;

    protected void setLastMessage(int newMessage) {
        this.lastMessage = newMessage;
    }

    protected int getLastMessage() {
        return this.lastMessage;
    }

    public void send(MidiMessage msg, long timeStamp) {
        byte[] incomingMessage = msg.getMessage();
        setLastMessage(incomingMessage[1]);
        System.out.println(lastMessage);
    }
    public void close() {
    }
}