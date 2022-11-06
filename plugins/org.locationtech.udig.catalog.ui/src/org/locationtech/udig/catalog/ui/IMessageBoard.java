/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui;

/**
 * Abstracts out a place to post messages.
 *
 * @see StatusLineMessageBoardAdapter
 * @author Jesse
 * @since 1.1.0
 */
public interface IMessageBoard {
    /**
     * Adds/Sets a message on the message board.
     *
     * @param message message to set on the message board.
     */
    void putMessage(String message, Type messageType);

    enum Type {
        NORMAL, ERROR, WARNING
    }
}
