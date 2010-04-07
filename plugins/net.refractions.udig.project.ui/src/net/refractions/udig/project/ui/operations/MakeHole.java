package net.refractions.udig.project.ui.operations;

import net.refractions.udig.catalog.util.GeoToolsAdapters;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.IStyleBlackboard;
import net.refractions.udig.project.ProjectBlackboardConstants;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.Command;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import com.vividsolutions.jts.geom.Geometry;


public class MakeHole implements IOp {

    /**
     * Calls a command which makes a spatial filter and puts it on the 
     * styleBlackboard
     */
    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        final ILayer layer = (ILayer) target;
        final IMap map = layer.getMap();

        //get all selected features
        Query query = new DefaultQuery(layer.getSchema().getTypeName(), layer.getFilter());
        
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
                    //Key:  ProjectBlackboardConstants    String LAYER__DATA_QUERY = "net.refractions.udig.project.view"; //$NON-NLS-1$
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
