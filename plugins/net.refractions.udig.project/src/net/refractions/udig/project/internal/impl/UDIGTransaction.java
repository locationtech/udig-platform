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
package net.refractions.udig.project.internal.impl;

import java.io.IOException;

import net.refractions.udig.project.internal.Messages;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;

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
        throw new RuntimeException(Messages.UDIGTransaction_commitException); 
    }

    @Override
    public void rollback() throws IOException {
        super.rollback();
    }

    @Override
    public synchronized void close() {
        throw new RuntimeException(Messages.UDIGTransaction_closeException); 
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
