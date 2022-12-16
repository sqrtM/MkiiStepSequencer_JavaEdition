package org.mason.MkIISeq;

import javax.sound.midi.*;
import java.util.Vector;

import java.util.Scanner;

import static java.lang.Integer.parseInt;
import static javax.sound.midi.MidiSystem.getMidiDevice;

public class Main {
    public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException {

        MidiDevice.Info[] allMidiDeviceInfo = findMidiDevices();
        MidiDevice selectedDevice = selectMidiDevice(allMidiDeviceInfo);
        Receiver selectedReceiver =  openMidiDevice(selectedDevice);

        SequencerBank Mkii = new SequencerBank(selectedReceiver);
        Mkii.mainLoop();
    }

    private static MidiDevice.Info[] findMidiDevices()  {

        Vector<Object> midiDeviceInfo = new Vector<>();
        MidiDevice.Info[] allMidiDeviceInfo = MidiSystem.getMidiDeviceInfo();

        for (int i = 0; i < allMidiDeviceInfo.length; i++) {
            midiDeviceInfo.add(allMidiDeviceInfo[i]);
            System.out.println(i + ".) " + allMidiDeviceInfo[i]);
        }
        return allMidiDeviceInfo;
    }

    private static MidiDevice selectMidiDevice(MidiDevice.Info[] allMidiDeviceInfo) throws MidiUnavailableException {

        Scanner in = new Scanner(System.in);

        System.out.println("Please select a valid MIDI Device from the list above.");
        MidiDevice.Info selectedDeviceInfo = allMidiDeviceInfo[parseInt(in.nextLine())];
        System.out.println(selectedDeviceInfo + " selected. Connecting...");
        return getMidiDevice(selectedDeviceInfo);
    }

    private static Receiver openMidiDevice(MidiDevice selectedDevice) throws MidiUnavailableException {

        if (!(selectedDevice).isOpen()) {
            try {
                selectedDevice.open();
                System.out.println(selectedDevice.getDeviceInfo() + " successfully connected.");
            } catch (MidiUnavailableException e) { throw new MidiUnavailableException(); }
        }
        return selectedDevice.getReceiver();
    }
}