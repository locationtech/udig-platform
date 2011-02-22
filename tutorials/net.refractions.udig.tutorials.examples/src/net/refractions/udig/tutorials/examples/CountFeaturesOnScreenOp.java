package net.refractions.udig.tutorials.examples;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.util.GeoToolsAdapters;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.render.IViewportModel;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.feature.visitor.FeatureVisitor;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;
import org.geotools.filter.FilterType;
import org.geotools.filter.FilterVisitor;
import org.geotools.filter.GeometryFilter;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * An example Layer operation that counts the featues on the screen.
 * <p>
 * This example sets up a filter based on the viewport bounds.
 * </p>
 * To use this examle you will need to fill in the plugin.xml IOp extension point.
 * @author Jody Garnett
 */
public class CountFeaturesOnScreenOp implements IOp {
    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        if( monitor == null) monitor = new NullProgressMonitor();
        monitor.beginTask("Count Features", 100 );
        ILayer layer = (ILayer) target;
        IViewportModel viewport = layer.getMap().getViewportModel();
        try {
            IGeoResource resource = layer.findGeoResource(FeatureSource.class);
            if( resource == null ){
                System.out.println("features: 0");
                return;
            }
            FeatureSource source = resource.resolve(FeatureSource.class, new SubProgressMonitor(monitor,10) );
            FeatureType schema = source.getSchema();
            GeometryAttributeType defaultGeometryType = schema.getDefaultGeometry();
            CoordinateReferenceSystem dataCRS = defaultGeometryType.getCoordinateSystem();

            ReferencedEnvelope screenBounds = viewport.getBounds();
            ReferencedEnvelope dataBounds = screenBounds.transform( dataCRS, true );

            FilterFactory ff = FilterFactoryFinder.createFilterFactory();
            GeometryFilter filter = ff.createGeometryFilter( FilterType.GEOMETRY_BBOX );
            filter.addLeftGeometry( ff.createAttributeExpression( defaultGeometryType.getName() ));
            filter.addRightGeometry( ff.createBBoxExpression( dataBounds ));

            FeatureCollection features = source.getFeatures( filter );
            System.out.println("features: "+features.size());
            // or using a visitor
            class CountVisitor implements FeatureVisitor {
                public int count = 0;
                public void visit( Feature fetaure ) {
                    count++;
                }
            }
            CountVisitor counter = new CountVisitor();
            features.accepts( counter, GeoToolsAdapters.progress( new SubProgressMonitor(monitor,90)));
            System.out.println("features: "+counter.count);
        }
        finally {
            monitor.done();
        }
    }
}
