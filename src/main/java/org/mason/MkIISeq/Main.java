package org.mason.MkIISeq;

import org.mason.MkIISeq.Entity.Sequencer;

import javax.sound.midi.*;

public class Main {

    public static void main(String[] args) throws MidiUnavailableException {
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

        Sequencer newSeq = new Sequencer(selectedReceiver);
        selectedTransmitter.setReceiver(newSeq);

        //newSeq.GUI.createAndShowGUI();
        System.out.println("Sequencer initialized. Starting.");
    }
}

