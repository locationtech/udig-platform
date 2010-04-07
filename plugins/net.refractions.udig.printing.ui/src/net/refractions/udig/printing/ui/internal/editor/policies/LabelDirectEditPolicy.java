/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.printing.ui.internal.editor.policies;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.impl.LabelBoxPrinter;
import net.refractions.udig.printing.ui.internal.editor.commands.RenameLabelCommand;
import net.refractions.udig.printing.ui.internal.editor.figures.BoxFigure;

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
