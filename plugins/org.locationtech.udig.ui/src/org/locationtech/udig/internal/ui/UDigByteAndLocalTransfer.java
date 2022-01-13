/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.internal.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.locationtech.udig.core.logging.LoggingSupport;

/**
 * Appears to be a transfer for passing an object around
 * the uDig application.
 * <p>
 * The internals of this class appear to duplicate URLTransfer; so this may
 * be a cut and paste of the URLTransfer type prior to it being added to
 * Eclipse 3.4?
 * </p>
 */
public class UDigByteAndLocalTransfer extends ByteArrayTransfer implements UDIGTransfer{
	private static UDigByteAndLocalTransfer _instance = new UDigByteAndLocalTransfer();

	static final String CFSTR_INETURL = "InternalObject"; //$NON-NLS-1$

	private static final int CFSTR_INETURLID = Transfer
			.registerType(CFSTR_INETURL);

	private long startTime;

	public Object object;

    @Override
    public boolean isSupportedType( TransferData transferData ) {
        return super.isSupportedType(transferData);
    }

	public UDigByteAndLocalTransfer() {

		// do nothing.
	}

	public static UDigByteAndLocalTransfer getInstance() {

		return _instance;
	}

	@Override
    protected int[] getTypeIds() {

		return new int[] { CFSTR_INETURLID };
	}

	@Override
    public String[] getTypeNames() {

		return new String[] { CFSTR_INETURL };
	}

	@Override
	public TransferData[] getSupportedTypes() {
		return super.getSupportedTypes();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void javaToNative(Object object, TransferData transferData) {

	    startTime = System.currentTimeMillis();
	    if( object instanceof IStructuredSelection){
	    	IStructuredSelection selection=(IStructuredSelection) object;
	    	List<Object> elements=new ArrayList<>();
	    	for (Iterator<Object> iter = selection.iterator(); iter.hasNext();) {
				elements.add(iter.next());
			}
	    	this.object=elements.toArray();
	    }
	    this.object = object;
	    if (transferData != null)
	    {
	      super.javaToNative(String.valueOf(startTime).getBytes(), transferData);
	    }
	}

	/**
	 * This implementation of <code>nativeToJava</code> converts a platform
	 * specific representation of a URL and optionally, a title to a java
	 * <code>String[]</code>. For additional information see
	 * <code>Transfer#nativeToJava</code>.
	 *
	 * @param transferData
	 *            the platform specific representation of the data to be been
	 *            converted
	 * @return a java <code>String[]</code> containing a URL and optionally a
	 *         title if the conversion was successful; otherwise null
	 */
    @Override
    public Object nativeToJava(TransferData transferData) {

        byte[] bytes = (byte[])super.nativeToJava(transferData);
        if (bytes == null) return null;

        try
        {
          long startTime = Long.valueOf(new String(bytes)).longValue();
          return this.startTime == startTime ? object : null;
        }
        catch (NumberFormatException exception)
        {
            InputStreamReader reader = new InputStreamReader(
                    new ByteArrayInputStream(bytes));
            StringBuffer buf = new StringBuffer();
            char[] chars = new char[bytes.length / 2];
            int read=0;
            try {
                read=reader.read(chars);
            } catch (IOException e) {
                LoggingSupport.log(UiPlugin.getDefault(), "Error reading transfer data", e); //$NON-NLS-1$
            }
            buf.append(chars,0,read);
            return buf.toString().trim();
        }


	}

	@Override
    public boolean validate(Object object) {
		return true;
	}
}
