/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.command;

/**
 * Provides a little introspection into the command stack.  This is the read only interface to a command stack
 * @author Jesse
 * @since 0.3
 */
public interface CommandStack {

    /**
     * @return <code>true</code> if the last command can be undone.
     */
    public boolean canUndo();

    /**
     * return <code>true</code> if a command is available to be redone.
     */
    public boolean canRedo();
}
