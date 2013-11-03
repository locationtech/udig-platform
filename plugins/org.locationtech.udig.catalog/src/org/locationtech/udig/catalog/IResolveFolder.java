/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog;

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
