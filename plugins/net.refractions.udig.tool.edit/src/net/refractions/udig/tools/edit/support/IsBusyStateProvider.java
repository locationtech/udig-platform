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
package net.refractions.udig.tools.edit.support;

import net.refractions.udig.core.IProvider;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;

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
