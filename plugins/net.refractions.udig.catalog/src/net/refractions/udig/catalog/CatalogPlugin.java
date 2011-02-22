package net.refractions.udig.catalog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.internal.Messages;
import net.refractions.udig.catalog.internal.ResolveManager;
import net.refractions.udig.catalog.internal.ServiceFactoryImpl;
import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;
import net.refractions.udig.ui.PreShutdownTask;
import net.refractions.udig.ui.ProgressManager;
import net.refractions.udig.ui.ShutdownTaskList;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

/**
 * The main plugin class to be used in the desktop.
 */
public class CatalogPlugin extends Plugin {
    public static final String ID = "net.refractions.udig.catalog"; //$NON-NLS-1$

    // The shared instance.
    private static CatalogPlugin plugin;

    // Resource bundle.
    private ResourceBundle resourceBundle;

    private ICatalog[] catalogs;
    private IServiceFactory serviceFactory;

    private IPreferenceStore preferenceStore;

    private volatile IResolveManager resolveManager;

    /**
     * The constructor. See {@link #start(BundleContext)} for the initialization of the plugin.
     */
    public CatalogPlugin() {
        super();
        plugin = this;
    }

    /**
     * This method is called upon plug-in activation
     */
    public void start( BundleContext context ) throws Exception {
        super.start(context);

        catalogs = new ICatalog[]{new CatalogImpl()};
        serviceFactory = new ServiceFactoryImpl();

        resolveManager = new ResolveManager();
        preferenceStore = new ScopedPreferenceStore(new InstanceScope(), getBundle()
                .getSymbolicName());
        try {
            plugin.restoreFromPreferences();
            addSaveLocalCatalogShutdownHook();
        } catch (BackingStoreException e) {
            CatalogPlugin.log(null, e);
            handlerLoadingError(e);
        } catch (MalformedURLException e) {
            CatalogPlugin.log(null, e);
            handlerLoadingError(e);
        }
    }

    private void addSaveLocalCatalogShutdownHook() {
        ShutdownTaskList.instance().addPreShutdownTask(new PreShutdownTask(){

            public int getProgressMonitorSteps() {
                try {
                    return getLocalCatalog().members(ProgressManager.instance().get()).size();
                } catch (IOException e) {
                    return 0;
                }
            }

            public boolean handlePreShutdownException( Throwable t, boolean forced ) {
                CatalogPlugin.log("Error storing local catalog", t); //$NON-NLS-1$
                return true;
            }

            public boolean preShutdown( IProgressMonitor monitor, IWorkbench workbench,
                    boolean forced ) throws Exception {
                ICatalog[] toDispose = getCatalogs();
                monitor.beginTask(Messages.CatalogPlugin_SavingCatalog, 4 + (4 * toDispose.length));
                SubProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, 4);
                storeToPreferences(subProgressMonitor);
                subProgressMonitor.done();
                for( ICatalog catalog : toDispose ) {
                    subProgressMonitor = new SubProgressMonitor(monitor, 4);
                    catalog.dispose(subProgressMonitor);
                    subProgressMonitor.done();
                }
                return true;
            }

        });
    }

    /**
     * Opens a dialog warning the user that an error occurred while loading the local catalog
     *
     * @param e the exception that occurred
     */
    private void handlerLoadingError( Exception e ) {
        try {
            File backup = new File(getLocalCatalogFile().getParentFile(), "corruptedLocalCatalog"); //$NON-NLS-1$
            copy(getLocalCatalogFile(), backup);
        } catch (IOException ioe) {
            log("Coulding make a back up of the corrupted local catalog", ioe); //$NON-NLS-1$
        }
        boolean addShutdownHook = MessageDialog
                .openQuestion(
                        Display.getDefault().getActiveShell(),
                        Messages.CatalogPlugin_title,
                        Messages.CatalogPlugin_message);
        if (addShutdownHook) {
            addSaveLocalCatalogShutdownHook();
        }
    }

    private void copy( File file, File backup ) throws IOException {
        FileChannel in = new FileInputStream(file).getChannel(), out = new FileOutputStream(backup)
                .getChannel();
        final int BSIZE = 1024;
        ByteBuffer buffer = ByteBuffer.allocate(BSIZE);
        while( in.read(buffer) != -1 ) {
            buffer.flip(); // Prepare for writing
            out.write(buffer);
            buffer.clear(); // Prepare for reading
        }
    }

    /**
     * Cleanup after shared images.
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     * @param context
     * @throws Exception
     */
    public void stop( BundleContext context ) throws Exception {

        super.stop(context);

        plugin = null;
        resourceBundle = null;
    }

    public void restoreFromPreferences() throws BackingStoreException, MalformedURLException {
        try {

            ((CatalogImpl) getLocalCatalog()).loadFromFile(getLocalCatalogFile(),
                    getServiceFactory());
            loadCatalogs();
        } catch (Throwable t) {
            CatalogPlugin.log(null, new Exception(t));
        }
    }

    private void loadCatalogs() {
        final List<ICatalog> cats = new LinkedList<ICatalog>();
        ExtensionPointUtil.process(getDefault(),
                "net.refractions.udig.catalog.ICatalog", new ExtensionPointProcessor(){ //$NON-NLS-1$
                    public void process( IExtension extension, IConfigurationElement element )
                            throws Exception {
                        cats.add((ICatalog) element.createExecutableExtension("class")); //$NON-NLS-1$
                    }
                });

        ICatalog[] ctemp = new ICatalog[cats.size() + 1];
        ctemp[0] = catalogs[0];
        Iterator<ICatalog> ii = cats.iterator();
        for( int i = 1; i < ctemp.length && ii.hasNext(); i++ ) {
            ctemp[i] = ii.next();
        }
        catalogs = ctemp;
    }

    public void storeToPreferences( IProgressMonitor monitor ) throws BackingStoreException,
            IOException {
        ((CatalogImpl) getLocalCatalog()).saveToFile(getLocalCatalogFile(), getServiceFactory(),
                monitor);
    }

    private File getLocalCatalogFile() throws IOException {
        File userLocation = new File(FileLocator.toFileURL(Platform.getInstanceLocation().getURL())
                .getFile());
        if (!userLocation.exists())
            userLocation.mkdirs();
        File catalogLocation = new File(userLocation, ".localCatalog"); //$NON-NLS-1$
        return catalogLocation;
    }

    /**
     * Returns the shared instance.
     */
    public static CatalogPlugin getDefault() {
        return plugin;
    }
    /**
     * Add a catalog listener for changed to this catalog.
     *
     * @param listener
     */
    public static void addListener( IResolveChangeListener listener ) {
        ((CatalogImpl) getDefault().getLocalCatalog()).addCatalogListener(listener);
    }

    /**
     * Remove a catalog listener that was interested in this catalog.
     *
     * @param listener
     */
    public static void removeListener( IResolveChangeListener listener ) {
        ((CatalogImpl) getDefault().getLocalCatalog()).removeCatalogListener(listener);
    }

    /**
     * Returns the string from the plugin's resource bundle, or 'key' if not found.
     */
    public static String getResourceString( String key ) {
        ResourceBundle bundle = CatalogPlugin.getDefault().getResourceBundle();
        try {
            return (bundle != null) ? bundle.getString(key) : key;
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Returns the plugin's resource bundle,
     */
    public ResourceBundle getResourceBundle() {
        try {
            if (resourceBundle == null)
                resourceBundle = ResourceBundle
                        .getBundle("net.refractions.udig.catalog.CatalogPluginResources"); //$NON-NLS-1$
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
        return resourceBundle;
    }
    /**
     * @return Returns the catalogs found ... local one is slot[0]. TODO hook the extension point
     *         up.
     */
    public ICatalog[] getCatalogs() {
        if (catalogs == null)
            loadCatalogs();

        int i = 0;
        if( catalogs!=null )
            i=catalogs.length;
        ICatalog[] c=new ICatalog[i];
        if( catalogs!=null )
            System.arraycopy(catalogs, 0, c, 0, c.length);
        return c;
    }

    /**
     * @return the local catalog. Equivalent to getCatalogs()[0]
     */
    public ICatalog getLocalCatalog() {
        return getCatalogs()[0];
    }
    /**
     * @return Returns the serviceFactory.
     */
    public IServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    /**
     * Attempts to turn data into a URL. If data is an array or Collection, it will return the first
     * successful URL it can find. This is a utility method. Feel free to move it to another class.
     * In the future, it might be nice to have it return a List of the URLs it found, not just the
     * first one.
     *
     * @param data
     * @return a URL if it can find one, or null otherwise
     */
    public static URL locateURL( Object data ) {
        if (data == null)
            return null;

        return toURL(data);
    }

    private static URL toURL( Object data ) {
        URL url = null;
        if (data instanceof String) {
            String string = (String) data;
            int index = string.indexOf("\n"); //$NON-NLS-1$
            if (index > -1)
                string = string.substring(0, index);

            // try to turn into a string directly
            try {
                url = new URL(string);
            } catch (MalformedURLException e) {
                // try to go to a file first
                try {
                    url = new File(string).toURL();
                } catch (MalformedURLException e1) {
                    // do nothing
                }
            }

        } else if (data instanceof File) {
            try {
                url = ((File) data).toURL();
            } catch (MalformedURLException e) {
                // do nothing
            }
        } else if (data instanceof URL) {
            url = (URL) data;
        } else if (data instanceof IGeoResource) {
            IGeoResource resource = (IGeoResource) data;
            return resource.getIdentifier();
        } else if (data instanceof IService) {
            IService service = (IService) data;
            return service.getIdentifier();
        }

        return url;
    }

    /**
     * Logs the Throwable in the plugin's log.
     * <p>
     * This will be a user visable ERROR iff:
     * <ul>
     * <li>t is an Exception we are assuming it is human readable or if a message is provided
     */
    public static void log( String message2, Throwable t ) {
        String message=message2;
        if (message == null)
            message = ""; //$NON-NLS-1$
        int status = t instanceof Exception || message != null ? IStatus.ERROR : IStatus.WARNING;
        getDefault().getLog().log(new Status(status, ID, IStatus.OK, message, t));
    }
    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much prefered to do this:
     *
     * <pre><code>
     * private static final String RENDERING = &quot;net.refractions.udig.project/render/trace&quot;;
     * if (ProjectUIPlugin.getDefault().isDebugging() &amp;&amp; &quot;true&quot;.equalsIgnoreCase(RENDERING)) {
     *     System.out.println(&quot;your message here&quot;);
     * }
     *
     */
    public static void trace( String message, Throwable e ) {
        if (getDefault().isDebugging()) {
            if (message != null)
                System.out.println(message);
            if (e != null)
                e.printStackTrace();
        }
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
     * Returns the preference store for this UI plug-in.
     * This preference store is used to hold persistent settings for this plug-in in
     * the context of a workbench. Some of these settings will be user controlled,
     * whereas others may be internal setting that are never exposed to the user.
     * <p>
     * If an error occurs reading the preference store, an empty preference store is
     * quietly created, initialized with defaults, and returned.
     * </p>
     * <p>
     * <strong>NOTE:</strong> As of Eclipse 3.1 this method is
     * no longer referring to the core runtime compatibility layer and so
     * plug-ins relying on Plugin#initializeDefaultPreferences
     * will have to access the compatibility layer themselves.
     * </p>
     *
     * @return the preference store
     */
    public IPreferenceStore getPreferenceStore() {
        return preferenceStore;
    }

    public IResolveManager getResolveManager() {
        return resolveManager;
    }

}
