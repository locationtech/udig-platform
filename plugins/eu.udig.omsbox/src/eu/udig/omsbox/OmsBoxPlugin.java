package eu.udig.omsbox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.libs.internal.Activator;
import net.refractions.udig.mapgraphic.internal.MapGraphicService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import eu.udig.omsbox.processingregion.ProcessingRegionMapGraphic;
import eu.udig.omsbox.utils.ImageCache;
import eu.udig.omsbox.utils.OmsBoxConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class OmsBoxPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "eu.udig.omsbox"; //$NON-NLS-1$

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

    /**
     * Utility method to get the heap memory from the prefs.
     * 
     * @return the heap memory last saved or 64 megabytes.
     */
    public int retrieveSavedHeap() {
        IPreferenceStore preferenceStore = OmsBoxPlugin.getDefault().getPreferenceStore();
        int savedRam = preferenceStore.getInt(OMSBOX_RAM_KEY);
        if (savedRam <= 0) {
            savedRam = 64;
        }
        return savedRam;
    }

    /**
     * Save the heap memory to the preferences.
     * 
     * @param mem the memory to save.
     */
    public void saveHeap( int mem ) {
        IPreferenceStore preferenceStore = OmsBoxPlugin.getDefault().getPreferenceStore();
        preferenceStore.setValue(OMSBOX_RAM_KEY, mem);
    }

    public static final String OMSBOX_LOG_KEY = "OMSBOX_LOG_KEY"; //$NON-NLS-1$

    /**
     * Utility method to get the log level from the prefs.
     * 
     * @return the log level string.
     */
    public String retrieveSavedLogLevel() {
        IPreferenceStore preferenceStore = OmsBoxPlugin.getDefault().getPreferenceStore();
        String savedLogLevel = preferenceStore.getString(OMSBOX_LOG_KEY);
        if (savedLogLevel.length() == 0) {
            savedLogLevel = "OFF";
        }
        return savedLogLevel;
    }

    /**
     * Save the log level to the preferences.
     * 
     * @param logLevel the log level to save.
     */
    public void saveLogLevel( final String logLevel ) {
        IPreferenceStore preferenceStore = OmsBoxPlugin.getDefault().getPreferenceStore();
        preferenceStore.setValue(OMSBOX_LOG_KEY, logLevel);
    }

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

    public String getClasspathJars() throws Exception {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(".");
            sb.append(File.pathSeparator);

            String sysClassPath = System.getProperty("java.class.path");
            if (sysClassPath != null && sysClassPath.length() > 0 && !sysClassPath.equals("null")) {
                sb.append(sysClassPath);
                sb.append(File.pathSeparator);
            }

            // add this plugins classes
            Bundle omsBundle = Platform.getBundle(OmsBoxPlugin.PLUGIN_ID);
            String pluginPath = getPath(omsBundle, "/");
            if (pluginPath != null) {
                sb.append(pluginPath);
                sb.append(File.pathSeparator);
                sb.append(pluginPath + File.separator + "bin");
            }
            // add udig libs
            Bundle udigLibsBundle = Platform.getBundle(Activator.ID);
            String udigLibsFolderPath = getPath(udigLibsBundle, "lib");
            if (udigLibsFolderPath != null) {
                sb.append(File.pathSeparator);
                sb.append(udigLibsFolderPath);
                sb.append(File.separator);
                sb.append("*");
            }

            // add this plugins libs
            String libsFolderPath = getPath(omsBundle, "libs");
            if (libsFolderPath   != null) {
                sb.append(File.pathSeparator);
                sb.append(libsFolderPath);
                sb.append(File.separator);
                sb.append("*");
            }

            // add loaded jars
            String[] retrieveSavedJars = retrieveSavedJars();
            for( String file : retrieveSavedJars ) {
                sb.append(File.pathSeparator);
                sb.append(file);
            }

            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private HashMap<String, Process> runningProcessesMap = new HashMap<String, Process>();

    private ILayer processingRegionLayer;
    public void addProcess( Process process, String id ) {
        Process tmp = runningProcessesMap.get(id);
        if (tmp != null) {
            tmp.destroy();
        }
        runningProcessesMap.put(id, process);
    }

    public void killProcess( String id ) {
        Process process = runningProcessesMap.get(id);
        if (process != null) {
            runningProcessesMap.remove(id);
            process.destroy();
        }
        // also cleanup if needed
        Set<Entry<String, Process>> entrySet = runningProcessesMap.entrySet();
        for( Entry<String, Process> entry : entrySet ) {
            if (entry.getValue() == null) {
                String key = entry.getKey();
                if (key != null)
                    runningProcessesMap.remove(key);
            }
        }
    }

    public HashMap<String, Process> getRunningProcessesMap() {
        return runningProcessesMap;
    }

    public void cleanProcess( String id ) {
        runningProcessesMap.remove(id);
    }

    private String getPath( Bundle omsBundle, String path ) throws IOException {
        URL entry = omsBundle.getEntry(path);
        if (entry == null) {
            return null;
        }
        URL resolvedURL = FileLocator.resolve(entry);
        File file = new File(resolvedURL.getFile());
        return file.getAbsolutePath();
    }

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
            IStatus status = new Status(IStatus.WARNING,PLUGIN_ID,"unable to locate processing region decorator", eek);
            getDefault().getLog().log( status );
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

}
