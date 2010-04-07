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
package net.refractions.udig.printing.ui.internal;

import net.refractions.udig.printing.model.BoxPrinter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * A utility class for processing Box extensions.  Provides a convenient way of accessing
 * the data in a particular box configuration element.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class BoxFactory {

    private IConfigurationElement configurationElement;
    private boolean visible;
    private String description;
    private ImageDescriptor smallImage;
    private ImageDescriptor largeImage;
    private String name;
    private Class< ? extends BoxPrinter> type;
    private boolean attempted=false;

    public BoxFactory( IConfigurationElement element ) {
        this.configurationElement=element;
        this.visible="true".equals(element.getAttribute("visible"));  //$NON-NLS-1$//$NON-NLS-2$
        this.description=element.getAttribute("description"); //$NON-NLS-1$
        this.smallImage=(createDescriptor(element, element.getAttribute("smallImage"))); //$NON-NLS-1$
        this.largeImage=(createDescriptor(element, element.getAttribute("largeImage"))); //$NON-NLS-1$
        this.name=element.getAttribute("name"); //$NON-NLS-1$
    }

    private ImageDescriptor createDescriptor( IConfigurationElement element, String imagePath ) {
        if( imagePath==null )
            return null;
        
        String pluginID = element.getNamespaceIdentifier();
        
        ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(pluginID, imagePath);
        return imageDescriptor;
    }

    public boolean isVisible() {
        return visible;
    }

    public synchronized BoxPrinter createBox() throws CoreException {
        BoxPrinter box = (BoxPrinter) configurationElement.createExecutableExtension("class");//$NON-NLS-1$
        this.type=box.getClass();
        return box; 
    }
    
    public synchronized Class< ? extends BoxPrinter> getType() {
//        return null;
        if( type==null && !attempted ){
            attempted=true;
            try {
                createBox();
            } catch (CoreException e) {
                PrintingPlugin.log("", e); //$NON-NLS-1$
            }
        }
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ImageDescriptor getSmallImage() {
        return smallImage;
    }

    public ImageDescriptor getLargeImage() {
        return largeImage;
    }

    /**
     * Returns the id of the EditAction extension that is the "default" action.  In practice this is
     * the action that is ran on a double click.
     */
    public String getDefaultActionID() {
        return configurationElement.getAttribute("defaultAction"); //$NON-NLS-1$;
    }

}
