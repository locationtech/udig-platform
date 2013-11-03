/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.printing.ui.internal.editor.parts;

import org.locationtech.udig.printing.ui.internal.editor.BoxAction;

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
