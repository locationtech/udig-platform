/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.printing.ui.internal.editor.policies;

import org.locationtech.udig.printing.model.Box;
import org.locationtech.udig.printing.model.impl.LabelBoxPrinter;
import org.locationtech.udig.printing.ui.internal.editor.commands.RenameLabelCommand;
import org.locationtech.udig.printing.ui.internal.editor.figures.BoxFigure;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

/**
 * Allows the text of a label to be edited.
 * 
 * @author Richard Gould
 * @since 0.3
 */
public class LabelDirectEditPolicy extends DirectEditPolicy {
    
    protected Command getDirectEditCommand(DirectEditRequest request) {
        RenameLabelCommand cmd = new RenameLabelCommand();
        cmd.setNode((LabelBoxPrinter) ((Box) getHost().getModel()).getBoxPrinter());
        cmd.setName((String) request.getCellEditor().getValue());
        return cmd;
    }
    
    protected void showCurrentEditValue( DirectEditRequest request ) {
        String value = (String) request.getCellEditor().getValue();
        ((BoxFigure) getHostFigure()).setName(value);
    }
}
