/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.operation;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.catalog.util.GeoToolsAdapters;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.process.vector.TransformProcess;
import org.geotools.process.vector.TransformProcess.Definition;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.util.ProgressListener;

/**
 * This class pops up a dialog and asks the user to 
 * fiddle with the expressions used to create a
 * new temporary layer.
 * <p>
 * While the Dialog may be simple; this is a reasonable
 * example at how to force an operation that processes
 * data in a way that does not force all the data into
 * memory.
 * <p>
 * However the createTemporaryResource method may use a
 * MemoryDataStore depending on how your environment is
 * configured.
 * <p>
 * I would like to see this class changed to be a wizard,
 * with an option to export directly to shapefile
 * (rather than a temporary resource ).
 * <p>
 * @author Jody Garnett
 */
public class TransformOperation implements IOp {

    
    public void op(final Display display, Object target, final IProgressMonitor monitor)
            throws Exception {
        final IGeoResource handle = (IGeoResource) target;
        final SimpleFeatureSource featureSource = handle.resolve( SimpleFeatureSource.class, null);
        SimpleFeature feature;
        final FeatureCollection<SimpleFeatureType, SimpleFeature> collection = featureSource
                .getFeatures();
        FeatureIterator<SimpleFeature> iterator = collection.features();
        try {
            if (!iterator.hasNext()) {
                return; // no contents ... ignore
            }
            feature = iterator.next();
        } finally {
            iterator.close();
        }
        final SimpleFeature sample = feature;
        PlatformGIS.asyncInDisplayThread( new Runnable(){
            public void run() {
                final TransformDialog dialog = new TransformDialog( display.getActiveShell(), sample );
                int result = dialog.open();
                if( result == Window.CANCEL ){
                    return;
                }
                if( result == Window.OK){
                    try {
                        PlatformGIS.runBlockingOperation( new IRunnableWithProgress(){
                            public void run(IProgressMonitor monitor) {
                                try {
                                    final IGeoResource reshaped = process( featureSource, dialog, monitor );
                                    PlatformGIS.asyncInDisplayThread(new Runnable(){

                                        public void run() {
                                            dialog.executePostAction(handle, reshaped);
                                        }
                                        
                                    }, true);
                                } catch (IOException e) {
                                    throw new RuntimeException( e.getMessage(), e );
                                }
                            }
                        }, monitor );
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }               
            }
        }, true );
    }
    /** Called to do the actual processing once we have everything set up 
     * @return */
    public IGeoResource process(SimpleFeatureSource source, TransformDialog dialog, IProgressMonitor monitor ) throws IOException {
        if( monitor == null ) monitor = new NullProgressMonitor();
        
        monitor.beginTask(Messages.ReshapeOperation_task, 100 );

        List<Definition> transform = dialog.getTransform();
        TransformProcess process = new TransformProcess();
        DefaultTransaction transaction = new DefaultTransaction("Processing "+source.getName() ); //$NON-NLS-1$
        try {
            SimpleFeatureCollection collection = source.getFeatures();
            SimpleFeatureCollection output = process.executeList(collection, transform);

            
            final SimpleFeatureType featureType = output.getSchema();
            
            IGeoResource scratch = CatalogPlugin.getDefault().getLocalCatalog().createTemporaryResource( featureType );
            final SimpleFeatureStore store = scratch.resolve(SimpleFeatureStore.class, SubMonitor.convert(monitor,Messages.ReshapeOperation_createTempSpaceTask, 10));
            
            store.setTransaction( transaction );
            
            ProgressListener progessListener = GeoToolsAdapters.progress( SubMonitor.convert(monitor,"processing "+source.getName(), 90));         //$NON-NLS-1$

            output.accepts(new FeatureVisitor(){
                boolean warning = true;
                public void visit( Feature rawFeature ) {
                    SimpleFeature feature = (SimpleFeature) rawFeature;
                    String fid = feature.getID();
                    
                    try {
                        store.addFeatures(DataUtilities.collection(feature));
                    } catch (Throwable t) {
                        if (warning) {
                            System.out.println( "Process "+fid+":"+t);
                            t.printStackTrace();
                            warning = false;
                        }
                    }
                }
            }, progessListener );
            transaction.commit();
            
            // TODO: we need to show our new scratch feature in the catalog view?
            return scratch;
        }
        catch (RuntimeException huh){
            transaction.rollback();
            huh.printStackTrace();
        }
        finally {
            monitor.done();
        }
        return null; // no result
    }
    
    static int count = 0;
    public static String getNewTypeName( String typeName ){
        return typeName + (count++);
    }
}
