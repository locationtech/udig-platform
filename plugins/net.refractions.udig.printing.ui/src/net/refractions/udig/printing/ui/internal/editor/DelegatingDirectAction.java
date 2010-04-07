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
package net.refractions.udig.printing.ui.internal.editor;

import net.refractions.udig.printing.ui.internal.PrintingPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Permits the lazy loading of edit actions.  The name of the edit action is loaded from the extension point
 * but the class is not created until the action is clicked. 
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class DelegatingDirectAction extends SelectionAction implements IAction {

    private IConfigurationElement element;
    private IActionDelegate action;

    public DelegatingDirectAction( IWorkbenchPart part, IConfigurationElement element ) {
        super(part);
        this.element=element;
        setText(element.getAttribute("name")); //$NON-NLS-1$
        setId(element.getAttribute("id")); //$NON-NLS-1$
    }
    
    @Override
    public void run() {
        if( action==null ){
            try {
                action=(IActionDelegate) element.createExecutableExtension("action");//$NON-NLS-1$
            } catch (CoreException e) {
                PrintingPlugin.log("", e); //$NON-NLS-1$
            } 
        }
        if( action!=null )
            action.run(this);
    }

    @Override
    protected boolean calculateEnabled() {
        return false;
    }

}
