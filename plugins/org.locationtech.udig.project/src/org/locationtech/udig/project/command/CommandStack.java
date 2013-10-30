/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.command;

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
