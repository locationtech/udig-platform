/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IResolveAdapterFactory;
import org.locationtech.udig.catalog.IResolveManager;
import org.locationtech.udig.catalog.ResolveAdapterFactory;
import org.locationtech.udig.core.internal.ExtensionPointList;

/**
 * Default implementation of {@link IResolveManager}
 *
 * @author Jesse
 * @since 1.1.0
 * @version 1.3.2
 */
public class ResolveManager implements IResolveManager {

    private static final String LIST_ID = "org.locationtech.udig.catalog.resolvers"; //$NON-NLS-1$

    /**
     * Resolver configuration elements mapped to resulting factory, used to lazily create factories
     * as needed without reprocessing the extension point again and again.
     * <p>
     * This represents IResolveAdapterFactories that have been registered using XML.
     */
    Map<IConfigurationElement, IResolveAdapterFactory> factories = new HashMap<>();

    /**
     * Is this a cache of exceptions thrown?
     */
    Map<IResolveAdapterFactory, Set<Class<?>>> exceptions = new HashMap<>();

    /**
     * Set of registered factories. This represents IResolveAdapterFactories that have been
     * registered using code.
     */
    Set<IResolveAdapterFactory> registeredFactories = new HashSet<>();

    public ResolveManager() {
        List<IConfigurationElement> extensionList = ExtensionPointList
                .getExtensionPointList(LIST_ID);

        for (IConfigurationElement element : extensionList) {
            factories.put(element, null);
        }
    }

    /**
     * Check with the registered IResolveAdaptorFactories to see if any of them can convert provided
     * handle to requested target class.
     *
     * @param resolve Resolve handle to convert
     * @param adapter Requested target interface
     */
    @Override
    public boolean canResolve(IResolve resolve, Class<?> adapter) {
        for (Map.Entry<IConfigurationElement, IResolveAdapterFactory> entry : factories
                .entrySet()) {
            IConfigurationElement element = entry.getKey();
            try {
                // check the configuration information before making a factory
                if (isResolvableType(element, resolve) && isTargetTypeSupported(element, adapter)) {
                    /**
                     * Use the extension point information as a quick sanity check to ensure the
                     * factory is relevant to the problem at hand
                     */
                    IResolveAdapterFactory factory = getResolveAdapterFactory(entry);
                    if (factory.canAdapt(resolve, adapter)) {
                        return true;
                    }
                }
            } catch (RuntimeException ignore) {
                /**
                 * Problem encountered with entry; depending on the problem we may consider
                 * disabling the entry and logging a warning?
                 */
                CatalogPlugin.log(ignore.getLocalizedMessage(), ignore);
            }
        }

        /**
         * Go through registered factories second, ensuring that XML can always override something
         * that has been hard coded
         */
        for (IResolveAdapterFactory factory : registeredFactories) {
            try {
                Set<Class<?>> ignoreSet = exceptions.get(factory);
                if (ignoreSet != null && ignoreSet.contains(adapter)) {
                    continue; // skip this as it is listed as an exception
                }
                if (factory.canAdapt(resolve, adapter)) {
                    return true;
                }
            } catch (RuntimeException ignore) {
                // problem encountered with factory; depending
                // on the problem we may consider removing the factory
                CatalogPlugin.log(ignore.getLocalizedMessage(), ignore);
            }
        }
        return false; // nobody can adapt to the requested interface
    }

    /**
     * XML check on the "resolveableType" entry to ensure the provided resolve handle meets the
     * basic requirements.
     *
     * @param entry
     * @param resolve
     * @return true if we resolve handle is applicable ot this factory
     */
    private boolean isResolvableType(IConfigurationElement element, IResolve resolve) {
        String requiredType = element.getAttribute("resolveableType"); //$NON-NLS-1$
        if (requiredType == null) {
            String badPuppy = element.getContributor().getName();
            if (badPuppy == null) {
                badPuppy = "A plugin"; //$NON-NLS-1$
            }
            System.out.println(
                    badPuppy + " failed to configure ResolveAdaptorFactory: requiredType missing "); //$NON-NLS-1$
            return false; // this should not happen! did you forget to fill in their XML
                          // resoleableType info?
        }
        try {
            Class<?> clazz = resolve.getClass().getClassLoader().loadClass(requiredType);
            return clazz.isAssignableFrom(resolve.getClass());
        } catch (ClassNotFoundException huh) {
            return false;
        }
    }

    /**
     * XML check on the "resolve" entries to ensure the target type is supported (we will still have
     * to check with canResolve but hopefully we can rule out a few before hand).
     *
     * @param entry
     * @param target
     * @return true if the target should be considered
     */
    private boolean isTargetTypeSupported(IConfigurationElement element, Class<?> target) {
        IConfigurationElement[] resolveList = element.getChildren("resolve"); //$NON-NLS-1$
        for (IConfigurationElement child : resolveList) {
            String resolveType = child.getAttribute("type"); //$NON-NLS-1$
            if (!resolveType.startsWith("net.refractions") //$NON-NLS-1$
                    && !resolveType.startsWith("org.locationtech.udig")) { //$NON-NLS-1$
                /**
                 * We first try a class loader trick to grab the target class without forcing the
                 * load of the plugin where the element comes from (this works in may cases where
                 * the type is something common from org.locationtech.udig.libs)
                 */
                try {
                    ClassLoader classLoader = target.getClassLoader();
                    if (classLoader == null) {
                        classLoader = ClassLoader.getSystemClassLoader();
                    }
                    Class<?> resolvedClass = classLoader.loadClass(resolveType);

                    if (target.isAssignableFrom(resolvedClass)) {
                        return true;
                    } else {
                        continue; // we were able to load the class and it did not match
                    }
                } catch (ClassNotFoundException e2) {
                    // that is no good, let's try using the RCP classloader
                }
            }
            // Okay that optimization failed; lets use the platform facilities
            // like a good RCP programmer
            try {
                // only way to check is to actually make the factory, and use
                // the factory's class loader to grab the target class
                IResolveAdapterFactory factory = getResolveAdapterFactory(element);
                if (factory instanceof BrokenIResolveAdapaterFactory) {
                    // not even this factory can be loaded ... skip
                    continue;
                }
                ClassLoader classLoader = factory.getClass().getClassLoader();
                if (classLoader != null) {
                    Class<?> resolvedClass = classLoader.loadClass(resolveType);
                    if (target.isAssignableFrom(resolvedClass)) {
                        return true;
                    } else {
                        continue; // we were able to load the class and it did not match
                    }
                } else {
                    // we can not figure out what the resolvedClass is (probably a configuration
                    // error)
                    throw new ClassNotFoundException(resolveType);
                }
            } catch (ClassNotFoundException notFound) {
                ILog log = CatalogPlugin.getDefault().getLog();
                Status status = new Status(IStatus.WARNING, element.getContributor().getName(),
                        "Cannot determine resolve class for " //$NON-NLS-1$
                                + element.getDeclaringExtension().getUniqueIdentifier(),
                        notFound);
                log.log(status);
            }
        }
        return false;
    }

    /**
     * Register the provided adapter factory with the resolve manager.
     * <p>
     * The provided factory will be checked by all IResolve implementations as part of their API
     * contract. You can use your factory to wrap additional interfaces around existing IResolve
     * implementations.
     */
    @Override
    public void registerResolves(IResolveAdapterFactory factory) {
        registeredFactories.add(factory);
    }

    @Override
    public void register(ResolveAdapterFactory factory) {
        registeredFactories.add(factory);
    }

    /**
     * Used as a placeholder to mark broken IResolveAdapatorFactory instances.
     * <p>
     * This only occurs when the configuration element "class" has a failure on being constructed.
     *
     * @author Jody
     */
    static class BrokenIResolveAdapaterFactory implements IResolveAdapterFactory {
        CoreException problem;

        public BrokenIResolveAdapaterFactory(CoreException coreException) {
            problem = coreException;
        }

        @Override
        public <T> T adapt(IResolve resolve, Class<T> adapter, IProgressMonitor monitor)
                throws IOException {
            if (monitor == null)
                monitor = new NullProgressMonitor();

            monitor.beginTask(problem.toString(), 1);
            monitor.done();
            throw (IOException) new IOException("This factory is broken:" + problem) //$NON-NLS-1$
                    .initCause(problem);
        }

        /**
         * This factory is broken and cannot adapt anything
         */
        @Override
        public boolean canAdapt(IResolve resolve, Class<? extends Object> adapter) {
            return false;
        }
    }

    protected IResolveAdapterFactory getResolveAdapterFactory(
            Map.Entry<IConfigurationElement, IResolveAdapterFactory> entry) {
        synchronized (factories) {
            if (entry.getValue() != null)
                return entry.getValue();

            IConfigurationElement element = entry.getKey();
            IResolveAdapterFactory factory;
            try {
                factory = (IResolveAdapterFactory) element.createExecutableExtension("class"); //$NON-NLS-1$
            } catch (CoreException e) {
                CatalogPlugin.log(e.toString(), e);
                factory = new BrokenIResolveAdapaterFactory(e);
            }
            entry.setValue(factory);
            return factory;
        }
    }

    protected IResolveAdapterFactory getResolveAdapterFactory(IConfigurationElement element) {
        synchronized (factories) {
            IResolveAdapterFactory factory = factories.get(element);
            if (factory != null)
                return factory;

            try {
                factory = (IResolveAdapterFactory) element.createExecutableExtension("class"); //$NON-NLS-1$
            } catch (CoreException e) {
                String message = "Marking IResolveAdaptorFactory as unavaiable:" + e; //$NON-NLS-1$
                IStatus status = new Status(IStatus.WARNING, element.getContributor().getName(),
                        message, e);
                CatalogPlugin.getDefault().getLog().log(status);
                factory = new BrokenIResolveAdapaterFactory(e);
            }
            factories.put(element, factory);
            return factory;
        }
    }

    /**
     * Go through the available factories and ask them to resolve to the provided targetType.
     */
    @Override
    public <T> T resolve(IResolve resolve, Class<T> adapter, IProgressMonitor monitor)
            throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();
        int count = registeredFactories.size() + factories.size();

        monitor.beginTask("Searching for " + adapter.getCanonicalName(), count * 10); //$NON-NLS-1$
        try {
            for (Map.Entry<IConfigurationElement, IResolveAdapterFactory> entry : factories
                    .entrySet()) {
                IConfigurationElement element = entry.getKey();
                try {
                    // check the configuration information before making a factory
                    if (isResolvableType(element, resolve)
                            && isTargetTypeSupported(element, adapter)) {
                        /**
                         * Use the extension point information as a quick sanity check to ensure the
                         * factory is relevant to the problem at hand
                         */
                        IResolveAdapterFactory factory = getResolveAdapterFactory(entry);
                        if (factory.canAdapt(resolve, adapter)) {
                            // we think we can do this one...
                            IProgressMonitor subMonitor = SubMonitor.convert(monitor,
                                    factory.getClass().getCanonicalName(), 10);
                            Object value = factory.adapt(resolve, adapter, subMonitor);
                            if (value != null) {
                                return adapter.cast(value);
                            }
                        } else {
                            monitor.worked(10);
                        }
                    } else {
                        monitor.worked(10);
                    }
                } catch (RuntimeException ignore) {
                    /**
                     * Problem encountered with entry; depending on the problem we may consider
                     * disabling the entry and logging a warning?
                     */
                    CatalogPlugin.log(ignore.getLocalizedMessage(), ignore);
                }
            }

            /**
             * Go through registered factories second, ensuring that XML can always override
             * something that has been hard coded
             */
            for (IResolveAdapterFactory factory : registeredFactories) {
                try {
                    Set<Class<?>> ignoreSet = exceptions.get(factory);
                    if (ignoreSet != null && ignoreSet.contains(adapter)) {
                        monitor.worked(10);
                        continue; // skip this as it is listed as an exception
                    }
                    if (factory.canAdapt(resolve, adapter)) {
                        // we think we can do this one...
                        IProgressMonitor subMonitor = SubMonitor.convert(monitor,
                                factory.getClass().getCanonicalName(), 10);
                        Object value = factory.adapt(resolve, adapter, subMonitor);
                        if (value != null) {
                            return adapter.cast(value);
                        }
                    } else {
                        monitor.worked(10);
                    }
                } catch (RuntimeException ignore) {
                    // problem encountered with factory; depending
                    // on the problem we may consider removing the factory
                    CatalogPlugin.log(ignore.getLocalizedMessage(), ignore);
                }
            }
            return null; // we have nothing to offer
        } finally {
            monitor.done();
        }
    }

    /**
     * Remove the indicated factory from further consideration.
     *
     * @param factory
     */
    @Override
    public void unregisterResolves(IResolveAdapterFactory factory) {
        registeredFactories.remove(factory);
        exceptions.remove(registeredFactories);
    }

    @Override
    public void unregister(ResolveAdapterFactory factory) {
        registeredFactories.remove(factory);
        exceptions.remove(registeredFactories);
    }

    /**
     * Register a couple of interfaces that should be skipped for the provided factory.
     *
     * @param factory factory previous registered
     * @param resolveType Please skip the resolve target
     */
    @Override
    public void unregisterResolves(IResolveAdapterFactory factory, Class<?> resolveType) {
        if (!registeredFactories.contains(factory))
            throw new IllegalArgumentException(factory + " is not a registered factory"); //$NON-NLS-1$

        Set<Class<?>> set = exceptions.get(factory);
        if (set == null) {
            set = new HashSet<>();
            exceptions.put(factory, set);
        }
        set.add(resolveType);
    }

}
