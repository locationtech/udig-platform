package net.refractions.udig.issues.internal;

import net.refractions.udig.issues.IIssuesManager;
import net.refractions.udig.ui.ProgressManager;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class IssuesActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.refractions.udig.issues"; //$NON-NLS-1$

    static final String ICONS_PATH = "icons/"; //$NON-NLS-1$

	// The shared instance
	private static IssuesActivator plugin;

    private Images images=new Images();
	
	/**
	 * The constructor
	 */
	public IssuesActivator() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
        PlatformUI.getWorkbench().addWorkbenchListener(new IWorkbenchListener(){

            public void postShutdown( IWorkbench workbench ) {
            }

            public boolean preShutdown( IWorkbench workbench, boolean forced ) {
                try{
                IIssuesManager.defaultInstance.save(ProgressManager.instance().get());
                }catch (Exception e) {
                    log("Error saving issues", e); //$NON-NLS-1$
                    boolean result = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), Messages.IssuesActivator_errorTitle,  
                            Messages.IssuesActivator_errorMessage); 
                    return result;
                    
                }
                return true;
            }
            
        });
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
        images.cleanUp();
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static IssuesActivator getDefault() {
		return plugin;
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
            message = "Error in Issues plugin:" + e; //$NON-NLS-1$
        getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, message, e));
    }

    public Images getImages() {
    	images.ensureReady();
        return images;
    }


}
