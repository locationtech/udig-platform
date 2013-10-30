/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.tools.jgrass.movefeatures;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import org.locationtech.udig.tools.jgrass.utils.OperationUtils;

/**
 * Operation to move features one layer down. 
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class FeatureDownMover extends OperationUtils implements IOp {

    public void op( final Display display, Object target, IProgressMonitor monitor ) throws Exception {
        ILayer selectedLayer = (ILayer) target;
        moveFeatures(display, monitor, selectedLayer, false);
    }

}
