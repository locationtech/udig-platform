/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.printing.ui.internal.editor.commands;

import org.locationtech.udig.printing.ui.EditActionFactory;
import org.locationtech.udig.printing.ui.actions.EditMapAction;
import org.locationtech.udig.printing.ui.internal.editor.policies.LabelDirectEditPolicy;
import org.locationtech.udig.printing.ui.internal.editor.policies.MapEditPolicy;

import org.eclipse.gef.ui.actions.DirectEditAction;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Factory that creates actions for MapEditPolicies and LabelDirectEditPolicies.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class StandardEditActionFactory implements EditActionFactory {

    public Action create( IWorkbenchPart part, Object policy ) {
        if (policy instanceof LabelDirectEditPolicy) {
            return new DirectEditAction(part);
        }
        
        if (policy instanceof MapEditPolicy ) {
            return new EditMapAction(part);
        }
        
        return null;
    }

}
