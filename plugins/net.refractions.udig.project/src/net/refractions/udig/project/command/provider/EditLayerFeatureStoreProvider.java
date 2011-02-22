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
package net.refractions.udig.project.command.provider;

import java.io.IOException;

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.MapCommand;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureStore;

/**
 * Obtains the feature store from the current edit layer.
 * @author Jesse
 * @since 1.1.0
 */
public class EditLayerFeatureStoreProvider implements IBlockingProvider<FeatureStore> {

    private IMap map;
    private MapCommand command;

    public EditLayerFeatureStoreProvider(IMap map) {
        this.map=map;
    }
    public EditLayerFeatureStoreProvider(MapCommand command) {
        this.command=command;
    }

    public FeatureStore get( IProgressMonitor monitor, Object... params ) throws IOException {
        if( map==null )
            map=command.getMap();
        return map.getEditManager().getEditLayer().getResource(FeatureStore.class, monitor);
    }

}
