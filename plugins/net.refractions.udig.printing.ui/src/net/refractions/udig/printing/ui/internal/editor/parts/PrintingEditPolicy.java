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
package net.refractions.udig.printing.ui.internal.editor.parts;

import net.refractions.udig.printing.ui.internal.editor.BoxAction;

import org.eclipse.gef.Request;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;

/**
 * EditPolicy for {@link LabelPart}.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class PrintingEditPolicy extends AbstractEditPolicy {

    private BoxAction action;


    public PrintingEditPolicy( BoxAction action2 ) {
        this.action=action2;
    }


    @Override
    public boolean understandsRequest( Request req ) {
        return req.getType().equals(action.getRequest().getType());
    }


    public BoxAction getAction() {
        return action;
    }
    
    
}
