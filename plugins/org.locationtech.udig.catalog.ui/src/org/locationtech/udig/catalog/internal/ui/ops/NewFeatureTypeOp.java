/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.ui.ops;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.ui.actions.ResetService;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.ui.FeatureTypeEditorDialog;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.FeatureTypeEditorDialog.ValidateFeatureType;
import org.locationtech.udig.ui.operations.IOp;

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
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.opengis.feature.simple.SimpleFeatureType;

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

        final SimpleFeatureType[] featureType = new SimpleFeatureType[1];
        if (!testing) {
            
            final FeatureTypeEditorDialog[] dialog=new FeatureTypeEditorDialog[1];
            PlatformGIS.syncInDisplayThread(new Runnable(){
                public void run() {
                    dialog[0] = new FeatureTypeEditorDialog(display
                            .getActiveShell(), new ValidateFeatureType(){

                                public String validate( SimpleFeatureType featureBuilder ) {
                                    return null;
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
                while( !Arrays.asList(ds.getTypeNames()).contains(featureType[0].getName().getLocalPart() ) && start+5000>System.currentTimeMillis()){
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
            SimpleFeatureType type, URL oldID ) throws MalformedURLException, IOException {
        File file;
        if (!oldID.getProtocol().equals("file")) { //$NON-NLS-1$
            try {
                file = new File(FileLocator.toFileURL(Platform.getInstanceLocation().getURL())
                        .getFile()
                        + type.getName().getLocalPart() + ".shp"); //$NON-NLS-1$
            } catch (IOException e) {
                file = new File(System.getProperty("java.user") + type.getName().getLocalPart() + ".shp"); //$NON-NLS-1$ //$NON-NLS-2$
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
            file = new File(s + type.getName().getLocalPart() + ".shp"); //$NON-NLS-1$
        }
        ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());
        params.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, true);
        
        DataStore ds = factory.createDataStore(params);
        ds.createSchema(type);
        List<IService> service = CatalogPlugin.getDefault().getServiceFactory().createService(
                file.toURI().toURL());
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
