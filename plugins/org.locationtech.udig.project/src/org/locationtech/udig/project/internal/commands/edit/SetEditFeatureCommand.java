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

import java.io.IOException;
import java.util.Iterator;

import org.locationtech.udig.core.IBlockingProvider;
import org.locationtech.udig.core.StaticBlockingProvider;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;

/**
 * Sets the current editable SimpleFeature
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class SetEditFeatureCommand extends AbstractLayerManagerControlCommand implements
UndoableMapCommand{
    private IBlockingProvider<SimpleFeature> newVictim;

    private IBlockingProvider<ILayer> newLayer;

    private SimpleFeature oldEditVictim;

    private Layer oldEditLayer;

    private Filter oldSelection;

    private Layer layer;

    /**
     * Creates a new instance of SetEditFeatureCommand.
     * 
     * @param feature the new editable SimpleFeature.
     * @param featureStore A featurestore that contains the editable SimpleFeature.
     */
    public SetEditFeatureCommand( SimpleFeature feature, ILayer layer ) {
        init(new StaticBlockingProvider<SimpleFeature>(feature),
                new StaticBlockingProvider<ILayer>(layer));
    }

    /**
     * Creates a new instance of SetEditFeatureCommand
     * 
     * @param feature the new editable SimpleFeature
     */
    public SetEditFeatureCommand( final SimpleFeature feature ) {
        this.init(new StaticBlockingProvider<SimpleFeature>(feature),
            this.newLayer = new IBlockingProvider<ILayer>(){
    
                public ILayer get( IProgressMonitor monitor, Object... objects ) throws IOException {
                    for( Iterator<Layer> iter = editManager.getMapInternal().getLayersInternal()
                            .iterator(); iter.hasNext(); ) {
                        Layer thisLayer = iter.next();
                        if (thisLayer.hasResource(FeatureSource.class)) {
                        	FeatureStore<SimpleFeatureType, SimpleFeature> fs = thisLayer.getResource(FeatureStore.class, null);
                            FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
							FeatureCollection<SimpleFeatureType, SimpleFeature>  results = fs.getFeatures(filterFactory
                                    .id(FeatureUtils.stringToId(filterFactory, feature.getID())));
                            if (results.size() == 1) {
                                return thisLayer;
                            }
                        }
                    }
                    return null;
                }
                
            }
        );
    }

    public SetEditFeatureCommand( IBlockingProvider<SimpleFeature> feature, IBlockingProvider<ILayer> layer) {
        init(feature, layer);
    }
    
    private void init( IBlockingProvider<SimpleFeature> feature, IBlockingProvider<ILayer> layer2 ) {
        this.newVictim=feature;
        this.newLayer=layer2;
    }

    /**
     * @see org.locationtech.udig.project.internal.command.MapCommand#run()
     */
    public void run( IProgressMonitor monitor ) throws Exception {
        oldEditVictim=editManager.getEditFeature();
        oldEditLayer=editManager.getEditLayerInternal();
        if (newVictim == null) {
            editManager.setEditFeature(null, null);
            layer = (Layer) newLayer.get(monitor);
            if( layer!=null ){
                oldSelection=(Filter) layer.getFilter();
                layer.setFilter(Filter.EXCLUDE);
            }
        }else{
            SimpleFeature feature = newVictim.get(monitor);
            layer = (Layer) newLayer.get(monitor); 
            if( layer!=null ){
                
                editManager.setEditFeature(feature, layer);
                oldSelection=(Filter) layer.getFilter();
                Filter filter;
                if (feature == null) {
                    filter = (Filter) Filter.EXCLUDE;
                } else {
                    FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
                    filter = ff.id(feature.getIdentifier());
                }
                layer.setFilter(filter);
            }
        }
    }

    /**
     * @see org.locationtech.udig.project.internal.command.MapCommand#copy()
     */
    public MapCommand copy() {
        return new SetEditFeatureCommand(newVictim, newLayer);
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.SetEditFeatureCommand_setCurrentEditFeature; 
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        if( layer!=null ){
            layer.setFilter(oldSelection);
        }
        editManager.setEditFeature(oldEditVictim, oldEditLayer);
    }

}
