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

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineManager;

/**
 * Adapts a {@link StatusLineManager} as a {@link IMessageBoard}
 *
 * @author Jesse
 * @since 1.1.0
 */
public class StatusLineMessageBoardAdapter implements IMessageBoard {

    final IStatusLineManager manager;

    public StatusLineMessageBoardAdapter(final IStatusLineManager manager) {
        this.manager = manager;
    }

    @Override
    public void putMessage(String message, Type type) {
        if (type == Type.ERROR)
            manager.setErrorMessage(message);
        else {
            manager.setErrorMessage(null);
            manager.setMessage(message);
        }
    }

}
