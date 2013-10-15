/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.command.provider;

import java.io.IOException;

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.MapCommand;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Obtains the feature store from the current edit layer.
 * @author Jesse
 * @since 1.1.0
 */
public class EditLayerFeatureStoreProvider implements IBlockingProvider<FeatureStore<SimpleFeatureType, SimpleFeature>> {

    private IMap map;
    private MapCommand command;

    public EditLayerFeatureStoreProvider(IMap map) {
        this.map=map;
    }
    public EditLayerFeatureStoreProvider(MapCommand command) {
        this.command=command;
    }
    
    public FeatureStore<SimpleFeatureType, SimpleFeature> get( IProgressMonitor monitor, Object... params ) throws IOException {
        if( map==null )
            map=command.getMap();
        return map.getEditManager().getEditLayer().getResource(FeatureStore.class, monitor);
    }

}
