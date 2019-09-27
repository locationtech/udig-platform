/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.commands.selection;

import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.factory.GeoTools;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

/**
 * TODO Purpose of org.locationtech.udig.project.internal.commands.selection
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class FIDSelectCommand extends AbstractCommand implements UndoableMapCommand {

    private Layer layer;
    private String id;
    private Filter oldFilter;

    /**
     * Set the select of a layer to be a single feature as specified by the FID
     * 
     * @param layer
     * @param featureID
     */
    public FIDSelectCommand( ILayer layer, String featureID ) {
        this.layer = (Layer) layer;
        this.id = featureID;
    }

    /**
     * Set the select of a layer to be a single feature as specified by the feature
     * 
     * @param layer
     * @param featureID
     */
    public FIDSelectCommand( ILayer layer, SimpleFeature feature ) {
        this.layer = (Layer) layer;
        this.id = feature.getID();
    }

    /**
     * @see org.locationtech.udig.project.internal.command.UndoableCommand#rollback()
     */
    public void rollback( IProgressMonitor monitor ) {
        if (oldFilter != null)
            layer.setFilter(oldFilter);
    }

    /**
     * @see org.locationtech.udig.project.internal.command.MapCommand#run()
     */
    public void run( IProgressMonitor monitor ) {
        oldFilter = layer.getFilter();
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
		Filter filter = filterFactory.id(FeatureUtils.stringToId(filterFactory, id));
        layer.setFilter(filter);
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.FIDSelectCommand_featureSelection; 
    }

}
