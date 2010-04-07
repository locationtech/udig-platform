/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.commands.edit;

import java.io.IOException;
import java.util.Iterator;

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.core.StaticBlockingProvider;
import net.refractions.udig.core.internal.FeatureUtils;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

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
     * @see net.refractions.udig.project.internal.command.MapCommand#run()
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
                if( feature==null )
                    filter=(Filter) Filter.EXCLUDE;
				else {
					FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
					filter = filterFactory.id(
                    		FeatureUtils.stringToId(filterFactory, feature.getID()));
				}
                layer.setFilter(filter);
            }
        }
    }

    /**
     * @see net.refractions.udig.project.internal.command.MapCommand#copy()
     */
    public MapCommand copy() {
        return new SetEditFeatureCommand(newVictim, newLayer);
    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.SetEditFeatureCommand_setCurrentEditFeature; 
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        if( layer!=null )
            layer.setFilter(oldSelection);
        editManager.setEditFeature(oldEditVictim, oldEditLayer);
    }

}
