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

import java.io.IOException;
import java.util.Collection;
import java.util.List;



/**
 * Provides the methods the {@link StrategizedIssuesList} requires for accessing the remote store.
 *
 * @author Jesse
 * @since 1.1.0
 */
public interface IListStrategy {

    /**
     * Adds issues to the back end
     *
     * @param issues issues to add.
     */
    void addIssues(List<? extends IIssue> issues) throws IOException;
    /**
     * Saves the issue to the storage
     *
     * @param issue issue to save
     */
    void modifyIssue(IIssue issue) throws IOException;
    /**
     * Removes the issues from storage
     *
     * @param issues issues to remove.
     */
    void removeIssues( Collection< ? extends IIssue> issues ) throws IOException;
    /**
     * Returns a list of issues, the issues should be ordered in the correct order.
     *
     * @return a list of issues
     */
    Collection< ? extends IIssue> getIssues() throws IOException;
    /**
     * Returns The extension id of the list as defined in a plugin.xml file.
     * @return The extension id of the list as defined in a plugin.xml file.
     */
    String getExtensionID();
}
