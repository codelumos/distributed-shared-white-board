package org.unimelb.whiteboard.client.menu;

import org.unimelb.whiteboard.client.whiteboard.PaintManager;
import org.unimelb.whiteboard.client.whiteboard.WhiteBoardWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Edit menu in the menu bar.
 */
public class EditMenu extends JMenu {
    private final WhiteBoardWindow window;
    private final PaintManager paintManager;
    private final JMenuItem redoItem;
    private final JMenuItem undoItem;

    public EditMenu(WhiteBoardWindow window) {
        super("Edit");
        this.window = window;
        paintManager = window.getPaintManager();
        this.setMnemonic(KeyEvent.VK_E);

        undoItem = new JMenuItem("Undo", KeyEvent.VK_Z);

        // Check whether the operate system is Mac OS, decide use ctrl or command
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Mac OS")) {
            undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        } else {
            undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        }
        undoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (paintManager.getMode() == PaintManager.SERVER_MODE) {
                    window.getPaintManager().undo();
                    undoItem.setEnabled(paintManager.isUndoAllow());
                }
            }
        });
        this.add(undoItem);

        redoItem = new JMenuItem("Redo");
        if (osName.startsWith("Mac OS")) {
            redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.SHIFT_MASK));
        } else {
            redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
        }
        redoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (paintManager.getMode() == PaintManager.SERVER_MODE) {
                    window.getPaintManager().redo();
                    redoItem.setEnabled(paintManager.isRedoAllow());
                }
            }
        });
        this.add(redoItem);
        updateEnable();
    }

    /**
     * Set whether redo is able.
     */
    public void setRedoEnable(Boolean isAble) {
        if (window.getPaintManager().getMode() == PaintManager.SERVER_MODE) {
            redoItem.setEnabled(isAble);
        }
    }

    /**
     * Set whether undo is able.
     */
    public void setUndoEnable(Boolean isAble) {
        if (window.getPaintManager().getMode() == PaintManager.SERVER_MODE) {
            undoItem.setEnabled(isAble);
        }
    }

    /**
     * Update the undo and redo button's isEnable.
     */
    public void updateEnable() {
        if (paintManager.getMode() == PaintManager.CLIENT_MODE) {
            undoItem.setEnabled(false);
        } else {
            undoItem.setEnabled(paintManager.isUndoAllow());
        }

        if (paintManager.getMode() == PaintManager.CLIENT_MODE) {
            redoItem.setEnabled(false);
        } else {
            redoItem.setEnabled(paintManager.isRedoAllow());
        }
    }
}
