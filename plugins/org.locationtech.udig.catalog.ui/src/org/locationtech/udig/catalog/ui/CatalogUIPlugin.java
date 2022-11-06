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
package org.locationtech.udig.catalog.ui;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IResolveChangeEvent;
import org.locationtech.udig.catalog.IResolveChangeEvent.Type;
import org.locationtech.udig.catalog.IResolveChangeListener;
import org.locationtech.udig.catalog.IResolveDelta;
import org.locationtech.udig.catalog.IResolveDelta.Kind;
import org.locationtech.udig.catalog.IResolveFolder;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.core.AbstractUdigUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Lifecycle & Resource management for RegistryUI.
 * <p>
 * The CatalogUIPlugin provides access for shared images descriptors.
 * </p>
 * Example use of a shared image descriptor:
 *
 * <pre>
 * <code>
 * ImageRegistry images = CatalogUIPlugin.getDefault().getImageRegistry();
 * ImageDescriptor image = CatalogUIPlugin.getDefault().getImageDescriptor(ISharedImages.IMG_DATASTORE_OBJ);
 * </code>
 * </pre>
 *
 * </p>
 * <h3>Implementation Note</h3>
 * </p>
 * The CatalogUIPlugin delegates the following resource management tasks:
 * <ul>
 * <li>ResourceBundle: Policy</li>
 * <li>ImageDescriptors: Images</li>
 * </ul>
 * These resources are intended for use by classes within this plugin.
 * </p>
 *
 * @author Jody Garnett, Refractions Research, Inc
 */
public class CatalogUIPlugin extends AbstractUdigUIPlugin {

    private static CatalogUIPlugin INSTANCE;

    /**
     * The id of the plug-in
     */
    public static final String ID = "org.locationtech.udig.catalog.ui"; //$NON-NLS-1$

    /**
     * Preference store for the last directory open by the file selection dialog
     */
    public static final String PREF_OPEN_DIALOG_DIRECTORY = "udig.preferences.openDialog.lastDirectory"; //$NON-NLS-1$

    /**
     * Icons path (value "icons/")
     */
    public static final String ICONS_PATH = "icons/"; //$NON-NLS-1$

    private static final String LABELS_PREFERENCE_STORE = "CATALOG_LABELS_PREFERENCE_STORAGE"; //$NON-NLS-1$

    /**
     * The constructor.
     */
    public CatalogUIPlugin() {
        super();
        INSTANCE = this;
    }

    /**
     * Set up shared images.
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @param context
     * @throws Exception
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        registerChangeListener();
    }

    /**
     * Registers a listener with the local catalog for reset events so that there is a mechanism to
     * reset the label cache for an IResolve.
     */
    private void registerChangeListener() {
        CatalogPlugin.addListener(new IResolveChangeListener() {

            @Override
            public void changed(IResolveChangeEvent event) {
                if (!PlatformUI.isWorkbenchRunning() || PlatformUI.getWorkbench().isClosing()) {
                    return;
                }
                // IPreferenceStore p = getPreferenceStore();
                if (event.getType() == Type.POST_CHANGE && event.getDelta() != null) {
                    // TODO enable when resolve information is available
                    // updateCache(event.getDelta(), p);
                }

            }

            private void updateCache(IResolveDelta delta, IPreferenceStore p) {

                if (delta.getKind() == Kind.REPLACED || delta.getKind() == Kind.REMOVED
                        || delta.getKind() == Kind.CHANGED) {
                    if (delta.getResolve() != null && delta.getResolve().getIdentifier() != null) {
                        String string = LABELS_PREFERENCE_STORE
                                + delta.getResolve().getIdentifier().toString();
                        p.setToDefault(string);
                    }
                }
                List<IResolveDelta> children = delta.getChildren();
                for (IResolveDelta delta2 : children) {
                    updateCache(delta2, p);
                }
            }

        });
    }

    /**
     * Logs the Throwable in the plugin's log.
     * <p>
     * This will be a user visible ERROR if:
     * <ul>
     * <li>t is an Exception we are assuming it is human readable or if a message is provided
     * </ul>
     * </p>
     *
     * @param message
     * @param t
     */
    public static void log(String message2, Throwable t) {
        String message = message2;
        if (message == null)
            message = ""; //$NON-NLS-1$
        int status = t instanceof Exception || message != null ? IStatus.ERROR : IStatus.WARNING;
        getDefault().getLog().log(new Status(status, ID, IStatus.OK, message, t));
    }

    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much preferred to do this:
     *
     * <pre>
     * <code>
     * private static final String RENDERING = &quot;org.locationtech.udig.project/render/trace&quot;;
     * if (ProjectUIPlugin.getDefault().isDebugging() &amp;&amp; &quot;true&quot;.equalsIgnoreCase(RENDERING)) {
     *     System.out.println(&quot;your message here&quot;);
     * }
     * </code>
     * </pre>
     *
     * </p>
     *
     * @param message
     * @param e
     */
    public static void trace(String message, Throwable e) {
        if (getDefault().isDebugging()) {
            if (message != null)
                System.out.println(message);
            if (e != null)
                e.printStackTrace();
        }
    }

    public static void trace(String message) {
        if (getDefault().isDebugging()) {
            if (message != null)
                System.out.println(message);
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
     * @return true if -debug is on for this plugin
     */
    public static boolean isDebugging(final String trace) {
        return getDefault().isDebugging()
                && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$
    }

    /**
     * Quick and dirty image generated based on ID, this image is shared and should not be disposed.
     * <p>
     * This method does not block and can be safely used to by a LabelProvider. This method does not
     * make use of any information available via an info object (because that would require blocking
     * and be unsafe).
     * </p>
     *
     * @see glyph
     * @param resource
     * @return Image representing provided resource
     */
    public static Image image(IResolve resolve) {

        if (resolve == null) {
            return null;
        }
        if (resolve instanceof IResolveFolder) {
            return CatalogUIPlugin.getDefault().getImage(ISharedImages.FOLDER_OBJ);
        } else if (resolve instanceof IGeoResource) {
            IGeoResource resource = (IGeoResource) resolve;
            boolean isFeature = resource.canResolve(FeatureSource.class);
            String iconId = iconInternalResource(resource.getID(), isFeature);
            return CatalogUIPlugin.getDefault().getImage(iconId);
        } else if (resolve instanceof IService) {
            IService service = (IService) resolve;
            boolean isFeature = service.canResolve(DataStore.class);

            String iconId = iconInternalService(service.getID(), isFeature);
            return CatalogUIPlugin.getDefault().getImage(iconId);
        } else if (resolve instanceof ICatalog) {
            return CatalogUIPlugin.getDefault().getImage(ISharedImages.CATALOG_OBJ);
        }
        return CatalogUIPlugin.getDefault().getImage(ISharedImages.RESOURCE_OBJ);
    }

    public static ImageDescriptor icon(IResolve resolve) throws IOException {
        return icon(resolve, new NullProgressMonitor());
    }

    /**
     * Create icon for provided resource, this will block!
     *
     * @param resource
     * @param monitor used to track progress in fetching an appropriate icon
     * @return ImageDescriptor for resource.
     * @throws IOException
     */
    public static ImageDescriptor icon(final IResolve resolve, IProgressMonitor monitor)
            throws IOException {

        if (resolve.canResolve(ImageDescriptor.class)) {
            ImageDescriptor descriptor = resolve.resolve(ImageDescriptor.class, monitor);
            if (descriptor != null) {
                return descriptor;
            }
        }
        if (resolve instanceof IGeoResource) {
            ImageDescriptor icon = icon((IGeoResource) resolve, monitor);
            return icon != null ? icon : new ImageDescriptor() {

                @Override
                public ImageData getImageData() {
                    return image(resolve).getImageData();
                }

            };
        }

        if (resolve instanceof IService) {
            ImageDescriptor icon = icon((IService) resolve, monitor);
            if (icon != null) {
                return icon;
            }
            return CatalogUIPlugin.getDefault().getImageDescriptor(ISharedImages.SERVER_OBJ);
        }

        if (resolve instanceof IResolveFolder) {
            ImageDescriptor icon = icon((IResolveFolder) resolve, monitor);
            return icon != null ? icon
                    : CatalogUIPlugin.getDefault().getImageDescriptor(ISharedImages.FOLDER_OBJ);
        }

        if (resolve instanceof ICatalog)
            return CatalogUIPlugin.getDefault().getImageDescriptor(ISharedImages.CATALOG_OBJ);

        return CatalogUIPlugin.getDefault().getImageDescriptor(ISharedImages.RESOURCE_OBJ);
    }

    /**
     * Create icon for provided resource, this will block!
     *
     * @param resource
     * @return ImageDescriptor for resource.
     * @throws IOException
     */
    private static ImageDescriptor icon(IGeoResource resource, IProgressMonitor monitor)
            throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        // check for dynamic icon first!
        if (resource.canResolve(ImageDescriptor.class)) {
            ImageDescriptor icon = resource.resolve(ImageDescriptor.class, monitor);
            if (icon != null)
                return icon;
        }
        // check for static icon next
        try {
            IGeoResourceInfo info;
            info = resource.resolve(IGeoResourceInfo.class, monitor);

            ImageDescriptor icon = info.getImageDescriptor();
            if (icon != null)
                return icon;
        } catch (Throwable t) {
            log("Error obtaining info", t); //$NON-NLS-1$
            return null;
        }
        // check for default icon last
        boolean isFeature = resource.canResolve(FeatureSource.class);
        String iconId = iconInternalResource(resource.getID(), isFeature);
        return CatalogUIPlugin.getDefault().getImageDescriptor(iconId);
    }

    /**
     * Lookup default resource icon id
     */
    private static String iconInternalResource(ID id, boolean isFeature) {
        if (id.isDecorator()) {
            return ISharedImages.GRAPHIC_OBJ;
        }
        if (id.isWMS()) {
            return ISharedImages.GRID_OBJ;
        }
        if (id.isMemory()) {
            return ISharedImages.MEMORY_OBJ;
        }
        if (isFeature) {
            return ISharedImages.FEATURE_OBJ;
        } else {
            return ISharedImages.GRID_OBJ;
        }
    }

    /**
     * Create icon for provided folder, this will block!
     *
     * @return ImageDescriptor for folder.
     * @throws IOException
     */
    private static ImageDescriptor icon(IResolveFolder folder, IProgressMonitor monitor)
            throws IOException {

        if (monitor == null)
            monitor = new NullProgressMonitor();

        // check for dynamic icon first!
        if (folder.canResolve(ImageDescriptor.class)) {
            ImageDescriptor icon = folder.resolve(ImageDescriptor.class, monitor);
            if (icon != null)
                return icon;
        }
        // check for default icon last
        return CatalogUIPlugin.getDefault().getImageDescriptor(ISharedImages.FOLDER_OBJ);
    }

    /**
     * Create icon for provided service, this will block!
     *
     * @param resource
     * @return ImageDescriptor for resource.
     * @throws IOException
     */
    private static ImageDescriptor icon(IService service, IProgressMonitor monitor)
            throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        // check for dynamic icon first!
        if (service.canResolve(ImageDescriptor.class)) {
            ImageDescriptor icon = service.resolve(ImageDescriptor.class, monitor);
            if (icon != null)
                return icon;
        }
        // check for static icon next
        try {
            IServiceInfo info;
            info = service.resolve(IServiceInfo.class, monitor);
            if (info != null) {
                ImageDescriptor icon = info.getImageDescriptor();
                if (icon != null) {
                    return icon;
                }
            }
        } catch (Throwable t) {
            log("Error obtaining info", t); //$NON-NLS-1$
            return null;
        }
        // check for default icon last
        boolean isFeature = service.canResolve(DataStore.class);
        String iconId = iconInternalService(service.getID(), isFeature);
        return CatalogUIPlugin.getDefault().getImageDescriptor(iconId);
    }

    private static String iconInternalService(ID id, boolean isFeature) {
        if (id.isFile()) {
            if (isFeature) {
                return ISharedImages.FEATURE_FILE_OBJ;
            } else {
                return ISharedImages.GRID_FILE_OBJ;
            }
        }
        if (id.isDecorator()) {
            return ISharedImages.MAP_GRAPHICS_OBJ;
        }
        if (id.isWMS()) {
            return ISharedImages.WMS_OBJ;
        }
        if (id.isWFS()) {
            return ISharedImages.WFS_OBJ;
        }
        if (id.isJDBC()) {
            return ISharedImages.DATABASE_OBJ;
        }
        if (isFeature) {
            return ISharedImages.DATASTORE_OBJ;
        } else {
            return ISharedImages.SERVER_OBJ;
        }
    }

    @Override
    public IPath getIconPath() {
        return new Path(ICONS_PATH);
    }

    public static CatalogUIPlugin getDefault() {
        return INSTANCE;
    }

}
