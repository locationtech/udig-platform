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
