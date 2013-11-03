/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog;

import java.util.List;


/**
 * Extension that allows {@link IResolveAdapterFactory} implementations
 * to provide a type information in the same fashion as the extension point.
 * <p>
 * 
 * @author Jody Garnett (LISAsoft)
 * @since version 1.3.2
 */
public abstract class ResolveAdapterFactory implements IResolveAdapterFactory {
    
    /**
     * List of adapter types types this factory can connect to.
     * <p>
     * This method is used by ResolveManager2 to short list appropriate factories
     * prior to calling {@link #canAdapt(IResolve, Class)}.
     * 
     * @return list of adapter types this factory can connect to
     */
    public abstract List<Class<?>> getAdapterList();
    
    public abstract List<String> getAdapterNames();
    
    public abstract Class<?> getResolveType();
    
    public abstract String getResolveName();
}
