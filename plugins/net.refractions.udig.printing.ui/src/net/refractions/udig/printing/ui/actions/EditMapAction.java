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
package net.refractions.udig.printing.ui.actions;

import java.util.List;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.impl.MapBoxPrinter;
import net.refractions.udig.printing.ui.internal.Messages;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Action that allows the map, that is part of the page, to be edited.
 * @author Richard Gould
 * @since 0.3
 */
public class EditMapAction extends SelectionAction {
    
    public static final String EDIT_MAP_REQUEST = "EDIT_MAP"; //$NON-NLS-1$
    public static final String EDIT_MAP = EDIT_MAP_REQUEST;
    private Request request;

    /**
     * Construct <code>EditMapAction</code>.
     *
     * @param part
     */
    public EditMapAction( IWorkbenchPart part ) {
        super(part);
        request = new Request(EDIT_MAP_REQUEST);
        setText(Messages.EditMapAction_action_text); 
        setId(EDIT_MAP);
        setToolTipText(Messages.EditMapAction_action_tooltip); 
        
    }

    /**
     * TODO summary sentence for calculateEnabled ...
     * 
     * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
     * @return
     */
    protected boolean calculateEnabled() {
    	if (getSelectedObjects().isEmpty())
    		return false;
    	List parts = getSelectedObjects();
    	for (int i=0; i<parts.size(); i++){
    		Object o = parts.get(i);
    		if (!(o instanceof EditPart))
    			return false;
    		EditPart part = (EditPart)o;
    		if (!(part.getModel() instanceof Box))
                if( !(((Box)part.getModel()).getBoxPrinter() instanceof MapBoxPrinter) )
                    return false;
    	}
    	return true;
    }
    
    private Command getCommand() {
    	List editparts = getSelectedObjects();
    	CompoundCommand cc = new CompoundCommand();
    	cc.setDebugLabel("Edit Maps");//$NON-NLS-1$
    	for (int i=0; i < editparts.size(); i++) {
    		EditPart part = (EditPart)editparts.get(i);
    		cc.add(part.getCommand(request));
    	}
    	return cc;
    }

    /**
     * TODO summary sentence for run ...
     * 
     * @see org.eclipse.jface.action.IAction#run()
     * 
     */
    public void run() {
        execute(getCommand());
    }
}
