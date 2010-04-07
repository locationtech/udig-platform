package net.refractions.udig.catalog.ui.workflow;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.UDIGConnectionFactory;
import net.refractions.udig.catalog.ui.UDIGConnectionFactoryDescriptor;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.core.Pair;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class EndConnectionState extends State {

    /** selected descriptor from previous page * */
    UDIGConnectionFactoryDescriptor descriptor;

    /** the connection factory * */
    ;
    UDIGConnectionFactory factory;

    /** connection errors * */
    Map<IService, Throwable> errors;

    /** collection of services * */
    Collection<IService> services=new HashSet<IService>();

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
    public EndConnectionState( UDIGConnectionFactoryDescriptor descriptor, boolean validateServices ) {
        this.descriptor = descriptor;
        factory = descriptor.getConnectionFactory();
        this.validateServices = validateServices;
    }
    
    @Override
    public void init( IProgressMonitor monitor ) throws IOException {
        super.init(monitor);
        disposeOldServices(monitor);
    }

    @Override
    public Pair<Boolean, State> dryRun() {
        boolean hasServices = !services.isEmpty();
        return new Pair<Boolean, State>(hasServices, null);
    }

    
    @Override
    public boolean run( IProgressMonitor monitor ) throws IOException {
        if (factory == null)
            return false; // something went wrong, crap out

        if( !services.isEmpty() ){
            return true;
        }
        monitor.beginTask(Messages.ConnectionState_task, IProgressMonitor.UNKNOWN);
        errors = null;
        try {
                // use the context object to try to build connection info
            Object context = getWorkflow().getContext();
    
            if( context instanceof IService ){
                services = Collections.singleton((IService)context);
            }else if (context instanceof IGeoResource){
                IService service = ((IGeoResource) context).service(monitor);
                services = Collections.singleton(service);
            }else{
            
                Map<String, Serializable> params = factory.createConnectionParameters(context);
                if (params != null && params.isEmpty())
                    params = null;
                
                URL url = factory.createConnectionURL(context);
                HashSet<URL> urls = new HashSet<URL>();
                if (url != null) {
                    urls.add(url);
                }
        
                if (params == null && urls.isEmpty())
                    return false; // could not build connection info
    
                services = constructServices(monitor, params, urls);
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
                    List< ? extends IGeoResource> resources = service.resources(membersMonitor);

                    if (true || !validateServices)
                        continue;
//                    try {
//                        SubProgressMonitor infoMonitor = new SubProgressMonitor(monitor, 8);
//                        try {
//                            for( IGeoResource resource : resources ) {
//                                try {
//                                    monitor.setTaskName(MessageFormat.format(
//                                            Messages.ConnectionState_loadingLayer,
//                                            new Object[]{resource.getIdentifier().getRef()}));
//                                    resource.getInfo(infoMonitor);
//                                } catch (Exception e) {
//                                    CatalogUIPlugin.log("", e); //$NON-NLS-1$
//                                }
//                            }
//                        } finally {
//                            infoMonitor.done();
//                        }
//
//                    } finally {
//                        membersMonitor.done();
//                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    errors.put(service, t);
                    itr.remove();
                }
            }
        } finally {
            monitor.done();
        }

        // even if errors occured, we are still done
        // return true;
        return errors == null || errors.isEmpty();
    }
    
    private void disposeOldServices( IProgressMonitor monitor ) {
        if (services == null || services.isEmpty()) {
            return;
        }

        final List<IService> toDispose = new ArrayList<IService>(services);
        services.clear();

        IRunnableWithProgress runnable = new IRunnableWithProgress(){
            public void run(IProgressMonitor monitor) {
                monitor.beginTask("Disposing dereferenced services", toDispose.size());
                // dispose old services
                for( IService service : toDispose ) {
                    if( service.parent(monitor)==null){
                        service.dispose(SubMonitor.convert(monitor));
                    }
                    monitor.worked(1);
                }
            }
        };
        
        // disposing of services could take a long time so do it in a non-UI thread
        if( Display.getCurrent()!=null ){
            PlatformGIS.run(runnable);
        }else{
            try {
                runnable.run(new NullProgressMonitor());
            } catch (InvocationTargetException e) {
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            } catch (InterruptedException e) {
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
        }
    }

    public static Collection<IService> constructServices( IProgressMonitor monitor, Map<String, Serializable> params, Collection<URL> urls ) {

        // use the parameters/url to acquire a set of services
        IServiceFactory sFactory = CatalogPlugin.getDefault().getServiceFactory();
        monitor.setTaskName(Messages.ConnectionState_task);

        Collection<IService> services = new HashSet<IService>();
        if (urls != null) {
            for( URL url : urls ) {
                Collection<IService> searchResult = searchLocalCatalog(url, monitor);
                if (searchResult.isEmpty()) {
                    List<IService> created = sFactory.createService(url);
                    services.addAll(created);
                } else {
                    services.addAll(searchResult);
                }
            }
        } 
        
        if( params!=null ){
            Set<IService> results = new HashSet<IService>(sFactory.createService(params));
            for( IService service : results ) {
                Collection<IService> searchResult = searchLocalCatalog(service.getIdentifier(), monitor);
                if (searchResult.isEmpty() ) {
                    services.add(service);
                } else {
                    services.addAll(searchResult);
                }
            }
        }
        
        return services;
    }

    private static Collection<IService> searchLocalCatalog( URL url, IProgressMonitor monitor ) {
        ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
        
        List<IResolve> resolves = localCatalog.find(url, monitor);
        ArrayList<IService> services = new ArrayList<IService>();
        for( IResolve iResolve : resolves ) {
            if (iResolve instanceof IService) {
                IService service = (IService) iResolve;
                services.add(service);
            }
        }
        
        return services;

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

    public void setValidateState( boolean validateState ) {
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
    public void setNextState( EndConnectionState state ) {
        nextState = state;
    }

    /**
     * Sets the collection of "Selected" or "preferred" resources. This allows the connection page
     * to specify a set of preferred resource for the next states.
     * 
     * @param resourceIDs the ids of the preferred resources
     */
    public void setSelectedResources( Collection<URL> resourceIDs ) {
        this.selectedResources = new ArrayList<URL>(resourceIDs);
    }

    /**
     * Returns the collection of "Selected" or "preferred" resources. This allows the connection page
     * to specify a set of preferred resource for the next states. returns the ids of the preferred
     * resources
     */
    public Collection<URL> getSelectedResources() {
        if( selectedResources==null ){
            return Collections.emptyList();
        }
        return selectedResources;
    }

    public void setServices( Collection<IService> services2 ) {
        disposeOldServices(new NullProgressMonitor());
        services.addAll(services2);
    }

}