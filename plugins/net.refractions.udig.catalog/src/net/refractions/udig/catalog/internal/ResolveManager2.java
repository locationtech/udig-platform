/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
import net.refractions.udig.catalog.IResolveManager;
import net.refractions.udig.catalog.ResolveAdapterFactory;
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
 * @since 1.3.3
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
            try {
                ResolveAdapterFactoryProxy proxy = new ResolveAdapterFactoryProxy(element);
                register(proxy);
            }
            catch (Throwable t){
                CatalogPlugin.log(element.getNamespaceIdentifier()+" failed:"+t, t);
            }
        }
    }

    @Override
    public boolean canResolve(IResolve resolve, Class<?> targetClass) {
        Map<String, IResolveAdapterFactory> available = getFactories(resolve.getClass());
        String targetTypeName = targetClass.getName();
        IResolveAdapterFactory factory = available.get(targetTypeName);
        if (factory != null){
            // we found a factory directly responsible for this connection
            try {
                return factory.canAdapt(resolve, targetClass);
            } catch (Throwable t) {
                String msg = "IResolveAdapterFactory " + factory.getClass().getName()
                        + " canAdapt check failed:" + t;
                CatalogPlugin.trace(msg, t);
                return false; // factory was unable to function
            }
        }
        else {
            // factories that were not registered against a specific targetClass
            //
            List<IResolveAdapterFactory> genericFactories = factories.get(null);
            if( genericFactories != null ){
                for( IResolveAdapterFactory fallback : genericFactories ){
                    try {
                        if( fallback.canAdapt( resolve, targetClass ) ){
                            return true; // found one!
                        }
                    }
                    catch( Throwable t ){
                        String factoryName = fallback.getClass().getName();
                        CatalogPlugin.trace( "IResolveFactory "+factoryName+" unable to test for "+targetTypeName+":"+t, t);
                    }
                }
            }
            return false; 
        }
    }

    @Override
    public <T> T resolve(IResolve resolve, Class<T> targetClass, IProgressMonitor monitor)
            throws IOException {
        String targetTypeName = targetClass.getName();
        
        Map<String, IResolveAdapterFactory> available = getFactories(resolve.getClass());
        IResolveAdapterFactory factory = available.get(targetTypeName);
        if (factory != null) {
            // we found a factory directly responsible for this connection
            T connected = factory.adapt(resolve, targetClass, monitor);
            return connected;
        }
        else {
            List<IResolveAdapterFactory> genericFactories = factories.get(null);
            for( IResolveAdapterFactory fallback : genericFactories ){
                try {
                    if( fallback.canAdapt( resolve, targetClass ) ){
                        T connected = fallback.adapt( resolve, targetClass, monitor );
                        if( connected != null ){
                            return connected;
                        }
                    }
                }
                catch( Throwable t ){
                    String factoryName = fallback.getClass().getName();
                    CatalogPlugin.trace( "IResolveFactory "+factoryName+" unable to convert to "+targetTypeName+":"+t, t);
                }
            }
            return null; // unable to convert
        }
    }

    @Override
    public void register(ResolveAdapterFactory factory) {
        String className = factory.getResolveName();
        
        List<IResolveAdapterFactory> register = factories.get(className);
        if (register == null) {
            register = new ArrayList<IResolveAdapterFactory>(2);
            factories.put(className, register);
        }
        register.add(factory);
    }

    @Override
    public void registerResolves(IResolveAdapterFactory factory) {
        if( factory instanceof ResolveAdapterFactory ){
            register((ResolveAdapterFactory) factory );
        }
        else {
            registerGeneric( factory );
        }
    }

    private void registerGeneric(IResolveAdapterFactory factory) {
        List<IResolveAdapterFactory> register = factories.get(null);
        if (register == null) {
            register = new ArrayList<IResolveAdapterFactory>(2);
            factories.put(null, register);
        }
        GenericResolveAdapterFactory item = new GenericResolveAdapterFactory(factory);
        register.add(item);
    }

    @Override
    public void unregisterResolves(IResolveAdapterFactory factory) {
        if( factory instanceof ResolveAdapterFactory ){
            unregister((ResolveAdapterFactory) factory );
        }
        else {
            unregisterGeneric( factory );
        }
    }

    @Override
    public void unregister(ResolveAdapterFactory factory) {
        String className = factory.getResolveName();
        List<IResolveAdapterFactory> register = factories.get(className);
        if (register != null) {
            register.remove( factory );
        }
    }
    
    @Override
    public void unregisterResolves(IResolveAdapterFactory factory, Class<?> resolveType) {
        GenericResolveAdapterFactory item = lookupGenericFactory( factory );
        item.getExcludes().add( resolveType );
    }

    private void unregisterGeneric(IResolveAdapterFactory factory) {
        List<IResolveAdapterFactory> register = factories.get(null);
        if (register != null) {
            GenericResolveAdapterFactory item = lookupGenericFactory(factory);
            register.remove( item );
        }
    }

    private GenericResolveAdapterFactory lookupGenericFactory(IResolveAdapterFactory factory) {
        List<IResolveAdapterFactory> genericFactories = factories.get(null);
        if( genericFactories == null ){
            return null; // no generic factories have been registered
        }
        for( IResolveAdapterFactory generic : genericFactories ){
            if( generic instanceof GenericResolveAdapterFactory ){
                GenericResolveAdapterFactory manual = (GenericResolveAdapterFactory) generic;
                if( manual == factory || manual.getFactory() == factory ){
                    return manual;
                }
            }
        }
        return null;
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
            } else if (factory instanceof ResolveAdapterFactory) {
                ResolveAdapterFactory factory2 = (ResolveAdapterFactory) factory;
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
            if (factory instanceof ResolveAdapterFactory) {
                for (Class<?> adpaterClass : ((ResolveAdapterFactory) factory).getAdapterList()) {
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
     * Holds a manually registered "generic" IResolveAdapterFactory instance.
     * <p>
     * Provides additional support an exclude list, used to offer limited control of manually registered factories.
     * This functionality is superseded by {@link ResolveAdapterFactory#getAdapterNames()}.
     * 
     * @author Jody Garnett (LISAsoft)
     * @since 1.3.3
     */
    static class GenericResolveAdapterFactory implements IResolveAdapterFactory {
        IResolveAdapterFactory factory;
        Set<Class<?>> excludes = new HashSet<Class<?>>();
        
        GenericResolveAdapterFactory( IResolveAdapterFactory factory ){
            if( factory == null ){
                throw new NullPointerException("Factory required");
            }
            this.factory = factory;
        }
        public boolean canAdapt(IResolve resolve, Class<? extends Object> adapterType) {
            if (excludes.contains(adapterType)){
                return false; // type has been manually excluded 
            }
            return factory.canAdapt(resolve, adapterType);
        }
        public <T> T adapt(IResolve resolve, Class<T> adapterType, IProgressMonitor monitor)
                throws IOException {
            return factory.adapt(resolve, adapterType, monitor);
        }
        /**
         * Used to store excludes provided by {@link IResolveManager#unregisterResolves(IResolveAdapterFactory, Class).
         * <p>
         * This set overrides the functionality of #canAdapt(IResolve, Class) above.
         * @return Set of types mannually excluded
         */
        public Set<Class<?>> getExcludes() {
            return excludes;
        }
        public IResolveAdapterFactory getFactory() {
            return factory;
        }
        @Override
        public String toString() {
            return "ManualResolveAdapterFactory "+factory.getClass().getName() +" excludes:"+excludes;
        }
    }
    /**
     * Proxy supporting the lazy loading of an IResolveAdapterFactory while providing access to its
     * list of supported types.
     * 
     * @author Jody Garnett (LISAsoft)
     * @since 1.3.3
     */
    static class ResolveAdapterFactoryProxy extends ResolveAdapterFactory {
        IConfigurationElement config;

        IResolveAdapterFactory factory;

        /** Provide class lookup by full name */
        Map<String, Class<?>> adapterTypes = new HashMap<String, Class<?>>();

        private String resolveName;

        private Class<?> resolveType;

        public ResolveAdapterFactoryProxy(ResolveAdapterFactory factory) {
            this.config = null;
            this.factory = factory;
            for (Class<?> adapterClass : factory.getAdapterList()) {
                String adapterTypeName = adapterClass.getName();
                adapterTypes.put(adapterTypeName, adapterClass);
            }
            this.resolveName = factory.getResolveName();
            this.resolveType = factory.getResolveType();
        }

        public ResolveAdapterFactoryProxy(IConfigurationElement config) {
            this.config = config;
            resolveName = config.getAttribute("resolveableType");

            for (IConfigurationElement element : config.getChildren("resolve")) {
                String adapterTypeName = element.getAttribute("type");
                adapterTypes.put(adapterTypeName, null);
            }
        }

        public String getResolveName() {
            return resolveName;
        }

        public synchronized Class<?> getResolveType() {
            if (resolveType == null) {
                resolveType = loadClass(factory, resolveName);
            }
            return resolveType;
        }

        private synchronized void loadFactory() {
            if (factory == null) {
                try {
                    factory = (IResolveAdapterFactory) config.createExecutableExtension("class");
                } catch (CoreException problem) {
                    String factoryName = config.getAttribute("class");
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
            if (factory == null) {
                loadFactory();
            }
            return factory.canAdapt(resolve, adapterType);           
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
        static class ExceptionResolveAdapaterFactory extends ResolveAdapterFactory {
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

            @Override
            public List<String> getAdapterNames() {
                return Collections.emptyList();
            }

            @Override
            public Class<?> getResolveType() {
                return Void.class;
            }

            @Override
            public String getResolveName() {
                return "java.lang.Void";
            }
        }
    }
}
