/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.bookmarks;

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
