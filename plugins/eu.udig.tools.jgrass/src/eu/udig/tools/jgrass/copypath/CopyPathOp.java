/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.tools.jgrass.copypath;

import java.io.File;

import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import eu.udig.tools.jgrass.utils.OperationUtils;

/**
 * Operation to copy the path of a file. 
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class CopyPathOp extends OperationUtils implements IOp {
    public void op( final Display display, Object target, IProgressMonitor monitor ) throws Exception {
        if (target instanceof File) {
            final File file = (File) target;
            Display.getDefault().syncExec(new Runnable(){
                public void run() {
                    final Clipboard cb = new Clipboard(display);
                    TextTransfer textTransfer = TextTransfer.getInstance();
                    cb.setContents(new Object[]{file.getAbsolutePath()}, new Transfer[]{textTransfer});
                }
            });
        }
    }

}
