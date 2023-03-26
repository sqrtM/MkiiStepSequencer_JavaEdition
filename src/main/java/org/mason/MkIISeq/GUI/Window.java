package org.mason.MkIISeq.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Window {

    private int[] info;
    private final JFrame frame = new JFrame("Java Step Sequencer");


    public void setInfo(int[] newInfo) {
        info = newInfo;
        frame.repaint();
    }

    public void createAndShowGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Panel(1));
        frame.pack();
        frame.setVisible(true);
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
            graphics.drawString(Arrays.toString(info),10,index * 20);
        }
    }

}
