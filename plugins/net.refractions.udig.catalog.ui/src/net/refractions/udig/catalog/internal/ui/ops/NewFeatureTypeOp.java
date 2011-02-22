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
package net.refractions.udig.catalog.internal.ui.ops;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.ui.actions.ResetService;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.ui.FeatureTypeEditorDialog;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.FeatureTypeEditorDialog.ValidateFeatureTypeBuilder;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.indexed.IndexedShapefileDataStoreFactory;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;

/**
 * Creates a new feature type in the selected service. The user is queried to define the feature
 * type.
 *
 * @author jones
 * @since 1.1.0
 */
public class NewFeatureTypeOp implements IOp {

    private boolean testing;
    private boolean error = false;

    public void op( final Display display, final Object target, final IProgressMonitor monitor )
            throws Exception {
        final IService service = (IService) target;
        final DataStore ds = service.resolve(DataStore.class, monitor);
        if (!(ds instanceof ShapefileDataStore)) {
            try {
                ds.createSchema(null);
            } catch (UnsupportedOperationException e) {
                if (testing) {
                    error = true;
                    return;
                } else {
                    display.asyncExec(new Runnable(){
                        public void run() {
                            MessageDialog
                                    .openInformation(display.getActiveShell(),
                                            Messages.NewFeatureTypeOp_title,
                                            Messages.NewFeatureTypeOp_message);
                        }
                    });
                    return;
                }
            } catch (Exception e) {
                // try it
            }
        }

        final FeatureType[] featureType = new FeatureType[1];
        if (!testing) {

            final FeatureTypeEditorDialog[] dialog=new FeatureTypeEditorDialog[1];
            PlatformGIS.syncInDisplayThread(new Runnable(){
                public void run() {
                    dialog[0] = new FeatureTypeEditorDialog(display
                            .getActiveShell(), new ValidateFeatureTypeBuilder(){

                                public boolean validate( FeatureTypeBuilder featureBuilder ) {
                                    return true;
                                }

                    });
                }
            });
            int code=-1;
            do {
                code=openDialog(display, dialog[0], ds);
                if( code==Window.CANCEL){
                    featureType[0]=null;
                }else
                    featureType[0]=dialog[0].getFeatureType(true);
            } while( featureType[0] == null && code==Window.OK);
        } else {
            featureType[0] = DataUtilities.createType("TestName", "Geom:MultiLineString"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (featureType[0] == null)
            return;

        try {

            if (ds instanceof ShapefileDataStore) {
                createShapefile(display, monitor, featureType[0], service.getIdentifier());
            } else {
                ds.createSchema(featureType[0]);
                long start=System.currentTimeMillis();
                while( !Arrays.asList(ds.getTypeNames()).contains(featureType[0].getTypeName() ) && start+5000>System.currentTimeMillis()){
                    Thread.sleep(300);
                }

                ResetService.reset(Collections.singletonList(service), new SubProgressMonitor(monitor, 2));
            }
        } catch (IOException e) {
            CatalogUIPlugin.log("Error creating feature type in datastore: "+ds.getClass(), e); //$NON-NLS-1$
            display.asyncExec(new Runnable(){
                public void run() {
                    MessageDialog.openError(display.getActiveShell(), Messages.NewFeatureTypeOp_0,
                            Messages.NewFeatureTypeOp_1 +
                            Messages.NewFeatureTypeOp_2+ds.getClass().getSimpleName());
                }
            });
            return;
        }

    }

    private void createShapefile( final Display display, IProgressMonitor monitor,
            FeatureType type, URL oldID ) throws MalformedURLException, IOException {
        File file;
        if (!oldID.getProtocol().equals("file")) { //$NON-NLS-1$
            try {
                file = new File(FileLocator.toFileURL(Platform.getInstanceLocation().getURL())
                        .getFile()
                        + type.getTypeName() + ".shp"); //$NON-NLS-1$
            } catch (IOException e) {
                file = new File(System.getProperty("java.user") + type.getTypeName() + ".shp"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            final File f = file;
            if (!testing) {
                display.asyncExec(new Runnable(){
                    public void run() {
                        MessageDialog.openInformation(display.getActiveShell(),
                                Messages.NewFeatureTypeOp_shpTitle,
                                Messages.NewFeatureTypeOp_shpMessage
                                        + f.toString());
                    }
                });
            }
        } else {
            String s = new File(oldID.getFile()).toString();
            int lastIndexOf = s.lastIndexOf(".shp"); //$NON-NLS-1$
            s = s.substring(0, lastIndexOf == -1 ? s.length() : lastIndexOf + 1);
            lastIndexOf = s.lastIndexOf(File.separator);
            s = s.substring(0, lastIndexOf == -1 ? s.length() : lastIndexOf + 1);
            file = new File(s + type.getTypeName() + ".shp"); //$NON-NLS-1$
        }
        IndexedShapefileDataStoreFactory factory = new IndexedShapefileDataStoreFactory();
        DataStore ds = factory.createDataStore(file.toURL());
        ds.createSchema(type);
        List<IService> service = CatalogPlugin.getDefault().getServiceFactory().createService(
                file.toURL());
        for( IService service2 : service ) {
            try {
                if (service2.resolve(DataStore.class, monitor) instanceof ShapefileDataStore)
                    CatalogPlugin.getDefault().getLocalCatalog().add(service2);
            } catch (Exception e) {
                continue;
            }
        }
    }

    private int openDialog( final Display display, final FeatureTypeEditorDialog dialog, final DataStore dataStore ) {
        final int[] code=new int[1];
        PlatformGIS.syncInDisplayThread(new Runnable(){

            public void run() {
                dialog.setDataStore(dataStore);
                dialog.setBlockOnOpen(true);
                code[0]=dialog.open();
            }

        });
        return code[0];
    }

    /**
     * only for testing
     */
    public void testingSetTesting( boolean b ) {
        testing = b;
    }
    /**
     * only for testing
     */
    public boolean testingIsError() {
        return error;
    }
}
