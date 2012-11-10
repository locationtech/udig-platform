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
package net.refractions.udig.catalog.document;

import java.io.File;

/**
 * IDocument stored as an "attachment".
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.2
 */
public interface IAttachment extends IDocument {

    /**
     * Used to save an attachment out to the local file system.
     * <p>
     * This method is called under user control, the generic "open" functionality is expected to
     * work against a read-only cached temporary file.
     * <p>
     * In order to be forgiving, the provided path<
     * <ul>
     * <li>Directory: file will be saved in the provided location using the file name provided by
     * {@link #getName()}.</li>
     * <li>File: file name will be used as provided</li>
     * </ul>
     * 
     * @param newfile Path used to save the provided document
     * @return true if saved successfully, otherwise false
     */
    public boolean saveAs(File newfile);

}
