package org.unimelb.whiteboard.client.Menus;

import org.unimelb.whiteboard.client.Client;
import org.unimelb.whiteboard.client.ClientUser.UserManager;
import org.unimelb.whiteboard.client.WhiteBoard.PaintManager;
import org.unimelb.whiteboard.client.WhiteBoard.WhiteBoardView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;

/**
 * File menu in the menu bar.
 */
public class FileMenu extends JMenu {

    public FileMenu(Client client, WhiteBoardView wbv, PaintManager paintManager, UserManager userManager) {
        super("File(F)");

        JMenuItem newMenuItem = new JMenuItem("New", KeyEvent.VK_N);
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        newMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paintManager.clearAll();
                client.setCurrentSavePath(null);
            }
        });
        this.add(newMenuItem);

        JMenuItem openMenuItem = new JMenuItem("Open", KeyEvent.VK_O);
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        openMenuItem.addActionListener(new FileOpenListener(wbv, client));
        this.add(openMenuItem);

        JMenuItem saveMenuItem = new JMenuItem("Save", KeyEvent.VK_S);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveMenuItem.addActionListener(new FileSaveListener(wbv, client, "save"));
        this.add(saveMenuItem);

        JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
        saveAsMenuItem.addActionListener(new FileSaveListener(wbv, client, "saveAs"));
        this.add(saveAsMenuItem);

        this.addSeparator();
        JMenuItem exitMenuItem = new JMenuItem("Exit to org.unimelb.whiteboard.client.Lobby", KeyEvent.VK_E);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
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
                client.switch2Lobby();
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
