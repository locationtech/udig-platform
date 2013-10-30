/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.listeners;

import org.locationtech.udig.issues.IIssue;

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
