package net.refractions.udig.catalog;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

/**
 * Advertise the availability of an IService implementation to the catalog.
 * <p>
 * This class needs to fulfill two objects:
 * <ul>
 * <li>ServiceExtension: captures what is needed to create a Service from either a URL or Connection
 * parametrs.</li>
 * <li>ServiceExtension2: communicates failure if creating a service is not possible</li>
 * </ul>
 * This class represents a stable base to extend; rather then keeping up with any changes to
 * ServiceExtension and ServiceExtension2. Ideas for future improvements include producing a data
 * structure to hold both connection failures; and *warnings* assocaited with service creation.
 * <p>
 * Clients should not use this class directly but instead make use of ServiceFactory:
 * <code>CatalogPlugin().getDefault().getServiceFactory().createService( url )</code>
 * </p>
 * The IRepository.acquire method also makes use of the above ServiceFactory.
 */
public abstract class IServiceExtension implements ServiceExtension, ServiceExtension2 {
    /**
     * Default implementation returns null; indicating that the provided URL
     * is not supported.
     */
    public Map<String, Serializable> createParams( URL url ) {
        return null;
    }

    public abstract IService createService( URL id, Map<String, Serializable> params );

    public String reasonForFailure( Map<String, Serializable> params ) {
        return "not supported";
    }

    public String reasonForFailure( URL url ) {
        return "not supported";
    }

}