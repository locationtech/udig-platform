/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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
