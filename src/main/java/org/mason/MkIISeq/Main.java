package org.mason.MkIISeq;

import javax.sound.midi.*;
import java.util.Vector;

import java.util.Scanner;

import static java.lang.Integer.parseInt;
import static javax.sound.midi.MidiSystem.getMidiDevice;

public class Main {
    public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException {

        // get list of all midi ports
        MidiDevice.Info[] allMidiDeviceInfo = findMidiDevices();

        // select midiIn, attempt to open the port, then open the receiver
        MidiDevice selectedMidiIn = selectMidiIn(allMidiDeviceInfo);
        openMidiDevice(selectedMidiIn);
        Receiver selectedReceiver =  openDeviceReceiver(selectedMidiIn);

        // select midiOut, attempt to open the port, then open the transmitter
        MidiDevice selectedMidiOut = selectMidiOut(allMidiDeviceInfo);
        openMidiDevice(selectedMidiOut);
        Transmitter selectedTransmitter = openDeviceTransmitter(selectedMidiOut);

        System.out.println("Ports configured. Initializing sequencer...");

        selectedTransmitter.setReceiver(new MidiInputReceiver());

        SequencerBank Mkii = new SequencerBank(selectedReceiver, selectedTransmitter);
        Mkii.mainLoop();
    }

    private static MidiDevice.Info[] findMidiDevices()  {

        Vector<MidiDevice.Info> midiDeviceInfo = new Vector<>();
        MidiDevice.Info[] allMidiDeviceInfo = MidiSystem.getMidiDeviceInfo();

        for (int i = 0; i < allMidiDeviceInfo.length; i++) {
            midiDeviceInfo.add(allMidiDeviceInfo[i]);
            System.out.println(i + ".) " + allMidiDeviceInfo[i]);
        }
        return allMidiDeviceInfo;
    }

    private static MidiDevice selectMidiIn(MidiDevice.Info[] allMidiDeviceInfo) throws MidiUnavailableException {

        Scanner in = new Scanner(System.in);

        System.out.println("Please select a valid MIDI IN port from the list above.");
        MidiDevice.Info selectedDeviceInfo = allMidiDeviceInfo[parseInt(in.nextLine())];
        System.out.println(selectedDeviceInfo + " selected. Connecting...");
        return getMidiDevice(selectedDeviceInfo);
    }

    private static MidiDevice selectMidiOut(MidiDevice.Info[] allMidiDeviceInfo) throws MidiUnavailableException {

        Scanner in = new Scanner(System.in);

        System.out.println("Please select a valid MIDI Out port from the list above.");
        MidiDevice.Info selectedDeviceInfo = allMidiDeviceInfo[parseInt(in.nextLine())];
        System.out.println(selectedDeviceInfo + " selected. Connecting...");
        return getMidiDevice(selectedDeviceInfo);
    }

    private static void openMidiDevice(MidiDevice selectedDevice) throws MidiUnavailableException {

        if (!(selectedDevice).isOpen()) {
            try {
                System.out.println("Attempting to open " + selectedDevice.getDeviceInfo() + "...");
                selectedDevice.open();
                System.out.println("Success.");
            } catch (MidiUnavailableException e) {
                throw new MidiUnavailableException();
            }
        }
    }

    private static Receiver openDeviceReceiver(MidiDevice selectedDevice) throws MidiUnavailableException {
        Receiver selectedReceiver = selectedDevice.getReceiver();
        return selectedReceiver;
    }

    private static Transmitter openDeviceTransmitter(MidiDevice selectedDevice) throws MidiUnavailableException {
        Transmitter selectedTransmitter = selectedDevice.getTransmitter();
        return selectedTransmitter;
    }
}