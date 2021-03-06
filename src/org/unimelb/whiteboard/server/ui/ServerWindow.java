package org.unimelb.whiteboard.server.ui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ServerWindow {

    private JFrame frame;
    private JTextArea logArea;
    private JScrollPane scrollPane;
    private JLabel addressLabel;

    /**
     * Create the application.
     */
    public ServerWindow(String address, String port) {
        initialize(address, port);
    }

    public JFrame getFrame() {
        return frame;
    }

    public JTextArea getLogArea() {
        return logArea;
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize(String address, String port) {
        frame = new JFrame();
        frame.setTitle("Server");
        frame.setBounds(100, 100, 457, 297);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.out.println("Server close.");
            }
        });

        addressLabel = new JLabel("<html><body>Address: " + address + "<br>Port: " + port + "</body></html>");
        addressLabel.setBounds(20, 18, 400, 32);
        frame.getContentPane().add(addressLabel);

        logArea = new JTextArea();
        logArea.setLineWrap(true);
        logArea.setEditable(false);
        scrollPane = new JScrollPane(logArea);
        scrollPane.setBounds(20, 60, 400, 170);
        frame.getContentPane().add(scrollPane);
    }
}
