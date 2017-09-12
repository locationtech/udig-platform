/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.internal.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;

import org.locationtech.udig.core.AbstractUdigUIPlugin;
import org.locationtech.udig.core.internal.ExtensionPointProcessor;
import org.locationtech.udig.core.internal.ExtensionPointUtil;
import org.locationtech.udig.internal.ui.operations.OperationMenuFactory;
import org.locationtech.udig.ui.MenuBuilder;
import org.locationtech.udig.ui.UDIGMenuBuilder;
import org.locationtech.udig.ui.internal.Messages;
import org.locationtech.udig.ui.preferences.PreferenceConstants;
import org.locationtech.udig.ui.preferences.RuntimeFieldEditor;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.Preferences;

import com.google.common.base.Function;
/**
 * The UiPlugin helps integrate uDig with your custom RCP application.
 * <p>
 * The UiPlugin actually contains a sample UDIGApplication (that is used
 * for demos), but the real intention here is to isolate all the code
 * needed for your own custom (or existing) RCP application.
 * @author Jody 
 */
public class UiPlugin extends AbstractUdigUIPlugin {

    /** Icons path (value "icons/") */
    public final static String ICONS_PATH = "icons/";//$NON-NLS-1$

    public final static String ID = "org.locationtech.udig.ui"; //$NON-NLS-1$

    public static final String DROP_ACTIONS_ID = ID + ".dropActions"; //$NON-NLS-1$

    public static final String DROP_TRANSFERS_ID = ID + ".dropTransfers"; //$NON-NLS-1$

    public static final String MAPPINGS_FILENAME = "about.mappings"; //$NON-NLS-1$

    private static final String UDIG_VERSION_KEY = "1"; //$NON-NLS-1$

    private static final String UDIG_PRODUCT_ID = "org.locationtech.udig.product"; //$NON-NLS-1$

    private URL iconsUrl;

    private OperationMenuFactory operationMenuFactory;
    private MenuBuilder menuBuilder;
    /**
     * Version of uDig as determined from the product bundle.
     * @see loadVersion()
     */
    private String version;
    
    private static UiPlugin INSTANCE;

    /**
     * The constructor.
     */
    public UiPlugin() {
        super();
        INSTANCE = this;
    }

    /**
     * This method is called upon plug-in activation
     * 
     * @param context
     * @throws Exception
     */
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        iconsUrl = context.getBundle().getEntry(ICONS_PATH);
        Authenticator.setDefault(new UDIGAuthenticator());
        /*
         * TODO Further code can nuke the previously set authenticator. Proper security access
         * should be configured to prevent this.
         */
        disableCerts();
        try {
            loadVersion();

            java.lang.System.setProperty("http.agent", "uDig " + getVersion() + " (http://udig.refractions.net)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            java.lang.System.setProperty("https.agent", "uDig " + getVersion() + " (http://udig.refractions.net)");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        } catch (Throwable e) {
            log("error determining version", e); //$NON-NLS-1$
        }
    }

    /**
     * this is completely temporary.  It allows SSL and HTTPS connections to just accept all certificates.
     */
    private static void disableCerts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted( java.security.cert.X509Certificate[] certs, String authType ) {
            }
            public void checkServerTrusted( java.security.cert.X509Certificate[] certs, String authType ) {
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL"); //$NON-NLS-1$
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }

        // Now you can access an https URL without having the certificate in the truststore
        try {
            URL url = new URL("https://hostname/index.html"); //$NON-NLS-1$
            url.toString();
        } catch (MalformedURLException e) {
        }
    }
    /**
     * This method hunts down the version recorded in the current product.
     * 
     * @throws IOException
     */
    private void loadVersion() {
        IProduct product = Platform.getProduct();
        if (product == null || !(UDIG_PRODUCT_ID.equals(product.getId()))) {
            // chances are someone is using the SDK with their own
            // application or product.
            String message = "Unable to parse version from about.mappings file. Defaulting to a blank string."; //$NON-NLS-1$
            this.getLog().log(new Status(IStatus.INFO, ID, 0, message, null));
            this.version = "";
            return;
        }
        Bundle pluginBundle = product.getDefiningBundle();

        URL mappingsURL = FileLocator.find(pluginBundle, new Path(MAPPINGS_FILENAME), null);
        if (mappingsURL != null) {
            try {
                mappingsURL = FileLocator.resolve(mappingsURL);
            } catch (IOException e) {
                mappingsURL = null;
                String message = "Unable to find " + mappingsURL + " Defaulting to a blank string."; //$NON-NLS-1$
                this.getLog().log(new Status(IStatus.ERROR, ID, 0, message, e));
            }
        }
        PropertyResourceBundle bundle = null;
        if (mappingsURL != null) {
            InputStream is = null;
            try {
                is = mappingsURL.openStream();
                bundle = new PropertyResourceBundle(is);
            } catch (IOException e) {
                bundle = null;
                String message = "Unable to parse version from about.mappings file. Defaulting to a blank string."; //$NON-NLS-1$
                this.getLog().log(new Status(IStatus.ERROR, ID, 0, message, e));
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                }
            }
        }

        if (bundle != null) {
            this.version = bundle.getString(UDIG_VERSION_KEY);
        }
    }
    /**
     * This is the version of uDig being deployed; obtained from the current product
     * if available.
     *
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     * Creates an image descriptor for later use.
     */
    synchronized ImageDescriptor create( String id ) {
        URL url = null;
        try {
            url = new URL(iconsUrl, id);
        } catch (MalformedURLException e) {
            // System.out.println( "Could not locate "+id );
            return null;
        }
        ImageDescriptor image = ImageDescriptor.createFromURL(url);
        getImageRegistry().put(id, image);
        return image;
    }

    /**
     * Returns the shared instance.
     */
    public static UiPlugin getDefault() {
        return INSTANCE;
    }

    public OperationMenuFactory getOperationMenuFactory() {
        if (operationMenuFactory == null) {
            operationMenuFactory = new OperationMenuFactory();
        }

        return operationMenuFactory;
    }

    /**
     * Logs the given throwable to the platform log, indicating the class and
     * method from where it is being logged (this is not necessarily where it
     * occurred).
     * 
     * This convenience method is for internal use by the Workbench only and
     * must not be called outside the Workbench.
     * 
     * @param clazz
     *            The calling class.
     * @param methodName
     *            The calling method name.
     * @param t
     *            The throwable from where the problem actually occurred.
     */
    public static void log( Class clazz, String methodName, Throwable t ) {
        String msg = MessageFormat.format("Exception in {0}.{1}: {2}", //$NON-NLS-1$
                new Object[]{clazz.getName(), methodName, t});
        log(msg, t);
    }
    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     */
    public static void log( String message2, Throwable e ) {
        String message = message2;
        if (message == null)
            message = ""; //$NON-NLS-1$
        getDefault().getLog().log(new Status(IStatus.INFO, ID, 0, message, e));
    }

    /**
     * Log the status to the default log.
     * @param status
     */
    public static void log( IStatus status ) {
        getDefault().getLog().log(status);
    }
    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much preferred to do this:<pre><code>
     * private static final String RENDERING = "org.locationtech.udig.project/render/trace";
     * if( ProjectUIPlugin.getDefault().isDebugging() && "true".equalsIgnoreCase( RENDERING ) ){
     *      System.out.println( "your message here" );
     * 
     */
    private static void trace( String message, Throwable e ) {
        if (getDefault().isDebugging()) {
            if (message != null){
                System.out.println(message); //$NON-NLS-1$
            }
            if (e != null){
                e.printStackTrace(System.out);
            }
        }
    }
    /**
     * Messages that only engage if getDefault().isDebugging() and the trace option traceID is true.
     * Available trace options can be found in the Trace class.  (They must also be part of the .options file) 
     */
    public static void trace( String traceID, Class< ? > caller, String message, Throwable e ) {
        if (isDebugging(traceID)) {
            trace(caller, message, e);
        }
    }

    /**
     * Adds the name of the caller class to the message. 
     *
     * @param caller class of the object doing the trace.
     * @param message tracing message, may be null.
     * @param e exception, may be null.
     */
    public static void trace( Class< ? > caller, String message, Throwable e ) {
        trace(caller.getSimpleName() + ": " + message, e); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Performs the Platform.getDebugOption true check on the provided trace
     * <p>
     * Note: ProjectUIPlugin.getDefault().isDebugging() must also be on.
     * <ul>
     * <li>Trace.RENDER - trace rendering progress
     * </ul>
     * </p>
     * 
     * @param trace currently only RENDER is defined
     */
    public static boolean isDebugging( final String trace ) {
        return getDefault().isDebugging() && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$    
    }
    /**
     * Get the MenuFactory which will create the menus for this plugin
     * 
     * @return The MenuFactory singleton
     */
    public MenuBuilder getMenuFactory() {
        if (menuBuilder == null) {
            menuBuilder = lookupMenuBuilder();
        }

        return menuBuilder;
    }

    private MenuBuilder lookupMenuBuilder() {

        Class interfaceClass = MenuBuilder.class;
        String prefConstant = PreferenceConstants.P_MENU_BUILDER;
        String xpid = MenuBuilder.XPID;
        String idField = MenuBuilder.ATTR_ID;
        String classField = MenuBuilder.ATTR_CLASS;

        MenuBuilder mb = (MenuBuilder) lookupConfigurationObject(interfaceClass, getPreferenceStore(), ID, prefConstant, xpid,
                idField, classField);
        if (mb != null) {
            return mb;
        }

        return new UDIGMenuBuilder();
    }

    /**
     * Returns the max heap size in MB
     */
    public static int getMaxHeapSize() throws IOException {
        final Pattern pattern = Pattern.compile("Xmx([0-9]+)([mMgGkKbB])");
        final int[] heapS = new int[1];
        processAppIni(true, new Function<String, String>(){

            public String apply( String line ) {
                if (line.matches(".*Xmx.*")) { //$NON-NLS-1$
                    Matcher matcher = pattern.matcher(line);
                    matcher.find();
                    int num = Integer.parseInt(matcher.group(1));
                    String unit = matcher.group(2).toLowerCase();
                    if (unit.equals("m")) { //$NON-NLS-1$
                        heapS[0] = num * 1;
                    } else if (unit.equals("g")) { //$NON-NLS-1$
                        heapS[0] = num * 1024;
                    } else if (unit.equals("k")) { //$NON-NLS-1$
                        heapS[0] = num / 1024;
                    } else if (unit.equals("b")) { //$NON-NLS-1$
                        heapS[0] = num / (1024 * 1024);
                    }
                }
                return line;
            }

        });

        return heapS[0];
    }

    /**
     * Sets the max heap size in the configuration file so on a restart the maximum size will be changed
     *
     * @param maxHeapSize new heapsize. 1024M 1G are legal options
     * @return the configFile to use for setting configuration information
     */
    public static void setMaxHeapSize( final String maxHeapSize ) throws FileNotFoundException, IOException {
        processAppIni(false, new Function<String, String>(){

            public String apply( String line ) {
                if (line.matches(".*Xmx([0-9]+)([mMgGkKbB]).*")) { //$NON-NLS-1$
                    line = line.replaceFirst("Xmx([0-9]+)([mMgGkKbB])", "Xmx" + maxHeapSize + "M"); //$NON-NLS-1$ //$NON-NLS-2$
                }
                return line;
            }

        });

    }
    
    /**
     * The quote used to delimit multiple proxies.
     */
    private static final String PROXYQUOTES = "'";

    /**
     * Sets the proxy.
     *
     * @param proxyHost the proxy server. If null disables proxy.
     * @param proxyPort the server port. If null disables proxy.
     * @param proxyNonHost the servers for which to bypass proxy.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void setProxy( String proxyHost, String proxyPort, String proxyNonHost ) throws FileNotFoundException,
            IOException {
        File iniFile = getIniFile();
        
        if (iniFile == null) {
                String message = ".ini file does not exist. Changes will not be saved."; //$NON-NLS-1$
                MessageDialog.openWarning(null, null, message);
                return;
        }
        
        BufferedReader bR = null;
        StringBuilder sB = new StringBuilder();
        try {
            bR = new BufferedReader(new FileReader(iniFile));
            String line = null;
            while( (line = bR.readLine()) != null ) {
                if (line.matches(".*Dhttp.proxy.*") || line.matches(".*Dhttp.nonProxy.*")) {
                    continue;
                }
                if (line.matches("")) {
                    continue;
                }
                sB.append(line).append("\n");
            }
        } finally {
            bR.close();
        }

        if (proxyHost != null && proxyHost.length() > 0 && proxyPort != null && proxyPort.length() > 0) {
            sB.append("-D" + RuntimeFieldEditor.PROXYHOST + "=").append(proxyHost).append("\n");
            sB.append("-D" + RuntimeFieldEditor.PROXYPORT + "=").append(proxyPort).append("\n");
            if (proxyNonHost != null && proxyNonHost.length() > 0) {
                // add quotes for multiple non proxy hosts
                proxyNonHost = PROXYQUOTES + proxyNonHost + PROXYQUOTES;
                sB.append("-D" + RuntimeFieldEditor.PROXYNONHOSTS + "=").append(proxyNonHost).append("\n");
            }
        }
        BufferedWriter bW = null;
        try {
            bW = new BufferedWriter(new FileWriter(iniFile));
            bW.write(sB.toString());
        } finally {
            bW.close();
        }

    }

    /**
     * Gets the proxy settings.
     *
     * @return a {@link Properties} containing the proxy settings.
     * @throws FileNotFoundException
     * @throws IOException
     */
    @SuppressWarnings("nls")
    public static Properties getProxySettings() throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        File iniFile = getIniFile();
        if (iniFile == null) {
            properties.put(RuntimeFieldEditor.PROXYHOST, System.getProperty(RuntimeFieldEditor.PROXYHOST));
            properties.put(RuntimeFieldEditor.PROXYPORT, System.getProperty(RuntimeFieldEditor.PROXYPORT));
            properties.put(RuntimeFieldEditor.PROXYNONHOSTS, System.getProperty(RuntimeFieldEditor.PROXYNONHOSTS));
        } else {
            BufferedReader bR = new BufferedReader(new FileReader(iniFile));
            String line = null;
            while( (line = bR.readLine()) != null ) {
                if (line.matches(".*D" + RuntimeFieldEditor.PROXYHOST + ".*")) {
                    String proxyHost = line.split("=")[1].trim();
                    properties.put(RuntimeFieldEditor.PROXYHOST, proxyHost);
                }
                if (line.matches(".*D" + RuntimeFieldEditor.PROXYPORT + ".*")) {
                    String proxyPort = line.split("=")[1].trim();
                    properties.put(RuntimeFieldEditor.PROXYPORT, proxyPort);
                }
                if (line.matches(".*D" + RuntimeFieldEditor.PROXYNONHOSTS + ".*")) {
                    String proxyNonHosts = line.split("=")[1].trim();
                    // remove quotes if there are
                    proxyNonHosts = proxyNonHosts.replaceAll(PROXYQUOTES, "");
                    properties.put(RuntimeFieldEditor.PROXYNONHOSTS, proxyNonHosts);
                }
            }
            bR.close();
        }

        return properties;
    }

    private static void processAppIni( boolean readOnly, Function<String, String> func ) throws IOException {
        File iniFile = getIniFile();
        if (iniFile != null && iniFile.exists()) {
            BufferedReader bR = null;
            BufferedWriter bW = null;
            try {
                Collection<String> updatedLines = new ArrayList<String>();
                bR = new BufferedReader(new FileReader(iniFile));
                String line = null;
                while( (line = bR.readLine()) != null ) {
                    String newLine = func.apply(line);
                    updatedLines.add(newLine);
                    updatedLines.add("\n"); //$NON-NLS-1$
                }
                if (!readOnly) {
                    bW = new BufferedWriter(new FileWriter(iniFile));
                    for( String string : updatedLines ) {
                        bW.write(string);
                    }
                }
            } finally {
                try {
                    if (bR != null)
                        bR.close();
                } finally {
                    if (bW != null)
                        bW.close();
                }
            }
        }

        if (!readOnly) {
            UiPlugin.log("udig.ini changed:" + iniFile, null);
        }
    }

    private static File getIniFile() {
        URL installLoc = Platform.getInstallLocation().getURL();
        File appFolder = new File(installLoc.getFile());
        String[] list = appFolder.list();
        String iniName = null;
        for( String l : list ) {
            if (l.endsWith(".ini")) { //$NON-NLS-1$
                iniName = l;
            }
        }
        if( iniName == null ){
            return null; // must be running from eclipse
        }
        File iniFile = new File(appFolder, iniName);
        return iniFile;
    }

    /**
     * Looks a configuration object using the preference store and extension 
     * points to locate the class and instantiate it. If there is a problem,
     * null is returned and the caller is expect to supply a default value of
     * their own. Exceptions are not thrown, but messages will be logged.
     * 
     * These configuration objects are typically defined in 
     * plugin_customization.ini files, and these values are loaded into the 
     * preference store. The parameter <tt>prefConstant</tt> is used to look
     * up this value, and should be the key (prefixed by the plug-in name,
     * org.locationtech.udig.ui) used in the ini file. 
     * 
     * The returned object will either be an instances of 
     * <tt>interfaceClass</tt> or <tt>null</tt>.
     * 
     * The parameter <tt>xpid</tt> is the extension point ID that the value
     * specified in the ini file should point to. This extension point must
     * contain an attribute used for an id, and an attribute used for the class
     * which is an implementation of <tt>interfaceClass</tt>. <tt>idField</tt>
     * indicates the name of the attribute for id, and <tt>classField</tt>
     * indicates the name of the attribute for the class.
     * 
     * Example:
     * plugin_customization.ini
     * <pre>
     * org.locationtech.udig.ui/workbenchConfiguration=org.locationtech.udig.internal.ui.UDIGWorkbenchConfiguration
     * </pre>
     * 
     * <b><tt>store</tt></b>: org.locationtech.udig.internal.ui.UiPlugin.getPreferenceStore() 
     * (this corresponds to the first part of the key)
     * 
     * <b><tt>pluginID</tt></b>: "org.locationtech.udig.ui"
     * 
     * <b><tt>prefConstant</tt></b>: "workbenchConfiguration"
     * 
     * 
     * <pre>
     *     <extension
     *       point="org.locationtech.udig.ui.workbenchConfigurations">
     *         <workbenchConfiguration
     *           class="org.locationtech.udig.internal.ui.UDIGWorkbenchConfiguration"
     *           id="org.locationtech.udig.internal.ui.UDIGWorkbenchConfiguration"/>
     *     </extension>
     * </pre>
     * 
     * <b><tt>xpid</tt></b>: "org.locationtech.udig.ui.workbenchConfigurations"
     * <b><tt>idField</tt></b>: "id"
     * <b><tt>classField</tt></b>: "class"
     * 
     * This will return an instance of <tt>org.locationtech.udig.ui.WorkbenchConfiguration</tt>,
     * or null if it cannot find one (in which case, check the logs!).
     * 
     * Make sure to be a good developer and use constants. Also make sure to 
     * use a default implementation if this returns null! The code should not
     * explode!
     * 
     * TODO It would be nice to simplify this API call.
     *
     * @param interfaceClass instance of the interface that will be instantiated and returned
     * @param store the preference store used to lookup prefConstant
     * @param pluginID the ID of the plug-in that the preference store lives
     * @param prefConstant key used in plugin_customization.ini
     * @param xpid extension point id key
     * @param idField id attribute key used in extension point
     * @param classField class attribute key used in extension point
     */
    public static Object lookupConfigurationObject( Class< ? > interfaceClass, final IPreferenceStore store,
            final String pluginID, final String prefConstant, final String xpid, final String idField, final String classField ) {

        final String configurationID = store.getString(prefConstant);

        if (configurationID != null && !configurationID.equals("")) {
            try {
                final Object[] configObj = new Object[1];
                final Throwable[] error = new Throwable[1];
                ExtensionPointProcessor p = new ExtensionPointProcessor(){

                    public void process( IExtension extension, IConfigurationElement element ) throws Exception {
                        try {
                            if (element.getAttribute(idField) != null && element.getAttribute(idField).equals(configurationID)) {
                                Object obj = element.createExecutableExtension(classField);
                                configObj[0] = obj;
                            }
                        } catch (Exception e) {
                            configObj[0] = null;
                            error[0] = e;
                        }
                    }

                };
                ExtensionPointUtil.process(getDefault(), xpid, p);

                if (configObj[0] != null) {
                    return configObj[0];
                } else {
                    MessageFormat format = new MessageFormat(Messages.UDIGWorkbenchWindowAdvisor_specifiedButNotFound);
                    Object[] args = new Object[]{configurationID, interfaceClass.getName()};
                    StringBuffer message = format.format(args, new StringBuffer(), null);
                    Throwable e = null;
                    if (error[0] != null) {
                        e = error[0];
                    }
                    trace(message.toString(), e);
                }
            } catch (Exception e) {
                log(MessageFormat.format(Messages.UDIGWorkbenchWindowAdvisor_classNotFound, new Object[]{configurationID},
                        interfaceClass.getName()), e);
            }
        }

        return null;
    }

    /**
     * Gets preferences that are user specific. You don't have to worry about the preferences
     * changes interfering with preferences of another user's workspace.
     * 
     * @return preferences that are user specific
     * @throws CoreException
     * @throws IOException
     */
    // public static synchronized IExportedPreferences getUserPreferences() throws CoreException,
    // IOException {
    // if (preferences == null) {
    //                    preferences=new UDIGExportedPreferences(getDefault().getPreferenceStore(), "^^preference^root^^"); //$NON-NLS-1$
    // }
    // return preferences;
    // }

    public static Preferences getUserPreferences() {
        return new InstanceScope().getNode(ID);
    }

	public IPath getIconPath() {
		return new Path(ICONS_PATH);
	}
}
