/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.geotools.data;

import java.io.IOException;
import java.util.Map;

import org.geotools.data.DataAccessFactory;
import org.geotools.data.DataAccessFactory.Param;

/**
 * Utility class for working with Factory Param array.
 * <p>
 * This should migrate to geotools because use params is a pain.
 * @author jody
 * @since 1.2.0
 */
public class Params {
    private Param[] params;
    private DataAccessFactory factory;

    public Params( DataAccessFactory factory ){
        this.factory = factory;
        this.params = factory.getParametersInfo();
    }
    
    public <T> T lookup( Class<T> type, String key, Map<String,?> connectionParams ){
        Param param = getParam( key );
        if( param == null ) return null;        
        if( param.type != type ){
            return null; // not a good idea
        }
        Object value;
        try {
            value = param.lookUp( connectionParams );
        } catch (IOException e) {
            return null; // silient
        }
        return type.cast( value );
    }
    /**
     * Return the first param value of the indicated type found in the connectionParams
     *
     * @param <T>
     * @param type
     * @param connectionParams
     * @return
     */
    public <T> T lookup( Class<T> type, Map<String,?> connectionParams ){
        if( type == null ) return null;
        for( Param param : params ){
            try {
                Object value = param.lookUp( connectionParams );
                if( value != null && type.isInstance(value)){
                    return type.cast(value);
                }
            } catch (IOException e) {
                // continue
            }
        }
        return null; // not found
    }
    
    /**
     * Look up params by key.
     *
     * @param key
     * @return param of the indicated key
     */
    public Param getParam( String key ){
        if( key == null ) return null;
        for( Param param : params ){
            if( param.key.equals(key )){
                return param;
            }
        }
        return null;
    }
    
    /**
     * Look up params by type; returning the first entry of the requested type.
     *
     * @param type
     * @return first param of the indicated type
     */
    public Param getParam( Class<?> type ){
        if( type == null ) return null;
        for( Param param : params ){
            if( param.type == type){
                return param;
            }
        }
        return null;
    }
    
    
}
