package net.refractions.udig.catalog.ui.workflow;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ui.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 *
 * State selects IGeoResources.  The selected resources are those in the {@link #getResources()} map.  The keys are the selected
 * resources and the values are the parents.  State normally uses a ConnectionState but it isn't required is {@link #setServices(List)}
 * is used to set the services.
 *
 * @author Justin Deolive
 * @since 1.1.0
 */
public class ResourceSelectionState extends Workflow.State {

    /** list of resources * */
    Map<IGeoResource, IService> resources;
    private Collection<IService> services;

    public Collection<IService> getServices() {
        if( services!=null )
            return services;
        EndConnectionState state = getWorkflow().getState(EndConnectionState.class);
        if (state == null)
            return null; // could occur if the connection state added dynamically

        return state.getServices();
    }

    public void setServices( Collection<IService> services ) {
        this.services = services;
    }

    public void setResources( Map<IGeoResource, IService> resources ) {
        this.resources = resources;
    }

    public Map<IGeoResource, IService> getResources() {
        return resources;
    }

    @Override
    public void init( IProgressMonitor monitor ) throws IOException {
        super.init(monitor);

        // try to generate some default resources based on context
        // LinkedHashMap to keep the order
        resources = new LinkedHashMap<IGeoResource, IService>();

        // use the context object to try and match a resource
        Object context = getWorkflow().getContext();
        if (context != null) {
            URL url = CatalogPlugin.locateURL(context);
            if (url != null) {
                addResource(monitor, url);
            }else if( context instanceof Iterable ){
                Iterable iterable = (Iterable) context;
                for( Object object : iterable ) {
                    url=CatalogPlugin.locateURL(object);
                    if( url!=null )
                        addResource(monitor, url);
                }
            }
        }

        // look through all the services, automatically "select" those with
        // only a single member
        // TODO: replace this with a preference or hinting system
        Collection<IService> services = getServices();
        List<IService> toRemove=new ArrayList<IService>();
        for( IService service : services ) {
            List< ? extends IGeoResource> members = service.resources(monitor);
            if (members != null && members.size() < 1){
                toRemove.add(service);
                continue;
            }
            if( members!=null && members.size()==1 ){
                resources.put( members.get(0), service);
            }
//            else {
//                for( IGeoResource resource : members ) {
//                    resources.put(resource, service);
//                }
//            }
        }
        if (!toRemove.isEmpty())
            services.removeAll(toRemove);
    }

    private void addResource( IProgressMonitor monitor, URL url ) throws IOException {
        IGeoResource match = match(getServices(), url, monitor);
        if (match != null) {
            resources.put(match, match.service(monitor));
        }
    }

    @Override
    public boolean run( IProgressMonitor monitor ) throws IOException {
        // complete if all the resources have been "selected"

        if (resources == null || resources.isEmpty())
            return false;

        int count = 0;

        Set<IService> parents=new HashSet<IService>();

        for( Map.Entry<IGeoResource, IService> entry: resources.entrySet() ) {
            parents.add(entry.getValue());
        }

        Collection<IService> services = getServices();
        try{
        	monitor.beginTask("",services.size()*10); //$NON-NLS-1$
        for( IService service : services ) {
        	SubProgressMonitor subMonitor = new SubProgressMonitor(monitor,10);
        	try{

                URL identifier = service.getIdentifier();
                monitor.setTaskName(MessageFormat.format(Messages.ResourceSelectionState_taskName, new Object[] {identifier.getProtocol()+"://"+identifier.getPath()})); //$NON-NLS-1$
            count += service.resources(subMonitor).size();
        	}finally{
        		subMonitor.done();
        	}
        }
        }finally{
        	monitor.done();
        }

        return count == resources.size() || parents.size()==getServices().size();
    }

    // Seems to try to match the url to a Georesource.
    private IGeoResource match( Collection<IService> services, URL url, IProgressMonitor monitor )
            throws IOException {

    	// if there is no ref then the url is not matching a georesource
        if (url.getRef() == null || url.getRef().equals("")) //$NON-NLS-1$
            return null;

        for( IService service : services ) {
            for( IGeoResource resource : service.resources(monitor) ) {
                if (resource.getIdentifier() == null)
                    continue;

                if (url.equals(resource.getIdentifier()))
                    return resource;
            }
        }

        return null;
    }

	@Override
	public String getName() {
		return Messages.ResourceSelectionState_stateName;
	}
}
