/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * (C) C.U.D.A.M. Universita' di Trento
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.catalog.jgrass.core;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.ui.UDIGConnectionFactory;

/**
 * <p>
 * connection factory to the JGrasstools TMS service
 * </p>
 * <p>
 * <i>Note: based on the WMS plugin</i>
 * </p>
 * 
 * @author Andrea Antonello - www.hydrologis.com
 * @since 1.3.2
 */
public class JGTtmsConnectionFactory extends UDIGConnectionFactory {

    public boolean canProcess( Object context ) {
        if (context instanceof IResolve) {
            IResolve resolve = (IResolve) context;
            try {
                // what enters should be a JGrassService, which resolves to a
                // folder
                return (resolve.canResolve(File.class) && resolve.resolve(File.class, null).exists()//
                && resolve.resolve(File.class, null).getName().endsWith(".mapurl"));
            } catch (IOException e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Serializable> createConnectionParameters( Object context ) {
        if (context instanceof IResolve) {
            Map<String, Serializable> params = createParams((IResolve) context);
            if (!params.isEmpty())
                return params;
        }
        return Collections.EMPTY_MAP;
    }

    @SuppressWarnings("unchecked")
    static public Map<String, Serializable> createParams( IResolve handle ) {
        if (handle instanceof JGTtmsService) {
            JGTtmsService jgttmsService = (JGTtmsService) handle;
            return jgttmsService.getConnectionParams();
        }
        return Collections.EMPTY_MAP;
    }

    /** 'Create' params given the provided url, no magic occurs */
    static public Map<String, Serializable> createParams( URL url ) {
        JGTtmsServiceExtension factory = new JGTtmsServiceExtension();
        Map<String, Serializable> params = factory.createParams(url);
        if (params != null)
            return params;

        Map<String, Serializable> params2 = new HashMap<String, Serializable>();
        params2.put(JGTtmsServiceExtension.KEY, url);
        return params2;
    }

    public URL createConnectionURL( Object context ) {
        return null;
    }

}
