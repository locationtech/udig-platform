/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.style.ui;

import net.refractions.udig.project.internal.Layer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class EditStyleAction implements IWorkbenchWindowActionDelegate {

    public final static String ID = "net.refractions.udig.style.openStyleEditorAction"; //$NON-NLS-1$
    
    private Layer selectedLayer;
    
    public void dispose() {
    }

    public void init( IWorkbenchWindow window ) {
        
    }

    public void run( IAction action ) {
        Display.getDefault().asyncExec(new Runnable(){
            public void run() {
                StyleView styleView = null;
                try {
                    IWorkbenchPage page  = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    page.showView( StyleView.VIEW_ID );
                    
                    //styleView = (StyleView) 
                    //if (selectedLayer != null);
                        //styleView.setSelectedLayer(selectedLayer);
                } 
                catch (PartInitException e2) {
                    e2.printStackTrace(); 
                }
            }
        });
    }

    public void selectionChanged( IAction action, ISelection selection ) {
        if (selection.isEmpty() || !(selection instanceof IStructuredSelection)) 
            return;
        
        StructuredSelection sselection = (StructuredSelection)selection;
        if (sselection.getFirstElement() instanceof Layer) {
            selectedLayer = (Layer)sselection.getFirstElement();
        }
    }
    
    
}