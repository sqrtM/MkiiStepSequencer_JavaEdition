package org.mason.MkIISeq;

import org.jetbrains.annotations.NotNull;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;

public class Message {

    private final static byte HEX_OFFSET = 112;
    private final static int NOTE_OFFSET = 36;
    // 8 is a placeholder; this will be mutable
    private static int bankLength = 8;

    private final static int MEMORY_BANKS = 16 - bankLength;
    private static int[][] totalMemory = new int[bankLength][MEMORY_BANKS];

    private byte[] mkiiSysexInput = {
            (byte) 0xF0, 0x00, 0x20, 0x6B, 0x7F, 0x42, 0x02, 0x00, 0x10, 0x70, 0x14, (byte) 0xF7
    };

    private @NotNull SysexMessage buildMessage(int currentBeat) throws InvalidMidiDataException {
        int previousBeat = currentBeat == 0 ? bankLength - 1 : currentBeat - 1;
        //this may or may not work because of the base 10 to base 16 conversion...
        byte currentSequencePosition = (byte) Integer.parseInt(Integer.toHexString((HEX_OFFSET + currentBeat)), 16);
        byte previousSequencePosition = (byte) (currentSequencePosition == HEX_OFFSET
                ? HEX_OFFSET + bankLength - 1 : currentSequencePosition - 1);
        byte[] message = mkiiSysexInput.clone();

        SysexMessage outgoingSysex = new SysexMessage(message, message.length);

        return outgoingSysex;
    }

    static class ActiveOnMessage extends Message {

    }

    static class ActiveOffMessage extends Message {

    }

    static class InactiveMessage extends Message {

    }

}
