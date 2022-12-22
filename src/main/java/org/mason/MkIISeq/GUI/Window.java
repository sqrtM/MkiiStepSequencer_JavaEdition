package org.mason.MkIISeq.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Window {

    private static byte[] info;

    public static void setInfo(byte[] newInfo) {
        info = newInfo;
    }

    private static byte[] getInfo() {
        return info;
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Java Step Sequencer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Panel());
        frame.pack();
        frame.setVisible(true);
    }

    static class Panel extends JPanel {
        public Panel() {
            setBorder(BorderFactory.createLineBorder(Color.blue));
        }

        public Dimension getPreferredSize() {
            return new Dimension(250,200);
        }

        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            graphics.drawString(Arrays.toString(getInfo()),10,20);
        }
    }

}
