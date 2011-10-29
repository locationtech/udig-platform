/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
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
package org.tcat.citd.sim.udig.bookmarks;

import java.util.Collection;

/**
 * Quick listener to <code>BookmarkService</code> providing notification of changes to 
 * the list of bookmarks
 * 
 * @see java.util.EventListener
 */
public interface BookmarkListener {
    /**
     * Captures a change to the bookmarks
     * 
     * @author paul.pfeiffer
     * @version 1.3.0
     */
    public static class Event {
        public Collection<IBookmark> source;
        public Event( Collection<IBookmark> source ) {
            this.source = source;
        }
    }

    /**
     * BookmarkListener event notification.
     * 
     * @param event the event which occurred
     */
    void handleEvent( Event event );

}
