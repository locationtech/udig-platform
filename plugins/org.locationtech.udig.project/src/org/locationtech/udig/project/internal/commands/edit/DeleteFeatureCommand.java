/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.commands.edit;

import org.locationtech.udig.core.IBlockingProvider;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.PostDeterminedEffectCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.factory.GeoTools;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory;

/**
 * Deletes a feature from the map.
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class DeleteFeatureCommand extends AbstractEditCommand implements UndoableMapCommand, 
    PostDeterminedEffectCommand {

    IBlockingProvider<SimpleFeature> featureProvider;

    private IBlockingProvider<ILayer> layerProvider;

    protected boolean done;

	private SimpleFeature feature;

	private ILayer oldLayer;

    /**
     * Construct <code>DeleteFeatureCommand</code>.
     */
    public DeleteFeatureCommand( IBlockingProvider<SimpleFeature> featureProvider, IBlockingProvider<ILayer> layerProvider ) {
        this.featureProvider = featureProvider;
        this.layerProvider = layerProvider;
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#run()
     */
    public void run( IProgressMonitor monitor ) throws Exception {
        execute(monitor);
    }

    public boolean execute( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.DeleteFeatureCommand_deleteFeature, 4); 
        monitor.worked(1);
        feature = featureProvider.get(new SubProgressMonitor(monitor, 1));
        if( feature==null )
            return false;
        oldLayer = layerProvider.get(new SubProgressMonitor(monitor, 1));
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
		oldLayer.getResource(FeatureStore.class, null).removeFeatures(
                filterFactory.id(FeatureUtils.stringToId(filterFactory, feature.getID())));
        map.getEditManagerInternal().setEditFeature(null, null);
        return true;
    }
    public MapCommand copy() {
        return new DeleteFeatureCommand(featureProvider, layerProvider);
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.DeleteFeatureCommand_deleteFeature; 
    }

    /**
     * @see org.locationtech.udig.project.command.UndoableCommand#rollback()
     */
    public void rollback( IProgressMonitor monitor ) throws Exception {
        if( feature==null )
            return;
        map.getEditManagerInternal().setEditFeature(feature, (Layer) oldLayer);
        map.getEditManagerInternal().addFeature(feature, (Layer) oldLayer);
    }

}
