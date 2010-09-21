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
package net.refractions.udig.catalog;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * An element that is part of the IResolve Hierarchy but is there only for structural purposes.  It can have a Title and a Icon associated 
 * so the folder can be displayed in the UI.
 * 
 * @author jesse
 * @since 1.1.0
 */
public interface IResolveFolder extends IResolve {
    /**
     * Loads an image from some source that represents this folder.
     *
     * @param monitor a monitor to display the progress of obtaining the image.
     * @return an image from some source that represents this folder.
     */
    public ImageDescriptor getIcon( IProgressMonitor monitor );
    /**
     * Obtains a title to use for when displaying this resolve in the ui
     *
     * @return a title to use for when displaying this resolve in the ui
     */
    public String getTitle();
    
    public IService getService( IProgressMonitor monitor );
    
}
