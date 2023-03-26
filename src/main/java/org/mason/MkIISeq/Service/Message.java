package org.mason.MkIISeq.Service;

import org.mason.MkIISeq.sequencer.Color;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;
import java.util.Arrays;

public class Message {

    private final byte HEX_OFFSET = 0x70; // 112

    private final int PAD_ADDRESS = 9;
    private final int PAD_COLOR = 10;

    private final byte inactiveOffColor = Color.CYAN.getColor();
    private final byte inactiveOnColor = Color.PURPLE.getColor();
    private final byte activeOnColor = Color.WHITE.getColor();
    private final byte activeOffColor = Color.GREEN.getColor();
    private final byte bankColor = Color.BLUE.getColor();

    private final byte[] mkiiDefaultSysexMessage = {
            (byte) 0xF0,
            0x00, 0x20, 0x6B, 0x7F, 0x42,
            0x02, 0x00, 0x10, 0x70, 0x14,
            (byte) 0xF7
    };

    Receiver selectedReceiver;

    public Message(Receiver receiver) {
        this.selectedReceiver = receiver;
    }


    public byte[][] build(int[] sequencerMemory, int beat) {

        byte[][] outgoingMessage = new byte[8][mkiiDefaultSysexMessage.length]; //8 = BANK LENGTH
        Arrays.fill(outgoingMessage, mkiiDefaultSysexMessage.clone());

        // 8 = BANK LENGTH
        for (int i = 0; i < 8; i++) {
            if (sequencerMemory[i] == 1) {
                outgoingMessage[i][PAD_COLOR] = beat == i ? activeOnColor : inactiveOnColor;
            } else {
                outgoingMessage[i][PAD_COLOR] = beat == i ? activeOffColor : inactiveOffColor;
            }
            outgoingMessage[i][PAD_ADDRESS] = (byte) (i + HEX_OFFSET);
            System.out.println((Arrays.toString(outgoingMessage[i])));
        }
        return outgoingMessage;
    }

    public void send(byte[] outgoingMessage) throws InvalidMidiDataException {
            SysexMessage finalMessage = new SysexMessage();
            finalMessage.setMessage(outgoingMessage, mkiiDefaultSysexMessage.length);
            this.selectedReceiver.send(finalMessage, -1);
    }
}
