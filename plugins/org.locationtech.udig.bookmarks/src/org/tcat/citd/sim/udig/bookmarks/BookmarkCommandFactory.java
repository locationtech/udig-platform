/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
