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

import net.refractions.udig.issues.IIssuesManager;
import net.refractions.udig.issues.IRemoteIssuesList;

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
