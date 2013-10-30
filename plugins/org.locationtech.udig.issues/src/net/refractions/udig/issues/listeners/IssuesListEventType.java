/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.issues.listeners;

/**
 * The types of change that can happen to a list. Add, remove, save
 * 
 * @author jones
 * @since 1.0.0
 */
public enum IssuesListEventType {

    /**
     * Items were added
     */
    ADD,
    /**
     * Items were removed
     */
    REMOVE,
    /**
     * Indicates that an issues in the list has been saved
     */
    SAVE,
    /**
     * Indicates that the issues list has been loaded.  The collection of features in
     * the event are the features that were added as a result of the refresh.
     */
    REFRESH,
}
