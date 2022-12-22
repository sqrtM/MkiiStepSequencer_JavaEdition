package org.mason.MkIISeq.sequencer;

import org.mason.MkIISeq.GUI.Window;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;

import static java.lang.Math.*;

public abstract class Sequencer {
    // 8 is a placeholder; this will be mutable
    protected final int bankLength = 8;
    private int activeMemory = 0;

    /*
    what im thinking is that instead of trying to solve the inheretance
    problem is i should go back to the original idea wherein there
    is no "2d array" stored statically. just individual arrays
    each operating completely independently.

    the above fields should all be blank and instantiated in the
    subclass. maybe this should even be an interface rather than an
    abstract class?

    VirtualMidiReceiver (VMR) will need to be rethought. maybe I will
    need to add an argument which takes the bankID? or the full
    array, even. it could even be translated into a binary number
    which would be kind of swag, if not a little too cute.

    So then, the VMR would not even need to "get" anything from anywhere.
    the only thing it inherits beyond getters and setters are NOTE_OFFSET
    and bankLength which are both not worth a full inhereitance.

    theoretically, the receiver could just simply pull in the message and
    return it wholesale, having the sequencer itself deal with it in its
    entirety. it does certainly seem to be doing a little more than just
    "receiving", so I would imagine the answer isn't in abandoning OOP
    but rather in rethinking what all these fucking classes are doing.
     */

    private final byte HEX_OFFSET = 112;
    protected final int NOTE_OFFSET = 36;

    private final int PAD_ADDRESS = 9;
    private final int PAD_COLOR = 10;

    private final byte inactiveOffColor = Color.CYAN.getColor();
    private final byte inactiveOnColor = Color.PURPLE.getColor();
    private final byte activeOnColor = Color.WHITE.getColor();
    private final byte activeOffColor = Color.GREEN.getColor();
    private final byte bankColor = Color.BLUE.getColor();

    private final byte[] mkiiDefaultSysexMessage = {
            (byte) 0xF0, 0x00, 0x20,
            0x6B, 0x7F, 0x42, 0x02,
            0x00, 0x10, 0x70,
            0x14, (byte) 0xF7
    };

    protected int getActiveMemory() {
        return activeMemory;
    }

    protected void setActiveMemory(int newActiveMemory) {
        this.activeMemory = newActiveMemory;
    }

    protected byte[] buildMemBankChangeMessage(int newBank) {
        byte[] outgoingMessage = mkiiDefaultSysexMessage.clone();
        outgoingMessage[PAD_ADDRESS] = (byte) (HEX_OFFSET + newBank);
        outgoingMessage[PAD_COLOR] = bankColor;
        return outgoingMessage;
    }

    protected Receiver selectedReceiver;

    /*
    what this SHOULD do is return a byte array of length bankLength which are all valid outgoing
    messages. we can then send that to the sendMessage function and it will iterate through the
    array and send each one individually. but that seems like O*2 work....
     */
    protected void buildMessage(int sequencerMemory, int beatLocation) throws InvalidMidiDataException {
        byte[] outgoingMessage = mkiiDefaultSysexMessage.clone();
        byte beat = (byte) (log(beatLocation) / log(2));

        for (int i = 0; i < bankLength; i++) {
            int status = (sequencerMemory & (1 << i)) >> i;
            if (status == 1) {
                outgoingMessage[PAD_COLOR] = beat == i ? activeOnColor  : inactiveOnColor;
            } else {
                outgoingMessage[PAD_COLOR] = beat == i ? activeOffColor : inactiveOffColor;
            }
            outgoingMessage[PAD_ADDRESS] = (byte) (i + HEX_OFFSET);
            sendMessage(outgoingMessage);
        }
    }

    protected void sendMessage(byte[] message) throws InvalidMidiDataException {
        SysexMessage finalMessage = new SysexMessage();
        finalMessage.setMessage(message, 12);
        selectedReceiver.send(finalMessage, -1);
        Window.setInfo(message);
    }
}
