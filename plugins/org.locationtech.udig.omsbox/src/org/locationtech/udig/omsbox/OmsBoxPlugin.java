/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.omsbox;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.mapgraphic.internal.MapGraphicService;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.ApplicationGIS;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.DataUtilities;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import org.locationtech.udig.omsbox.processingregion.ProcessingRegionMapGraphic;
import org.locationtech.udig.omsbox.utils.ImageCache;
import org.locationtech.udig.omsbox.utils.OmsBoxConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class OmsBoxPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.locationtech.udig.omsbox"; //$NON-NLS-1$

    // The shared instance
    private static OmsBoxPlugin plugin;

    /**
     * The constructor
     */
    public OmsBoxPlugin() {
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

        ImageCache.getInstance().dispose();
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static OmsBoxPlugin getDefault() {
        return plugin;
    }

    public static final String JARPATH_SPLITTER = "@@@"; //$NON-NLS-1$
    public static final String OMSBOX_LOADED_JARS_KEY = "OMSBOX_LOADED_JARS_KEY"; //$NON-NLS-1$

    /**
     * Gets the saved jars paths from the preferences, filtering those that still exist.
     * 
     * @return the existing jar from the preferences or <code>null</code>.
     */
    public String[] retrieveSavedJars() {
        IPreferenceStore preferenceStore = OmsBoxPlugin.getDefault().getPreferenceStore();
        String loadedJars = preferenceStore.getString(OMSBOX_LOADED_JARS_KEY);
        if (loadedJars != null && loadedJars.length() > 0) {
            String[] jarSplits = loadedJars.split(JARPATH_SPLITTER);

            // filter out only existing jars
            List<String> existingList = new ArrayList<String>();
            for( String jarPath : jarSplits ) {
                File f = new File(jarPath);
                if (f.exists()) {
                    existingList.add(jarPath);
                }
            }

            if (existingList.size() == 0) {
                return new String[0];
            }

            String[] existingJarsArray = (String[]) existingList.toArray(new String[existingList.size()]);
            return existingJarsArray;
        }
        return new String[0];
    }

    /**
     * Save a list of jar paths to the preferences.
     * 
     * @param jarsList the list of jars to save.
     */
    public void saveJars( List<String> jarsList ) {
        IPreferenceStore preferenceStore = OmsBoxPlugin.getDefault().getPreferenceStore();
        StringBuilder sb = new StringBuilder();
        for( String jarPath : jarsList ) {
            sb.append(JARPATH_SPLITTER);
            sb.append(jarPath);
        }
        String jarsPathPref = sb.toString().replaceFirst(JARPATH_SPLITTER, ""); //$NON-NLS-1$
        preferenceStore.putValue(OMSBOX_LOADED_JARS_KEY, jarsPathPref);
    }

    public static final String OMSBOX_RAM_KEY = "OMSBOX_RAM_KEY"; //$NON-NLS-1$


    public static final String OMSBOX_LOG_KEY = "OMSBOX_LOG_KEY"; //$NON-NLS-1$



    public static final String LAST_CHOSEN_FOLDER = "last_chosen_folder_key";

    /**
     * Utility method for file dialogs to retrieve the last folder.
     * 
     * @return the path to the last folder chosen or the home folder.
     */
    public String getLastFolderChosen() {
        IPreferenceStore store = getPreferenceStore();
        String lastFolder = store.getString(LAST_CHOSEN_FOLDER);

        if (lastFolder != null) {
            File f = new File(lastFolder);
            if (f.exists() && f.isDirectory()) {
                return lastFolder;
            }
            if (f.exists() && f.isFile()) {
                return f.getParent();
            }
        }

        return new File(System.getProperty("user.home")).getAbsolutePath();
    }

    /**
     * Utility method for file dialogs to set the last folder.
     * 
     * @param folderPath
     */
    public void setLastFolderChosen( String folderPath ) {
        IPreferenceStore store = getPreferenceStore();
        store.putValue(LAST_CHOSEN_FOLDER, folderPath);
    }

    /**
     * Get the folder named spatialtoolbox in the installation folder.
     * 
     * @return the folder or <code>null</code>.
     */
    public static File getExtraSpatialtoolboxLibsFolder() {
        Location installLocation = Platform.getInstallLocation();
        File installFolder = DataUtilities.urlToFile(installLocation.getURL());
        if (installFolder != null && installFolder.exists()) {
            File omsboxLibsFolder = new File(installFolder, "spatialtoolbox");
            if (omsboxLibsFolder.exists()) {
                return omsboxLibsFolder;
            }
        }
        return null;
    }





    private ILayer processingRegionLayer;

    /**
     * Looks up the ProcessingRegion decorator for the current Map.
     * <p>
     * You can use this to find the ProcessingRegion (which is storied
     * on the StyleBlackboard for this layer).
     * 
     * @return layer for processing region (if found);
     */
    public ILayer getProcessingRegionMapGraphic() {

        /*
         * load the right mapgraphic layer
         */
        try {
            List<IResolve> mapgraphics = CatalogPlugin.getDefault().getLocalCatalog().find(MapGraphicService.SERVICE_URL, null);
            List<IResolve> members = mapgraphics.get(0).members(null);
            for( IResolve resolve : members ) {
                if (resolve.canResolve(ProcessingRegionMapGraphic.class)) {
                    IMap activeMap = ApplicationGIS.getActiveMap();
                    List<ILayer> layers = activeMap.getMapLayers();
                    boolean isAlreadyLoaded = false;
                    for( ILayer layer : layers ) {
                        if (layer.hasResource(ProcessingRegionMapGraphic.class)) {
                            isAlreadyLoaded = true;
                            processingRegionLayer = layer;
                        }
                    }

                    if (!isAlreadyLoaded) {
                        List< ? extends ILayer> addedLayersToMap = ApplicationGIS.addLayersToMap(activeMap,
                                Collections.singletonList(resolve.resolve(IGeoResource.class, null)), layers.size());
                        for( ILayer l : addedLayersToMap ) {
                            IGeoResource geoResource = l.getGeoResource();
                            if (geoResource.canResolve(ProcessingRegionMapGraphic.class)) {
                                processingRegionLayer = l;
                            }
                        }
                    }
                    break;
                }
            }
            return processingRegionLayer;
        } catch (IOException eek) {
            IStatus status = new Status(IStatus.WARNING, PLUGIN_ID, "unable to locate processing region decorator", eek);
            getDefault().getLog().log(status);
            return null;
        }

    }

    private boolean doIgnoreProcessingRegion = true;
    public boolean doIgnoreProcessingRegion() {
        return doIgnoreProcessingRegion;
    }

    public void setDoIgnoreProcessingRegion( boolean doIgnoreProcessingRegion ) {
        this.doIgnoreProcessingRegion = doIgnoreProcessingRegion;
    }

    /**
     * Utility method to get the gisbase preference.
     * 
     * @return the gisbase string.
     */
    public String getGisbasePreference() {
        IPreferenceStore preferenceStore = OmsBoxPlugin.getDefault().getPreferenceStore();
        String savedGisbase = preferenceStore.getString(OmsBoxConstants.GRASS_ENVIRONMENT_GISBASE_KEY);
        return savedGisbase;
    }

    /**
     * Save the gisbase preference.
     * 
     * @param gisbase the gisbase to save.
     */
    public void setGisbasePreference( final String gisbase ) {
        IPreferenceStore preferenceStore = OmsBoxPlugin.getDefault().getPreferenceStore();
        preferenceStore.setValue(OmsBoxConstants.GRASS_ENVIRONMENT_GISBASE_KEY, gisbase);
    }

    /**
     * Utility method to get the shell preference.
     * 
     * @return the shell string.
     */
    public String getShellPreference() {
        IPreferenceStore preferenceStore = OmsBoxPlugin.getDefault().getPreferenceStore();
        String savedShell = preferenceStore.getString(OmsBoxConstants.GRASS_ENVIRONMENT_SHELL_KEY);
        return savedShell;
    }

    /**
     * Save the shell preference.
     * 
     * @param shell the shell to save.
     */
    public void setShellPreference( final String shell ) {
        IPreferenceStore preferenceStore = OmsBoxPlugin.getDefault().getPreferenceStore();
        preferenceStore.setValue(OmsBoxConstants.GRASS_ENVIRONMENT_SHELL_KEY, shell);
    }

    /**
     * Utility method to get the mapcalc history.
     * 
     * @return the mapcalc history, divided by @@@..
     */
    public String getMapcalcHistory() {
        IPreferenceStore preferenceStore = OmsBoxPlugin.getDefault().getPreferenceStore();
        String savedHistory = preferenceStore.getString(OmsBoxConstants.MAPCALCHISTORY_KEY);
        return savedHistory;
    }

    /**
     * Save the mapcalc history.
     * 
     * @param mapcalcHistory the mapcalc history to save.
     */
    public void setMapcalcHistory( final String mapcalcHistory ) {
        IPreferenceStore preferenceStore = OmsBoxPlugin.getDefault().getPreferenceStore();
        preferenceStore.setValue(OmsBoxConstants.MAPCALCHISTORY_KEY, mapcalcHistory);
    }

    /**
     * Getter for the internal config folder.
     * 
     * @return the path to the configuration folder.
     */
    public File getConfigurationsFolder() {
        return getStateLocation().toFile();
    }

    public static void log( String message ) {
        getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, message));
    }

    /**
     * @return the working folder set in the preferences.
     */
    public String getWorkingFolder() {
        IPreferenceStore preferenceStore = OmsBoxPlugin.getDefault().getPreferenceStore();
        String workingFolder = preferenceStore.getString(OmsBoxConstants.WORKINGFOLDER);
        if (workingFolder.length() == 0) {
            return null;
        }
        return workingFolder + "/";
    }

    public void setWorkingFolder( String path ) {
        IPreferenceStore preferenceStore = OmsBoxPlugin.getDefault().getPreferenceStore();
        preferenceStore.setValue(OmsBoxConstants.WORKINGFOLDER, path);
    }

}
