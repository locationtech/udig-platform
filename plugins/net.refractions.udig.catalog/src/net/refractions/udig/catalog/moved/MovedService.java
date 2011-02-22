package net.refractions.udig.catalog.moved;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import net.refractions.udig.catalog.IForward;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;

/**
 * This class represents a service that has been moved to a new location.
 * <p>
 * For details please see the IForward interface.
 * </p>
 * @author Jody Garnett
 */
public class MovedService extends IService implements IForward  {
    URL identifier;
    URL forward;

    public MovedService( URL id, URL forward ){
        this.identifier = id;
        this.forward = forward;
    }
    public Map<String, Serializable> getConnectionParams() {
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put( MovedServiceExtention.ID_KEY, identifier );
        params.put( MovedServiceExtention.FORWARD_KEY, forward );
        return params;
    }

    public IServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return null;
    }

    @Override
    public List< ? extends IGeoResource> resources( IProgressMonitor monitor ) throws IOException {
        return Collections.EMPTY_LIST;
    }

    public URL getIdentifier() {
        return identifier;
    }

    public Throwable getMessage() {
        return new IllegalStateException("Service has moved to "+forward );
    }

    public Status getStatus() {
        return Status.BROKEN;
    }

    public URL getForward() {
        return forward;
    }
}
