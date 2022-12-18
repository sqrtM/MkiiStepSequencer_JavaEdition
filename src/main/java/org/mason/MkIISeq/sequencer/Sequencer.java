package org.mason.MkIISeq.sequencer;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;

public abstract class Sequencer {
    // 8 is a placeholder; this will be mutable
    protected final int bankLength = 8;
    protected final int MEMORY_BANKS = 16 - bankLength;

    private boolean[][] totalMemory = new boolean[MEMORY_BANKS][bankLength];
    private int activeMemory = 0;
    private int[] beatContainer = new int[MEMORY_BANKS];

    private final byte HEX_OFFSET = 112;
    protected final int NOTE_OFFSET = 36;

    private final int PAD_ADDRESS = 9;
    private final int PAD_COLOR = 10;

    private final byte COLOR_NONE = 0x00;
    private final byte COLOR_RED = 0x01;
    private final byte COLOR_GREEN = 0x04;
    private final byte COLOR_YELLOW = 0x05;
    private final byte COLOR_BLUE = 0x10;
    private final byte COLOR_CYAN = 0x14;
    private final byte COLOR_PURPLE = 0x11;
    private final byte COLOR_WHITE = 0x7F;

    private final byte inactiveOffColor = COLOR_CYAN;
    private final byte inactiveOnColor = COLOR_PURPLE;
    private final byte activeOnColor = COLOR_WHITE;
    private final byte activeOffColor = COLOR_GREEN;
    private final byte bankColor = COLOR_BLUE;

    private final byte[] mkiiDefaultSysexMessage = {
            (byte) 0xF0, 0x00, 0x20, 0x6B, 0x7F, 0x42, 0x02, 0x00, 0x10, 0x70, 0x14, (byte) 0xF7
    };

    protected static Receiver selectedReceiver;

    protected boolean[][] getTotalMemory() {
        return totalMemory;
    }

    protected void setTotalMemory(boolean[][] newTotalMemory) {
        this.totalMemory = newTotalMemory;
    }

    protected int getActiveMemory() {
        return activeMemory;
    }

    protected void setActiveMemory(int newActiveMemory) {
        this.activeMemory = newActiveMemory;
    }

    protected int[] getBeatContainer() {
        return beatContainer;
    }

    protected void setBeatContainer(int[] newBeatContainer) {
        this.beatContainer = newBeatContainer;
    }

    protected byte[] buildMemBankChangeMessage(int newBank) {
        byte[] outgoingMessage = mkiiDefaultSysexMessage.clone();
        outgoingMessage[PAD_ADDRESS] = (byte) (HEX_OFFSET + newBank);
        outgoingMessage[PAD_COLOR] = bankColor;
        return outgoingMessage;
    }

    protected byte[] buildMessage(boolean status, int pad) {
        byte[] outgoingMessage = mkiiDefaultSysexMessage.clone();
        outgoingMessage[PAD_ADDRESS] = (byte) (HEX_OFFSET + pad);
        if (beatContainer[activeMemory] == pad) {
            outgoingMessage[PAD_COLOR] = status ? activeOnColor  : inactiveOnColor;

        } else {
            outgoingMessage[PAD_COLOR] = status ? activeOffColor : inactiveOffColor;
        }
        return outgoingMessage;
    }

    protected void sendMessage(byte[] message) throws InvalidMidiDataException {
        SysexMessage finalMessage = new SysexMessage();
        finalMessage.setMessage(message, 12);
        selectedReceiver.send(finalMessage, -1);
    }
}
