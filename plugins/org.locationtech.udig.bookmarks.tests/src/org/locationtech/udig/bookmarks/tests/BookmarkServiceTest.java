/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.bookmarks.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.eclipse.emf.common.util.URI;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.bookmarks.Bookmark;
import org.locationtech.udig.bookmarks.BookmarksPlugin;
import org.locationtech.udig.bookmarks.IBookmark;
import org.locationtech.udig.bookmarks.IBookmarkService;
import org.locationtech.udig.bookmarks.internal.MapReference;

public class BookmarkServiceTest {

    private IBookmarkService service;

    private Bookmark bookmark;

    private ReferencedEnvelope referencedEnvelope = new ReferencedEnvelope(-170.0, 170.0, -90.0,
            90.0, DefaultGeographicCRS.WGS84);

    private URI mapUri = URI.createURI("map uri"); //$NON-NLS-1$

    private URI projectUri = URI.createURI("project uri");; //$NON-NLS-1$

    private MapReference mapID = new MapReference(mapUri, projectUri, "Test Map Reference"); //$NON-NLS-1$

    @Before
    public void setup() {
        service = BookmarksPlugin.getBookmarkService();
    }

    @Test
    public void testAddBookmark() {
        Collection<IBookmark> bookmarks = service.getBookmarks();
        assertTrue(bookmarks.isEmpty());

        bookmark = new Bookmark(referencedEnvelope, mapID, "Test Bookmark 1"); //$NON-NLS-1$
        service.addBookmark(bookmark);
        bookmarks = service.getBookmarks();
        assertEquals(1, bookmarks.size());

        Bookmark bookmark2 = new Bookmark(referencedEnvelope, mapID, "Test Bookmark 1"); //$NON-NLS-1$
        service.addBookmark(bookmark2);
        bookmarks = service.getBookmarks();
        assertEquals(2, bookmarks.size());

    }

}
