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

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineManager;

/**
 * Adapts a {@link StatusLineManager} as a {@link IMessageBoard}
 * @author Jesse
 * @since 1.1.0
 */
public class StatusLineMessageBoardAdapter implements IMessageBoard{

    final IStatusLineManager manager;

    public StatusLineMessageBoardAdapter( final IStatusLineManager manager ) {
        this.manager = manager;
    }

    public void putMessage( String message, Type type ) {
        if( type==Type.ERROR )
            manager.setErrorMessage(message);
        else{
            manager.setErrorMessage(null);
            manager.setMessage(message);
        }
    }

}
