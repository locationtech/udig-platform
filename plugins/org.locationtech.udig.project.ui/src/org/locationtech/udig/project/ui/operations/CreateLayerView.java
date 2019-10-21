/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.commands.AddLayerCommand;
import org.locationtech.udig.ui.operations.IOp;

/**
 * Creates a layer that is a view based on the selection of the currently selected layer.
 * 
 * @author Jesse
 */
public class CreateLayerView implements IOp {

    public void op(Display display, Object target, IProgressMonitor monitor)
            throws Exception {
        ILayer layer = (ILayer) target;
        IMap map = layer.getMap();
        Layer view = map.getLayerFactory().createLayer(layer.findGeoResource(FeatureSource.class));
        view.getStyleBlackboard().put(ProjectBlackboardConstants.LAYER__DATA_QUERY, layer.getFilter());
        AddLayerCommand command = new AddLayerCommand(view);
        map.sendCommandASync(command);
    }

}
