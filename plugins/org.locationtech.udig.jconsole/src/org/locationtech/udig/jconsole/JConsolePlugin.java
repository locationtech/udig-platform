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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import org.locationtech.udig.jconsole.java.JavaCodeScanner;
import org.locationtech.udig.jconsole.javadoc.JavaDocScanner;
import org.locationtech.udig.jconsole.util.JavaColorProvider;
import org.locationtech.udig.omsbox.core.FieldData;
import org.locationtech.udig.omsbox.core.ModuleDescription;
import org.locationtech.udig.omsbox.core.OmsModulesManager;

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

    public HashMap<String, List<ModuleDescription>> gatherModules() {
        // TODO chance if necessary
        HashMap<String, List<ModuleDescription>> availableModules = OmsModulesManager.getInstance().browseModules(false);
        return availableModules;
    }

    public String[] getModulesFieldsNames() {
        // TODO cache if necessary
        HashMap<String, List<ModuleDescription>> availableModules = gatherModules();
        List<String> names = new ArrayList<String>();

        Collection<List<ModuleDescription>> modulesDescriptions = availableModules.values();
        for( List<ModuleDescription> modulesDescriptionList : modulesDescriptions ) {
            for( ModuleDescription moduleDescription : modulesDescriptionList ) {
                List<FieldData> inputsList = moduleDescription.getInputsList();
                for( FieldData inFieldData : inputsList ) {
                    names.add(inFieldData.fieldName);
                }
                List<FieldData> outputsList = moduleDescription.getOutputsList();
                for( FieldData outFieldData : outputsList ) {
                    names.add(outFieldData.fieldName);
                }
            }
        }
        return names.toArray(new String[0]);
    }

    public LinkedHashMap<String, List<String>> modulesName2FieldsNames() {
        // TODO cache if necessary
        HashMap<String, List<ModuleDescription>> availableModules = gatherModules();

        LinkedHashMap<String, List<String>> modulesName2FieldsNames = new LinkedHashMap<String, List<String>>();

        Collection<List<ModuleDescription>> modulesDescriptions = availableModules.values();
        for( List<ModuleDescription> modulesDescriptionList : modulesDescriptions ) {
            for( ModuleDescription moduleDescription : modulesDescriptionList ) {
                String moduleName = moduleDescription.getName();

                List<String> names = new ArrayList<String>();
                List<FieldData> inputsList = moduleDescription.getInputsList();
                for( FieldData inFieldData : inputsList ) {
                    names.add(inFieldData.fieldName);
                }
                List<FieldData> outputsList = moduleDescription.getOutputsList();
                for( FieldData outFieldData : outputsList ) {
                    names.add(outFieldData.fieldName);
                }

                modulesName2FieldsNames.put(moduleName, names);

            }
        }
        return modulesName2FieldsNames;
    }

}
