package org.mason.MkIISeq.sequencer;

import org.mason.MkIISeq.GUI.Window;
import org.mason.MkIISeq.sequencer.state.StateMachine;

import javax.sound.midi.*;

import static java.lang.Math.log;
import static java.lang.Math.pow;

public class SequencerBank implements Receiver {

    private final Receiver selectedReceiver;
    // bankLength is temporarily final.
    // it will be made mutable in the future.
    private final int bankLength = 8;
    private int activeMemory = 0;

    protected final int NOTE_OFFSET = 36;
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

    protected int getActiveMemory() {
        return activeMemory;
    }
    protected void setActiveMemory(int newActiveMemory) {
        this.activeMemory = newActiveMemory;
    }

    public SequencerBank(Receiver receiver) {
        selectedReceiver = receiver;
    }

    StateMachine state = new StateMachine();
    public Window GUI = new Window();

    public void initThreads() {
        for(int i = 0; i < 8; i++) {
            int index = i;
            state.setSequencerThread(i, new Thread(() -> {
                 while(selectedReceiver != null) {
                     try {
                         mainLoop(index);
                         Thread.sleep(400);
                     } catch (InterruptedException | InvalidMidiDataException e) {
                         throw new RuntimeException(e);
                     }
                 }
            }));
            state.getSequencerThread(i).start();
        }
    }

    public void mainLoop(int index) throws InvalidMidiDataException {
        if (getActiveMemory() == index) {
            buildMessage(state.getSequencerState(index), state.getBeatState(index));
        }
        char beat = state.getBeatState(index);
        // read: "if beat is less than (one) minus (one left shifted bankLength-1 times)...
        // beat increments left by one. otherwise, set it to the initial state of "1"".
        state.setBeatState(index, (char) (
                beat < 1 << bankLength - 1 ? (beat << 1) : 1
        ));
        // java's big endian-ness makes this "increment left" idea a little confusing,
        // but just think of it like a unary ++ operation and it feels less weird.
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        byte[] incomingMessage = message.getMessage();
        try {
            handleIncomingMessage(incomingMessage[1]);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleIncomingMessage(byte incomingMessage) throws InvalidMidiDataException {
        if (incomingMessage >= NOTE_OFFSET + bankLength && incomingMessage < NOTE_OFFSET + 16) {
            setActiveMemory(incomingMessage - NOTE_OFFSET - bankLength);
        } else {
            char incomingBinaryMessage = (char) pow(2, incomingMessage - NOTE_OFFSET);
            char currentActiveState = state.getSequencerState(getActiveMemory());
            // while a little opaque, this bitwise XOR finds the button you just pressed and flips
            // the status for you in the state without having to do any array manip, which is honestly
            // quite convenient, if not needlessly obtuse.
            state.setSequencerState(getActiveMemory(), (char) (currentActiveState ^ incomingBinaryMessage));
            buildMessage(currentActiveState, state.getBeatState(getActiveMemory()));
            GUI.setInfo(currentActiveState);
        }
    }

    protected byte[] buildMemBankChangeMessage(int newBank) {
        byte[] outgoingMessage = mkiiDefaultSysexMessage.clone();
        outgoingMessage[PAD_ADDRESS] = (byte) (HEX_OFFSET + newBank);
        outgoingMessage[PAD_COLOR] = bankColor;
        return outgoingMessage;
    }

    /*
    what this probably SHOULD do if we're going to obey the law of "every function should do one thing"
    is return a byte[][] of length bankLength which are all valid outgoing
    messages. we can then send that to the sendMessage function, and it will iterate through the
    array and send each one individually. but that seems like O*2 work....
    */
    protected void buildMessage(int sequencerMemory, int beatLocation) throws InvalidMidiDataException {
        byte[] outgoingMessage = mkiiDefaultSysexMessage.clone();
        byte beat = (byte) (log(beatLocation) / log(2));

        for (int i = 0; i < bankLength; i++) {
            // bitwise magic here to find the beat "index" within the binary number
            // and return whether or not it's flipped. definitely has the "cool" factor,
            // but is a little too "cute" for a serious production.
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
        finalMessage.setMessage(message, mkiiDefaultSysexMessage.length);
        selectedReceiver.send(finalMessage, -1);
    }

    public void close() {
    }
}
