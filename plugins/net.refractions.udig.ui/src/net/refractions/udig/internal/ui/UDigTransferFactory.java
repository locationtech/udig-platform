/**
 *
 */
package net.refractions.udig.internal.ui;

import net.refractions.udig.ui.TransferFactory;

import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

/**
 * Creates the transferObjects used by
 *
 * @author jones
 *
 */
public class UDigTransferFactory implements TransferFactory {

	/**
	 * @see net.refractions.udig.ui.TransferFactory#getTransfers()
	 */
	public Transfer[] getTransfers() {
	    return new Transfer[] { UDigByteAndLocalTransfer.getInstance(),
                FeatureTextTransfer.getInstance(), GeometryTextTransfer.getInstance(),
                FilterTextTransfer.getInstance(),
				TextTransfer.getInstance(), FileTransfer.getInstance(), HTMLTransfer.getInstance() };
	}
}
