/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.issues;

import java.util.List;
import java.util.Set;

import net.refractions.udig.issues.listeners.IIssuesListListener;


/**
 * Encapsulates a list of issues.  The default implementation is a list kept 
 * in memory.  But other implementation can be defined by the user.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface IIssuesList extends List<IIssue> {
    /**
     * Returns a set of all the issue groups that are in the list. 
     *
     * @return a set of all the issue groups.
     */
    public Set<String> getGroups();
    /**
     * Gets the all issues with the groupID
     *
     * @param groupId groupId of a group of issues
     * @return all issues with the groupID
     */
    public List<IIssue> getIssues(String groupId);
    /**
     * Remove all issues in the group.
     *
     * @param groupId group id of issues to remove 
     */
    public void removeIssues(String groupId);
    /**
     * Add Listener to list
     *
     * @param listener listener to add
     */
    public void addListener( IIssuesListListener listener );
    /**
     * Remove listener from list
     *
     * @param listener listener to remove
     */
    public void removeListener( IIssuesListListener listener );
    /**
     * Returns The extension id of the list as defined in a plugin.xml file.
     * @return The extension id of the list as defined in a plugin.xml file.
     */
    public String getExtensionID();
}