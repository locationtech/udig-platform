/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.locationtech.udig.catalog.internal.CatalogImpl;
import org.locationtech.udig.catalog.internal.Messages;
import org.locationtech.udig.catalog.internal.ResolveManager2;
import org.locationtech.udig.catalog.internal.ServiceFactoryImpl;
import org.locationtech.udig.core.internal.ExtensionPointProcessor;
import org.locationtech.udig.core.internal.ExtensionPointUtil;
import org.locationtech.udig.ui.PreShutdownTask;
import org.locationtech.udig.ui.ProgressManager;
import org.locationtech.udig.ui.ShutdownTaskList;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

/**
 * The main plugin class to be used in the desktop.
 */
public class CatalogPlugin extends Plugin {

    private static final String EXTENSION_POINT_ICATALOG = "org.locationtech.udig.catalog.ICatalog"; //$NON-NLS-1$

    public static final String ID = "org.locationtech.udig.catalog"; //$NON-NLS-1$

    // The shared instance.
    private static CatalogPlugin plugin;

    // Resource bundle.
    private ResourceBundle resourceBundle;

    /**
     * Local catalog used as a repository to manage servies that are known the the client
     */
    private CatalogImpl local;

    /**
     * Cache of catalogues (possibly remote) that may be searched for additional spatial information
     */
    private List<ISearch> catalogs;

    /**
     * Service factory ued to construct new IService instances allowing connection to spatial
     * information.
     */
    private IServiceFactory serviceFactory;

    /**
     * Plugin preferenceStore (from bundle) used to store catalog information.
     */
    private IPreferenceStore preferenceStore;

    /**
     * ResolveManager used to allow plugins to teach new resolve targets to existing IService and
     * IGeoResoruce implementations.
     */
    private volatile IResolveManager resolveManager;

    /**
     * Lock used to protect map of available services; for the last call?
     */
    private Lock registeredLock = new ReentrantLock();

    /**
     * Map of ServiceExtension by "id", access control policed by above "lock".
     */
    Map<String, ServiceExtension> registered = null; // lazy creation

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
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        local = new CatalogImpl();
        catalogs = Collections.emptyList();
        serviceFactory = new ServiceFactoryImpl();
        resolveManager = new ResolveManager2();

        // ensure a preference store is around so we can save to it in the shutdown hook
        preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
                getBundle().getSymbolicName());

        try {
            if (Display.getCurrent() != null) {
                CatalogPlugin.trace("Restoring Local Catalog", null); //$NON-NLS-1$
            }
            plugin.restoreFromPreferences();
        } catch (Throwable e) {
            CatalogPlugin.log("Unable to restore catalog:" + e, e); //$NON-NLS-1$
            handlerLoadingError(e);
        }
        addSaveLocalCatalogShutdownHook();
    }

    private void addSaveLocalCatalogShutdownHook() {
        ShutdownTaskList.instance().addPreShutdownTask(new PreShutdownTask() {

            @Override
            public int getProgressMonitorSteps() {
                try {
                    return getLocalCatalog().members(ProgressManager.instance().get()).size();
                } catch (IOException e) {
                    return 0;
                }
            }

            @Override
            public boolean handlePreShutdownException(Throwable t, boolean forced) {
                CatalogPlugin.log("Error storing local catalog", t); //$NON-NLS-1$
                return true;
            }

            @Override
            public boolean preShutdown(IProgressMonitor monitor, IWorkbench workbench,
                    boolean forced) throws Exception {
                ISearch[] toDispose = getCatalogs();
                monitor.beginTask(Messages.CatalogPlugin_SavingCatalog, 4 + (4 * toDispose.length));
                SubMonitor subMonitor = SubMonitor.convert(monitor, 4);
                storeToPreferences(subMonitor);
                subMonitor.done();
                for (ISearch catalog : toDispose) {
                    subMonitor = SubMonitor.convert(monitor, 4);
                    catalog.dispose(subMonitor);
                    subMonitor.done();
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
    private void handlerLoadingError(Throwable e) {
        try {
            File backup = new File(getLocalCatalogFile().getParentFile(), "corruptedLocalCatalog"); //$NON-NLS-1$
            copy(getLocalCatalogFile(), backup);
        } catch (IOException ioe) {
            log("Coulding make a back up of the corrupted local catalog", ioe); //$NON-NLS-1$
        }
        boolean addShutdownHook = MessageDialog.openQuestion(Display.getDefault().getActiveShell(),
                Messages.CatalogPlugin_ErrorLoading, Messages.CatalogPlugin__ErrorLoadingMessage);
        if (addShutdownHook) {
            addSaveLocalCatalogShutdownHook();
        }
    }

    private void copy(File file, File backup) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        FileOutputStream outputStream = new FileOutputStream(backup);
        FileChannel in = inputStream.getChannel(), out = outputStream.getChannel();
        final int BSIZE = 1024;
        ByteBuffer buffer = ByteBuffer.allocate(BSIZE);
        while (in.read(buffer) != -1) {
            buffer.flip(); // Prepare for writing
            out.write(buffer);
            buffer.clear(); // Prepare for reading
        }
        inputStream.close();
        outputStream.close();
    }

    /**
     * Cleanup after shared images.
     * <p>
     *
     * @see addSaveLocalCatalogShutdownHook
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     * @param context
     * @throws Exception
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
        resourceBundle = null;
    }

    /**
     * Load the getLocalCatalogFile() into the local catalog() and restore any external catalogs.
     */
    public void restoreFromPreferences() {
        try {
            if (getLocalCatalog() instanceof CatalogImpl) {
                CatalogImpl localCatalog = (CatalogImpl) getLocalCatalog();
                File catalogFile = getLocalCatalogFile();
                IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
                localCatalog.loadFromFile(catalogFile, serviceFactory);
            }
        } catch (Throwable t) {
            CatalogPlugin.log("Trouble restoring local catalog:" + t, t); //$NON-NLS-1$
        }
        try {
            loadCatalogs();
        } catch (Throwable t) {
            CatalogPlugin.log("Trouble connectin remote catalogs:" + t, t); //$NON-NLS-1$
        }
    }

    /**
     * Go through and load the "external" catalogs ... this is used to populate getCatalogs() and
     * the result is cached.
     */
    private List<ISearch> loadCatalogs() {
        final List<ISearch> availableCatalogs = new LinkedList<>();
        ExtensionPointUtil.process(getDefault(), EXTENSION_POINT_ICATALOG,
                new ExtensionPointProcessor() {
                    @Override
                    public void process(IExtension extension, IConfigurationElement element)
                            throws Exception {
                        ISearch externalCatalog = (ISearch) element
                                .createExecutableExtension("class"); //$NON-NLS-1$
                        availableCatalogs.add(externalCatalog); // $NON-NLS-1$
                    }
                });
        return availableCatalogs;
    }

    public void storeToPreferences(IProgressMonitor monitor)
            throws BackingStoreException, IOException {
        ((CatalogImpl) getLocalCatalog()).saveToFile(getLocalCatalogFile(), getServiceFactory(),
                monitor);
    }

    /**
     * File used to load/save the local catalog.
     *
     * @return
     * @throws IOException
     */
    private File getLocalCatalogFile() throws IOException {
        // working directory for the application as a file
        File userLocation = new File(
                FileLocator.toFileURL(Platform.getInstanceLocation().getURL()).getFile());
        // will create the file if needed
        if (!userLocation.exists())
            userLocation.mkdirs();
        // local catalog saved in working directory/.localCatalog
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
    public static void addListener(IResolveChangeListener listener) {
        ((CatalogImpl) getDefault().getLocalCatalog()).addCatalogListener(listener);
    }

    /**
     * Remove a catalog listener that was interested in this catalog.
     *
     * @param listener
     */
    public static void removeListener(IResolveChangeListener listener) {
        ((CatalogImpl) getDefault().getLocalCatalog()).removeCatalogListener(listener);
    }

    /**
     * Returns the string from the plugin's resource bundle, or 'key' if not found.
     */
    public static String getResourceString(String key) {
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
                        .getBundle("org.locationtech.udig.catalog.CatalogPluginResources"); //$NON-NLS-1$
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
        return resourceBundle;
    }

    /**
     * Available catalogs supporting the ISearch interface. Please note that not all of these
     * catalogs are "local"; although the first [0] one is the same as getLocal().
     *
     * @return Returns the catalogs found ... local one is slot[0].
     */
    public synchronized ISearch[] getCatalogs() {
        if (catalogs == null) {
            // look up all catalogs and populate catalogs
            catalogs = loadCatalogs();
        }
        List<ISearch> c = new ArrayList<>();
        c.add(local);
        c.addAll(catalogs);
        return c.toArray(new ISearch[c.size()]);
    }

    public synchronized void addSearchCatalog(ISearch searchCatalog) {
        // force init
        getCatalogs();

        catalogs.add(searchCatalog);
    }

    /**
     * @return the local catalog. Equivalent to getCatalogs()[0]
     */
    public ICatalog getLocalCatalog() {
        return local;
    }

    /**
     * Access to the local repository which you can add/remove services to.
     * <p>
     * The local repository is used to reflect what servies are known to the application. In
     * particular it is used to track all services that are "connected" so that the application can
     * clean up (ie call dispose) on these services when it is being shutdown.
     * <p>
     * This repository is persisted (currently the the contents are saved into the catalog plugin
     * preferencestore, although a successful geoserver implementation indicates that the use of a
     * hibernate is also appropriate).
     *
     * @return Local repository used to manage known serivces and all connected services.
     */
    public IRepository getLocal() {
        return local;
    }

    /**
     * Service factory used to connect to new services.
     * <p>
     * Services hold on to live connections; please take care to call dispose() or to add the
     * service to the local repository so that the service is disposed when the application shuts
     * down.
     *
     * @return Returns the serviceFactory.
     */
    public IServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    /**
     * List of registered {@link ServiceExtension}s.
     *
     * @return Registered {@link ServiceExtension}
     */
    public List<ServiceExtension> getServiceExtensions() {
        List<ServiceExtension> list = new ArrayList<>(getRegisteredExtensions().values());
        return Collections.unmodifiableList(list);
    }

    /**
     * Register a service extension by hand.
     *
     * @param id
     * @param extension
     */
    public void register(String id, ServiceExtension extension) {
        try {
            registeredLock.lock();
            getRegisteredExtensions();
            if (registered.containsKey(id)) {
                ServiceExtension existing = registered.get(id);
                String exsistingName = existing != null ? existing.getClass().getSimpleName()
                        : null;
                throw new IllegalStateException(
                        "ServiceExtension " + id + " already registered with " + exsistingName); //$NON-NLS-1$ //$NON-NLS-2$
            }

            registered.put(id, extension);
        } finally {
            registeredLock.unlock();
        }
    }

    /**
     * Internal method to lazily process the {@link #CATALOG_SERVICE_EXTENSION}
     *
     * @return Map of {@link ServiceExtension} by id
     */
    Map<String, ServiceExtension> getRegisteredExtensions() {
        try {
            registeredLock.lock();
            if (registered == null) { // load available
                // we are going to sort the map so that "generic" fallback datastores are selected
                // last
                //
                registered = new HashMap<>();
                ExtensionPointUtil.process(CatalogPlugin.getDefault(),
                        ServiceExtension.EXTENSION_ID, new ExtensionPointProcessor() { // $NON-NLS-1$
                            @Override
                            public void process(IExtension extension, IConfigurationElement element)
                                    throws Exception {
                                // extentionIdentifier used to report any problems;
                                // in the event of failure we want to be able to report
                                // who had the problem
                                // String extensionId = extension.getUniqueIdentifier();
                                String id = element.getAttribute("id"); //$NON-NLS-1$
                                ServiceExtension se = (ServiceExtension) element
                                        .createExecutableExtension("class"); //$NON-NLS-1$
                                if (id == null || id.length() == 0) {
                                    id = se.getClass().getSimpleName();
                                }
                                registered.put(id, se);
                            }
                        });
            }
            return registered;
        } finally {
            registeredLock.unlock();
        }
    }

    /**
     * Look up a specific implementation; used mostly for test cases
     */
    public <E extends ServiceExtension> E serviceImplementation(Class<E> implementation) {
        for (Map.Entry<String, ServiceExtension> entry : getRegisteredExtensions().entrySet()) {
            String id = entry.getKey();
            ServiceExtension serviceExtension = entry.getValue();

            if (id == null || serviceExtension == null)
                continue;
            if (implementation.isInstance(serviceExtension)) {
                return implementation.cast(serviceExtension);
            }
        }
        return null;
    }

    /**
     * Look up a specific implementation; used mostly for test cases
     */
    public ServiceExtension serviceImplementation(String serviceExtensionId) {
        for (Map.Entry<String, ServiceExtension> entry : getRegisteredExtensions().entrySet()) {
            String id = entry.getKey();
            ServiceExtension serviceExtension = entry.getValue();

            if (id == null || serviceExtension == null)
                continue;
            if (serviceExtensionId.equalsIgnoreCase(id)) {
                return serviceExtension;
            }
        }
        return null;
    }

    /**
     * Helper method to log a message in the plugin's log.
     * <p>
     * This will be a user visable ERROR iff:
     * <ul>
     * <li>t is an Exception we are assuming it is human readable or if a message is provided</li>
     * </ul>
     *
     * @param message log message
     * @param t Throwable causing the message; if this is an exception an error will be logged
     */
    public static void log(String message, Throwable t) {
        String msg = message == null ? "" : message; //$NON-NLS-1$
        int status = t instanceof Exception || message != null ? IStatus.ERROR : IStatus.INFO;
        getDefault().getLog().log(new Status(status, ID, IStatus.OK, msg, t));
    }

    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much preferred to do this:
     *
     * <pre>
     * <code> private static final String RENDERING =
     * &quot;org.locationtech.udig.project/render/trace&quot;; if
     * (ProjectUIPlugin.getDefault().isDebugging() &amp;&amp;
     * &quot;true&quot;.equalsIgnoreCase(RENDERING)) { System.out.println(&quot;your message
     * here&quot;); }
     */
    public static void trace(String message, Throwable e) {
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
    public static boolean isDebugging(final String trace) {
        return getDefault().isDebugging()
                && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$
    }

    /**
     * Returns the preference store for this UI plug-in. This preference store is used to hold
     * persistent settings for this plug-in in the context of a workbench. Some of these settings
     * will be user controlled, whereas others may be internal setting that are never exposed to the
     * user.
     * <p>
     * If an error occurs reading the preference store, an empty preference store is quietly
     * created, initialized with defaults, and returned.
     * </p>
     * <p>
     * <strong>NOTE:</strong> As of Eclipse 3.1 this method is no longer referring to the core
     * runtime compatibility layer and so plug-ins relying on Plugin#initializeDefaultPreferences
     * will have to access the compatibility layer themselves.
     * </p>
     *
     * @return the preference store
     */
    public IPreferenceStore getPreferenceStore() {
        return preferenceStore;
    }

    /**
     * ResolveManager used to allow plugins to teach new resolve targets to existing IService and
     * IGeoResoruce implementations.
     *
     * @return IResolveManager
     */
    public IResolveManager getResolveManager() {
        return resolveManager;
    }

}
