package org.mason.MkIISeq;

import org.mason.MkIISeq.sequencer.VirtualMIDIReceiver;
import org.mason.MkIISeq.sequencer.SequencerBank;

import javax.sound.midi.*;

public class Main {

    public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException {
        DeviceInitializer deviceInitializer = new DeviceInitializer();

        // get list of all midi ports
        MidiDevice.Info[] allMidiDeviceInfo = deviceInitializer.findMidiDevices();

        // select midiIn, attempt to open the port, then open the receiver
        MidiDevice selectedMidiIn = deviceInitializer.selectMidiIn(allMidiDeviceInfo);
        deviceInitializer.openMidiDevice(selectedMidiIn);
        Receiver selectedReceiver = deviceInitializer.openDeviceReceiver(selectedMidiIn);

        // select midiOut, attempt to open the port, then open the transmitter
        MidiDevice selectedMidiOut = deviceInitializer.selectMidiOut(allMidiDeviceInfo);
        deviceInitializer.openMidiDevice(selectedMidiOut);
        Transmitter selectedTransmitter = deviceInitializer.openDeviceTransmitter(selectedMidiOut);

        System.out.println("Ports configured. Initializing sequencer...");

        VirtualMIDIReceiver MkiiReceiver = new VirtualMIDIReceiver();
        selectedTransmitter.setReceiver(MkiiReceiver);

        SequencerBank newSeq = new SequencerBank(0, selectedReceiver, MkiiReceiver);
        newSeq.mainLoop();

        //for (int i = 0; i < 8; i++){
        //    Thread sequencerBankThread = new Thread(new MultiThreadSequencer(i, selectedReceiver));
        //    sequencerBankThread.start();
        //}
    }
}

