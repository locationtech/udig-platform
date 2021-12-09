/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.ui.workflow;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;
import org.locationtech.udig.catalog.ui.UDIGConnectionFactory;
import org.locationtech.udig.catalog.ui.UDIGConnectionFactoryDescriptor;
import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.catalog.ui.wizard.CatalogImport;
import org.locationtech.udig.core.Pair;
import org.locationtech.udig.ui.PlatformGIS;

/**
 * This is the "end of the line" when using an import wizard to add services to the catalog.
 * <p>
 * This wizard State is responsible for trying to connect to services.
 * <ul>
 * <li>If it can connect the method {@link #getServices()} makes the list available to the
 * {@link CatalogImport#performFinish} which will add the servies to the catalog</li>
 * <li>If it cannot connect it is responsible for indicating the connection error to the user so
 * they can fix things</li>
 * </ul>
 * This approach, while annoying as developer, prevents us needing to create services twice. You may
 * also find the code looking in the local catalog to see if we already have a connection.
 *
 * @author putnal
 * @since 1.2.0
 */
public class EndConnectionState extends State {

    /** selected descriptor from previous page * */
    UDIGConnectionFactoryDescriptor descriptor;

    /** the connection factory * */
    ;

    UDIGConnectionFactory factory;

    /** connection errors * */
    Map<IService, Throwable> errors;

    /** collection of services * */
    Collection<IService> services = new HashSet<>();

    /***********************************************************************************************
     * 52�North added next State
     **********************************************************************************************/
    EndConnectionState nextState;

    private boolean validateServices;

    private Collection<URL> selectedResources;

    /**
     * Create instance
     *
     * @param descriptor The connection factory descriptor to use in state
     * @param validateServices indicates whether the service should be probed for its members and
     *        metadata.
     */
    public EndConnectionState(UDIGConnectionFactoryDescriptor descriptor,
            boolean validateServices) {
        this.descriptor = descriptor;
        factory = descriptor.getConnectionFactory();
        this.validateServices = validateServices;
    }

    @Override
    public void init(IProgressMonitor monitor) throws IOException {
        super.init(monitor);
        disposeOldServices(monitor);
    }

    @Override
    public Pair<Boolean, State> dryRun() {
        boolean hasServices = !services.isEmpty();
        return new Pair<>(hasServices, null);
    }

    @Override
    public boolean run(IProgressMonitor monitor) throws IOException {
        if (factory == null)
            return false; // something went wrong, abort

        if (!services.isEmpty()) {
            return true;
        }

        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        List<IService> availableServices;

        monitor.beginTask(Messages.ConnectionState_task, IProgressMonitor.UNKNOWN);

        errors = null;
        try {
            // use the context object to try to build connection info
            Object context = getWorkflow().getContext();

            if (context instanceof IService) {
                services.add((IService) context);
                // services = Collections.singleton((IService)context);
            } else if (context instanceof IGeoResource) {
                IService service = ((IGeoResource) context).service(monitor);
                services.add((IService) context);
                services.add(service);
            } else {

                Map<String, Serializable> params = factory.createConnectionParameters(context);

                URL url = factory.createConnectionURL(context);

                if (params == null && url == null)
                    return false; // could not build connection info

                if (url != null) {
                    availableServices = catalog.constructServices(url, monitor);
                    if (!availableServices.isEmpty()) {
                        services.add(availableServices.iterator().next());
                        return true;
                    }
                }

                if (params != null && !params.isEmpty()) {
                    availableServices = catalog.constructServices(params, monitor);
                    if (!availableServices.isEmpty()) {
                        services.add(availableServices.iterator().next());
                    }
                }
            }
        } catch (Throwable t) {
            CatalogPlugin.log(t.getLocalizedMessage(), t);
            return false;
        }

        // even if errors occured, we are still done
        // return true;
        return errors == null || errors.isEmpty();
    }

    private void disposeOldServices(IProgressMonitor monitor) {
        if (services == null || services.isEmpty()) {
            return;
        }

        final List<IService> toDispose = new ArrayList<>(services);
        services.clear();

        IRunnableWithProgress runnable = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) {
                monitor.beginTask("Disposing dereferenced services", toDispose.size()); //$NON-NLS-1$
                // dispose old services
                for (IService service : toDispose) {
                    if (service.parent(monitor) == null) {
                        service.dispose(SubMonitor.convert(monitor));
                    }
                    monitor.worked(1);
                }
            }
        };

        // disposing of services could take a long time so do it in a non-UI thread
        if (Display.getCurrent() != null) {
            PlatformGIS.run(runnable);
        } else {
            try {
                runnable.run(new NullProgressMonitor());
            } catch (InvocationTargetException e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            } catch (InterruptedException e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            }
        }
    }

    /**
     * Deprecated use CatalogImpl.constructServices();
     * <p>
     * Responsible for providing a list of services produced either/and connection parameters or
     * urls.
     * <p>
     * Calling code is responsible for adding these to the local catalog (or otherwise cleaning up
     * the mess).
     *
     * @param monitor
     * @param params
     * @param urls
     * @return List of matching Services (may be created from service factory or retrieved from
     *         local catalog)
     */
    @Deprecated
    public static Collection<IService> constructServices(IProgressMonitor monitor,
            Map<String, Serializable> params, Collection<URL> urls) {
        // use the parameters/url to acquire a set of services
        //
        IServiceFactory sFactory = CatalogPlugin.getDefault().getServiceFactory();

        monitor.setTaskName(Messages.ConnectionState_task);

        Collection<IService> services = new HashSet<>();
        if (urls != null && !urls.isEmpty()) {
            for (URL url : urls) {
                Collection<IService> searchResult = searchLocalCatalog(url, monitor);
                if (searchResult.isEmpty()) {
                    List<IService> created = sFactory.createService(url);
                    services.addAll(created);
                } else {
                    services.addAll(searchResult);
                }
            }
        }

        if (params != null && !params.isEmpty()) {
            Set<IService> results = new HashSet<>(sFactory.createService(params));
            for (IService service : results) {
                Collection<IService> searchResult = searchLocalCatalog(service.getIdentifier(),
                        monitor);
                if (searchResult.isEmpty()) {
                    services.add(service);
                } else {
                    services.addAll(searchResult);
                }
            }
        }
        return services;
    }

    private static Collection<IService> searchLocalCatalog(URL url, IProgressMonitor monitor) {
        ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();

        List<IResolve> resolves = localCatalog.find(url, monitor);
        ArrayList<IService> services = new ArrayList<>();
        for (IResolve iResolve : resolves) {
            if (iResolve instanceof IService) {
                IService service = (IService) iResolve;
                services.add(service);
            }
        }

        return services;

    }

    /**
     * 52�North added return true, if there are no errors and there is a successor state(which can
     * be the same state)
     */
    @Override
    public boolean hasNext() {
        return (errors != null && !errors.isEmpty()) || nextState != null;
    }

    /**
     * 52�North changed Method returns null if there is not a succesor state. Method returns a
     * ConnectionErrorState, if any errors have occured. Method returns a State, if there are no
     * errors and there is a successor state
     */
    @Override
    public State next() {
        // if errors occured, go a handling state, otherwise defer back to pipe
        if ((errors == null || errors.isEmpty()) && nextState == null) {
            return null;
        } else {
            if (errors != null && !errors.isEmpty()) {
                return new ConnectionErrorState(errors);
            } else {
                return nextState;
            }
        }

    }

    public UDIGConnectionFactoryDescriptor getDescriptor() {
        return descriptor;
    }

    public UDIGConnectionFactory getConnectionFactory() {
        return factory;
    }

    public Collection<IService> getServices() {
        return services;
    }

    @Override
    public String getName() {
        return Messages.ConnectionState_name;
    }

    public boolean isValidateState() {
        return validateServices;
    }

    public void setValidateState(boolean validateState) {
        this.validateServices = validateState;
    }

    public Map<IService, Throwable> getErrors() {
        if (errors == null)
            return Collections.emptyMap();
        return errors;
    }

    /**
     * 52�North added
     *
     * @param state
     */
    public void setNextState(EndConnectionState state) {
        nextState = state;
    }

    /**
     * Sets the collection of "Selected" or "preferred" resources. This allows the connection page
     * to specify a set of preferred resource for the next states.
     *
     * @param resourceIDs the ids of the preferred resources
     */
    public void setSelectedResources(Collection<URL> resourceIDs) {
        this.selectedResources = new ArrayList<>(resourceIDs);
    }

    /**
     * Returns the collection of "Selected" or "preferred" resources. This allows the connection
     * page to specify a set of preferred resource for the next states. returns the ids of the
     * preferred resources
     */
    public Collection<URL> getSelectedResources() {
        if (selectedResources == null) {
            return Collections.emptyList();
        }
        return selectedResources;
    }

    public void setServices(Collection<IService> services2) {
        disposeOldServices(new NullProgressMonitor());
        services.addAll(services2);
    }

}
