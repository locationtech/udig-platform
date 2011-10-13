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
package net.refractions.udig.project.internal;

import java.awt.Color;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;
import net.refractions.udig.project.StyleContent;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

/**
 * Responsible for determining the initial "default" styles placed on the layer style blackboard.
 * 
 * @author Jody Garnett
 * @since 1.2.3
 */
public final class SetDefaultStyleProcessor implements ExtensionPointProcessor {
    private final IGeoResource theResource;
    private final Layer theLayer;

    public SetDefaultStyleProcessor( IGeoResource theResource, Layer theLayer ) {
        if( theResource == null ){
            throw new NullPointerException("Resource required to create default styles");
        }
        this.theResource = theResource;
        if( theLayer == null){
            throw new NullPointerException("Layer is required to create default styles");
        }
        this.theLayer = theLayer;
    }
    public void process( IExtension extension, IConfigurationElement element ) throws Exception {
        StyleContent styleContent = (StyleContent) element.createExecutableExtension("class"); //$NON-NLS-1$
        Color defaultColor = theLayer.getDefaultColor();
        Object style = styleContent.createDefaultStyle(theResource, defaultColor, null);

        if (style != null) {
            theLayer.getStyleBlackboard().put(styleContent.getId(), style);
        }
    }

    public void run() {
        ExtensionPointUtil.process(ProjectPlugin.getPlugin(), StyleContent.XPID, this);
    }
}