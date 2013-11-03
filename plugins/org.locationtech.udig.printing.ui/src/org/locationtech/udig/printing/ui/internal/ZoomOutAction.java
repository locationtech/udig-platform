/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.printing.ui.internal;

import org.locationtech.udig.printing.model.Page;
import org.locationtech.udig.printing.ui.internal.editor.PageEditorInput;
import org.locationtech.udig.project.ui.UDIGEditorInput;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Action to zoom out the print page.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class ZoomOutAction extends Action implements IEditorActionDelegate {

    public void setActiveEditor( IAction action, IEditorPart targetEditor ) {
    }

    public void run( IAction action ) {
        run();
    }

    public void run() {
        Page page = null;
        IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        UDIGEditorInput editorInput = (UDIGEditorInput) workbenchWindow.getActivePage()
                .getActiveEditor().getEditorInput();
        if (editorInput instanceof PageEditorInput) {
            page = (Page) ((PageEditorInput) editorInput).getProjectElement();
        }
        if (page == null) {
            throw new RuntimeException(Messages.PrintAction_pageError);
        }

        Dimension pSize = page.getSize();

        float factor = (float) pSize.height / (float) pSize.width;
        float xPlus = 10f;
        float yPlus = xPlus * factor;
        int w = pSize.width - (int) xPlus;
        int h = pSize.height - (int) yPlus;
        page.setSize(new Dimension(w, h));

    }

    public void selectionChanged( IAction action, ISelection selection ) {
    }

}
