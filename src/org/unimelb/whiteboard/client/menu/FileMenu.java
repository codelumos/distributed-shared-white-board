package org.unimelb.whiteboard.client.menu;

import org.unimelb.whiteboard.client.Client;
import org.unimelb.whiteboard.client.user.UserManager;
import org.unimelb.whiteboard.client.whiteboard.PaintManager;
import org.unimelb.whiteboard.client.whiteboard.WhiteBoardWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;

/**
 * File menu in the menu bar.
 */
public class FileMenu extends JMenu {

    public FileMenu(Client client, WhiteBoardWindow window, PaintManager paintManager, UserManager userManager) {
        super("File");
        this.setMnemonic(KeyEvent.VK_F);

        JMenuItem newMenuItem = new JMenuItem("New", KeyEvent.VK_N);
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        newMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paintManager.clearAll();
                client.setCurrentSavePath(null);
            }
        });
        this.add(newMenuItem);

        JMenuItem openMenuItem = new JMenuItem("Open", KeyEvent.VK_O);
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        openMenuItem.addActionListener(new FileOpenListener(window, client));
        this.add(openMenuItem);

        JMenuItem saveMenuItem = new JMenuItem("Save", KeyEvent.VK_S);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        saveMenuItem.addActionListener(new FileSaveListener(window, client, "save"));
        this.add(saveMenuItem);

        JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
        saveAsMenuItem.addActionListener(new FileSaveListener(window, client, "saveAs"));
        this.add(saveAsMenuItem);

        this.addSeparator();
        JMenuItem exitMenuItem = new JMenuItem("Exit to Lobby", KeyEvent.VK_E);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (paintManager.getMode() == PaintManager.CLIENT_MODE) {
                    try {
                        client.getTempRemoteDoor().leave(client.getUserId());
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    client.removeRoom();
                    userManager.clear();
                }
                client.openLobby();
            }
        });
        this.add(exitMenuItem);

        JMenuItem closeMenuItem = new JMenuItem("Close");
        closeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        this.add(closeMenuItem);

        if (paintManager.getMode() == PaintManager.CLIENT_MODE) {
            newMenuItem.setEnabled(false);
            openMenuItem.setEnabled(false);
            saveMenuItem.setEnabled(false);
            saveAsMenuItem.setEnabled(false);
            closeMenuItem.setEnabled(false);
        }

    }
}
