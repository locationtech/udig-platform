/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.internal.ui;

import org.eclipse.swt.dnd.TransferData;

/**
 * Makes public the validate method so that the udig DND framework can validate a transfer
 * 
 * @author jones
 * @since 1.0.0
 */
public interface UDIGTransfer {
    /**
     * Returns true if the transfer can transfer to and from the object.
     *
     * @return true if the transfer can transfer to and from the object.
     */
    public boolean validate(Object object);
    
    /**
     * @see org.eclipse.swt.dnd.Transfer#javaToNative(java.lang.Object, org.eclipse.swt.dnd.TransferData)
     */
    public void javaToNative( Object object, TransferData transferData );

    /**
     * @see org.eclipse.swt.dnd.Transfer#nativeToJava(org.eclipse.swt.dnd.TransferData)
     */
    public Object nativeToJava( TransferData transferData );
}
