package org.mason.MkIISeq;

import javax.sound.midi.*;
import java.util.Arrays;

public class SequencerBank {

    // 8 is a placeholder; this will be mutable
    private static final int bankLength = 8;
    private final int BANK_ID = 0;
    private static final int MEMORY_BANKS = 16 - bankLength;
    // make this 2D later. currently 1D for testing purposes.
    private static final int[] totalMemory = new int[bankLength];

    private final static byte HEX_OFFSET = 112;
    private final static int NOTE_OFFSET = 36;

    private static Receiver selectedReceiver;
    private static Transmitter selectedTransmitter;


    private static final int PAD_ADDRESS =  9;
    private static final int PAD_COLOR   = 10;

    private static final int INACTIVE_OFF = -1;
    private static final int INACTIVE_ON  =  0;
    private static final int ACTIVE_OFF   =  1;
    private static final int ACTIVE_ON    =  2;

    private final static byte COLOR_NONE   = 0x00;
    private final static byte COLOR_RED    = 0x01;
    private final static byte COLOR_GREEN  = 0x04;
    private final static byte COLOR_YELLOW = 0x05;
    private final static byte COLOR_BLUE   = 0x10;
    private final static byte COLOR_CYAN   = 0x14;
    private final static byte COLOR_PURPLE = 0x11;
    private final static byte COLOR_WHITE  = 0x7F;

    private static byte inactiveOffColor;
    private static byte inactiveOnColor;
    private static byte activeOnColor;
    private static byte activeOffColor;
    private static byte bankColor;

    private static final byte[] mkiiDefaultSysexMessage = {
            (byte) 0xF0, 0x00, 0x20, 0x6B, 0x7F, 0x42, 0x02, 0x00, 0x10, 0x70, 0x14, (byte) 0xF7
    };

    public SequencerBank(Receiver selectedReceiver, Transmitter selectedTransmitter, byte inactiveOffColor, byte inactiveOnColor, byte activeOnColor, byte activeOffColor, byte bankColor) {
        SequencerBank.selectedReceiver = selectedReceiver;
        SequencerBank.selectedTransmitter = selectedTransmitter;

        SequencerBank.inactiveOffColor = inactiveOffColor;
        SequencerBank.inactiveOnColor = inactiveOnColor;
        SequencerBank.activeOnColor = activeOnColor;
        SequencerBank.activeOffColor = activeOffColor;
        SequencerBank.bankColor = bankColor;
    }

    public SequencerBank(Receiver selectedReceiver, Transmitter selectedTransmitter) {
        this(selectedReceiver, selectedTransmitter, COLOR_CYAN, COLOR_PURPLE, COLOR_WHITE, COLOR_GREEN, COLOR_BLUE);
    }

    public SequencerBank() {
        throw new IllegalStateException("no midi device selected.");
    }


    // sleep is just for debugging. not permanent.
    public void mainLoop() throws InvalidMidiDataException {
        int beat = 0;
        while (selectedReceiver != null) {
            try {
                Thread.sleep(1000);
                buildMessage(beat);
            } catch (InterruptedException e) {
                System.out.println("InterruptedException Exception" + e.getMessage());
            }
            beat++;
            if (beat >= bankLength) { beat = 0; }
        }
    }

    // this is sending like 6 times in a row for some reason.
    protected static void parseIncomingMessage(int padNumber) throws InvalidMidiDataException {
        byte targetPad = (byte) (HEX_OFFSET + (padNumber - NOTE_OFFSET));
        System.out.println(Arrays.toString(totalMemory));
        byte[] outgoingMessage = mkiiDefaultSysexMessage.clone();
        outgoingMessage[PAD_COLOR] = activeOnColor; outgoingMessage[PAD_ADDRESS] = targetPad;
        sendMessage(outgoingMessage);
        totalMemory[padNumber - NOTE_OFFSET] += 1;
    }

    private void buildMessage(int beat) throws InvalidMidiDataException {
        byte locationInBank = (byte) (HEX_OFFSET + beat);
        byte[] outgoingMessage = mkiiDefaultSysexMessage.clone();

        for (int pad = 0; pad < bankLength; pad++) {
            switch (totalMemory[pad]) {
                case INACTIVE_OFF -> {
                    outgoingMessage[PAD_COLOR] = inactiveOffColor; outgoingMessage[PAD_ADDRESS] = locationInBank;
                    sendMessage(outgoingMessage);
                }
                case INACTIVE_ON -> {
                    outgoingMessage[PAD_COLOR] = inactiveOnColor; outgoingMessage[PAD_ADDRESS] = locationInBank;
                    sendMessage(outgoingMessage);
                }
                case ACTIVE_OFF -> {
                    outgoingMessage[PAD_COLOR] = activeOffColor; outgoingMessage[PAD_ADDRESS] = locationInBank;
                    sendMessage(outgoingMessage);
                }
                case ACTIVE_ON -> {
                    outgoingMessage[PAD_COLOR] = activeOnColor; outgoingMessage[PAD_ADDRESS] = locationInBank;
                    sendMessage(outgoingMessage);
                }
                default -> throw new IllegalStateException("Unexpected value: " + pad);
            }
        }
        System.out.println(beat);
    }

    private static void sendMessage(byte[] message) throws InvalidMidiDataException {
        SysexMessage finalMessage = new SysexMessage();
        finalMessage.setMessage(message, 12);
        selectedReceiver.send(finalMessage, -1);
    }
}