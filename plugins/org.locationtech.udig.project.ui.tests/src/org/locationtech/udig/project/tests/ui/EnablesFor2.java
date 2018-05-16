/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.tests.ui;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

/**
 * Throws an exception if target is not an array of 2 layer objects
 * @author jones
 * @since 1.0.0
 */
public class EnablesFor2 implements IOp {

    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        ILayer[] layers=(ILayer[])target;
        if( layers.length!=2)
            throw new RuntimeException("target does not have 2 layers, instead: "+layers.length); //$NON-NLS-1$
        
        System.out.println("Good"); //$NON-NLS-1$
    }

}
