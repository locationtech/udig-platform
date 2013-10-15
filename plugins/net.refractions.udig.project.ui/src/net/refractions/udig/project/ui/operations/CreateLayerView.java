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
package net.refractions.udig.project.ui.operations;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ProjectBlackboardConstants;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.commands.AddLayerCommand;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;

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
