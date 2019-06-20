/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xueldor.encrypt;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.im.InputContext;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import static javax.swing.TransferHandler.NONE;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 *
 * @author xuexiangyu
 */
public class DefaultTextTransferHandler extends TransferHandler {

    public void exportToClipboard(JComponent comp, Clipboard clipboard,
            int action) throws IllegalStateException {
        if (comp instanceof JTextComponent) {
            JTextComponent text = (JTextComponent) comp;
            int p0 = text.getSelectionStart();
            int p1 = text.getSelectionEnd();
            if (p0 != p1) {
                try {
                    Document doc = text.getDocument();
                    String srcData = doc.getText(p0, p1 - p0);
                    StringSelection contents = new StringSelection(srcData);

                        // this may throw an IllegalStateException,
                    // but it will be caught and handled in the
                    // action that invoked this method
                    clipboard.setContents(contents, null);

                    if (action == TransferHandler.MOVE) {
                        doc.remove(p0, p1 - p0);
                    }
                } catch (BadLocationException ble) {
                }
            }
        }
    }

    public boolean importData(JComponent comp, Transferable t) {
        if (comp instanceof JTextComponent) {
            DataFlavor flavor = getFlavor(t.getTransferDataFlavors());

            if (flavor != null) {
                InputContext ic = comp.getInputContext();
                if (ic != null) {
                    ic.endComposition();
                }
                try {
                    String data = (String) t.getTransferData(flavor);

                    ((JTextComponent) comp).replaceSelection(data);
                    return true;
                } catch (UnsupportedFlavorException ufe) {
                } catch (IOException ioe) {
                }
            }
        }
        return false;
    }

    public boolean canImport(JComponent comp,
            DataFlavor[] transferFlavors) {
        JTextComponent c = (JTextComponent) comp;
        if (!(c.isEditable() && c.isEnabled())) {
            return false;
        }
        return (getFlavor(transferFlavors) != null);
    }

    public int getSourceActions(JComponent c) {
        return NONE;
    }

    private DataFlavor getFlavor(DataFlavor[] flavors) {
        if (flavors != null) {
            for (DataFlavor flavor : flavors) {
                if (flavor.equals(DataFlavor.stringFlavor)) {
                    return flavor;
                }
            }
        }
        return null;
    }
}
