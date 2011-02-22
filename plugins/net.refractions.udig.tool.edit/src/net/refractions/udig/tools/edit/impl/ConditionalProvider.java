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
package net.refractions.udig.tools.edit.impl;

import net.refractions.udig.core.IProvider;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;

/**
 * Returns one message when creating and another in other edit states.
 * @author Jesse
 * @since 1.1.0
 */
public class ConditionalProvider implements IProvider<String> {

    private EditToolHandler handler;
    private String creatingMessage;
    private String defaultMessage;

    public ConditionalProvider(EditToolHandler handler, String defaultMessage, String creatingMessage){
        this.handler=handler;
        this.creatingMessage=creatingMessage;
        this.defaultMessage=defaultMessage;
    }

    public String get(Object... params) {
        if( handler.getCurrentState()==EditState.CREATING)
            return creatingMessage;
        return defaultMessage;
    }

}
