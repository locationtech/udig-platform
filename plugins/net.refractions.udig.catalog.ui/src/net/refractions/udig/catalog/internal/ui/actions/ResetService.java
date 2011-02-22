package net.refractions.udig.catalog.internal.ui.actions;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * Resets a selection of services
 *
 * @author Jody Garnett
 * @since 0.8
 */
public class ResetService extends ActionDelegate {

    IStructuredSelection current;
    public void run( IAction action ) {
        if ((current == null)) {
            return;
        }
        PlatformGIS.run(new ISafeRunnable(){

            public void handleException( Throwable exception ) {
                CatalogUIPlugin.log("Error resetting: "+current, exception); //$NON-NLS-1$
            }

            public void run() throws Exception {
                List<IService> servers = new ArrayList<IService>();
                for( Iterator selection = current.iterator(); selection.hasNext(); ) {
                    try {
                        servers.add((IService) selection.next());
                    } catch (ClassCastException huh) {
                        CatalogUIPlugin.trace("Should not happen: " + huh); //$NON-NLS-1$
                    }
                }
                reset(servers, null);
            }


        });
    }
    /**
     * Allows a list of services to be reset.
     * <p>
     * In each case a replacement service is made using the same connection
     * parameters; the old service is disposed; and the replacement
     * placed into the catalog.
     * <p>
     * Client code listing to catalog change events will see the event fired off
     * any client code that has tried to cache the IService (to avoid doing a look
     * up each time) will be in trouble.
     * @param servers List of IService handles to reset
     * @param monitor Progress Monitor used to interrupt the command if needed
     */
    public static void reset( List<IService> servers, IProgressMonitor monitor ) {
        IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();

        for( IService original : servers ) {
            try {
                final URL ID = original.getIdentifier();
                CatalogUIPlugin.trace("Reset service " + original.getIdentifier()); //$NON-NLS-1$

                Map<java.lang.String, java.io.Serializable>
                    connectionParams = original.getConnectionParams();

                IService replacement = null; // unknown
                TEST: for( IService candidate : serviceFactory.createService(connectionParams) ) {
                    try {
                        CatalogUIPlugin.trace(ID + " : connecting"); //$NON-NLS-1$
                        IServiceInfo info = candidate.getInfo(monitor);

                        CatalogUIPlugin.trace(ID + " : found " + info.getTitle()); //$NON-NLS-1$
                        replacement = candidate;

                        break TEST;
                    } catch (Throwable t) {
                        CatalogUIPlugin.trace(ID + " : ... " + t.getLocalizedMessage()); //$NON-NLS-1$
                    }
                }
                if (replacement == null) {
                    CatalogUIPlugin.log("Could not reset "+ID+" - as we could not connect!",null); //$NON-NLS-1$
                    continue; // skip - too bad we cannot update status the original
                }
                catalog.replace(ID, replacement);
            } catch (Throwable failed) {
                CatalogUIPlugin.log("Reset failed", failed); //$NON-NLS-1$
            }
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection ) {
        if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
            current = (IStructuredSelection) selection;
        }
    }

    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
     *      org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart( IAction action, IWorkbenchPart targetPart ) {
    }
}
