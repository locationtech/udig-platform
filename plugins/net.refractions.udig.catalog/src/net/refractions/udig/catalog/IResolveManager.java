/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * An IResolverFactoryManager processes the net.refractions.udig.resolvers extension point and allows client 
 * code to resolve {@link IResolve} objects to other objects. 
 * 
 * <p>
 * This class is similar in functionality to the IAdapterManager in eclipse.
 * </p>
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface IResolveManager {

    /**
     * Resolves an {@link IResolve} to another object or throws a {@link IllegalArgumentException} if the resolve
     * cannot be resolved to the targetType
     *
     * @param resolve the {@link IResolve} to resolve.
     * @param targetType the desired type to resolve to
     * @param monitor A monitor for providing feedback about the current progress.
     * @return an object of targetType.
     */
    public <T> T resolve(IResolve resolve, Class<T> targetType, IProgressMonitor monitor) throws IOException;

    /**
     * Returns true if the resolve can be resolved to the targetClass.
     *
     * @param resolve resolve to resolve to another class
     * @param targetClass desired type to resolve to
     * @return true if the resolve can be resolved to the targetClass.
     */
    public boolean canResolve(IResolve resolve, Class<?> targetClass);

    //
    // Manage RsolveAdapterFacotry instances
    //
    /**
     * Register factory with the Resolve manager.
     * <p>
     * The {@link ResolveAdapterFactory#getResolveName()} is used
     * register.
     * 
     * @param factory
     */
    public void register(ResolveAdapterFactory factory );
    

    /**
     *  Unregisters a factory from the ResolveManager.
     * <p>
     * The {@link ResolveAdapterFactory#getResolveName()} is used.
     * 
     * @param factory
     */
    public void unregister( ResolveAdapterFactory factory );
    
    //
    // Generic IResolveAdapterFactory management
    // (Does not provide type information)
    //
    /**
     * Registers factory with the ResolveManager with the
     * adaptable type of {@link IResolve}.
     *
     * @param factory new factory to use for resolving IResolves
     */
    public void registerResolves(IResolveAdapterFactory factory);

    /**
     * Unregisters a factory from the ResolveManager.
     *
     * @param factory factory to unregister.
     */
    public void unregisterResolves(IResolveAdapterFactory factory);
    
    /**
     * Excludes a type from being resolved for a given factory.
     * <p>
     * ResolveManager will not use the provided factory to resolve to the resolveType
     *
     * @param factory 
     * @param resolveType
     * @deprecated Functionality superseded by {@link ResolveAdapterFactory#getAdapterNames()}
     */
    public void unregisterResolves(IResolveAdapterFactory factory, Class<?> resolveType);

}
