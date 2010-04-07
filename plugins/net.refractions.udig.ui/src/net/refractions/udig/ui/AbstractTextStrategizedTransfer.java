/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.ui;

import net.refractions.udig.internal.ui.UDIGTransfer;

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
