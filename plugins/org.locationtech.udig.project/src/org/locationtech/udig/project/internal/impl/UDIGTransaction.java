/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.impl;

import java.io.IOException;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.locationtech.udig.project.internal.Messages;

/**
 * This class helps protect the system from others trying to commit or close the transaction
 * programmatically rather than working through the EditManager API.
 *
 * @author jones
 * @since 1.0.0
 */
public class UDIGTransaction extends DefaultTransaction implements Transaction {
    @Override
    public void commit() throws IOException {
        throw new IllegalStateException(Messages.UDIGTransaction_commitException);
    }

    @Override
    public void rollback() throws IOException {
        super.rollback();
    }

    @Override
    public synchronized void close() {
        throw new IllegalStateException(Messages.UDIGTransaction_closeException);
    }

    public void commitInternal() throws IOException {
        super.commit();
    }

    public void rollbackInternal() throws IOException {
        super.rollback();
    }

    synchronized void closeInternal() {
        super.close();
    }
}
