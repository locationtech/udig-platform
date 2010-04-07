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
import net.refractions.udig.printing.model.BoxPrinter;
import net.refractions.udig.printing.model.Page;
import net.refractions.udig.printing.ui.internal.editor.commands.BoxCreateCommand;
import net.refractions.udig.printing.ui.internal.editor.commands.SetConstraintCommand;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * This is a policy for the Page.  It allows new boxes to be created.
 * 
 * @author Richard Gould
 * @since 0.3
 */
public class CustomXYLayoutEditPolicy extends XYLayoutEditPolicy {

    protected Command createAddCommand(EditPart child, Object constraint) {
        return null;
    }
    
    protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
    	if (!(constraint instanceof Rectangle)) {
    		return null;
    	}
    	
    	if (getHostFigure().getBounds().contains((Rectangle) constraint)) {
	        
	        SetConstraintCommand locationCommand = new SetConstraintCommand();
	        locationCommand.setNode((Box) child.getModel());
	        locationCommand.setLocation(((Rectangle) constraint).getLocation());
	        locationCommand.setSize(((Rectangle) constraint).getSize());
	        return locationCommand;
        }
        return null;
    }
    
    protected Command getCreateCommand(CreateRequest request) {
		Object child = request.getNewObjectType();
		if (!(child instanceof Class)) {
			return null;
		}
		Class childClass = (Class) child;
		if (BoxPrinter.class.isAssignableFrom(childClass)) {
			// return a command that can add a Shape to a ShapesDiagram 
			return new BoxCreateCommand((Box)request.getNewObject(), 
					(Page)getHost().getModel(), (Rectangle)getConstraintFor(request));
		}
		return null;
    }
    
    protected Command getDeleteDependantCommand(Request request) {
        return null;
    }    

    /**
     * TODO summary sentence for createChildEditPolicy ...
     * 
     * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChildEditPolicy(org.eclipse.gef.EditPart)
     * @param child
     * @return
     */
    protected EditPolicy createChildEditPolicy( EditPart child ) {
        ResizableEditPolicy policy = new ResizableEditPolicy();
        policy.setResizeDirections(-1);
        return policy;
    }
}
