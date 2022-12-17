package org.mason.MkIISeq;

import javax.sound.midi.*;
import java.util.Arrays;

public class SequencerBank {

    // 8 is a placeholder; this will be mutable
    private final int bankLength = 8;
    private final int BANK_ID = 0;
    private final int MEMORY_BANKS = 16 - bankLength;
    private final int[] totalMemory = new int[bankLength];

    private final byte HEX_OFFSET = 112;
    private final int NOTE_OFFSET = 36;

    private static Receiver selectedReceiver;

    private final int PAD_ADDRESS =  9;
    private final int PAD_COLOR   = 10;

    private final int INACTIVE_OFF = -1;
    private final int INACTIVE_ON  =  0;
    private final int ACTIVE_OFF   =  1;
    private final int ACTIVE_ON    =  2;

    private final byte COLOR_NONE   = 0x00;
    private final byte COLOR_RED    = 0x01;
    private final byte COLOR_GREEN  = 0x04;
    private final byte COLOR_YELLOW = 0x05;
    private final byte COLOR_BLUE   = 0x10;
    private final byte COLOR_CYAN   = 0x14;
    private final byte COLOR_PURPLE = 0x11;
    private final byte COLOR_WHITE  = 0x7F;

    private byte inactiveOffColor = COLOR_CYAN;
    private byte inactiveOnColor = COLOR_PURPLE;
    private byte activeOnColor = COLOR_WHITE;
    private byte activeOffColor = COLOR_GREEN;
    private byte bankColor = COLOR_BLUE;

    private final byte[] mkiiDefaultSysexMessage = {
            (byte) 0xF0, 0x00, 0x20, 0x6B, 0x7F, 0x42, 0x02, 0x00, 0x10, 0x70, 0x14, (byte) 0xF7
    };

    public SequencerBank(byte inactiveOffColor, byte inactiveOnColor, byte activeOnColor, byte activeOffColor, byte bankColor) {
        this.inactiveOffColor = inactiveOffColor;
        this.inactiveOnColor = inactiveOnColor;
        this.activeOnColor = activeOnColor;
        this.activeOffColor = activeOffColor;
        this.bankColor = bankColor;
    }

    public SequencerBank() {}



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

            if (aMsg[1] >= 36 && aMsg[1] <= 44) {
                try {
                    parseIncomingMessage(aMsg[1]);
                } catch (InvalidMidiDataException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        public void close() {}
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

    public void setSelectedReceiver(Receiver receiver) {
        selectedReceiver = receiver;
    }

    // this is sending like 6 times in a row for some reason.
    protected void parseIncomingMessage(int padNumber) throws InvalidMidiDataException {
        byte targetPad = (byte) (HEX_OFFSET + (padNumber - NOTE_OFFSET));
        System.out.println(Arrays.toString(totalMemory));
        byte[] outgoingMessage = mkiiDefaultSysexMessage.clone();
        outgoingMessage[PAD_COLOR] = activeOnColor; outgoingMessage[PAD_ADDRESS] = targetPad;
        sendMessage(outgoingMessage);
        totalMemory[padNumber - NOTE_OFFSET] = totalMemory[padNumber - NOTE_OFFSET] == 0 ? 1 : 0;
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

    private void sendMessage(byte[] message) throws InvalidMidiDataException {
        SysexMessage finalMessage = new SysexMessage();
        finalMessage.setMessage(message, 12);
        selectedReceiver.send(finalMessage, -1);
    }
}