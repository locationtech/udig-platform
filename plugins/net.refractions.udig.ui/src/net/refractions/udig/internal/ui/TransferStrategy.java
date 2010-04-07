/**
 * 
 */
package net.refractions.udig.internal.ui;

import org.eclipse.swt.dnd.TransferData;

/**
 * Many of the uDig Transfer objects can behave differently depending on how a user sets the workspace
 * preferences.  For example a user may want the FilterTextTransfer to drag and drop all the selected features 
 * or the user may want to drag/drop/cut/paste the filter in XML.
 * 
 */
public interface TransferStrategy {
	/**
	 * This is called by the Transfer's javaToNative method.
	 * @see org.eclipse.swt.dnd.Transfer#javaToNative(java.lang.Object, org.eclipse.swt.dnd.TransferData)
	 */
    public void javaToNative( Object object, TransferData transferData );

    /**
     * This is called by the Transfer's nativeToJava method
     * @see org.eclipse.swt.dnd.Transfer#nativeToJava(org.eclipse.swt.dnd.TransferData)
     */
    public Object nativeToJava( TransferData transferData );
}
