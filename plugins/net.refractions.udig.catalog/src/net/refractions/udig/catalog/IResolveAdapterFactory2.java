/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *    
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.catalog;

import java.util.List;


/**
 * Extension that allows {@link IResolveAdapterFactory} implementations
 * to provide a list of target adapter types in the same fashion as
 * the extension point.
 * <p>
 * 
 * @author Jody Garnett (LISAsoft)
 * @since version 1.3.2
 */
public interface IResolveAdapterFactory2 extends IResolveAdapterFactory {
    
    /**
     * List of adapter types types this factory can connect to.
     * <p>
     * This method is used by ResolveManager2 to short list appropriate factories
     * prior to calling {@link #canAdapt(IResolve, Class)}.
     * 
     * @return list of adapter types this factory can connenct to
     */
    public List<Class<?>> getAdapterList();
}