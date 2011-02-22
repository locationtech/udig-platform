/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.commands.edit;

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.PostDeterminedEffectCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.feature.Feature;
import org.geotools.filter.FilterFactoryFinder;

/**
 * Deletes a feature from the map.
 *
 * @author Jesse
 * @since 1.0.0
 */
public class DeleteFeatureCommand extends AbstractEditCommand implements UndoableMapCommand,
    PostDeterminedEffectCommand {

    IBlockingProvider<Feature> featureProvider;

    private IBlockingProvider<ILayer> layerProvider;

    protected boolean done;

	private Feature feature;

	private ILayer oldLayer;

    /**
     * Construct <code>DeleteFeatureCommand</code>.
     */
    public DeleteFeatureCommand( IBlockingProvider<Feature> featureProvider, IBlockingProvider<ILayer> layerProvider ) {
        this.featureProvider = featureProvider;
        this.layerProvider = layerProvider;
    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#run()
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
        oldLayer.getResource(FeatureStore.class, null).removeFeatures(
                FilterFactoryFinder.createFilterFactory().createFidFilter(feature.getID()));
        map.getEditManagerInternal().setEditFeature(null, null);
        return true;
    }
    public MapCommand copy() {
        return new DeleteFeatureCommand(featureProvider, layerProvider);
    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.DeleteFeatureCommand_deleteFeature;
    }

    /**
     * @see net.refractions.udig.project.command.UndoableCommand#rollback()
     */
    public void rollback( IProgressMonitor monitor ) throws Exception {
        if( feature==null )
            return;
        map.getEditManagerInternal().setEditFeature(feature, (Layer) oldLayer);
        map.getEditManagerInternal().addFeature(feature, (Layer) oldLayer);
    }

}
