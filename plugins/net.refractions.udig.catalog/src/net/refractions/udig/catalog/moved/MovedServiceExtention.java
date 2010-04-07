package net.refractions.udig.catalog.moved;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import net.refractions.udig.catalog.AbstractServiceExtention;
import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IService;

/**
 * Create a MovedService handle recording where a service was moved to 
 * in the catalog.
 * 
 * @author Jody Garnett (Refractions Research Inc)
 */
public class MovedServiceExtention extends AbstractServiceExtention {
    /**
     * Key used to look up in the connection parameters
     * the identifier of the this MovedService.
     * <p>
     * This is the same as the identifier of the service before it
     * was moved.
     */
    public static String ID_KEY = "id"; //$NON-NLS-1$
    
    /**
     * Key used to look up the connection parameter for the identifier
     * of the service where it exists now in the catalog.
     */
    public static String FORWARD_KEY = "forward"; //$NON-NLS-1$
        
    /**
     * Create the MovedService handle based on the provided parameters.
     */
    public IService createService( URL id, Map<String, Serializable> params ) {
        if ( id != null ){
            CatalogPlugin.trace("Ignoring requested id="+id+" for moved service", null ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        URL identifier = (URL) params.get( ID_KEY );
        URL forward = (URL) params.get( FORWARD_KEY );
        if( identifier == null || forward == null ){
            return null;
        }
        return new MovedService(new ID(id),new ID(forward));
    }

    public String reasonForFailure( Map<String, Serializable> params ) {
        if( !params.containsKey(ID_KEY) ||
            !params.containsKey(FORWARD_KEY) ){
            return null; // not interested
        }
        try {
            URL identifier = (URL) params.get( ID_KEY );
            URL forward = (URL) params.get( FORWARD_KEY );
            if( identifier == null || forward == null ){
                return null; // not interested
            }
        }
        catch (ClassCastException huh){
            return "ID and FORWARD are required to be URLs";
        }
        return null;
    }

}