/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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