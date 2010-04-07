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
package net.refractions.udig.printing.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Creates an action that will be added to the PageEditor's {@link org.eclipse.gef.ui.actions.ActionRegistry}.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface EditActionFactory {
    /**
     * Creates the action that must be added to the page editor.
     *
     * @param part the workbench part that the action will listen to.
     * @param policy the policy object that needs an action.
     * @return the action that must be added to the page editor.
     */
    public Action create( IWorkbenchPart part, Object policy);
}
