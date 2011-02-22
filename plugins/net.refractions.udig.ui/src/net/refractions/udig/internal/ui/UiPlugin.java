package net.refractions.udig.internal.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.PropertyResourceBundle;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;
import net.refractions.udig.internal.ui.operations.OperationMenuFactory;
import net.refractions.udig.ui.internal.Messages;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.Preferences;

/**
 * The main plugin class to be used in the desktop.
 */
public class UiPlugin extends AbstractUIPlugin  {
    // The shared instance.
    private static UiPlugin plugin;

    /** Icons path (value "icons/") */
    public final static String ICONS_PATH = "icons/";//$NON-NLS-1$

    public final static String ID = "net.refractions.udig.ui"; //$NON-NLS-1$

    public static final String DROP_ACTIONS_ID = ID + ".dropActions"; //$NON-NLS-1$

    public static final String DROP_TRANSFERS_ID = ID + ".dropTransfers"; //$NON-NLS-1$


	public static final String MAPPINGS_FILENAME="about.mappings"; //$NON-NLS-1$
	private static final String UDIG_VERSION_KEY = "1"; //$NON-NLS-1$
	private static final String UDIG_PRODUCT_ID = "net.refractions.udig.product";

    /** Managed Images instance */
    private Images images = new Images();

    private URL iconsUrl;

    private OperationMenuFactory operationMenuFactory;

	private String version;

    /**
     * The constructor.
     */
    public UiPlugin() {
        super();
        plugin = this;
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
        try{
            loadVersion();

            java.lang.System.setProperty("http.agent","uDig " + getVersion() + " (http://udig.refractions.net)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            java.lang.System.setProperty("https.agent","uDig " + getVersion() + " (http://udig.refractions.net)");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }catch(Throwable e){
            log("error determining version", e); //$NON-NLS-1$
        }
    }

    // this is completely temporary.  It allows SSL and HTTPS connections to just accept all certificates.
    private static void disableCerts() {
//      Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

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
	private void loadVersion() throws IOException {

        IProduct product = Platform.getProduct();
        if( product == null || !(UDIG_PRODUCT_ID.equals(product.getId()))){
            // chances are someone is using the SDK with their own
            // application or product.
            String message = "Unable to parse version from about.mappings file. Defaulting to a blank string."; //$NON-NLS-1$
            this.getLog().log(new Status(IStatus.INFO, ID, 0, message, null ));
            this.version = "";
            return;
        }
        Bundle pluginBundle = product.getDefiningBundle();

        URL mappingsURL = FileLocator.find(pluginBundle, new Path(MAPPINGS_FILENAME), null);
		if (mappingsURL != null)
			mappingsURL = FileLocator.resolve(mappingsURL);

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
        return plugin;
    }

    /**
     * Images instance for use with ImageConstants.
     *
     * @return Images for use with ImageConstants.
     */
    public Images getImages() {
        return images;
    }

    public OperationMenuFactory getOperationMenuFactory() {
        if (operationMenuFactory == null) {
            operationMenuFactory = new OperationMenuFactory();
        }

        return operationMenuFactory;
    }

    public void stop( BundleContext context ) throws Exception {
//        try{
////        if( preferences!=null )
////            preferences.flush();
//        }catch (Throwable e) {
//            log("Error saving preferences", e); //$NON-NLS-1$
//        }
        super.stop(context);

        plugin = null;
    }
    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     */
    public static void log( String message2, Throwable e ) {
        String message=message2;
        if (message == null)
            message = ""; //$NON-NLS-1$
        getDefault().getLog().log(new Status(IStatus.INFO, ID, 0, message, e));
    }
    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much prefered to do this:<pre><code>
     * private static final String RENDERING = "net.refractions.udig.project/render/trace";
     * if( ProjectUIPlugin.getDefault().isDebugging() && "true".equalsIgnoreCase( RENDERING ) ){
     *      System.out.println( "your message here" );
     *
     */
    private static void trace( String message, Throwable e ) {
        if (getDefault().isDebugging()) {
            if (message != null)
                System.out.println(message); //$NON-NLS-1$
            if (e != null)
                e.printStackTrace(System.out);
        }
    }
    /**
     * Messages that only engage if getDefault().isDebugging() and the trace option traceID is true.
     * Available trace options can be found in the Trace class.  (They must also be part of the .options file)
     */
    public static void trace( String traceID, Class<?> caller, String message, Throwable e ) {
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
    public static void trace( Class<?> caller, String message, Throwable e ) {
        trace("Tracing - "+caller.getSimpleName()+": "+message, e); //$NON-NLS-1$ //$NON-NLS-2$
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
        return getDefault().isDebugging()
                && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$
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
     * net.refractions.udig.ui) used in the ini file.
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
     * net.refractions.udig.ui/workbenchConfiguration=net.refractions.udig.internal.ui.UDIGWorkbenchConfiguration
     * </pre>
     *
     * <b><tt>store</tt></b>: net.refractions.udig.internal.ui.UiPlugin.getPreferenceStore()
     * (this corresponds to the first part of the key)
     *
     * <b><tt>pluginID</tt></b>: "net.refractions.udig.ui"
     *
     * <b><tt>prefConstant</tt></b>: "workbenchConfiguration"
     *
     *
     * <pre>
     *     <extension
     *       point="net.refractions.udig.ui.workbenchConfigurations">
     *         <workbenchConfiguration
     *           class="net.refractions.udig.internal.ui.UDIGWorkbenchConfiguration"
     *           id="net.refractions.udig.internal.ui.UDIGWorkbenchConfiguration"/>
     *     </extension>
     * </pre>
     *
     * <b><tt>xpid</tt></b>: "net.refractions.udig.ui.workbenchConfigurations"
     * <b><tt>idField</tt></b>: "id"
     * <b><tt>classField</tt></b>: "class"
     *
     * This will return an instance of <tt>net.refractions.udig.ui.WorkbenchConfiguration</tt>,
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
    public static Object lookupConfigurationObject(
            Class<?> interfaceClass,
            final IPreferenceStore store,
            final String pluginID,
            final String prefConstant,
            final String xpid,
            final String idField,
            final String classField ) {

        final String configurationID = store.getString(prefConstant);

        if (configurationID != null && !configurationID.equals("")) {
            try {
                final Object[] configObj = new Object[1];
                final Throwable[] error  = new Throwable[1];
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
                    MessageFormat format = new MessageFormat(
                            Messages.UDIGWorkbenchWindowAdvisor_specifiedButNotFound);
                    Object[] args = new Object[] {configurationID, interfaceClass.getName()};
                    StringBuffer message = format.format(args, new StringBuffer(), null);
                    Throwable e = null;
                    if (error[0] != null) {
                        e = error[0];
                    }
                    log(message.toString(), e);
                }
            } catch (Exception e) {
                log(
                        MessageFormat.format(Messages.UDIGWorkbenchWindowAdvisor_classNotFound,
                                new Object[] {configurationID}, interfaceClass.getName()),
                        e);
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
//    public static synchronized IExportedPreferences getUserPreferences() throws CoreException, IOException {
//                if (preferences == null) {
//                    preferences=new UDIGExportedPreferences(getDefault().getPreferenceStore(), "^^preference^root^^"); //$NON-NLS-1$
//                }
//        return preferences;
//    }

    public static Preferences getUserPreferences() {
        return new InstanceScope().getNode(ID);
    }
}
