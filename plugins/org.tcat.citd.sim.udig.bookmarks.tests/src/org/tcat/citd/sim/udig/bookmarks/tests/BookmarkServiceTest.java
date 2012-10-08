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
package org.tcat.citd.sim.udig.bookmarks.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.eclipse.emf.common.util.URI;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Before;
import org.junit.Test;
import org.tcat.citd.sim.udig.bookmarks.Bookmark;
import org.tcat.citd.sim.udig.bookmarks.BookmarksPlugin;
import org.tcat.citd.sim.udig.bookmarks.IBookmark;
import org.tcat.citd.sim.udig.bookmarks.IBookmarkService;
import org.tcat.citd.sim.udig.bookmarks.internal.MapReference;

public class BookmarkServiceTest {

    private IBookmarkService service;
    private Bookmark bookmark;
    private ReferencedEnvelope referencedEnvelope = new ReferencedEnvelope(-170.0, 170.0, -90.0, 90.0, DefaultGeographicCRS.WGS84);
    private URI mapUri = URI.createURI("map uri");
    private URI projectUri = URI.createURI("project uri");;
    private MapReference mapID = new MapReference(mapUri, projectUri, "Test Map Reference");
    
    @Before
    public void setup() {
        service = BookmarksPlugin.getBookmarkService();
    }
    
    @Test
    public void testAddBookmark() {
        Collection<IBookmark> bookmarks = service.getBookmarks();
        assertTrue(bookmarks.isEmpty());
        
        bookmark = new Bookmark(referencedEnvelope, mapID, "Test Bookmark 1");
        service.addBookmark(bookmark);
        bookmarks = service.getBookmarks();
        assertEquals(1, bookmarks.size());
        
        Bookmark bookmark2 = new Bookmark(referencedEnvelope, mapID, "Test Bookmark 1");
        service.addBookmark(bookmark2);
        bookmarks = service.getBookmarks();
        assertEquals(2, bookmarks.size());
        
    }

}
