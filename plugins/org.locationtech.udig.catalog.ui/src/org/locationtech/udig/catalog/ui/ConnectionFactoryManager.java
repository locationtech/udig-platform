/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2021, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.locationtech.udig.catalog.CatalogPlugin;

/**
 * Provides access to the connection Factories and their wizard pages.
 *
 * @author jeichar
 */
public class ConnectionFactoryManager {
    /**
     * Connection factory to the list of wizard pages providing connection information.
     *
     * @see #pageToFactory
     */
    Map<Descriptor<UDIGConnectionFactory>, List<Descriptor<UDIGConnectionPage>>> factoryToPage = new HashMap<>();

    /**
     * Map of wizard pages (a list of one or more, to the connection factory responsible for them).
     * <p>
     * Not sure I see the need for a List here, as it compicates normal lookup.
     *
     * @see #factoryToPage
     */
    Map<List<Descriptor<UDIGConnectionPage>>, Descriptor<UDIGConnectionFactory>> pageToFactory = new HashMap<>();

    /**
     * List of avaialble UDIGConnectionFactory descriptors.
     */
    List<UDIGConnectionFactoryDescriptor> descriptors;

    /** Singleton instance */
    private static ConnectionFactoryManager manager;

    /**
     * Will process the extension point when first constructed.
     */
    protected ConnectionFactoryManager() {
        IExtension[] extension = Platform.getExtensionRegistry()
                .getExtensionPoint(UDIGConnectionFactory.XPID).getExtensions();

        Descriptor<UDIGConnectionFactory> factory = null;
        List<Descriptor<UDIGConnectionPage>> wizardPages = new ArrayList<>();

        for (IExtension e : extension) {
            IConfigurationElement[] elements = e.getConfigurationElements();
            wizardPages = new ArrayList<>();
            factory = null;
            for (IConfigurationElement element : elements) {
                if ("factory".equals(element.getName())) { //$NON-NLS-1$
                    factory = new Descriptor<>("class", element); //$NON-NLS-1$
                }
                if ("wizardPage".equals(element.getName())) { //$NON-NLS-1$
                    wizardPages.add(new Descriptor<UDIGConnectionPage>("class", element)); //$NON-NLS-1$
                }
            }
            if (factory != null)
                factoryToPage.put(factory, wizardPages);
            if (factory != null)
                pageToFactory.put(wizardPages, factory);
        }
    }

    /**
     * Access to connection factory manager singleton.
     *
     * @return connection factory manager
     */
    public static synchronized ConnectionFactoryManager instance() {
        if (manager == null) {
            manager = new ConnectionFactoryManager();
        }

        return manager;
    }

    /**
     * Look up list of pages associated with the indicated factory.
     *
     * @param factory
     * @return List of wizard pages for the provided factory
     * @throws CoreException
     */
    public List<Descriptor<UDIGConnectionPage>> getPageDescriptor(
            Descriptor<UDIGConnectionFactory> factory) throws CoreException {
        return factoryToPage.get(factory);
    }

    // public Descriptor<UDIGConnectionFactory>
    // getFactoryDescriptor(List<Descriptor<UDIGConnectionPage>> page) throws CoreException{
    // return pageToFactory.get(page);
    // }

    /** Access to List of wizard pages */
    public Collection<List<Descriptor<UDIGConnectionPage>>> getPages() {
        return factoryToPage.values();
    }

    /**
     * Access to list of available connection factories.
     * <p>
     * Note descriptor is returned to allow for lazy loading
     *
     * @return list of available connection factories
     */
    public Collection<Descriptor<UDIGConnectionFactory>> getFactories() {
        return factoryToPage.keySet();
    }

    /**
     * Access to list of available connection factories (sorted by title).
     * <p>
     * A more useful/formal UDIGConnectionFactoryDescriptor is provided this time with additional
     * methods to query assocaited wizard information.
     *
     * @see #getFactories()
     * @return list of available connection factories
     */
    public synchronized List<UDIGConnectionFactoryDescriptor> getConnectionFactoryDescriptors() {
        if (descriptors == null) {
            descriptors = new ArrayList<>();
            Collection<Descriptor<UDIGConnectionFactory>> factories = getFactories();
            for (Descriptor<UDIGConnectionFactory> factoryDescriptor : factories) {
                try {
                    if (!getPageDescriptor(factoryDescriptor).isEmpty())
                        descriptors.add(new UDIGConnectionFactoryDescriptor(factoryDescriptor));
                } catch (CoreException e) {
                    CatalogUIPlugin.log("", e); //$NON-NLS-1$
                }
            }
            Collections.sort(descriptors, new Comparator<UDIGConnectionFactoryDescriptor>() {
                @Override
                public int compare(UDIGConnectionFactoryDescriptor o1,
                        UDIGConnectionFactoryDescriptor o2) {
                    String s1 = o1.getLabel(0);
                    String s2 = o2.getLabel(0);
                    return s1.compareTo(s2);
                }
            });
        }

        return descriptors;
    }

    /**
     * Gets the UDIGConnectionFactoryDescriptors that match the ids in the list
     */
    public List<UDIGConnectionFactoryDescriptor> getConnectionFactoryDescriptors(List<String> ids) {
        List<UDIGConnectionFactoryDescriptor> tmp = instance().getConnectionFactoryDescriptors();
        List<UDIGConnectionFactoryDescriptor> result = new ArrayList<>();
        for (UDIGConnectionFactoryDescriptor descriptor : tmp) {
            for (String id : ids) {
                if (id.equals(descriptor.getId()))
                    result.add(descriptor);
            }
        }
        return result;
    }

    /**
     * Provides lazy loading for a class declared in an extension
     *
     * @author jeichar
     *
     * @param <T> The class type that will be created
     */
    public static class Descriptor<T> {
        private IConfigurationElement element;

        private String classAttribute;

        private T instance;

        /**
         * Creates a new Descriptor
         *
         * @param classAttribute the attribute name of the element that is to be create
         */
        Descriptor(String classAttribute, IConfigurationElement element) {
            this.classAttribute = classAttribute;
            this.element = element;
        }

        @SuppressWarnings("unchecked")
        public synchronized T getConcreteInstance() throws CoreException {
            if (instance == null) {
                try {
                    instance = (T) element.createExecutableExtension(classAttribute);
                } catch (CoreException eek) {
                    if (CatalogPlugin.getDefault().isDebugging()) {
                        String toLoad = element.getAttribute(classAttribute);
                        System.out.println("Unable to load:" + toLoad + ":" + eek); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    throw eek;
                }
            }
            return instance;
        }

        public IConfigurationElement getConfigurationElement() {
            return element;
        }
    }
}
