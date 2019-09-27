/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.util.factory.GeoTools;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.udig.catalog.util.GeoToolsAdapters;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IStyleBlackboard;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.Command;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.ui.operations.IOp;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;


public class MakeHole implements IOp {

    /**
     * Calls a command which makes a spatial filter and puts it on the 
     * styleBlackboard
     */
    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        final ILayer layer = (ILayer) target;
        final IMap map = layer.getMap();

        //get all selected features
        Query query = new Query(layer.getSchema().getTypeName(), layer.getFilter());
        
        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = layer.getResource(FeatureSource.class, new SubProgressMonitor(monitor, 1)); 
        FeatureCollection<SimpleFeatureType, SimpleFeature>  features = featureSource.getFeatures(query);
        
        //combine them into one large polygon
        final Geometry union[] = new Geometry[1];
        features.accepts( new FeatureVisitor(){
            public void visit( Feature feature ) {
                SimpleFeature simple = (SimpleFeature) feature;
                Geometry geometry = (Geometry ) simple.getDefaultGeometry();
                if( union[0] == null ){
                    union[0] = geometry;
                }
                else {
                    union[0] = union[0].union( geometry );
                }
            }                    
        }, GeoToolsAdapters.progress(monitor) );
        
        final Geometry hole = union[0];
        
        MapCommand drillHoleCommand = new AbstractCommand(){
            
            public void run( IProgressMonitor monitor ) throws Exception {
                for( Layer targetLayer : getMap().getLayersInternal() ){
                    //make hole filter for target layer
                    if( targetLayer == layer ){
                        continue; // skip the source layer (because that would be silly)
                    }
                    SimpleFeatureType targetType = targetLayer.getSchema();
                    if( targetType == null ){
                        // must be a grid coverage or something
                        continue;
                    }
                    String targetGeomName = targetType.getGeometryDescriptor().getLocalName();
    
                    FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2( GeoTools.getDefaultHints() );
                    Filter cut= ff.not( ff.within( ff.property( targetGeomName ), ff.literal(hole) ) );
                    
                    //put it on style blackboard
                    //Key:  ProjectBlackboardConstants    String LAYER__DATA_QUERY = "org.locationtech.udig.project.view"; //$NON-NLS-1$
                    IStyleBlackboard styleBlackboard = layer.getStyleBlackboard();
                    styleBlackboard.put(ProjectBlackboardConstants.LAYER__DATA_QUERY, cut);
                }
            }

            public Command copy() {
                return this;
            }

            public String getName() {
                return "Create Hole Command"; //$NON-NLS-1$
            }
            
        };
        map.sendCommandSync( drillHoleCommand );
    }

}
