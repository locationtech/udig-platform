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
package org.locationtech.udig.tutorials.style.color;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.ui.operations.IOp;

public class RemoveColorOp implements IOp {

    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        ILayer layer = (ILayer) target;
        
        layer.getStyleBlackboard().remove( ColorStyle.ID );
        layer.refresh(null);
    }

}
