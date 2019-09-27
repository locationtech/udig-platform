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

import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.util.factory.GeoTools;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
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
     * @see org.locationtech.udig.project.internal.command.MapCommand#run()
     */
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

        Name[] names = featureType.getAttributeDescriptors().stream().map(e->e.getName()).toArray(Name[]::new);
        FeatureIterator<SimpleFeature> reader = results.features();
        try {
            if (reader.hasNext()) {
                try {
                    store.modifyFeatures(names, editFeature
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
     * @see org.locationtech.udig.project.internal.command.MapCommand#copy()
     */
    public MapCommand copy() {
        return new WriteEditFeatureCommand();
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#getName()
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
            
            Name[] names = featureType.getAttributeDescriptors().stream().map(e->e.getName()).toArray(Name[]::new);
            store.modifyFeatures(names, old
                    .getAttributes().toArray(), filter);            
        }
        monitor.done();
    }

}
