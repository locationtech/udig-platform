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

import org.locationtech.udig.issues.IIssuesManager;
import org.locationtech.udig.issues.IRemoteIssuesList;

/**
 * Enumerates the different types of events that {@link IIssuesManager} raises
 * @author Jesse
 * @since 1.1.0
 */
public enum IssuesManagerEventType {
    /**
     * Indicates that the issues list being used has been changed.
     * <p>
     * The old and new values are the old and new list
     * </p>
     */
    ISSUES_LIST_CHANGE,
    /**
     * Indicates that an issue in the issues list has become dirty or clean.  Events of this type are only
     * fired if the IssuesList is of type {@link IRemoteIssuesList}.
     * <p>
     * The old and new values are the old and new dirty state of the Manager
     * </p>
     */
    DIRTY_ISSUE,
    /**
     * Indicates that all the dirty issues are saved.
     * <p>
     * new value is null and old value is a collection of the issues that were saved.
     * </p>
     */
    SAVE
    
}
