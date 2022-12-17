package org.mason.MkIISeq.sequencer;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;
import java.util.Arrays;

public abstract class Sequencer {
    // 8 is a placeholder; this will be mutable
    protected final int bankLength = 8;
    protected final int MEMORY_BANKS = 16 - bankLength;
    protected final boolean[][] totalMemory = new boolean[bankLength][MEMORY_BANKS];

    protected int[] beatContainer = new int[MEMORY_BANKS];
    protected int activeMemory = 0;

    protected final byte HEX_OFFSET = 112;
    protected final int NOTE_OFFSET = 36;

    protected final boolean ACTIVE = true;
    protected final boolean INACTIVE = false;

    protected final int PAD_ADDRESS = 9;
    protected final int PAD_COLOR = 10;

    protected final byte COLOR_NONE = 0x00;
    protected final byte COLOR_RED = 0x01;
    protected final byte COLOR_GREEN = 0x04;
    protected final byte COLOR_YELLOW = 0x05;
    protected final byte COLOR_BLUE = 0x10;
    protected final byte COLOR_CYAN = 0x14;
    protected final byte COLOR_PURPLE = 0x11;
    protected final byte COLOR_WHITE = 0x7F;

    protected byte inactiveOffColor = COLOR_CYAN;
    protected byte inactiveOnColor = COLOR_PURPLE;
    protected byte activeOnColor = COLOR_WHITE;
    protected byte activeOffColor = COLOR_GREEN;
    protected byte bankColor = COLOR_BLUE;

    protected final byte[] mkiiDefaultSysexMessage = {
            (byte) 0xF0, 0x00, 0x20, 0x6B, 0x7F, 0x42, 0x02, 0x00, 0x10, 0x70, 0x14, (byte) 0xF7
    };

    protected static Receiver selectedReceiver;

    public class MidiInputReceiver implements Receiver {

        public void send(MidiMessage msg, long timeStamp) {
            byte[] incomingMessage = msg.getMessage();
            final int PAD_PRESSED = incomingMessage[1];

            if (PAD_PRESSED >= 36 && PAD_PRESSED <= 43) {
                boolean isTargetPadActive = totalMemory[activeMemory][PAD_PRESSED - NOTE_OFFSET];
                isTargetPadActive = !isTargetPadActive;
                try {
                    byte[] newMessage = buildMessage(isTargetPadActive, PAD_PRESSED);
                    sendMessage(newMessage);
                } catch (InvalidMidiDataException e) {
                    throw new RuntimeException(e);
                }
            } else if (PAD_PRESSED >= 44 && PAD_PRESSED <= 51) {
                activeMemory = PAD_PRESSED - 44;
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

    private byte[] buildMemBankChangeMessage(int newBank) {
        byte[] outgoingMessage = mkiiDefaultSysexMessage.clone();
        outgoingMessage[PAD_ADDRESS] = (byte) (HEX_OFFSET + newBank);
        outgoingMessage[PAD_COLOR] = bankColor;
        return outgoingMessage;
    }

    protected byte[] buildMessage(boolean status, int pad) {
        byte[] outgoingMessage = mkiiDefaultSysexMessage.clone();
        outgoingMessage[PAD_ADDRESS] = (byte) (HEX_OFFSET + pad);
        if (beatContainer[activeMemory] == pad) {
            outgoingMessage[PAD_COLOR] = status == ACTIVE   ? activeOnColor  : inactiveOnColor;
        } else {
            outgoingMessage[PAD_COLOR] = status == INACTIVE ? inactiveOffColor : activeOffColor;
        }
        return outgoingMessage;
    }

    protected void sendMessage(byte[] message) throws InvalidMidiDataException {
        SysexMessage finalMessage = new SysexMessage();
        finalMessage.setMessage(message, 12);
        selectedReceiver.send(finalMessage, -1);
    }
}
