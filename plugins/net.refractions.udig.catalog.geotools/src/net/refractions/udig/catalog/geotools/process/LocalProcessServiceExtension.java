package net.refractions.udig.catalog.geotools.process;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;

/**
 * Registers ProcessService in the local catalog.
 * <p>
 * Like MapGraphic these processes represent "local" content and will be registered into the local catalog
 * automatically during startup.
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.2.0
 */
public class LocalProcessServiceExtension implements ServiceExtension {
    /**
     * Key used to record the URL indicating a local process service.
     */
    public static final String KEY = "process"; //$NON-NLS-1$
    
    public IService createService( URL id, Map<String, Serializable> params ) {
        if( params.containsKey(KEY)){
            return new LocalProcessService();
        }
        return null;
    }
    
    public Map<String, Serializable> createParams( URL url ) {
        if( url != null && url.toExternalForm().startsWith( LocalProcessService.SERVICE_ID.toString())){
            Map<String,Serializable> map = new HashMap<String,Serializable>();
            map.put( KEY, url );
            return map;   
        }
        return null;
    }

}
