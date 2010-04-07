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
 * Represents the event where one of an issue's properties ({@link IIssue#getProperty(String)} has changed
 * @author Jesse
 * @since 1.1.0
 */
public class IssuePropertyChangeEvent extends AbstractIssueEvent {

    private final String propertyKey;

    /**
     * New Instance
     * 
     * @param propertyKey the property that has changed
     */
    public IssuePropertyChangeEvent( IIssue source, String propertyKey, Object newValue, Object oldValue ) {
        super(source, newValue, oldValue);
        this.propertyKey=propertyKey;
    }

    /**
     * Returns the name of the property that has changed
     *
     * @return the name of the property that has changed
     */
    public String getPropertyKey() {
        return propertyKey;
    }

}
