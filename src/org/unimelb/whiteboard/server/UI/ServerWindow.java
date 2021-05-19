package org.unimelb.whiteboard.server.UI;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class ServerWindow {

    private JFrame frame;
    private JTextArea logArea;
    private JLabel addressLabel;
    private JLabel portLabel;

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
        frame.setMinimumSize(new Dimension(450, 300));
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.out.println("Server Close!");
            }
        });

        JScrollPane scrollPane = new JScrollPane();

        logArea = new JTextArea();
        logArea.setLineWrap(true);
        logArea.setEditable(false);
        scrollPane.setViewportView(logArea);

        addressLabel = new JLabel("Address: " + address);

        portLabel = new JLabel("Port: " + port);
        GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(20)
                                .addComponent(addressLabel, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE))
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(20)
                                .addComponent(portLabel, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE))
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                                .addContainerGap())
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(20)
                                .addComponent(addressLabel, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
                                .addComponent(portLabel, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
                                .addGap(45)
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                                .addContainerGap())
        );
        frame.getContentPane().setLayout(groupLayout);
    }
}