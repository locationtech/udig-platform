/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.commands.edit;

import net.refractions.udig.core.internal.FeatureUtils;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Messages;
import net.refractions.udig.project.internal.ProjectPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

/**
 * Compares the editFeature with the corresponding feature in the data store (The feature with the
 * same fid) and replaces the old feature with the edit feature. If the feature is a new feature
 * then the feature is added to the datastore.
 * 
 * @author jeichar
 * @since 0.3
 */
public class WriteEditFeatureCommand extends AbstractEditCommand implements UndoableMapCommand{
    private SimpleFeature old;
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

        SimpleFeature editFeature = getMap().getEditManager().getEditFeature();
        ILayer editLayer = getMap().getEditManager().getEditLayer();
        editFeature = getMap().getEditManagerInternal().getEditFeature();
        editLayer = getMap().getEditManagerInternal().getEditLayerInternal();
        if (editFeature == null || editLayer == null){
            noChange=true;
            return;
        }
        old=SimpleFeatureBuilder.copy(editFeature);
        SimpleFeatureType featureType = editFeature.getFeatureType();
        FilterFactory factory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        FeatureStore<SimpleFeatureType, SimpleFeature> store = editLayer.getResource(FeatureStore.class, null);
        Filter filter = factory.id(FeatureUtils.stringToId(factory, editFeature.getID()));
        FeatureCollection<SimpleFeatureType, SimpleFeature>  results = store.getFeatures(filter);

        FeatureIterator<SimpleFeature> reader = results.features();
        try {
            if (reader.hasNext()) {
                try {
                    store.modifyFeatures(featureType.getAttributeDescriptors().toArray(new AttributeDescriptor[0]), editFeature
                            .getAttributes().toArray(), filter);
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
        SimpleFeature editFeature = getMap().getEditManager().getEditFeature();
        ILayer editLayer = getMap().getEditManager().getEditLayer();
       if( editFeature==null || editLayer==null )
            return;
        
        FilterFactory factory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        Filter filter = factory.id(FeatureUtils.stringToId(factory, old.getID()));
        FeatureStore<SimpleFeatureType, SimpleFeature> store = editLayer.getResource(FeatureStore.class, null);
        if( added ){
            store.removeFeatures(filter);
            getMap().getEditManagerInternal().setEditFeature(null,null);
        }else{
            SimpleFeatureType featureType = old.getFeatureType();
            store.modifyFeatures(featureType.getAttributeDescriptors().toArray(new AttributeDescriptor[0]), old
                    .getAttributes().toArray(), filter);            
        }
        monitor.done();
    }

}
