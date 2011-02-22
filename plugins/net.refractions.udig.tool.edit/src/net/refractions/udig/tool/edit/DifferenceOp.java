/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tool.edit;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.LayerFactory;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.operations.IOp;

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
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.shapefile.indexed.IndexedShapefileDataStoreFactory;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypes;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.feature.IllegalAttributeException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

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

        IndexedShapefileDataStoreFactory dsfac = new IndexedShapefileDataStoreFactory();
        File tmp = File.createTempFile(layers[0].getName() + "_" + layers[1].getName() + "_diff", ".shp"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        DataStore ds = dsfac.createDataStore(tmp.toURL());
        final FeatureType newSchema = FeatureTypes.newFeatureType(fromLayer.getSchema().getAttributeTypes(), "diff"); //$NON-NLS-1$
        ds.createSchema(newSchema);

        final FeatureSource fromSource = fromLayer.getResource(FeatureSource.class, monitor);
        final FeatureSource diffSource = diffLayer.getResource(FeatureSource.class, monitor);
        if (isGeometryCollection(fromSource.getSchema().getDefaultGeometry())) {
            MessageDialog.openError(display.getActiveShell(), Messages.differenceOp_inputError, fromLayer.getName() + Messages.differenceOp_multiGeoms);
            return;
        }
        if (isGeometryCollection(diffSource.getSchema().getDefaultGeometry())) {
            MessageDialog.openError(display.getActiveShell(), Messages.differenceOp_inputError, fromLayer.getName() + Messages.differenceOp_multiGeoms);
            return;
        }

        final FeatureCollection diffFeatures = FeatureCollections.newCollection();
        diffFeatures.addAll(diffSource.getFeatures());

        FeatureStore destStore = (FeatureStore)ds.getFeatureSource("diff"); //$NON-NLS-1$
        destStore.addFeatures(new FeatureReader(){
        // TODO this needs an undo
//        ((FeatureStore)fromSource).setFeatures(new FeatureReader() {
            FeatureCollection coll = fromSource.getFeatures();
            FeatureIterator iter = coll.features();
            FeatureIterator peek = coll.features();
            boolean hasNextCalled = false;

            public FeatureType getFeatureType() {
                return newSchema;
            }

            private Geometry diff(Feature f) {
                Geometry geom = f.getDefaultGeometry();
                FeatureIterator i = diffFeatures.features();
                try {
                    while (i.hasNext()) {
                        Feature diff = i.next();
                        Geometry g = geom.difference(diff.getDefaultGeometry());
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

            public Feature next() throws IOException, IllegalAttributeException, NoSuchElementException {
                Feature source=iter.next();
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
                        Feature f = peek.next();
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
        });

        // add the diff shapefile as a udig resource
        List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(tmp.toURL());

        IService service = services.get(0);
        CatalogPlugin.getDefault().getLocalCatalog().add(service);
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
    protected boolean isGeometryCollection(GeometryAttributeType g) {
        // Don't use instanceof because we want to allow subclasses
        return g.getClass().getName().equals("com.vividsolutions.jts.geom.GeometryCollection"); //$NON-NLS-1$
    }
}
