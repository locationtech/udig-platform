package net.refractions.udig.catalog.ui.workflow;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.UDIGConnectionFactory;
import net.refractions.udig.catalog.ui.UDIGConnectionFactoryDescriptor;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.catalog.ui.workflow.Workflow.State;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

public class EndConnectionState extends Workflow.State {

    /** selected descriptor from previous page * */
    UDIGConnectionFactoryDescriptor descriptor;

    /** the connection factory * */
    ;
    UDIGConnectionFactory factory;

    /** connection errors * */
    Map<IService, Throwable> errors;

    /** collection of services * */
    Set<IService> services;

    /** connection information * */
    Map<String, Serializable> params;
    Set<URL> urls;

    /***********************************************************************************************
     * 52¡North added next State
     **********************************************************************************************/
    EndConnectionState nextState;

    private boolean validateServices;

    /**
     * Create instance
     *
     * @param descriptor The connection factory descriptor to use in state
     * @param validateServices indicates whether the service should be probed for its members and
     *        metadata.
     */
    public EndConnectionState( UDIGConnectionFactoryDescriptor descriptor, boolean validateServices ) {
        this.descriptor = descriptor;
        factory = descriptor.getConnectionFactory();
        this.validateServices = validateServices;
    }

    @Override
    public boolean run( IProgressMonitor monitor ) throws IOException {
        if (factory == null)
            return false; // something went wrong, crap out
        monitor.beginTask(Messages.ConnectionState_task, IProgressMonitor.UNKNOWN);
        errors = null;
        try {
            if (urls == null && params == null) {
                // use the context object to try to build connection info
                Object context = getWorkflow().getContext();

                params = factory.createConnectionParameters(context);
                if (params != null && params.isEmpty())
                    params = null;
                URL url = factory.createConnectionURL(context);
                if (url != null) {
                    urls = new HashSet<URL>();
                    urls.add(url);
                }

                if (params == null && urls == null)
                    return false; // could not build connection info
            }

            // use the parameters/url to acquire a set of services
            IServiceFactory sFactory = CatalogPlugin.getDefault().getServiceFactory();
            monitor.setTaskName(Messages.ConnectionState_task);

            if (urls != null) {
                services = new HashSet<IService>();
                for( URL url : urls ) {
                    Set<IService> service = searchLocalCatalog(url, monitor);
                    if (service == null || service.isEmpty())
                        useServiceFactory(sFactory, url);
                    else
                        services.addAll(service);
                }
            } else {
                Set<IService> results = new HashSet<IService>(sFactory.createService(params));
                services = new HashSet<IService>();
                for( IService service : results ) {
                    Set<IService> tmp = searchLocalCatalog(service.getIdentifier(), monitor);
                    if (tmp == null || tmp.isEmpty()) {
                        services.add(service);
                    } else {
                        services.add(tmp.iterator().next());
                    }
                }
            }
        } catch (Throwable t) {
            CatalogPlugin.log(t.getLocalizedMessage(), t);
            return false;
        }

        try {
            // make the connection, catch errors for post handling
            errors = new HashMap<IService, Throwable>();
            for( Iterator<IService> itr = services.iterator(); itr.hasNext(); ) {
                IService service = itr.next();
                try {
                    SubProgressMonitor membersMonitor = new SubProgressMonitor(monitor, 2);
                    monitor.setTaskName(MessageFormat.format(Messages.ConnectionState_findLayers,
                            new Object[]{formatServiceID(service)}));
                    List<? extends IGeoResource> resources = service.resources(membersMonitor);

                    if (true || !validateServices)
                        continue;
                    try {
                        SubProgressMonitor infoMonitor = new SubProgressMonitor(monitor, 8);
                        try {
                            for( IGeoResource resource : resources ) {
                                try {
                                    monitor.setTaskName(MessageFormat.format(
                                            Messages.ConnectionState_loadingLayer,
                                            new Object[]{resource.getIdentifier().getRef()}));
                                    resource.getInfo(infoMonitor);
                                } catch (Exception e) {
                                    CatalogUIPlugin.log("", e); //$NON-NLS-1$
                                }
                            }
                        } finally {
                            infoMonitor.done();
                        }

                    } finally {
                        membersMonitor.done();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    errors.put(service, t);
                    itr.remove();
                }
            }
        } finally {
            monitor.done();
        }

        // even if errors occurred, we are still done
        // return true;
        return errors == null || errors.isEmpty();
    }

    private Set<IService> searchLocalCatalog( URL url, IProgressMonitor monitor ) {
        ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
        Set<IResolve> results = new HashSet<IResolve>(localCatalog.find(url, monitor));
        Set<IService> services = new HashSet<IService>();
        for( Iterator<IResolve> iter = results.iterator(); iter.hasNext(); ) {
            IResolve resolve = iter.next();
            if (resolve instanceof IService)
                services.add((IService) resolve);
        }
        if (results.isEmpty())
            return null;
        if (params != null) {
            Set<IService> matches = new HashSet<IService>();
            SERVICES: for( IService service : services ) {

                Map<String, Serializable> connectionParams = service.getConnectionParams();
                if (params.size() != connectionParams.size())
                    continue SERVICES;

                Set<Entry<String, Serializable>> entries = connectionParams.entrySet();
                MAP_COMPARISION: for( Entry<String, Serializable> entry : entries ) {
                    Serializable value = params.get(entry.getKey());
                    if (value == null) {
                        if (entry.getValue() == null)
                            continue MAP_COMPARISION;
                        else
                            continue SERVICES;
                    }
                    // permit more forgiving comparison of URLs.
                    if (value instanceof URL || entry.getValue() instanceof URL) {
                        if (!(entry.getValue() instanceof URL) || !(value instanceof URL))
                            continue SERVICES;

                        if (!URLUtils.urlEquals((URL) value, (URL) entry.getValue(), false))
                            continue SERVICES;
                        // TODO try to check IP in a non-blocking or quick manner.
                    } else if (!value.equals(entry.getValue())) {
                        continue SERVICES;
                    }

                }
            }
            return matches;
        }
        return services;
    }

    private void useServiceFactory( IServiceFactory sFactory, URL url ) {
        if (params != null) {
            services.addAll(sFactory.createService(params));
        } else
            services.addAll(sFactory.createService(url));
    }

    private String formatServiceID( IService service ) {
        URL identifier = service.getIdentifier();
        if ("file".equals(identifier.getProtocol())) { //$NON-NLS-1$
            File file = new File(identifier.getFile());
            return file.getName();
        }
        String host = identifier.getHost();
        return host;
    }

    /**
     * 52¡North added return true, if there are no errors and there is a successor state(which can
     * be the same state)
     */
    @Override
    public boolean hasNext() {
        return (errors != null && !errors.isEmpty()) || nextState != null;
    }

    /**
     * 52¡North changed Method returns null if there is not a successor state. Method returns a
     * ConnectionErrorState, if any errors have occurred. Method returns a State, if there are no
     * errors and there is a successor state
     */
    @Override
    public State next() {
        // if errors occurred, go a handling state, otherwise defer back to pipe
        if ((errors == null || errors.isEmpty()) && nextState == null) {
            return null;
        } else {
            if (!errors.isEmpty()) {
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

    public Set<IService> getServices() {
        return services;
    }

    public void setParams( Map<String, Serializable> params ) {
        this.params = params;
    }

    public void setURLs( Set<URL> urls ) {
        this.urls = urls;
        if (urls != null && urls.isEmpty())
            this.urls = null;
    }

    @Override
    public String getName() {
        return Messages.ConnectionState_name;
    }

    public boolean isValidateState() {
        return validateServices;
    }

    public void setValidateState( boolean validateState ) {
        this.validateServices = validateState;
    }

    public Map<IService, Throwable> getErrors() {
        if (errors == null)
            return Collections.emptyMap();
        return errors;
    }

    /**
     * 52¡North added
     *
     * @param state
     */
    public void setNextState( EndConnectionState state ) {
        nextState = state;
    }

    /**
     * 52¡North added method return the parameters for this state
     */
    public Map<String, Serializable> getParams() {
        return params;
    }

}
