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

import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.project.EditFeature;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.ProjectPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

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
    static class InterceptorProxy<P>  {
        P interceptor;
        private String contributorId;
        private String extensionId;
        InterceptorProxy( IConfigurationElement configuration, P interceptor ){
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

    private static <I> List<InterceptorProxy<I>> extensionItemList(String xid, String itemName, Class<I> type) {
        List<IConfigurationElement> extensionList = ExtensionPointList.getExtensionPointList(xid);
        List<InterceptorProxy<I>> items = new ArrayList<InterceptorProxy<I>>();
        for (IConfigurationElement element : extensionList) {
            String id = element.getAttribute("id");
            if (itemName.equals(element.getName())) {
                try {
                    Object item = element.createExecutableExtension("class");
                    InterceptorProxy<I> proxy = new InterceptorProxy<I>( element, type.cast(item));
                    items.add(proxy);
                } catch (Exception e) {
                    ProjectPlugin.log(itemName + " " + id + ":" + e, e);
                }
            }
        }
        return items;
    }
//    static class FeatureInterceptorProxy implements FeatureInterceptor {
//        FeatureInterceptor interceptor;
//        private String contributorId;
//        private String extensionId;
//        FeatureInterceptorProxy( IConfigurationElement configuration, FeatureInterceptor interceptor ){
//            this.contributorId = configuration.getContributor().getName();
//            this.extensionId = configuration.getAttribute("id");
//            this.interceptor = interceptor;
//        }
//        @Override
//        public void run(EditFeature feature) {
//            interceptor.run(feature);
//        }
//        public FeatureInterceptor getInterceptor() {
//            return interceptor;
//        }
//        public String getContributorId() {
//            return contributorId;
//        }
//        public String getExtensionId() {
//            return extensionId;
//        }
//    }
    static List<InterceptorProxy<FeatureInterceptor>> createFeatureList = null;
    private synchronized static List<InterceptorProxy<FeatureInterceptor>> featureCreatedInterceptors() {
        if (createFeatureList == null) {
            createFeatureList = extensionItemList(FeatureInterceptor.EXTENSION_ID,
                    FeatureInterceptor.CREATE, FeatureInterceptor.class);
        }
        return createFeatureList;
    }


    /**
     * Called to process a feature when initially created.
     * 
     * @see FeatureInterceptor#CREATE
     * @param feature
     */
    public static void runFeatureCreationInterceptors(EditFeature feature) {
        for( InterceptorProxy<FeatureInterceptor> proxy : featureCreatedInterceptors() ){
            try {
                proxy.getInterceptor().run(feature);
            } catch (Exception e) {
                if( ProjectPlugin.getPlugin().isDebugging() ){
                    String message = "FeatureInterceptor "+proxy.getExtensionId()+" featureCreate:"+e;
                    IStatus status = new Status(IStatus.INFO, proxy.getContributorId(), message, e );
                    ProjectPlugin.getPlugin().getLog().log(status);
                }
            }
        }
    }
    
    static List<InterceptorProxy<FeatureInterceptor>> deleteFeatureList = null;
    private synchronized static List<InterceptorProxy<FeatureInterceptor>> deleteFeatureInterceptors() {
        if (deleteFeatureList == null) {
            deleteFeatureList = extensionItemList(FeatureInterceptor.EXTENSION_ID,
                    FeatureInterceptor.DELETE, FeatureInterceptor.class);
        }
        return deleteFeatureList;
    }
    
    /**
     * Called to process a feature when initially created.
     * 
     * @see FeatureInterceptor#CREATE
     * @param feature
     */
    public static void runFeatureRemoveInterceptors(EditFeature feature) {
        for( InterceptorProxy<FeatureInterceptor> proxy : deleteFeatureInterceptors() ){
            try {
                proxy.getInterceptor().run(feature);
            } catch (Exception e) {
                if( ProjectPlugin.getPlugin().isDebugging() ){
                    String message = "FeatureInterceptor "+proxy.getExtensionId()+" featureCreate:"+e;
                    IStatus status = new Status(IStatus.INFO, proxy.getContributorId(), message, e );
                    ProjectPlugin.getPlugin().getLog().log(status);
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
