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
package net.refractions.udig.internal.ui;

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
