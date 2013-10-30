/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues;

/**
 * An object that will refresh the issues viewer in the issues view upon request.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface IRefreshControl {
    /**
     * Refreshes tree and labels from newly obtained data from the content and label providers.  
     * Same as refresh(true);
     */
    void refresh();
    /**
     * Refreshes tree and labels from newly obtained data from the content and label providers.  
     *
     * @param updateLabels true if labels should be refreshed.
     */
    void refresh(boolean updateLabels);
    /**
     * Refreshes the viewer starting with the provided element.
     *
     * @param element root element to refresh
     */
    void refresh(Object element);
    /**
     * Refreshes the viewer starting with the provided element.
     * Labels are updated if updateLabels is true 
     *
     * @param element root element to refresh
     * @param updateLabels true if labels should be refreshed.
     */
    public void refresh(Object element,
            boolean updateLabels);
}
