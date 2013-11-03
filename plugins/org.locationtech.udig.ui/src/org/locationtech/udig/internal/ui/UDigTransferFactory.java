/**
 * 
 */
package org.locationtech.udig.internal.ui;

import org.locationtech.udig.ui.TransferFactory;

import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.URLTransfer;

/**
 * Registers Transfer instances we are interested in processing.
 * <p>
 * The set of transfers contributed by this instance is:
 * <ul>
 * <li>URLTransfer - used to accept URL instances from a browser</li>
 * <li>UDigByteAndLocalTransfer - used to throw objects around</li>
 * <li>FeatureTextTransfer</li>
 * <li>GeometryTextTransfer</li>
 * <li>FilterTextTransfer</li>
 * <li>TextTransfer</li>
 * <li>FileTransfer</li>
 * <li>HTMLTransfer</li>
 * </ui>
 * @author jones
 */
public class UDigTransferFactory implements TransferFactory {

    /**
     * @see org.locationtech.udig.ui.TransferFactory#getTransfers()
     */
    public Transfer[] getTransfers() {
        return new Transfer[]{
                URLTransfer.getInstance(),
                UDigByteAndLocalTransfer.getInstance(),
                FeatureTextTransfer.getInstance(),
                GeometryTextTransfer.getInstance(),
                FilterTextTransfer.getInstance(),
                TextTransfer.getInstance(),
                FileTransfer.getInstance(),
                HTMLTransfer.getInstance()};
    }
}
