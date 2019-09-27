/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tool.edit;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureTypes;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IRepository;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.LayerFactory;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.operations.IOp;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;

/**
 * Cuts the polygons in layer 1 out of the polygons in layer 2.
 *  
 * @author jones
 * @since 1.1.0
 */
public class DifferenceOp implements IOp {

    @SuppressWarnings("unchecked")
    public void op( final Display display, Object target, IProgressMonitor monitor ) throws Exception {
        final ILayer[] layers=(ILayer[]) target;
        final int[] value=new int[1]; 
        final ILayer[] from=new ILayer[1];
        final ILayer[] diff=new ILayer[1];
        PlatformGIS.syncInDisplayThread(new Runnable(){
            public void run() {
                LayerSelection selection=new LayerSelection(display.getActiveShell(), layers);
                value[0]= selection.open();
                from[0]=selection.fromLayer; 
                diff[0]=selection.diffLayer; 
            }
        });
        if( value[0]==Window.CANCEL )
            return;
        ILayer fromLayer=from[0];
        ILayer diffLayer=diff[0];
        
        if( !fromLayer.hasResource(FeatureSource.class) ){
            MessageDialog.openError(display.getActiveShell(), Messages.differenceOp_inputError1, fromLayer.getName()+Messages.differenceOp_inputError2); 
            return;
        }
        if( !diffLayer.hasResource(FeatureSource.class) ){
            MessageDialog.openError(display.getActiveShell(), Messages.differenceOp_inputError1, diffLayer.getName()+Messages.differenceOp_inputError2);
            return;
        }
        
        ShapefileDataStoreFactory dsfac = new ShapefileDataStoreFactory();
        File tmp = File.createTempFile(layers[0].getName() + "_" + layers[1].getName() + "_diff", ".shp"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        DataStore ds = dsfac.createDataStore(tmp.toURL());
        final SimpleFeatureType newSchema = FeatureTypes.newFeatureType(
                fromLayer.getSchema().getAttributeDescriptors().toArray(
                        new AttributeDescriptor[0]), "diff"); //$NON-NLS-1$
        ds.createSchema(newSchema);
        
        final FeatureSource<SimpleFeatureType, SimpleFeature> fromSource = fromLayer.getResource(FeatureSource.class, monitor);
        final FeatureSource<SimpleFeatureType, SimpleFeature> diffSource = diffLayer.getResource(FeatureSource.class, monitor);
        if (isGeometryCollection(fromSource.getSchema().getGeometryDescriptor())) {
            MessageDialog.openError(display.getActiveShell(), Messages.differenceOp_inputError, fromLayer.getName() + Messages.differenceOp_multiGeoms);
            return;
        }
        if (isGeometryCollection(diffSource.getSchema().getGeometryDescriptor())) {
            MessageDialog.openError(display.getActiveShell(), Messages.differenceOp_inputError, fromLayer.getName() + Messages.differenceOp_multiGeoms);
            return;
        }
        
        final DefaultFeatureCollection diffFeatures = new DefaultFeatureCollection();
        diffFeatures.addAll(diffSource.getFeatures());
        
        FeatureStore<SimpleFeatureType, SimpleFeature> destStore = (FeatureStore<SimpleFeatureType, SimpleFeature>)ds.getFeatureSource("diff"); //$NON-NLS-1$
        
        // TODO: figure out whatever this FeatureReader is doing; and make it a feature collection instead
        destStore.addFeatures(DataUtilities.collection(new FeatureReader<SimpleFeatureType, SimpleFeature>(){
        // TODO this needs an undo
//        ((FeatureStore<SimpleFeatureType, SimpleFeature>)fromSource).setFeatures(new FeatureReader() {
        	FeatureCollection<SimpleFeatureType, SimpleFeature> coll = fromSource.getFeatures();
            FeatureIterator<SimpleFeature> iter = coll.features();
            FeatureIterator<SimpleFeature> peek = coll.features();
            boolean hasNextCalled = false;
            
            public SimpleFeatureType getFeatureType() {
                return newSchema;
            }
            
            private Geometry diff(SimpleFeature f) {
                Geometry geom = (Geometry) f.getDefaultGeometry();
                FeatureIterator<SimpleFeature> i = diffFeatures.features();
                try {
                    while (i.hasNext()) {
                        SimpleFeature diff = i.next();
                        Geometry g = geom.difference((Geometry) diff.getDefaultGeometry());
                        if (g.isEmpty()) {
                            return null;
                        }
                        geom = g;
                    }
                } finally {
                    i.close();
                }
                return geom;
            }
            
            public SimpleFeature next() throws IOException, IllegalAttributeException, NoSuchElementException {
                SimpleFeature source=iter.next();
                Geometry geom = diff(source);
                if (geom == null || !hasNextCalled) {
                    throw new NoSuchElementException("Use hasNext()."); //$NON-NLS-1$
                }
                
                if (geom instanceof LineString) {
                    geom = geom.getFactory().createMultiLineString(new LineString[] {(LineString) geom});
                }
                if (geom instanceof Polygon) {
                    geom = geom.getFactory().createMultiPolygon(new Polygon[]{(Polygon) geom});
                }
                source.setDefaultGeometry(geom);
                
                hasNextCalled = false;
                return source;
            }

            public boolean hasNext() throws IOException {
                if (hasNextCalled) {
                    return iter.hasNext();
                }

                // pointer chase forward to the next different geometry
                try {
                    Geometry g = null;
                    while (g == null) {
                        if (!peek.hasNext()) {
                            return false;
                        }
                        SimpleFeature f = peek.next();
                        g = diff(f);
                        
                        if (g == null) {
                            iter.next();
                        } else {
                            return true;
                        }
                    } 
                } finally {
                    hasNextCalled = true;
                }
                return false;
            }

            public void close() throws IOException {
                iter.close();
                peek.close();
            }
        }));
        
        // add the diff shapefile as a udig resource
        URL url = tmp.toURI().toURL();
        IRepository local = CatalogPlugin.getDefault().getLocal();
        IService service = local.acquire( url, null );
        
        // List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(tmp.toURL());
        // IService service = services.get(0);
        // CatalogPlugin.getDefault().getLocalCatalog().add(service);
        List< ? extends IGeoResource> resources = service.resources(null);
        
        IGeoResource resource = resources.get(0);
        
        Map map = ((Map)layers[0].getMap());
        LayerFactory factory = map.getLayerFactory();
        Layer outLayer = factory.createLayer(resource);
        map.getLayersInternal().add(outLayer);
    }

    static class LayerSelection extends Dialog{

        private ILayer[] layers;
        Combo fromCombo;
        Combo diffCombo;
        ILayer fromLayer;
        ILayer diffLayer;

        protected LayerSelection( Shell parentShell, ILayer[] layers ) {
            super(parentShell);
            this.layers=layers;
            fromLayer=layers[0];
            diffLayer=layers[1];
        }
        
        
        @Override
        protected Control createDialogArea( Composite parent ) {
            Composite comp= (Composite) super.createDialogArea(parent);
            Composite c=new Composite(comp, SWT.NONE);
            c.setLayout(new GridLayout(2,true));

            String[] names=new String[]{
                    layers[0].getName(),
                    layers[1].getName()
            };
            
            Label layer2=new Label(c, SWT.NONE);
            layer2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            layer2.setText("Subtract: "); //$NON-NLS-1$
            
            diffCombo=new Combo(c, SWT.DEFAULT);
            diffCombo.setLayoutData(new GridData(GridData.FILL_BOTH));
            diffCombo.setItems(names);
            diffCombo.select(1);
            
            Label layer1=new Label(c, SWT.NONE);
            layer1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            layer1.setText("From: "); //$NON-NLS-1$
            
            fromCombo=new Combo(c, SWT.DEFAULT);
            fromCombo.setLayoutData(new GridData(GridData.FILL_BOTH));
            fromCombo.setItems(names);
            fromCombo.select(0);
            
            diffCombo.addSelectionListener(new SelectionListener(){

                public void widgetSelected( SelectionEvent e ) {
                    widgetDefaultSelected(e);
                }

                public void widgetDefaultSelected( SelectionEvent e ) {
                    diffLayer=layers[diffCombo.getSelectionIndex()];
                }
                
            });
            
            fromCombo.addSelectionListener(new SelectionListener(){

                public void widgetSelected( SelectionEvent e ) {
                    widgetDefaultSelected(e);
                }

                public void widgetDefaultSelected( SelectionEvent e ) {
                    fromLayer=layers[fromCombo.getSelectionIndex()];
                }
                
            });
            return c;
        }
    }

    // this is lifted from Geometry, where it's a protected method.
    protected boolean isGeometryCollection(GeometryDescriptor g) {
        // Don't use instanceof because we want to allow subclasses
        return g.getClass().getName().equals("org.locationtech.jts.geom.GeometryCollection"); //$NON-NLS-1$
    }
}
