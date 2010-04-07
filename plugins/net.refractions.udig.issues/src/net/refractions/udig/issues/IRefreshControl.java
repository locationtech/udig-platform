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
package net.refractions.udig.issues;

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
