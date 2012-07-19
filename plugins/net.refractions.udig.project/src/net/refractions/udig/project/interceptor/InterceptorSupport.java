/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.project.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.project.EditFeature;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.ProjectPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Utility class for running the interceptors used by ProjectPlugin.
 * <p>
 * This utility class is made available for client code that takes ownership of feature or resource
 * creation and wants to resepct the interceptor lifecylce contract provided by ProjectPlugin.
 * 
 * @author Jody Garnett
 * @since 1.3.2
 */
public class InterceptorSupport {
    /**
     * Inner class used to hold onto a Interceptor and remember it's id for use in error reporting.
     * <p>
     * 
     * @author jody
     * @since 1.2.0
     * @param <P>
     */
    static class InterceptorProxy<P> {
        P interceptor;

        private String contributorId;

        private String extensionId;

        InterceptorProxy(IConfigurationElement configuration, P interceptor) {
            this.contributorId = configuration.getContributor().getName();
            this.extensionId = configuration.getAttribute("id");
            this.interceptor = interceptor;
        }

        public P getInterceptor() {
            return interceptor;
        }

        public String getContributorId() {
            return contributorId;
        }

        public String getExtensionId() {
            return extensionId;
        }
    }

    private static <I> List<InterceptorProxy<I>> extensionItemList(String xid, String itemName,
            Class<I> type) {
        List<IConfigurationElement> extensionList = ExtensionPointList.getExtensionPointList(xid);
        List<InterceptorProxy<I>> items = new ArrayList<InterceptorProxy<I>>();
        for (IConfigurationElement element : extensionList) {
            String id = element.getAttribute("id");
            if (itemName.equals(element.getName())) {
                try {
                    Object item = element.createExecutableExtension("class");
                    InterceptorProxy<I> proxy = new InterceptorProxy<I>(element, type.cast(item));
                    items.add(proxy);
                } catch (Exception e) {
                    ProjectPlugin.log(itemName + " " + id + ":" + e, e);
                }
            }
        }
        return items;
    }

    public static boolean matchSchema(SimpleFeatureType featureType, String schema) {
        if (featureType == null) {
            return false;
        }
        if (featureType.getTypeName().equals(schema)) {
            return true;
        }

        return false;
    }

    /**
     * Proxy implementation for {@link FeatureInterceptor} responsible for check of feature type
     * information, with lazy load of FeatureInterceptor if needed.
     * 
     * @author Jody Garnett (LISAsoft0
     * @since 1.3.0
     */
    static class FeatureInterceptorProxy implements FeatureInterceptor {
        FeatureInterceptor interceptor;

        private String schema;

        private IConfigurationElement element;

        FeatureInterceptorProxy(IConfigurationElement configuration) {
            this.schema = configuration.getAttribute("schema");
            this.element = configuration;
        }

        public FeatureInterceptorProxy(String schema, FeatureInterceptor interceptor) {
            this.schema = schema;
            this.interceptor = interceptor;
        }

        public boolean matchSchema(EditFeature feature) {
            return InterceptorSupport.matchSchema(feature.getFeatureType(), schema);
        }

        @Override
        public void run(EditFeature feature) {
            if (interceptor != null) {
                interceptor.run(feature);
            }
        }

        public synchronized FeatureInterceptor getInterceptor() throws CoreException {
            if (interceptor == null) {
                this.interceptor = (FeatureInterceptor) element.createExecutableExtension("class");
            }
            return interceptor;
        }

        public String getContributorId() {
            if( element != null ){
                return element.getContributor().getName();
            }
            return "internal";
        }

        public String getExtensionId() {
            if( element != null ){
                return element.getAttribute("id");
            }
            return "registered";
        }

        public String getName() {
            if( element != null ){
                String name = element.getAttribute("name");
                if (name == null || name.isEmpty()) {
                    name =getExtensionId();
                }
                return name;
            }
            else {
                String name = interceptor.getClass().getSimpleName();
                if( name == null || name.isEmpty() ){
                    return "Anonymous FeatureInterceptor";
                }
                return name;
            }
        }

        @Override
        public String toString() {
            StringBuilder build = new StringBuilder();
            build.append("FeatureInterceptorProxy ");

            build.append( getName() );
            
            if (interceptor != null) {
                build.append(" ");
                build.append(interceptor.getClass().getSimpleName());
            }
            return build.toString();
        }
    }
    /**
     * Used to register an additional FeatureInterceptor.
     * 
     * @param stage
     * @param schema
     * @param interceptor
     */
    public void registerFeatureInterceptor( String stage, String schema, FeatureInterceptor interceptor){
        List<FeatureInterceptorProxy> list = featureInterceptors(stage);
        FeatureInterceptorProxy proxy = new FeatureInterceptorProxy(schema,interceptor);
        list.add( proxy );
    }
    /**
     * Called set up a feature before it is added.
     * <p>
     * Great for setting up sensible default values, or performing a security check
     * to ensure the user has the ability to create a feature at this location.
     * 
     * @see FeatureInterceptor#PRE_CREATE
     * @param feature
     */
    public static void runFeaturePreCreateInterceptors(EditFeature feature) {
        runFeatureInterceptor(feature, FeatureInterceptor.PRE_CREATE);
    }
    /**
     * Called after a feature is added.
     * <p>
     * Intended for connecting any external references, recording feature creation
     * in an audit log etc.
     * <p>
     * Any warnings or errors registered against the EditFeature by your interceptor
     * will be displayed to the user. Note the feature is already added at this stage
     * so it is too late to cancel the creation of the feature.
     * 
     * @see FeatureInterceptor#CREATED
     * @param feature
     */
    public static void runFeatureCreatedInterceptors(EditFeature feature) {
        runFeatureInterceptor(feature, FeatureInterceptor.CREATED);
    }

    /**
     * Called after a feature has been deleted, intended to clean up any associated references or
     * caches.
     * 
     * @see FeatureInterceptor#PRE_DELETE
     * @param feature EditFeature that has just been removed
     */
    public static void runFeaturePreDeleteInterceprtors(EditFeature feature) {
        runFeatureInterceptor(feature, FeatureInterceptor.PRE_DELETE);
    }

    /**
     * Called after a feature has been deleted, intended to clean up any associated references or
     * caches.
     * 
     * @see FeatureInterceptor#    /**
     * Called after a feature has been deleted, intended to clean up any associated references or
     * caches.
     * 
     * @see FeatureInterceptor#DELETED
     * @param feature EditFeature that has just been removed
     */
    public static void runFeatureDeletedInterceprtors(EditFeature feature) {
        runFeatureInterceptor(feature, FeatureInterceptor.DELETED);
    }
    public static void runFeatureApplyInterceprtors(EditFeature feature) {
        runFeatureInterceptor(feature, FeatureInterceptor.APPLY);
    }
    public static void runFeatureCancelInterceprtors(EditFeature feature) {
        runFeatureInterceptor(feature, FeatureInterceptor.CANCEL);
    }
    public static void runFeatureActiveInterceprtors(EditFeature feature) {
        runFeatureInterceptor(feature, FeatureInterceptor.ACTIVATE);
    }
    public static void runFeatureInactiveInterceprtors(EditFeature feature) {
        runFeatureInterceptor(feature, FeatureInterceptor.DEACTIVATE);
    }
    static Map<String, List<FeatureInterceptorProxy>> featureInterceptorMap = new WeakHashMap<String, List<FeatureInterceptorProxy>>();

    private synchronized static List<FeatureInterceptorProxy> featureInterceptors(String stage) {
        if (featureInterceptorMap.containsKey(stage)) {
            List<FeatureInterceptorProxy> interceptorList = featureInterceptorMap.get(stage);
            return interceptorList;
        } else {
            List<FeatureInterceptorProxy> interceptorList = new CopyOnWriteArrayList<FeatureInterceptorProxy>();
            List<IConfigurationElement> extensionList = ExtensionPointList
                    .getExtensionPointList(FeatureInterceptor.EXTENSION_ID);
            for (IConfigurationElement element : extensionList) {
                String id = element.getAttribute("id");
                if (stage.equals(element.getName())) {
                    try {
                        FeatureInterceptorProxy proxy = new FeatureInterceptorProxy(element);
                        interceptorList.add(proxy);
                    } catch (Exception e) {
                        ProjectPlugin.log("FeatureInterceptor " + stage + " " + id + ":" + e, e);
                    }
                }
            }
            featureInterceptorMap.put(stage, interceptorList);
            return interceptorList;
        }
    }

    private static void runFeatureInterceptor(EditFeature feature, String stage) {
        for (FeatureInterceptorProxy interceptor : featureInterceptors(stage)) {
            boolean match = interceptor.matchSchema(feature);
            if (match) {
                try {
                    interceptor.run(feature);
                } catch (Throwable e) {
                    if (ProjectPlugin.getPlugin().isDebugging()) {
                        String message = "FeatureInterceptor " + interceptor.getExtensionId() + " "
                                + stage + ":" + e;
                        IStatus status = new Status(IStatus.INFO, interceptor.getContributorId(),
                                message, e);
                        ProjectPlugin.getPlugin().getLog().log(status);
                    }
                    feature.addWarning(interceptor.getName() + " " + e, e);
                }
            }
        }
    }

    public static void runLayerInterceptor(Layer layer, String configurationName) {
        List<IConfigurationElement> list = ExtensionPointList
                .getExtensionPointList(LayerInterceptor.EXTENSION_ID);
        for (IConfigurationElement element : list) {
            if (element.getName().equals(configurationName)) {
                String attribute = element.getAttribute("name"); //$NON-NLS-1$
                try {
                    LayerInterceptor interceptor = (LayerInterceptor) element
                            .createExecutableExtension("class"); //$NON-NLS-1$
                    interceptor.run(layer);
                } catch (CoreException e) {
                    ProjectPlugin
                            .log("Error creating class: " + element.getAttribute("class") + " part of layer interceptor: " + attribute, e); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                } catch (Throwable t) {
                    ProjectPlugin.log("error running interceptor: " + attribute, t); //$NON-NLS-1$
                }
            }
        }
        List<IConfigurationElement> interceptors = ExtensionPointList
                .getExtensionPointList(MapInterceptor.MAP_INTERCEPTOR_EXTENSIONPOINT);
        for (IConfigurationElement element : interceptors) {
            if (!configurationName.equals(element.getName())) {
                continue;
            }
            try {
                LayerInterceptor interceptor = (LayerInterceptor) element
                        .createExecutableExtension("class"); //$NON-NLS-1$
                interceptor.run(layer);
            } catch (Throwable e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            }
        }
    }
}
