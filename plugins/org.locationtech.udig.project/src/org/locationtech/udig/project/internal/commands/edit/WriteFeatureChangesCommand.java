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

import java.util.Collections;

import org.locationtech.udig.core.IBlockingProvider;
import org.locationtech.udig.core.StaticFeatureCollection;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.command.provider.EditFeatureProvider;
import org.locationtech.udig.project.command.provider.EditLayerFeatureStoreProvider;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.ProjectPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;

/**
 * Compares the editFeature with the corresponding feature in the data store (The feature with the
 * same fid) and replaces the old feature with the edit feature. If the feature is a new feature
 * then the feature is added to the datastore.
 * 
 * @author jeichar
 * @since 0.3
 */
public class WriteFeatureChangesCommand extends AbstractEditCommand implements UndoableMapCommand{
    private IBlockingProvider<SimpleFeature> featureProvider;
    private IBlockingProvider<FeatureStore<SimpleFeatureType, SimpleFeature>> storeProvider;
    private FeatureStore<SimpleFeatureType, SimpleFeature> store;
    private Id filter;
    private boolean added=false;
    private boolean noChange=false;
    private SimpleFeature editFeature;

    public WriteFeatureChangesCommand() {
        this(null, null);
    }
    
    public WriteFeatureChangesCommand( IBlockingProvider<SimpleFeature> feature, IBlockingProvider<FeatureStore<SimpleFeatureType, SimpleFeature>> featureStore ) {
        if(feature==null){
            this.featureProvider=new EditFeatureProvider(this);
        }else{
            this.featureProvider=feature;
        }
        if( featureStore==null ){
            this.storeProvider=new EditLayerFeatureStoreProvider(this);
        }else{
            this.storeProvider=featureStore;
        }
    }

    /**
     * @see org.locationtech.udig.project.internal.command.MapCommand#run()
     */
    @SuppressWarnings("deprecation") 
    public void run( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.WriteFeatureChangesCommand_runTask, 3); 
        SubProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor,1);
        editFeature = featureProvider.get(subProgressMonitor);
        subProgressMonitor.done();
        store = storeProvider.get(subProgressMonitor);
        if( editFeature==null || store==null ){
            noChange=true;
            return;
        }
            
        SimpleFeatureType featureType = editFeature.getFeatureType();
        FilterFactory factory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        subProgressMonitor = new SubProgressMonitor(monitor,1);
        subProgressMonitor.done();
        filter = factory.id(FeatureUtils.stringToId(factory, editFeature.getID()));
        FeatureCollection<SimpleFeatureType, SimpleFeature>  results = store.getFeatures(filter);

        Name[] names = featureType.getAttributeDescriptors().stream().map(e->e.getName()).toArray(Name[]::new);
        FeatureIterator<SimpleFeature> reader = results.features();
        try {
            if (reader.hasNext()) {
                try {                    
                    store.modifyFeatures(names, editFeature
							.getAttributes().toArray(), filter);
                } catch (Exception e) {
                    ProjectPlugin.log("", e); //$NON-NLS-1$
                    noChange=true;
                }

            } else {
                added=true;
                store.addFeatures(new StaticFeatureCollection(Collections.singleton(editFeature), featureType));
            }
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    /**
     * @see org.locationtech.udig.project.internal.command.MapCommand#copy()
     */
    public MapCommand copy() {
        return new WriteFeatureChangesCommand();
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.WriteFeatureChangesCommand_commandName;  
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        if( noChange )
            return;
        monitor.beginTask(Messages.WriteFeatureChangesCommand_rollbackTask, IProgressMonitor.UNKNOWN); 
        if( added ){
            store.removeFeatures(filter);
        }else{
            SimpleFeatureType featureType = this.editFeature.getFeatureType();
            Name[] names = featureType.getAttributeDescriptors().stream().map(e->e.getName()).toArray(Name[]::new);
            store.modifyFeatures(names, this.editFeature
                    .getAttributes().toArray(), filter);            
        }
    }

}
