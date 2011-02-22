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
package net.refractions.udig.project.tests.ui;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.ui.operations.IOp;

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
