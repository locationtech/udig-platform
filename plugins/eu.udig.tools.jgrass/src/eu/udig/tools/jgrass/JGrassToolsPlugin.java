/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.tools.jgrass;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.ExceptionDetailsDialog;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JGrassToolsPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "eu.udig.tools.jgrass"; //$NON-NLS-1$

    // The shared instance
    private static JGrassToolsPlugin plugin;

    /**
     * The constructor
     */
    public JGrassToolsPlugin() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static JGrassToolsPlugin getDefault() {
        return plugin;
    }

    /**
     * Logs the Throwable in the plugin's log.
     * <p>
     * This will be a user visable ERROR if:
     * <ul>
     * <li>t is an Exception we are assuming it is human readable or if a message is provided
     */
    public static void log( String message2, Throwable t ) {
        if (getDefault() == null) {
            t.printStackTrace();
            return;
        }
        String message = message2;
        if (message == null)
            message = ""; //$NON-NLS-1$
        int status = t instanceof Exception || message != null ? IStatus.ERROR : IStatus.WARNING;
        getDefault().getLog().log(new Status(status, PLUGIN_ID, IStatus.OK, message, t));
    }

    /**
     * @param outputFile
     * @param addToCatalog
     * @param addToActiveMap
     * @param progressMonitor
     */
    public void addServiceToCatalogAndMap( String outputFile, boolean addToCatalog, boolean addToActiveMap,
            IProgressMonitor progressMonitor ) {
        try {
            URL fileUrl = new File(outputFile).toURI().toURL();
            if (addToCatalog) {
                IServiceFactory sFactory = CatalogPlugin.getDefault().getServiceFactory();
                ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
                List<IService> services = sFactory.createService(fileUrl);
                for( IService service : services ) {
                    catalog.add(service);
                    if (addToActiveMap) {
                        IMap activeMap = ApplicationGIS.getActiveMap();
                        int layerNum = activeMap.getMapLayers().size();
                        List<IResolve> members = service.members(progressMonitor);
                        for( IResolve iRes : members ) {
                            if (iRes.canResolve(IGeoResource.class)) {
                                IGeoResource geoResource = iRes.resolve(IGeoResource.class, progressMonitor);
                                ApplicationGIS.addLayersToMap(null, Collections.singletonList(geoResource), layerNum);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            String message = "An error occurred while adding the service to the catalog.";
            ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, PLUGIN_ID, e);
            e.printStackTrace();
        }
    }

    /**
     * Writes a featurecollection to a shapefile
     * 
     * @param dataStore the datastore
     * @param collection the featurecollection
     * @throws IOException 
     */
    public void writeToShapefile( ShapefileDataStore dataStore, FeatureCollection<SimpleFeatureType, SimpleFeature> collection )
            throws IOException {
        String featureName = dataStore.getTypeNames()[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = dataStore.getFeatureSource(featureName);

        Transaction transaction = new DefaultTransaction("create");
        try {
            FeatureStore<SimpleFeatureType, SimpleFeature> featureStore = (FeatureStore<SimpleFeatureType, SimpleFeature>) featureSource;
            featureStore.setTransaction(transaction);
            featureStore.addFeatures(collection);
            transaction.commit();
        } catch (Exception eek) {
            transaction.rollback();
            throw new IOException("The transaction could now be finished, an error orrcurred", eek);
        } finally {
            transaction.close();
        }
    }
}
