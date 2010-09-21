/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.commands.edit;

import java.util.Collections;

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.core.StaticFeatureCollection;
import net.refractions.udig.core.internal.FeatureUtils;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.command.provider.EditFeatureProvider;
import net.refractions.udig.project.command.provider.EditLayerFeatureStoreProvider;
import net.refractions.udig.project.internal.Messages;
import net.refractions.udig.project.internal.ProjectPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
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
     * @see net.refractions.udig.project.internal.command.MapCommand#run()
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

        FeatureIterator<SimpleFeature> reader = results.features();
        try {
            if (reader.hasNext()) {
                try {
                    store.modifyFeatures(featureType.getAttributeDescriptors().toArray(
							new AttributeDescriptor[0]), editFeature
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
     * @see net.refractions.udig.project.internal.command.MapCommand#copy()
     */
    public MapCommand copy() {
        return new WriteFeatureChangesCommand();
    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#getName()
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
            store.modifyFeatures(featureType.getAttributeDescriptors().toArray(new AttributeDescriptor[0]), this.editFeature
                    .getAttributes().toArray(), filter);            
        }
    }

}
