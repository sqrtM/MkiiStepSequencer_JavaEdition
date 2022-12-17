package org.mason.MkIISeq;

import javax.sound.midi.*;
import java.util.Arrays;

public class SequencerBank {

    // 8 is a placeholder; this will be mutable
    private final int bankLength = 8;
    private final int BANK_ID = 0;
    private final int MEMORY_BANKS = 16 - bankLength;
    private final boolean[] totalMemory = new boolean[bankLength];

    private final byte HEX_OFFSET = 112;
    private final int NOTE_OFFSET = 36;

    private final boolean ACTIVE = true;
    private final boolean INACTIVE = false;

    private static Receiver selectedReceiver;

    private final int PAD_ADDRESS = 9;
    private final int PAD_COLOR = 10;

    private final int INACTIVE_OFF = 0;
    private final int INACTIVE_ON = 1;
    private final int ACTIVE_OFF = 2;
    private final int ACTIVE_ON = 3;

    private final byte COLOR_NONE = 0x00;
    private final byte COLOR_RED = 0x01;
    private final byte COLOR_GREEN = 0x04;
    private final byte COLOR_YELLOW = 0x05;
    private final byte COLOR_BLUE = 0x10;
    private final byte COLOR_CYAN = 0x14;
    private final byte COLOR_PURPLE = 0x11;
    private final byte COLOR_WHITE = 0x7F;

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

    public SequencerBank() {
    }

    public void setSelectedReceiver(Receiver receiver) {
        selectedReceiver = receiver;
    }



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
        public void close() {
        }
    }


    // sleep is just for debugging. not permanent.
    public void mainLoop() throws InvalidMidiDataException {
        int beat = 0;
        while (selectedReceiver != null) {
            try {
                Thread.sleep(1000);
                getMessageType(beat);
            } catch (InterruptedException e) {
                System.out.println("InterruptedException Exception" + e.getMessage());
            }
            beat++;
            if (beat >= bankLength) {
                beat = 0;
            }
        }
    }

    protected void parseIncomingMessage(int padNumber) throws InvalidMidiDataException {
        byte targetPad = (byte) (HEX_OFFSET + (padNumber - NOTE_OFFSET));
        byte[] outgoingMessage = mkiiDefaultSysexMessage.clone();

        outgoingMessage[PAD_COLOR] = inactiveOnColor;
        outgoingMessage[PAD_ADDRESS] = targetPad;
        sendMessage(outgoingMessage);
        totalMemory[padNumber - NOTE_OFFSET] = !totalMemory[padNumber - NOTE_OFFSET];
    }

    private void getMessageType(int beat) throws InvalidMidiDataException {
        for (int pad = 0; pad < bankLength; pad++) {
            byte[] currentBeatMessage = buildMessage(totalMemory[pad], pad, beat);
            sendMessage(currentBeatMessage);
        }
    }

    private byte[] buildMessage(boolean status, int pad, int beat) {
        byte[] outgoingMessage = mkiiDefaultSysexMessage.clone();
        outgoingMessage[PAD_ADDRESS] = (byte) (HEX_OFFSET + pad);
        if (beat == pad) {
            outgoingMessage[PAD_COLOR] = status ? activeOnColor : activeOffColor;
        } else {
            outgoingMessage[PAD_COLOR] = status ? inactiveOnColor : inactiveOffColor;
        }
        return outgoingMessage;
    }

    private void sendMessage(byte[] message) throws InvalidMidiDataException {
        SysexMessage finalMessage = new SysexMessage();
        finalMessage.setMessage(message, 12);
        selectedReceiver.send(finalMessage, -1);
    }
}