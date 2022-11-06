/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2022, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.command.provider;

import org.eclipse.core.runtime.IProgressMonitor;
import org.locationtech.udig.core.IBlockingProvider;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.MapCommand;

public class EditLayerProvider implements IBlockingProvider<ILayer> {

    private MapCommand command;

    private IMap map;

    public EditLayerProvider(MapCommand command) {
        this.command = command;
    }

    public EditLayerProvider(IMap map) {
        this.map = map;
    }

    @Override
    public ILayer get(IProgressMonitor monitor, Object... params) {
        if (map != null) {
            return map.getEditManager().getEditLayer();
        }
        return command.getMap().getEditManager().getEditLayer();
    }
}
