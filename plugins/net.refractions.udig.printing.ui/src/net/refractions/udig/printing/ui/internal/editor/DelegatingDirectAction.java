/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
