/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
