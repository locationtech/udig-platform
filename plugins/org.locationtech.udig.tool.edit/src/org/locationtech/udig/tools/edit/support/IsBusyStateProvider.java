/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.support;

import org.locationtech.udig.core.IProvider;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;

/**
 * Returns true if the current state is busy
 * @author Jesse
 * @since 1.1.0
 */
public class IsBusyStateProvider implements IProvider<Boolean> {

    private EditToolHandler handler;

    public IsBusyStateProvider( EditToolHandler handler ) {
        this.handler=handler;
    }

    public Boolean get(Object... params) {
        return handler.getCurrentState()==EditState.BUSY;
    }

}
