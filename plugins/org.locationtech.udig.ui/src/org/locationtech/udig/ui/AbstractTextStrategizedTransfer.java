/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;

import org.locationtech.udig.internal.ui.UDIGTransfer;

import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * An abstract class for all the Types of text transfers supported by uDig.  Drag and
 * Drop is extremely platform dependent (I'm really disappointed with the SWT design)
 * so this class is handling the Linux requirements.  It depends on getTypeIds and getTypeNames
 * being implemented which to me is stupid.  

 * @author jones
 * @since 1.1.0
 */
public abstract class AbstractTextStrategizedTransfer extends AbstractStrategizedTransfer
        implements
            UDIGTransfer {

    private static final String COMPOUND_TEXT = "COMPOUND_TEXT"; //$NON-NLS-1$
    private static final String UTF8_STRING = "UTF8_STRING"; //$NON-NLS-1$
    private static final int COMPOUND_TEXT_ID = registerType(COMPOUND_TEXT);
    private static final int UTF8_STRING_ID = registerType(UTF8_STRING);
    

    @Override
    public Object nativeToJava( TransferData transferData ) {
        return TextTransfer.getInstance().nativeToJava(transferData);
    }
    
    protected int[] getTypeIds() {
        return new int[] {UTF8_STRING_ID, COMPOUND_TEXT_ID};
    }

    protected String[] getTypeNames() {
        return new String[] {UTF8_STRING, COMPOUND_TEXT};
    }

}
