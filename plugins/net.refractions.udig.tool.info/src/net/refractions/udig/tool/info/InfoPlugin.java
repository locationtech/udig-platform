package net.refractions.udig.tool.info;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Plugin for UDIG Information facilities.
 * <p>
 * Information services is provided by:
 * <ul>
 * <li>InfoTool - a modal tool for the Map editor
 * <li>InfoView - a view used to display the results of the last information request
 * <li>InfoDisplay - allows tool to display new types of content
 * </ul>
 * </p>
 * <p>
 * Programatic access to the current information is not currently provided as part of the public
 * API. If you are interested in this please let us know and it can be moved over.
 * </p>
 */
public class InfoPlugin extends AbstractUIPlugin {
    // The shared instance.
    private static InfoPlugin plugin;

    public static final String ID = "net.refractions.udig.tool.info"; //$NON-NLS-1$

    public static final String IMG_OBJ_FILE = "icons/obj16/file_doc_obj.png"; //$NON-NLS-1$
    public static final String IMG_OBJ_LINK = "icons/obj16/link_doc_obj.png"; //$NON-NLS-1$

    /**
     * The constructor.
     */
    public InfoPlugin() {
        super();
        plugin = this;
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Access shared InfoPlugin instance.
     * 
     * @return Shared Instance
     */
    public static InfoPlugin getDefault() {
        return plugin;
    }

    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     */
    public static void log(String message, Throwable e) {
        getDefault().getLog().log(new Status(IStatus.INFO, ID, 0, message, e));
    }

    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much prefered to do this:
     * 
     * <pre><code>
     * private static final String RENDERING = "net.refractions.udig.project/render/trace";
     * if( ProjectUIPlugin.getDefault().isDebugging() && "true".equalsIgnoreCase( RENDERING ) ){
     *      System.out.println( "your message here" );
     * }
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

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        
        final Bundle bundle = Platform.getBundle(ID);

        final Path fileImgPath = new Path(IMG_OBJ_FILE);
        final ImageDescriptor fileImg = ImageDescriptor.createFromURL(
                FileLocator.find(bundle, fileImgPath, null));
        reg.put(IMG_OBJ_FILE, fileImg);
        
        final Path linkImgPath = new Path(IMG_OBJ_LINK);
        final ImageDescriptor linkImg = ImageDescriptor.createFromURL(
                FileLocator.find(bundle, linkImgPath, null));
        reg.put(IMG_OBJ_LINK, linkImg);
        
    }
    
}
