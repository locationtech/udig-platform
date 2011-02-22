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
package net.refractions.udig.issues.listeners;

import net.refractions.udig.issues.IIssue;

/**
 * Indicates that something in the issue has changed.
 *
 * @author Jesse
 * @since 1.1.0
 */
public interface IIssueListener {
    /**
     * Called when some of the data of an issue has changed.
     * Changes to {@link IIssue#getProperty(String)} will cause {@link #notifyPropertyChanged(IIssue, String, Object, Object)} to
     * be called.
     *
     *@param event the event data
     */
    void notifyChanged( IssueEvent event);

    /**
     * Called when a property of an issue ({@link IIssue#getProperty(String)}) is changed.
     *
     * @param event event data.
     */
    void notifyPropertyChanged( IssuePropertyChangeEvent event);
}
