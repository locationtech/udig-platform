/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
