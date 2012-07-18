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

import java.util.List;

import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.project.EditFeature;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.ProjectPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Utility class for running the interceptors used by ProjectPlugin.
 * <p>
 * This utility class is made available for client code that takes ownership
 * of feature or resource creation and wants to resepct the interceptor
 * lifecylce contract provided by ProjectPlugin.
 * 
 * @author Jody Garnett
 * @since 1.3.2
 */
public class InterceptorSupport {
    /**
     * Called to process a feature when initially created.
     * 
     * @see FeatureInterceptor#CREATE
     * @param feature
     */
    public static void runFeatureCreationInterceptors( EditFeature feature ) {
        List<IConfigurationElement> interceptors = ExtensionPointList
                .getExtensionPointList(FeatureInterceptor.EXTENSION_ID);
        for( IConfigurationElement element : interceptors ) {
            String id = element.getAttribute("id");
            if (FeatureInterceptor.CREATE.equals(element.getName())) {
                try {
                    FeatureInterceptor interceptor = (FeatureInterceptor) element
                            .createExecutableExtension("class");
                    interceptor.run(feature);
                } catch (Exception e) {
                    ProjectPlugin.log("FeatureInterceptor " + id + ":" + e, e);
                }
            }
        }
    }

    public static void runLayerInterceptor( Layer layer, String configurationName ) {
        List<IConfigurationElement> list = ExtensionPointList.getExtensionPointList(LayerInterceptor.EXTENSION_ID);
        for( IConfigurationElement element : list ) {
            if (element.getName().equals(configurationName)) {
                String attribute = element.getAttribute("name"); //$NON-NLS-1$
                try {
                    LayerInterceptor interceptor = (LayerInterceptor) element
                            .createExecutableExtension("class"); //$NON-NLS-1$
                    interceptor.run(layer);
                } catch (CoreException e) {
                    ProjectPlugin
                            .log(
                                    "Error creating class: " + element.getAttribute("class") + " part of layer interceptor: " + attribute, e); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                } catch (Throwable t) {
                    ProjectPlugin.log("error running interceptor: " + attribute, t); //$NON-NLS-1$
                }
            }
        }
        List<IConfigurationElement> interceptors = ExtensionPointList.getExtensionPointList(MapInterceptor.MAP_INTERCEPTOR_EXTENSIONPOINT);
        for( IConfigurationElement element : interceptors ) {
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
