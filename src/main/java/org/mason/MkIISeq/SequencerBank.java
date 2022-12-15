package org.mason.MkIISeq;

import org.jetbrains.annotations.NotNull;

import javax.sound.midi.*;

public class SequencerBank {


    // 8 is a placeholder; this will be mutable
    private static int bankLength = 8;
    private final static int BANK_ID = 0;
    private final static int MEMORY_BANKS = 16 - bankLength;
    private static int[][] totalMemory = new int[bankLength][MEMORY_BANKS];



    private final static byte COLOR_NONE   = 0x00;
    private final static byte COLOR_RED    = 0x01;
    private final static byte COLOR_GREEN  = 0x04;
    private final static byte COLOR_YELLOW = 0x05;
    private final static byte COLOR_BLUE   = 0x10;
    private final static byte COLOR_CYAN   = 0x14;
    private final static byte COLOR_PURPLE = 0x11;
    private final static byte COLOR_WHITE  = 0x7F;

    private final static int PAD_ADDRESS = 8;
    private final static int PAD_COLOR   = 9;
    private final static int INACTIVE    = 0;
    private final static int ACTIVE_OFF  = 1;
    private final static int ACTIVE_ON   = 2;

    private byte offcolor;
    private byte oncolor;
    private byte seqon;
    private byte seqoff;
    private byte bankcolor;


    public SequencerBank(byte offcolor, byte oncolor, byte seqon, byte seqoff, byte bankcolor) {
        this.offcolor = offcolor;
        this.oncolor = oncolor;
        this.seqon = seqon;
        this.seqoff = seqoff;
        this.bankcolor = bankcolor;
    }

    public SequencerBank() {
        this(COLOR_CYAN, COLOR_PURPLE, COLOR_WHITE, COLOR_GREEN, COLOR_BLUE);
    }

    private void getMessageType() throws InvalidMidiDataException {
        int beat = 0;
        for (int pad = 0; pad < bankLength; pad++) {
            switch (totalMemory[BANK_ID][pad]) {
                case INACTIVE -> {
                    System.out.println("inactive");
                    byte[] msg = { (byte) 0xF0, 0x00, 0x20, 0x6B, 0x7F, 0x42, 0x02, 0x00, 0x10, (byte) (112 + beat), offcolor, (byte) 0xF7 };
                }
                case ACTIVE_OFF -> {
                    System.out.println("active off");
                    Message.ActiveOffMessage msg = new Message.ActiveOffMessage();
                }
                case ACTIVE_ON -> {
                    System.out.println("active on");
                    Message.ActiveOnMessage msg = new Message.ActiveOnMessage();
                }
                default -> throw new IllegalStateException("Unexpected value: " + pad);
            }
        }
    }

}
