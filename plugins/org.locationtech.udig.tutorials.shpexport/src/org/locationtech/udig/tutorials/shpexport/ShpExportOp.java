/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.shpexport;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.operations.IOp;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class ShpExportOp implements IOp {

    @Override
    public void op(Display display, Object target, IProgressMonitor monitor) throws Exception {
        FeatureSource<SimpleFeatureType, SimpleFeature> source = (FeatureSource<SimpleFeatureType, SimpleFeature>) target;

        SimpleFeatureType featureType = source.getSchema();
        GeometryDescriptor geometryType = featureType.getGeometryDescriptor();
        CoordinateReferenceSystem crs = geometryType.getCoordinateReferenceSystem();

        String typeName = featureType.getTypeName();

        String filename = typeName.replace(':', '_');
        URL directory = FileLocator.toFileURL(Platform.getInstanceLocation().getURL());
        URL shpURL = new URL(directory.toExternalForm() + filename + ".shp"); //$NON-NLS-1$
        final File file = new File(shpURL.toURI());

        // promptOverwrite( file )
        if (file.exists()) {
            return;
        }

        // create and write the new shapefile
        ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();
        Map params = new HashMap();
        params.put("url", file.toURI().toURL());
        ShapefileDataStore dataStore = (ShapefileDataStore) factory.createNewDataStore(params);
        dataStore.createSchema(featureType);

        FeatureStore store = (FeatureStore) dataStore.getFeatureSource();
        store.addFeatures(source.getFeatures());
        dataStore.forceSchemaCRS(crs);
    }

    private void promptOverwrite(final Display display, final File file) {
        if (!file.exists())
            return;

        display.syncExec(new Runnable() {
            @Override
            public void run() {
                boolean overwrite = MessageDialog.openConfirm(display.getActiveShell(), "Warning",
                        "File Exists do you wish to overwrite?");
                if (overwrite) {
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
    private String promptSaveDialog(final String typeName) {
        final String filename = typeName.replace(':', '_');
        final String[] result = new String[1];

        PlatformGIS.syncInDisplayThread(new Runnable() {
            @Override
            public void run() {
                Display display = Display.getCurrent();
                FileDialog dialog = new FileDialog(display.getActiveShell(), SWT.SAVE);

                dialog.setFileName(filename + ".shp"); //$NON-NLS-1$
                dialog.setText("Export " + typeName);
                dialog.setFilterExtensions(new String[] { "shp", "SHP" }); //$NON-NLS-1$ //$NON-NLS-2$
                result[0] = dialog.open();
            }
        });

        return result[0];
    }
}
