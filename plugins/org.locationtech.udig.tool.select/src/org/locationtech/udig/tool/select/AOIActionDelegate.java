/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tool.select;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * TODO Purpose of 
 * Sets the AOI (Area of Interest) filter used by the TableView.
 * 
 * @see TableView.setAOIFilter();
 * @author leviputna
 * @since 1.2.0
 */
public class AOIActionDelegate extends Action implements IViewActionDelegate {
    
    private TableView view;
    private IStructuredSelection selection;

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run( IAction action ) {
        filterTable(action.isChecked());
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( IAction action, ISelection selection ) {
        try {
            this.selection = (IStructuredSelection) selection;
        } catch (Exception e) { // do nothing
        }
    }
    

    /* (non-Javadoc)
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    @Override
    public void init( IViewPart view ) {
        if (view != null && view instanceof TableView) {
            this.view = (TableView) view;
        }
    }
    
    public void setActivePart( IAction action, IWorkbenchPart targetPart ) {
        if (targetPart != null && targetPart instanceof TableView) {
            view = (TableView) targetPart;
        }
    }
    
    private void filterTable(boolean filter) {
        if (view != null) {
            view.setAOIFilter(filter);
        }
    }

}
