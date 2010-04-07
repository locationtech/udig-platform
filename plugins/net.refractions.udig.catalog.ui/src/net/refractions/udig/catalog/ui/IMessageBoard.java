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
package net.refractions.udig.catalog.ui;

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
    void putMessage( String message, Type messageType );
    
    enum Type{
        NORMAL, 
        ERROR,
        WARNING
    }
}
