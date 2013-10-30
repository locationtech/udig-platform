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
package org.locationtech.udig.printing.ui.actions;

import java.util.List;

import org.locationtech.udig.printing.model.Box;
import org.locationtech.udig.printing.model.impl.MapBoxPrinter;
import org.locationtech.udig.printing.ui.internal.Messages;

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
