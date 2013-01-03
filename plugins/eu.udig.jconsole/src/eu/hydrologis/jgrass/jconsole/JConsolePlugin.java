package eu.hydrologis.jgrass.jconsole;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import eu.hydrologis.jgrass.jconsole.java.JavaCodeScanner;
import eu.hydrologis.jgrass.jconsole.javadoc.JavaDocScanner;
import eu.hydrologis.jgrass.jconsole.util.JavaColorProvider;
import eu.udig.omsbox.core.ModuleDescription;
import eu.udig.omsbox.core.OmsModulesManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class JConsolePlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "eu.hydrologis.jgrass.jconsole";

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
        HashMap<String, List<ModuleDescription>> availableModules = OmsModulesManager.getInstance().browseModules(false);
        return availableModules;
    }

}
