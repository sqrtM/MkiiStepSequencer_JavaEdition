package org.mason.MkIISeq.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Window {

    private byte[] info;

    public void setInfo(byte[] newInfo) {
        info = newInfo;
    }

    private byte[] getInfo() {
        return info;
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Java Step Sequencer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        for (int i = 0; i < 8; i++) {
            frame.add(new Panel(i));
            frame.pack();
            frame.setVisible(true);
        }
    }

    class Panel extends JPanel {
        private final int index;
        public Panel(int index) {
            this.index = index;
            setBorder(BorderFactory.createLineBorder(Color.blue));
        }

        public Dimension getPreferredSize() {
            return new Dimension(250,50);
        }

        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            graphics.drawString(Arrays.toString(getInfo()),10,index * 20);
        }
    }

}
