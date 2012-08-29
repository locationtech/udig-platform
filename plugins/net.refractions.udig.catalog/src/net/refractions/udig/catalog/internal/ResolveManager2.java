/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.catalog.IResolveAdapterFactory2;
import net.refractions.udig.catalog.IResolveManager;
import net.refractions.udig.core.internal.ExtensionPointList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Second implementation of IResolveManager optimized based on review of AdapterManager.
 * <p>
 * Improvements provided by this implementation:
 * <ul>
 * <li>(pending) The ability to register (and unregister) IResolveAdapterFactory instances
 * programatically rather that strictly through the "net.refractions.udig.catalog.resolvers"
 * extensions point.
 * <li>
 * <li>(pending) Explicit control over if a plugin should be loaded in order to provide a needed
 * factory</li>
 * </ul>
 * 
 * @author Jody Garnett
 * @since 1.3.2
 */
public class ResolveManager2 implements IResolveManager {

    private static final String RESOLVE_FACTORY_EXTENSION_POINT = "net.refractions.udig.catalog.resolvers"; //$NON-NLS-1$

    // Maps resolve class name --> Map (target class name --> factory instance )
    Map<String, Map<String, IResolveAdapterFactory>> adapterLookup;

    // Maps factory instance -> Map (target class name --> target class )
    // Map<IResolveAdapterFactory,Map<String,Class<?>>> classLookup;
    // ReentrantReadWriteLock classLookupLock;

    // Map of Class -> Class[]
    Map<Class<?>, List<Class<?>>> classSearchOrderLookup;

    // Map full resolve class name --> List of factories
    Map<String, List<IResolveAdapterFactory>> factories;

    public ResolveManager2() {
        // classLookupLock = new ReentrantReadWriteLock();
        factories = new HashMap<String, List<IResolveAdapterFactory>>();
        List<IConfigurationElement> extensionList = ExtensionPointList
                .getExtensionPointList(RESOLVE_FACTORY_EXTENSION_POINT);
        for (IConfigurationElement element : extensionList) {
            ResolveAdapterFactoryProxy proxy = new ResolveAdapterFactoryProxy(element);
            String resolveName = proxy.getResolveName();
            registerResolves(proxy, resolveName);
        }
    }

    @Override
    public <T> T resolve(IResolve resolve, Class<T> targetClass, IProgressMonitor monitor)
            throws IOException {
        Map<String, IResolveAdapterFactory> available = getFactories(resolve.getClass());
        IResolveAdapterFactory factory = available.get(targetClass.getName());
        if (factory == null)
            return null; // nobody knows how to do this one

        T connected = factory.adapt(resolve, targetClass, monitor);

        return connected;
    }

    @Override
    public boolean canResolve(IResolve resolve, Class<?> targetClass) {
        Map<String, IResolveAdapterFactory> available = getFactories(resolve.getClass());
        IResolveAdapterFactory factory = available.get(targetClass.getName());
        if (factory == null)
            return false; // nobody knows how to do this one
        try {
            return factory.canAdapt(resolve, targetClass);
        } catch (Throwable t) {
            String msg = "IResolveAdapterFactory " + factory.getClass().getName()
                    + " canAdapt check failed:" + t;
            CatalogPlugin.trace(msg, t);
            return false; // factory was unable to function
        }

    }

    public void registerResolves(IResolveAdapterFactory factory, String className) {
        List<IResolveAdapterFactory> register = factories.get(className);
        if (register == null) {
            register = new ArrayList<IResolveAdapterFactory>(2);
            factories.put(className, register);
        }
        register.add(factory);
    }

    @Override
    public void registerResolves(IResolveAdapterFactory factory) {
        //
    }

    @Override
    public void unregisterResolves(IResolveAdapterFactory factory) {
        //
    }

    @Override
    public void unregisterResolves(IResolveAdapterFactory factory, Class<?> resolveType) {
        //
    }

    /**
     * Computes the adapters that the provided class can adapt to, along with the factory object
     * that can perform that transformation. Returns a table of adapter class name to factory
     * object.
     * 
     * @param adaptable
     */
    private Map<String, IResolveAdapterFactory> getFactories(Class<?> resolveClass) {
        // cache reference to lookup to protect against concurrent flush
        if (adapterLookup == null) {
            adapterLookup = Collections
                    .synchronizedMap(new HashMap<String, Map<String, IResolveAdapterFactory>>());
        }
        String resolveName = resolveClass.getName();
        Map<String, IResolveAdapterFactory> table = adapterLookup.get(resolveName);
        if (table != null) {
            return table;
        }
        table = new HashMap<String, IResolveAdapterFactory>();

        for (Class<?> type : searchOrder(resolveClass)) {
            String typeName = type.getName();
            addFactoriesFor(typeName, table);
        }
        adapterLookup.put(resolveName, table);
        return table;
    }

    public List<Class<?>> searchOrder(Class<?> resolveClass) {
        if (classSearchOrderLookup == null) {
            classSearchOrderLookup = new HashMap<Class<?>, List<Class<?>>>();
        }

        List<Class<?>> classes = classSearchOrderLookup.get(resolveClass);

        // compute class order only if it hasn't been cached before
        if (classes == null) {
            classes = doComputeClassOrder(resolveClass);
            classSearchOrderLookup.put(resolveClass, classes);
        }
        return classes;
    }

    private List<Class<?>> doComputeClassOrder(Class<?> resolveClass) {
        // first traverse class hierarchy
        List<Class<?>> classHierarchy = new ArrayList<Class<?>>();
        Class<?> traverse = resolveClass;
        while (traverse != null) {
            classHierarchy.add(traverse);
            traverse = traverse.getSuperclass();
        }
        // compute extended search order with classHierarchy and taking interfaces into account
        List<Class<?>> searchOrder = new ArrayList<Class<?>>(classHierarchy);
        Set<Class<?>> seen = new HashSet<Class<?>>(4);

        // now traverse interface hierarchy for each class
        for (Class<?> tranverse : classHierarchy) {
            computeInterfaceOrder(tranverse.getInterfaces(), searchOrder, seen);
        }
        return Collections.unmodifiableList(searchOrder);
    }

    private void computeInterfaceOrder(Class<?>[] interfaces, List<Class<?>> classes,
            Set<Class<?>> seen) {
        List<Class<?>> newInterfaces = new ArrayList<Class<?>>(interfaces.length);

        for (Class<?> interfaceClass : interfaces) {
            if (!seen.contains(interfaceClass)) {
                seen.add(interfaceClass);
                newInterfaces.add(interfaceClass);
            }
        }
        for (Class<?> interfaceClass : newInterfaces) {
            computeInterfaceOrder(interfaceClass.getInterfaces(), classes, seen);
        }
    }

    private void addFactoriesFor(String typeName, Map<String, IResolveAdapterFactory> table) {
        List<IResolveAdapterFactory> factoryList = factories.get(typeName);
        if (factoryList == null) {
            return;
        }
        for (IResolveAdapterFactory factory : factoryList) {
            if (factory instanceof ResolveAdapterFactoryProxy) {
                ResolveAdapterFactoryProxy proxy = (ResolveAdapterFactoryProxy) factory;
                for (String adapterName : proxy.getAdapterNames()) {
                    if (table.get(adapterName) == null) {
                        table.put(adapterName, factory);
                    }
                }
            } else if (factory instanceof IResolveAdapterFactory2) {
                IResolveAdapterFactory2 factory2 = (IResolveAdapterFactory2) factory;
                for (Class<?> adapterClass : factory2.getAdapterList()) {
                    String adapterName = adapterClass.getName();
                    if (table.get(adapterName) == null) {
                        table.put(adapterName, factory);
                    }
                }
            } else {
                // unable to deal with this factory? we could put it in a special list
                // we check all the time?
            }
        }
    }

    /**
     * ClassLoader aware "class for name" implementation making use of factory to provide a
     * classloader. Thanks to the inspiration of AdapterManager this code makes use of a cache to
     * prevent the reloading of classes.
     */
    // private Class<?> classForName( IResolveAdapterFactory factory, String adapterName ){
    // Class<?> adapterClass = cacheLookup(factory, adapterName);
    // if( adapterClass != null ) return adapterClass; // that was quick
    //
    // adapterClass = loadClass( factory, adapterName );
    // cache( factory, adapterClass ); // we even cache null to prevent repeat lookup
    //
    // return adapterClass;
    // }
    //
    // private void cache( IResolveAdapterFactory factory, Class<?> adapterClass ){
    // try {
    // classLookupLock.writeLock().lock();
    // if( classLookup == null ){
    // classLookup = new HashMap<IResolveAdapterFactory, Map<String,Class<?>>>();
    // }
    // Map<String, Class<?>> typeMap = classLookup.get( factory );
    // if( typeMap == null ){
    // typeMap = new HashMap<String, Class<?>>();
    // classLookup.put(factory, typeMap);
    // }
    // typeMap.put( adapterClass.getName(), adapterClass );
    // }
    // finally {
    // classLookupLock.writeLock().unlock();
    // }
    // }
    /**
     * Checks classLookup which provides a type map used to prevent repeated (slow) classloader
     * calls
     * 
     * @param factory
     * @param adapterName
     * @return requested class, or null if not found
     */
    // private Class<?> cacheLookup( IResolveAdapterFactory factory, String adapterName ){
    // try {
    // classLookupLock.readLock().lock();
    //
    // if( classLookup == null ) return null; // not found
    //
    // Map<String, Class<?>> typeMap = classLookup.get( factory );
    // if( typeMap == null ) return null; // not found
    //
    // Class<?> adapterClass = typeMap.get( adapterName );
    // return adapterClass;
    // }
    // finally {
    // classLookupLock.readLock().unlock();
    // }
    // }

    /**
     * Class cache design taken from AdapterManager (monkey see monkey do).
     * 
     * @param factory
     * @param typeName
     * @return
     */
    private static Class<?> loadClass(IResolveAdapterFactory factory, String className) {
        if (className == null) {
            return null;
        }
        try {
            ClassLoader contextClassloader = factory.getClass().getClassLoader();
            return contextClassloader.loadClass(className);
        } catch (ClassNotFoundException notFound) {
            // check if the factory knows what to do - perhaps load via spring
            // or service provider interface
            if (factory instanceof IResolveAdapterFactory2) {
                for (Class<?> adpaterClass : ((IResolveAdapterFactory2) factory).getAdapterList()) {
                    String adapterName = adpaterClass.getCanonicalName();
                    if (className.equals(adapterName)) {
                        return adpaterClass;
                    }
                }
            }
        }
        return null; // not available
    }

    /**
     * Proxy supporting the lazy loading of an IResolveAdapterFactory while providing access to its
     * list of supported types.
     * 
     * @author Jody Garnett (LISAsoft)
     * @since 1.3.2
     */
    static class ResolveAdapterFactoryProxy implements IResolveAdapterFactory2 {
        IConfigurationElement config;

        IResolveAdapterFactory factory;

        /** Provide class lookup by full name */
        Map<String, Class<?>> adapterTypes;

        private String resolveName;

        private Class<?> resolveClass;

        public ResolveAdapterFactoryProxy(IResolveAdapterFactory2 factory) {
            this.config = null;
            this.factory = factory;
            for (Class<?> adapterClass : factory.getAdapterList()) {
                String adapterTypeName = adapterClass.getName();
                adapterTypes.put(adapterTypeName, adapterClass);
            }
        }

        public ResolveAdapterFactoryProxy(IConfigurationElement config) {
            this.config = config;
            resolveName = config.getAttribute("type");

            for (IConfigurationElement element : config.getChildren("resolve")) {
                String adapterTypeName = element.getAttribute("type");
                adapterTypes.put(adapterTypeName, null);
            }
        }

        public String getResolveName() {
            return resolveName;
        }

        public synchronized Class<?> getResolveType() {
            if (resolveClass == null) {
                resolveClass = loadClass(factory, resolveName);
            }
            return resolveClass;
        }

        private synchronized void loadFactory() {
            if (factory == null) {
                try {
                    factory = (IResolveAdapterFactory) config.createExecutableExtension("type");
                } catch (CoreException problem) {
                    String factoryName = config.getAttribute("type");
                    factory = new ExceptionResolveAdapaterFactory(factoryName, problem);
                }
            }
        }

        @Override
        public boolean canAdapt(IResolve resolve, Class<? extends Object> adapterType) {
            // try for a quick check to see if we can answer without
            // loading the factory
            String adapterName = adapterType.getName();
            if (!adapterTypes.containsKey(adapterName)) {
                return false; // factory has never heard of this adapterType
            }
            // okay factory thinks it can do it ...
            if (factory != null) {
                return factory.canAdapt(resolve, adapterType);
            } else {
                // An earlier implementation would load the factory at this point to check
                // As an optimisation we are just going to return true at this poin
                // and let the adapt method (with its progress monitor) take on
                // the pain of loading the factory?
                return true;
            }
        }

        @Override
        public <T> T adapt(IResolve resolve, Class<T> adapterType, IProgressMonitor monitor)
                throws IOException {
            if (factory == null) {
                loadFactory();
            }
            return factory.adapt(resolve, adapterType, monitor);
        }

        public List<String> getAdapterNames() {
            return new ArrayList<String>(adapterTypes.keySet());
        }

        @Override
        public List<Class<?>> getAdapterList() {
            List<Class<?>> types = new ArrayList<Class<?>>();
            for (Entry<String, Class<?>> entry : adapterTypes.entrySet()) {
                Class<?> adapterType = entry.getValue();
                if (adapterType == null) {
                    String adapterName = entry.getKey();
                    adapterType = loadClass(factory, adapterName);
                    if (adapterType == null) {
                        adapterTypes.remove(adapterName);
                        continue; // unable to load this one!
                    }
                }
                types.add(adapterType);
            }
            return types;
        }

        /**
         * Used as a placeholder to mark broken IResolveAdapatorFactory instances.
         * <p>
         * This only occurs when the configuration element "class" has a failure on being
         * constructed.
         * 
         * @author Jody
         */
        static class ExceptionResolveAdapaterFactory implements IResolveAdapterFactory2 {
            CoreException problem;

            private String factoryName;

            public ExceptionResolveAdapaterFactory(String factoryName, CoreException coreException) {
                this.factoryName = factoryName;
                problem = coreException;
            }

            public <T> T adapt(IResolve resolve, Class<T> adapter, IProgressMonitor monitor)
                    throws IOException {
                if (monitor == null)
                    monitor = new NullProgressMonitor();

                monitor.beginTask(problem.toString(), 1);
                monitor.done();
                throw new IOException(
                        "IResolveAdapterFactory " + factoryName + " unavailable:" + problem, problem); //$NON-NLS-1$
            }

            /** This factory is broken and cannot adapt anything */
            public boolean canAdapt(IResolve resolve, Class<? extends Object> adapter) {
                return false;
            }

            @Override
            public List<Class<?>> getAdapterList() {
                return Collections.emptyList();
            }
        }
    }
}
