/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.jconsole;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.DataUtilities;
import org.locationtech.udig.jconsole.internal.JConsoleConstants;
import org.locationtech.udig.jconsole.java.JavaCodeScanner;
import org.locationtech.udig.jconsole.javadoc.JavaDocScanner;
import org.locationtech.udig.jconsole.util.JavaColorProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JConsolePlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.locationtech.udig.jconsole";

    public final static String JAVA_PARTITIONING = "__java_example_partitioning"; //$NON-NLS-1$

    public final static String LAST_FOLDER_KEY = "LAST_FOLDER_KEY"; //$NON-NLS-1$

    private JavaPartitionScanner fPartitionScanner;
    private JavaColorProvider fColorProvider;
    private JavaCodeScanner fCodeScanner;
    private JavaDocScanner fDocScanner;

    // The shared instance
    private static JConsolePlugin plugin;

    /**
     * The constructor
     */
    public JConsolePlugin() {
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
    public static JConsolePlugin getDefault() {
        return plugin;
    }

    /**
     * Return a scanner for creating Java partitions.
     *
     * @return a scanner for creating Java partitions
     */
    public JavaPartitionScanner getJavaPartitionScanner() {
        if (fPartitionScanner == null)
            fPartitionScanner = new JavaPartitionScanner();
        return fPartitionScanner;
    }

    /**
     * Returns the singleton Java code scanner.
     *
     * @return the singleton Java code scanner
     */
    public RuleBasedScanner getJavaCodeScanner() {
        if (fCodeScanner == null)
            fCodeScanner = new JavaCodeScanner(getJavaColorProvider());
        return fCodeScanner;
    }

    /**
     * Returns the singleton Java color provider.
     *
     * @return the singleton Java color provider
     */
    public JavaColorProvider getJavaColorProvider() {
        if (fColorProvider == null)
            fColorProvider = new JavaColorProvider();
        return fColorProvider;
    }

    /**
     * Returns the singleton Javadoc scanner.
     *
     * @return the singleton Javadoc scanner
     */
    public RuleBasedScanner getJavaDocScanner() {
        if (fDocScanner == null)
            fDocScanner = new JavaDocScanner(fColorProvider);
        return fDocScanner;
    }

    public File getLastOpenFolder() {
        IPreferenceStore preferenceStore = getPreferenceStore();
        String path = preferenceStore.getString(LAST_FOLDER_KEY);
        File file = new File(path);
        if (!file.exists()) {
            file = new File(System.getProperty("java.home"));
        }
        return file;
    }

    public void setLastOpenFolder( String path ) {
        IPreferenceStore preferenceStore = getPreferenceStore();
        preferenceStore.putValue(LAST_FOLDER_KEY, path);
    }

    private String loggerLevel = "OFF";
    public void setLoggerLevel( String level ) {
        loggerLevel = level;
    }
    public String getLoggerLevel() {
        return loggerLevel;
    }

    private String ram = "512";
    public void setRam( String ram ) {
        this.ram = ram;
    }
    public String getRam() {
        return ram;
    }
    
    private HashMap<String, Process> runningProcessesMap = new HashMap<String, Process>();

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

    /**
     * @return the java path used by the Application instance or "java".
     */
    public static String getApplicationJava() {
        String[] possibleJava = {"javaw.exe", "java.exe", "java"};
        Location installLocation = Platform.getInstallLocation();
        File installFolder = DataUtilities.urlToFile(installLocation.getURL());
        if (installFolder != null && installFolder.exists()) {
            File jreFolder = new File(installFolder, "jre/bin");
            if (jreFolder.exists()) {
                for( String pJava : possibleJava ) {
                    File java = new File(jreFolder, pJava);
                    if (java.exists()) {
                        return java.getAbsolutePath();
                    }
                }
            }
        }
        String jreDirectory = System.getProperty("java.home");
        File javaFolder = new File(jreDirectory, "jre/bin");
        if (javaFolder.exists()) {
            for( String pJava : possibleJava ) {
                File java = new File(javaFolder, pJava);
                if (java.exists()) {
                    return java.getAbsolutePath();
                }
            }
        }
        return "java";
    }

    public String getClasspathJars() throws Exception {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(".");
            sb.append(File.pathSeparator);

            String sysClassPath = System.getProperty("java.class.path");
            if (sysClassPath != null && sysClassPath.length() > 0 && !sysClassPath.equals("null")) {
                addPath(sysClassPath, sb);
                sb.append(File.pathSeparator);
            }

            // add this plugins classes
            // TODO : provide extension point to register bundle with additional jars
            /**
            Bundle omsBundle = Platform.getBundle(OmsBoxPlugin.PLUGIN_ID);
            String pluginPath = getPath(omsBundle, "/");
            if (pluginPath != null) {
                addPath(pluginPath, sb);
                sb.append(File.pathSeparator);
                addPath(pluginPath + File.separator + "bin", sb);
            }
            
            // add jars in the default folder
            File extraSpatialtoolboxLibsFolder = getExtraSpatialtoolboxLibsFolder();
            if (extraSpatialtoolboxLibsFolder != null) {
                File[] extraJars = extraSpatialtoolboxLibsFolder.listFiles(new FilenameFilter(){
                    public boolean accept( File dir, String name ) {
                        return name.endsWith(".jar");
                    }
                });
                for( File extraJar : extraJars ) {
                    sb.append(File.pathSeparator);
                    addPath(extraJar.getAbsolutePath(), sb);
                }
            }

            // add loaded jars
            String[] retrieveSavedJars = retrieveSavedJars();
            for( String file : retrieveSavedJars ) {
                sb.append(File.pathSeparator);
                addPath(file, sb);
            }

            **/
            // add udig libs
            Bundle udigLibsBundle = Platform.getBundle("org.locationtech.udig.libs");
            String udigLibsFolderPath = getPath(udigLibsBundle, "lib");
            if (udigLibsFolderPath != null) {
                sb.append(File.pathSeparator);
                addPath(udigLibsFolderPath + File.separator + "*", sb);

                File libsPluginPath = new File(udigLibsFolderPath).getParentFile();
                File[] toolsJararray = libsPluginPath.listFiles(new FilenameFilter(){
                    public boolean accept( File dir, String name ) {
                        return name.startsWith("tools_") && name.endsWith(".jar");
                    }
                });
                if (toolsJararray.length == 1) {
                    sb.append(File.pathSeparator);
                    addPath(toolsJararray[0].getAbsolutePath(), sb);
                }
            }

            // add custom libs from plugins
            addCustomLibs(sb);

            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getPath( Bundle bundle, String path ) throws IOException {
        URL entry = bundle.getEntry(path);
        if (entry == null) {
            return null;
        }
        URL resolvedURL = FileLocator.resolve(entry);
        File file = new File(resolvedURL.getFile());
        return file.getAbsolutePath();
    }
    
    private void addPath( String path, StringBuilder sb ) throws IOException {
        // TODO use File.pathSeparatorChar here instead?
        sb.append("\"").append(path).append("\"");
        // sb.append(path);
    }

    /**
     * Adds custom libs from the plugins.
     * 
     * FIXME this should hopefully get better at some point. 
     * 
     * @throws IOException 
     */
    private void addCustomLibs( StringBuilder sb ) throws IOException {
        // add some extra jars that are locked inside some eclipse plugins
        Bundle log4jBundle = Platform.getBundle("org.apache.log4j");
        String log4jFolderPath = getPath(log4jBundle, "/");
        if (log4jFolderPath != null) {
            sb.append(File.pathSeparator);
            addPath(log4jFolderPath + File.separator + "*", sb);
        }
        Bundle itextBundle = Platform.getBundle("com.lowagie.text");
        
        String itextPath = getPath(itextBundle, "/");
        if (itextPath != null) {
            itextPath = itextPath.replaceAll("!", "");
            sb.append(File.pathSeparator);
            addPath(itextPath, sb);
        }
        
        Location installLocation = Platform.getInstallLocation();
        File installFolder = DataUtilities.urlToFile(installLocation.getURL());
        if (installFolder != null && installFolder.exists()) {
            File pluginsFolder = new File(installFolder, "plugins");
            if (pluginsFolder.exists()) {

                File[] files = pluginsFolder.listFiles(new FilenameFilter(){
                    public boolean accept( File dir, String name ) {
                        boolean isCommonsLog = name.startsWith("org.apache.commons.logging_") && name.endsWith(".jar");
                        return isCommonsLog;
                    }
                });
                if (files.length > 1) {
                    sb.append(File.pathSeparator);
                    addPath(files[0].getAbsolutePath(), sb);
                }
                files = pluginsFolder.listFiles(new FilenameFilter(){
                    public boolean accept( File dir, String name ) {
                        boolean isJunit = name.startsWith("junit") && name.endsWith(".jar");
                        return isJunit;
                    }
                });
                if (files.length > 1) {
                    sb.append(File.pathSeparator);
                    addPath(files[0].getAbsolutePath(), sb);
                }
            }
        }
    }

    /**
     * Utility method to get the heap memory from the prefs.
     * 
     * @return the heap memory last saved or 64 megabytes.
     */
    public int retrieveSavedHeap() {
        IPreferenceStore preferenceStore = JConsolePlugin.getDefault().getPreferenceStore();
        int savedRam = preferenceStore.getInt(JConsoleConstants.PREFSTORE_HEAP_KEY);
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
        IPreferenceStore preferenceStore = JConsolePlugin.getDefault().getPreferenceStore();
        preferenceStore.setValue(JConsoleConstants.PREFSTORE_HEAP_KEY, mem);
    }
    
    /**
     * Utility method to get the log level from the prefs.
     * 
     * @return the log level string.
     */
    public String retrieveSavedLogLevel() {
        IPreferenceStore preferenceStore = JConsolePlugin.getDefault().getPreferenceStore();
        String savedLogLevel = preferenceStore.getString(JConsoleConstants.PREFSTORE_LOG_KEY);
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
        IPreferenceStore preferenceStore = JConsolePlugin.getDefault().getPreferenceStore();
        preferenceStore.setValue(JConsoleConstants.PREFSTORE_LOG_KEY, logLevel);
    }
}
