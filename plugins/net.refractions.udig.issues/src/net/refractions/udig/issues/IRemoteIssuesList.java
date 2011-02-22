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


/**
 * Indicates a subtype of issues list that is backed onto a (probably) remote source that does not notify when it is changed and therefore must
 * be manually refreshed().
 *
 * @author Jesse
 * @since 1.1.0
 */
public interface IRemoteIssuesList extends IIssuesList {
    /**
     * Queries the data source to ensure that the list accurately reflects the state of the backend store
     */
    void refresh() throws IOException;
    /**
     * Writes the state of the {@link IIssue} out to the store.
     */
    void save(IIssue issue) throws IOException;
}
