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
package net.refractions.udig.printing.ui.internal.editor.commands;

import net.refractions.udig.printing.ui.EditActionFactory;
import net.refractions.udig.printing.ui.actions.EditMapAction;
import net.refractions.udig.printing.ui.internal.editor.policies.LabelDirectEditPolicy;
import net.refractions.udig.printing.ui.internal.editor.policies.MapEditPolicy;

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
