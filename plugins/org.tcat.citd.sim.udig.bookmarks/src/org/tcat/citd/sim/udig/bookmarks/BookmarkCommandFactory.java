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
package org.tcat.citd.sim.udig.bookmarks;

import net.refractions.udig.project.command.NavCommand;

import org.tcat.citd.sim.udig.bookmarks.internal.command.GotoBookmarkCommand;

/**
 * Factory for creating bookmark-related commands
 * <p>
 *
 * </p>
 * @author cole.markham
 * @since 1.0.0
 */
public class BookmarkCommandFactory {
    /**
     * Creates a new BookmarkCommandFactory object
     * 
     * @return a new BookmarkCommandFactory object
     */
    public static BookmarkCommandFactory getInstance() {
        return instance;
    }
    private static final BookmarkCommandFactory instance = new BookmarkCommandFactory();
    private BookmarkCommandFactory(){
        // no op
    }

    /**
     * Creates a new {@linkplain GotoBookmarkCommand}
     * 
     * @param target The target bookmark for the new command
     * @return a new GotoBookmarkCommand object
     * @see NavCommand
     */
    public NavCommand createGotoBookmarkCommand(Bookmark target) {
        return new GotoBookmarkCommand(target);
    }
}
