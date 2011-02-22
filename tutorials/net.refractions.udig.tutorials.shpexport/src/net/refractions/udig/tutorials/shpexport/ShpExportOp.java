package net.refractions.udig.tutorials.shpexport;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.feature.visitor.FeatureVisitor;
import org.geotools.filter.Filter;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;
import org.geotools.filter.FilterType;
import org.geotools.filter.GeometryFilter;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.factory.GeotoolsFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

public class ShpExportOp implements IOp {

public void op(Display display, Object target, IProgressMonitor monitor)
        throws Exception {
    FeatureSource source = (FeatureSource) target;
    FeatureType featureType = source.getSchema();
    GeometryAttributeType geometryType = featureType.getDefaultGeometry();
    CoordinateReferenceSystem crs = geometryType.getCoordinateSystem();

    String typeName = featureType.getTypeName();

    // String filename = promptSaveDialog( typeName )
    String filename = typeName.replace(':', '_');
    URL directory = FileLocator.toFileURL( Platform.getInstanceLocation().getURL() );
    URL shpURL = new URL(directory.toExternalForm() + filename + ".shp");
    final File file = new File( shpURL.toURI() );

    // promptOverwrite( file )
    if (file.exists()){
        return;
    }

    // create and write the new shapefile
    ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();
    Map params = new HashMap();
    params.put( "url", file.toURL() );
    ShapefileDataStore dataStore =
        (ShapefileDataStore) factory.createNewDataStore( params );
    dataStore.createSchema( featureType );

    FeatureStore store = (FeatureStore) dataStore.getFeatureSource();
    store.addFeatures( source.getFeatures() );
    dataStore.forceSchemaCRS( crs );
    System.out.println(file);
    System.out.println(shpURL);
}

private void promptOverwrite(final Display display, final File file){
    if (!file.exists()) return;

    display.syncExec(new Runnable() {
        public void run() {
            boolean overwrite = MessageDialog.openConfirm(display
                    .getActiveShell(), "Warning",
                    "File Exists do you wish to overwrite?");
            if (overwrite){
                file.delete();
            }
        }
    });
}
    /**
     * Example of opening a save dialog in the display thread.
     *
     * @param typeName
     * @return filename provided by the user, or null
     */
    private String promptSaveDialog( final String typeName  ){
        final String filename = typeName.replace(':', '_');
        final String[] result = new String[1];

        PlatformGIS.syncInDisplayThread( new Runnable(){
            public void run() {
                Display display = Display.getCurrent();
                FileDialog dialog = new FileDialog( display.getActiveShell(), SWT.SAVE );

                dialog.setFileName( filename+".shp");
                dialog.setText("Export "+typeName );
                dialog.setFilterExtensions( new String[]{"shp", "SHP"} );
                result[0] = dialog.open();
            }
        });

        return result[0];
    }

    /**
     * You can use a FilterFactory to select out data for a given region.
     *
     * You can use this example with the viewport.getBounds().
     * @param source
     * @param bounds
     * @return
     * @throws Exception
     */
    private Filter filter( FeatureSource source, ReferencedEnvelope bounds ) throws Exception {
        FeatureType schema = source.getSchema();
        String geomName = schema.getDefaultGeometry().getName();
        CoordinateReferenceSystem geomCRS = schema.getDefaultGeometry().getCoordinateSystem();
        ReferencedEnvelope dataBounds = bounds.transform( geomCRS, true ); // true = don't be strict

        FilterFactory ff = FilterFactoryFinder.createFilterFactory();
        GeometryFilter filter = ff.createGeometryFilter( FilterType.GEOMETRY_BBOX );
        filter.addLeftGeometry( ff.createAttributeExpression( geomName ));
        filter.addRightGeometry( ff.createBBoxExpression( dataBounds ));

        return filter;
    }
    /**
     * You can use the CQL utility class to quickly create a filter.
     * <p>
     * You probably want to look at the FeatureType to check out what attributes are available?
     * @return
     * @throws Exception
     */
    private Filter filter() throws Exception {
        Filter filter = CQL.toFilter("POPULATION <= 500000");
        return filter;
    }

    /**
     * You can use an Iterator to go through all the features - but remember to close it.
     * @return
     * @throws Exception
     */
    private int totalLength(FeatureCollection features) throws Exception {
        Iterator<Feature> i = features.iterator();
        int length = 0;
        try {
            while( i.hasNext() ){
                String id = "unknown";
                try {
                    Feature feature = i.next();
                    id = feature.getID();
                    Geometry geometry = feature.getDefaultGeometry();
                    length += geometry.getLength();
                }
                catch( Throwable t ){
                    System.out.println("Invalid geometry for "+id+":"+t );
                    // ignore and try the next one...
                }
            }
        }
        finally {
            features.close( i );
        }
        return length;
    }
    /**
     * You can use a visitor to go through the features with out all the fuss...
     * @return
     * @throws Exception
     */
    private ReferencedEnvelope totalBounds(FeatureCollection features, IProgressMonitor monitor) throws Exception {
        FeatureType schema = features.getSchema();
        CoordinateReferenceSystem dataCRS = schema.getDefaultGeometry().getCoordinateSystem();
        final ReferencedEnvelope bounds = new ReferencedEnvelope( dataCRS ); // empty bounds
        features.accepts( new FeatureVisitor(){
            public void visit( Feature feature ) {
                bounds.expandToInclude( feature.getBounds() );
            }
        }, null );
        return bounds;
    }
}
