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
package net.refractions.udig.style;

import java.util.HashSet;
import java.util.Set;

import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;
import net.refractions.udig.project.internal.Layer;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

public class Styles {
    /**
     * 
     * Returns the set of style id's which support a particular layer.
     * 
     * @return A set of id's.
     */
    public static Set<String> getStyleIDs(final Layer layer) {
        final Set<String> ids = new HashSet<String>();
        ExtensionPointProcessor p = new ExtensionPointProcessor() {
            public void process( IExtension extension, IConfigurationElement element ) throws Exception {
                IStyleConfigurator sce = (IStyleConfigurator)element.createExecutableExtension("class"); //$NON-NLS-1$
                if (sce.canStyle(layer)) {
                    ids.add(sce.getStyleId());
                }
            }            
        };        
        ExtensionPointUtil.process( StylePlugin.getDefault(), IStyleConfigurator.XPID, p);
        return ids;
    }
}