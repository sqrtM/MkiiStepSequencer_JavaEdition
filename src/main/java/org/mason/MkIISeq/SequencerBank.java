package org.mason.MkIISeq;

import javax.sound.midi.*;
import java.util.Arrays;

public class SequencerBank {

    // 8 is a placeholder; this will be mutable
    private static int bankLength = 8;
    private final int BANK_ID = 0;
    private final int MEMORY_BANKS = 16 - bankLength;
    private int[][] totalMemory = new int[bankLength][MEMORY_BANKS];

    private final Receiver selectedDevice;

    private final int PAD_ADDRESS = 9;
    private final int PAD_COLOR   = 10;

    private final int INACTIVE_OFF = -1;
    private final int INACTIVE_ON  = 0;
    private final int ACTIVE_OFF   = 1;
    private final int ACTIVE_ON    = 2;

    private final static byte COLOR_NONE   = 0x00;
    private final static byte COLOR_RED    = 0x01;
    private final static byte COLOR_GREEN  = 0x04;
    private final static byte COLOR_YELLOW = 0x05;
    private final static byte COLOR_BLUE   = 0x10;
    private final static byte COLOR_CYAN   = 0x14;
    private final static byte COLOR_PURPLE = 0x11;
    private final static byte COLOR_WHITE  = 0x7F;

    private byte inactiveOffColor;
    private byte inactiveOnColor;
    private byte activeOnColor;
    private byte activeOffColor;
    private byte bankColor;


    public SequencerBank(Receiver selectedDevice, byte inactiveOffColor, byte inactiveOnColor, byte activeOnColor, byte activeOffColor, byte bankColor) {
        this.selectedDevice = selectedDevice;


        this.inactiveOffColor = inactiveOffColor;
        this.inactiveOnColor = inactiveOnColor;
        this.activeOnColor = activeOnColor;
        this.activeOffColor = activeOffColor;
        this.bankColor = bankColor;
    }

    public SequencerBank(Receiver selectedDevice) {
        this(selectedDevice, COLOR_CYAN, COLOR_PURPLE, COLOR_WHITE, COLOR_GREEN, COLOR_BLUE);
    }

    public SequencerBank() {
        throw new IllegalStateException("no midi device selected.");
    }

    // sleep is just for debugging. not permanent.
    public void mainLoop() throws InvalidMidiDataException {
        int beat = 0;
        while (selectedDevice != null) {
            try {
                Thread.sleep(1000);
                buildMessage(beat);
            } catch (InterruptedException e) {
                System.out.println("InterruptedException Exception" + e.getMessage());
            }
            beat++;
            if (beat > 8) { beat = 0; }
        }
    }

    private final byte[] mkiiDefaultSysexMessage = {
            (byte) 0xF0, 0x00, 0x20, 0x6B, 0x7F, 0x42, 0x02, 0x00, 0x10, 0x70, 0x14, (byte) 0xF7
    };

    private void buildMessage(int beat) throws InvalidMidiDataException {
        byte locationInBank = (byte) (112 + beat);
        byte[] outgoingMessage = mkiiDefaultSysexMessage.clone();

        for (int pad = 0; pad < bankLength; pad++) {
            switch (totalMemory[BANK_ID][pad]) {
                case INACTIVE_OFF -> {
                    System.out.println("inactive off");
                    outgoingMessage[PAD_COLOR] = inactiveOffColor; outgoingMessage[PAD_ADDRESS] = locationInBank;
                    sendMessage(outgoingMessage);
                }
                case INACTIVE_ON -> {
                    System.out.println("inactive on");
                    outgoingMessage[PAD_COLOR] = inactiveOnColor; outgoingMessage[PAD_ADDRESS] = locationInBank;
                    sendMessage(outgoingMessage);
                }
                case ACTIVE_OFF -> {
                    System.out.println("active off");
                    outgoingMessage[PAD_COLOR] = activeOffColor; outgoingMessage[PAD_ADDRESS] = locationInBank;
                    sendMessage(outgoingMessage);
                }
                case ACTIVE_ON -> {
                    System.out.println("active on");
                    outgoingMessage[PAD_COLOR] = activeOnColor; outgoingMessage[PAD_ADDRESS] = locationInBank;
                    sendMessage(outgoingMessage);
                }
                default -> throw new IllegalStateException("Unexpected value: " + pad);
            }
        }
        System.out.println(beat);
    }

    private void sendMessage(byte[] message) throws InvalidMidiDataException {
        SysexMessage finalMessage = new SysexMessage();
        finalMessage.setMessage(message, 12);
        selectedDevice.send(finalMessage, -1);
        System.out.println(Arrays.toString(message));
    }
}
