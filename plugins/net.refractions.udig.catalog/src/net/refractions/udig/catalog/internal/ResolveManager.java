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
package net.refractions.udig.catalog.internal;

import java.io.IOException;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.catalog.IResolveManager;
import net.refractions.udig.core.internal.ExtensionPointList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Default implementation of {@link IResolveManager}
 * @author Jesse
 * @since 1.1.0
 */
public class ResolveManager implements IResolveManager {

    private static final String LIST_ID = "net.refractions.udig.catalog.resolvers"; //$NON-NLS-1$
    Map<IConfigurationElement,IResolveAdapterFactory> factories=new IdentityHashMap<IConfigurationElement, IResolveAdapterFactory>();
    Map<IResolveAdapterFactory,Set<Class>> exceptions=new IdentityHashMap<IResolveAdapterFactory,Set<Class>>();
    Set<IResolveAdapterFactory> registeredFactories=new HashSet<IResolveAdapterFactory>();

    public ResolveManager() {
        List<IConfigurationElement> extensionList = ExtensionPointList.getExtensionPointList(LIST_ID);

        for( IConfigurationElement element : extensionList ) {
            factories.put(element, null);
        }
    }

    public boolean canResolve( IResolve resolve, Class targetClass ) {
        //search registered factories for resolves
        if( findRegisteredFactory(resolve, targetClass)!=null )
            return true;

        Set<IConfigurationElement> elements = factories.keySet();
        // search extension point for resolves.
        for( IConfigurationElement element : elements ) {
            if( canResolve(element,resolve, targetClass) ){
                return true;
            }
        }

        return false;
    }

    private IResolveAdapterFactory findRegisteredFactory( IResolve resolve, Class targetClass ) {
        for( IResolveAdapterFactory factory : registeredFactories ) {
            if( factory.canAdapt(resolve, targetClass) ){
                Set<Class> set = exceptions.get(factory);
                if( set==null || !set.contains(targetClass) )
                    return factory;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private boolean canResolve( IConfigurationElement element, IResolve resolve, Class targetClass ) {
        String requiredType=element.getAttribute("resolveableType"); //$NON-NLS-1$
        try {
            Class< ? > clazz=resolve.getClass().getClassLoader().loadClass(requiredType);
            if( !clazz.isAssignableFrom(resolve.getClass() ) )
                    return false;

            IConfigurationElement[] children = element.getChildren("resolve"); //$NON-NLS-1$
            for( IConfigurationElement child : children ) {
                String resolveType=child.getAttribute("type"); //$NON-NLS-1$
                try{
                    ClassLoader classLoader = targetClass.getClassLoader();
                    if( classLoader==null )
                        classLoader=ClassLoader.getSystemClassLoader();
                    Class< ? > resolvedClass = classLoader.loadClass(resolveType);

                    if( targetClass.isAssignableFrom(resolvedClass) )
                        return true;
                }catch(ClassNotFoundException e2){
                    continue;
                }
            }
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public void registerResolves( IResolveAdapterFactory factory ) {
        registeredFactories.add(factory);
    }

    public <T> T resolve( IResolve resolve, Class<T> targetType, IProgressMonitor monitor ) throws IOException {
        if( !canResolve(resolve, targetType) )
            return null;
        IResolveAdapterFactory factory=findFactory(resolve, targetType);

        if( factory==null )
            return null;

        return targetType.cast(factory.adapt(resolve, targetType, monitor) );
    }

    private IResolveAdapterFactory findFactory( IResolve resolve, Class targetType ) {
        IResolveAdapterFactory factory = findRegisteredFactory(resolve, targetType);
        if( factory!=null )
            return factory;

        Set<IConfigurationElement> elements = factories.keySet();
        IConfigurationElement found=null;

        for( IConfigurationElement element : elements ) {
            if( canResolve(element, resolve, targetType) ){
                found=element;
                break;
            }
        }

        if( found==null )
            return null;

        factory=factories.get(found);
        if( factory!=null )
            return factory;

        try {
            factory=(IResolveAdapterFactory) found.createExecutableExtension("class"); //$NON-NLS-1$
            factories.put(found, factory);
        } catch (CoreException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }

        return factory;
    }

    public void unregisterResolves( IResolveAdapterFactory factory ) {
        registeredFactories.remove(factory);
        exceptions.remove(registeredFactories);
    }

    public void unregisterResolves( IResolveAdapterFactory factory, Class resolveType ) {
        if( !registeredFactories.contains(factory) )
            throw new IllegalArgumentException(factory+" is not a registered factory"); //$NON-NLS-1$

        Set<Class> set = exceptions.get(factory);
        if( set==null ){
            set=new HashSet<Class>();
            exceptions.put(factory, set);
        }

        set.add(resolveType);
    }

}
