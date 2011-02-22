/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.commands.edit;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Messages;
import net.refractions.udig.project.internal.ProjectPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureType;
import org.geotools.filter.Filter;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;

/**
 * Compares the editFeature with the corresponding feature in the data store (The feature with the
 * same fid) and replaces the old feature with the edit feature. If the feature is a new feature
 * then the feature is added to the datastore.
 *
 * @author jeichar
 * @since 0.3
 */
public class WriteEditFeatureCommand extends AbstractEditCommand implements UndoableMapCommand{
    private Feature old;
    private boolean added=false;
    private boolean noChange=false;

    public WriteEditFeatureCommand() {
    }


    /**
     * @see net.refractions.udig.project.internal.command.MapCommand#run()
     */
    @SuppressWarnings("deprecation")
    public void run( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.WriteEditFeatureCommand_runTask, IProgressMonitor.UNKNOWN);

        Feature editFeature = getMap().getEditManager().getEditFeature();
        ILayer editLayer = getMap().getEditManager().getEditLayer();
        editFeature = getMap().getEditManagerInternal().getEditFeature();
        editLayer = getMap().getEditManagerInternal().getEditLayerInternal();
        if (editFeature == null || editLayer == null){
            noChange=true;
            return;
        }
        old=editFeature.getFeatureType().duplicate(editFeature);
        FeatureType featureType = editFeature.getFeatureType();
        FilterFactory factory = FilterFactoryFinder.createFilterFactory();
        FeatureStore store = editLayer.getResource(FeatureStore.class, null);
        Filter filter = factory.createFidFilter(editFeature.getID());
        FeatureCollection results = store.getFeatures(filter);

        FeatureIterator reader = results.features();
        try {
            if (reader.hasNext()) {
                try {
                    store.modifyFeatures(featureType.getAttributeTypes(), editFeature
                            .getAttributes(new Object[featureType.getAttributeCount()]), filter);
                } catch (Exception e) {
                    ProjectPlugin.log("",e); //$NON-NLS-1$
                    noChange=true;
                }

            } else {
                added=true;
                getMap().getEditManagerInternal().addFeature(editFeature, (Layer) editLayer);
            }
        } finally {
            if (reader != null)
                reader.close();
        }
        monitor.done();
    }

    /**
     * @see net.refractions.udig.project.internal.command.MapCommand#copy()
     */
    public MapCommand copy() {
        return new WriteEditFeatureCommand();
    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.SetEditFeatureCommand_setCurrentEditFeature;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        if( noChange )
            return;
        monitor.beginTask(Messages.WriteEditFeatureCommand_rollbackTask, IProgressMonitor.UNKNOWN);
        Feature editFeature = getMap().getEditManager().getEditFeature();
        ILayer editLayer = getMap().getEditManager().getEditLayer();
       if( editFeature==null || editLayer==null )
            return;

        FilterFactory factory = FilterFactoryFinder.createFilterFactory();
        Filter filter = factory.createFidFilter(old.getID());
        FeatureStore store = editLayer.getResource(FeatureStore.class, null);
        if( added ){
            store.removeFeatures(filter);
            getMap().getEditManagerInternal().setEditFeature(null,null);
        }else{
            FeatureType featureType = old.getFeatureType();
            store.modifyFeatures(featureType.getAttributeTypes(), old
                    .getAttributes(new Object[featureType.getAttributeCount()]), filter);
        }
        monitor.done();
    }

}
