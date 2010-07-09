package net.refractions.udig.catalog.ui;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.ICatalogInfo;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveChangeListener;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IResolveFolder;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.IResolveChangeEvent.Type;
import net.refractions.udig.catalog.IResolveDelta.Kind;
import net.refractions.udig.catalog.internal.ui.Images;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.catalog.util.HandleListener;
import net.refractions.udig.core.internal.CorePlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.osgi.framework.BundleContext;
//import org.picocontainer.Disposable;
//import org.picocontainer.MutablePicoContainer;
//import org.picocontainer.Startable;

/**
 * Lifecycle & Resource management for RegistryUI.
 * <p>
 * The CatalogUIPlugin provides access for shared images descriptors.
 * </p>
 * Example use of a shared image descriptor:
 * 
 * <pre><code>
 * ImageRegistry images = CatalogUIPlugin.getDefault().getImageRegistry();
 * ImageDescriptor image = images.getDescriptor(ISharedImages.IMG_DATASTORE_OBJ);
 * </code></pre>
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
public class CatalogUIPlugin extends AbstractUIPlugin {

    /**
     * The id of the plug-in
     */
    public static final String ID = "net.refractions.udig.catalog.ui"; //$NON-NLS-1$

    // public static final String DECORATOR_ID = "net.refractions.udig.registry.ui.decorator";
    // //$NON-NLS-1$
    /** Preference store for the last directory open by the file selection dialog */
    public static final String PREF_OPEN_DIALOG_DIRECTORY = "udig.preferences.openDialog.lastDirectory"; //$NON-NLS-1$

    /** Icons path (value "icons/") */
    public final static String ICONS_PATH = "icons/";//$NON-NLS-1$

    private static final String LABELS_PREFERENCE_STORE = "CATALOG_LABELS_PREFERENCE_STORAGE"; //$NON-NLS-1$

    private boolean loaded = false;

    // The shared instance.
    private static CatalogUIPlugin plugin;

    /** Managed Images instance */
    private Images images = new Images();
    //private volatile static MutablePicoContainer pluginContainer;

    /**
     * The constructor.
     */
    public CatalogUIPlugin() {
        super();
        plugin = this;
    }

    /**
     * Set up shared images.
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @param context
     * @throws Exception
     */
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        final URL iconsUrl = context.getBundle().getEntry(ICONS_PATH);

        images.initializeImages(iconsUrl, getImageRegistry());
        registerChangeListener();
    }
    /**
     * Registers a listener with the local catalog for reset events so that there is a mechanism to 
     * reset the label cache for an IResolve.
     */
    private void registerChangeListener() {
        CatalogPlugin.addListener(new IResolveChangeListener(){

            public void changed( IResolveChangeEvent event ) {
                if( PlatformUI.getWorkbench().isClosing() )
                    return;
//                IPreferenceStore p = getPreferenceStore();
                if( event.getType()==Type.POST_CHANGE 
                        && event.getDelta()!=null ){
                    //  TODO enable when resolve information is available
//                     updateCache(event.getDelta(), p);
                }

            }

            private void updateCache( IResolveDelta delta, IPreferenceStore p ) {

                if (delta.getKind() == Kind.REPLACED || delta.getKind() == Kind.REMOVED
                        || delta.getKind()==Kind.CHANGED) {
                    if (delta.getResolve() != null && delta.getResolve().getIdentifier() != null) {
                        String string = LABELS_PREFERENCE_STORE+delta.getResolve().getIdentifier().toString();
                        p.setToDefault(string);
                    }
                }
                List<IResolveDelta> children = delta.getChildren();
                for( IResolveDelta delta2 : children ) {
                    updateCache(delta2, p);
                }
            }
            
        });
    }

    /**
     * Cleanup after shared images.
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     * @param context
     * @throws Exception
     */
    public void stop( BundleContext context ) throws Exception {
        try{
//            storeLabels();
            images.cleanUp();
        }catch(Exception e){
           log("", e); //$NON-NLS-1$
        }finally{
            super.stop(context);
        }
    }

    /**
     * Returns the shared instance.
     * 
     * @return CatalogUIPlugin singleton
     */
    public static CatalogUIPlugin getDefault() {
        if (plugin == null || !plugin.loaded) {
            synchronized (CatalogUIPlugin.class) {

                if (plugin == null) {
                    plugin = new CatalogUIPlugin();
                }

            }
        }
        return plugin;
    }
    
    /**
     * Images instance for use with ImageConstants.
     * 
     * @return Images for use with ImageConstants.
     */
    public ISharedImages getImages() {
        return images;
    }

    /**
     * Logs the Throwable in the plugin's log.
     * <p>
     * This will be a user visable ERROR iff:
     * <ul>
     * <li>t is an Exception we are assuming it is human readable or if a message is provided
     * </ul>
     * </p>
     * 
     * @param message
     * @param t
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
     * </code></pre>
     * 
     * </p>
     * 
     * @param message
     * @param e
     */
    public static void trace( String message, Throwable e ) {
        if (getDefault().isDebugging()) {
            if (message != null)
                System.out.println(message);
            if (e != null)
                e.printStackTrace();
        }
    }
    public static void trace( String message ) {
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
    public static boolean isDebugging( final String trace ) {
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
    public static Image image( IResolve resolve ) {
        ISharedImages images = CatalogUIPlugin.getDefault().getImages();

        if (resolve == null) {
            return null;
        }
        if (resolve instanceof IResolveFolder ) {
        	return images.get(ISharedImages.FOLDER_OBJ);
        }else if (resolve instanceof IGeoResource) {
            IGeoResource resource = (IGeoResource) resolve;
            boolean isFeature = resource.canResolve(FeatureSource.class);
            String iconId = iconInternalResource( resource.getID(), isFeature );
            return images.get( iconId );
        } else if (resolve instanceof IService) {
            IService service = (IService) resolve;
            boolean isFeature = service.canResolve(DataStore.class);
            
            String iconId = iconInternalService( service.getID(), isFeature );
            return images.get( iconId );
        } else if (resolve instanceof ICatalog) {
            return images.get(ISharedImages.CATALOG_OBJ);
        }
        return images.get(ISharedImages.RESOURCE_OBJ);
    }

    public static ImageDescriptor icon( IResolve resolve ) throws IOException {
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
    public static ImageDescriptor icon( final IResolve resolve, IProgressMonitor monitor )
            throws IOException {

        if( resolve.canResolve(ImageDescriptor.class) ){
            ImageDescriptor descriptor = resolve.resolve(ImageDescriptor.class, monitor);
            if( descriptor!=null){
                return descriptor;
            }
        }
        if (resolve instanceof IGeoResource) {
            ImageDescriptor icon = icon((IGeoResource) resolve, monitor);
            return icon != null ? icon : new ImageDescriptor(){

                @Override
                public ImageData getImageData() {
                    return image(resolve).getImageData();
                }
                
            };
        }

        if (resolve instanceof IService) {
            ImageDescriptor icon = icon((IService) resolve, monitor);
            if( icon != null ){
            	return icon;
            }
            return Images.getDescriptor(ISharedImages.SERVER_OBJ);
        }

        if (resolve instanceof IResolveFolder) {
            ImageDescriptor icon = icon((IResolveFolder) resolve, monitor);
            return icon != null ? icon : Images.getDescriptor(ISharedImages.FOLDER_OBJ);
        }

        if (resolve instanceof ICatalog)
            return CatalogUIPlugin.getDefault().getImages().getImageDescriptor(
                    ISharedImages.CATALOG_OBJ);

        return CatalogUIPlugin.getDefault().getImages().getImageDescriptor(
                ISharedImages.RESOURCE_OBJ);
    }

    /**
     * Create icon for provided resource, this will block!
     * 
     * @param resource
     * @return ImageDescriptor for resource.
     * @throws IOException
     */
    private static ImageDescriptor icon( IGeoResource resource, IProgressMonitor monitor )
            throws IOException {
    	if( monitor == null ) monitor = new NullProgressMonitor();
    	
    	// check for dynamic icon first!
    	if( resource.canResolve( ImageDescriptor.class )){
    		ImageDescriptor icon = resource.resolve( ImageDescriptor.class, monitor);
    		if( icon != null ) return icon;
    	}
    	// check for static icon next
        try{
            IGeoResourceInfo info;
            info = resource.resolve(IGeoResourceInfo.class, monitor);            
            
            ImageDescriptor icon = info.getImageDescriptor();
            if( icon != null ) return icon;            
        }catch(Throwable t){
            log("Error obtaining info", t); //$NON-NLS-1$
            return null;
        }
        // check for default icon last
        boolean isFeature = resource.canResolve(FeatureSource.class);
        String iconId = iconInternalResource( resource.getID(), isFeature );
        return CatalogUIPlugin.getDefault().images.getImageDescriptor( iconId );
    }
    
    /** Lookup default resource icon id */
    private static String iconInternalResource( ID id, boolean isFeature ){
    	if (Identifier.isGraphic(id.toURL())) {
            return ISharedImages.GRAPHIC_OBJ;
        }
        if (Identifier.isWMS(id.toURL())) {
            return ISharedImages.GRID_OBJ;
        }
        if (Identifier.isGraphic(id.toURL())) {
            return ISharedImages.GRAPHIC_OBJ;
        }
        if (Identifier.isMemory(id.toURL())) {
            return ISharedImages.MEMORY_OBJ;
        }
        if( isFeature ){
        	return ISharedImages.FEATURE_OBJ;
        }
        else {
        	return ISharedImages.GRID_OBJ;
        }
    }

    /**
     * Create icon for provided folder, this will block!
     * 
     * @return ImageDescriptor for folder.
     * @throws IOException
     */
    private static ImageDescriptor icon( IResolveFolder folder, IProgressMonitor monitor )
            throws IOException {

    	if( monitor == null ) monitor = new NullProgressMonitor();
    	
    	// check for dynamic icon first!
    	if( folder.canResolve( ImageDescriptor.class )){
    		ImageDescriptor icon = folder.resolve( ImageDescriptor.class, monitor);
    		if( icon != null ) return icon;
    	}
        // check for default icon last
        return CatalogUIPlugin.getDefault().images.getImageDescriptor( ISharedImages.FOLDER_OBJ );
    }

    
    /**
     * Create icon for provided service, this will block!
     * 
     * @param resource
     * @return ImageDescriptor for resource.
     * @throws IOException
     */
    private static ImageDescriptor icon( IService service, IProgressMonitor monitor )
            throws IOException {
    	if( monitor == null ) monitor = new NullProgressMonitor();
    	
    	// check for dynamic icon first!
    	if( service.canResolve( ImageDescriptor.class )){
    		ImageDescriptor icon = service.resolve( ImageDescriptor.class, monitor);
    		if( icon != null ) return icon;
    	}
    	// check for static icon next
        try{
            IServiceInfo info;
            info = service.resolve(IServiceInfo.class, monitor);            
            if( info != null ){
                ImageDescriptor icon = info.getImageDescriptor();
                if( icon != null ) {
                    return icon;            
                }
            }
        }catch(Throwable t){
            log("Error obtaining info", t); //$NON-NLS-1$
            return null;
        }
        // check for default icon last
        boolean isFeature = service.canResolve( DataStore.class );
        String iconId = iconInternalService( service.getID(), isFeature );
        return CatalogUIPlugin.getDefault().images.getImageDescriptor( iconId );
    }

	private static String iconInternalService(ID id, boolean isFeature) {
		URL url = id.toURL();
		if (Identifier.isFile(url)) {
			if( isFeature ){
				return ISharedImages.FEATURE_FILE_OBJ;
			}
			else {
				return ISharedImages.GRID_FILE_OBJ;
			}
        }
        if (Identifier.isGraphic(url)) {
            return ISharedImages.MAP_GRAPHICS_OBJ;
        }
        if (Identifier.isWMS(url)) {
            return ISharedImages.WMS_OBJ;
        }
        if (Identifier.isWFS(url)) {
            return ISharedImages.WFS_OBJ;
        }
        if (Identifier.isJDBC(url)) {
            return ISharedImages.DATABASE_OBJ;
        }
        if (isFeature) {
            return ISharedImages.DATASTORE_OBJ;
        }
        else {
	        return ISharedImages.SERVER_OBJ;
        }
	}
   
}