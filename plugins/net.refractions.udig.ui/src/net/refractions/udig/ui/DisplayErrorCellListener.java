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
package net.refractions.udig.ui;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Writes a message to the Status Line if available and changes the BackgroundColor of the editor if possible.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class DisplayErrorCellListener implements ICellEditorListener {

    private CellEditor editor;
    private Color validBackgroundColor;
    private Color validForegroundColor;

    public DisplayErrorCellListener( CellEditor editor ) {
        this.editor=editor;
    }

    public void applyEditorValue() {
        setStatusLineMessage(false, true);
        setEditorBackground(false, true);
    }

    public void cancelEditor() {
        setStatusLineMessage(false, true);
        setEditorBackground(false, true);
    }

    public void editorValueChanged( boolean oldValidState, boolean newValidState ) {
        setStatusLineMessage(oldValidState, newValidState);
        setEditorBackground(oldValidState, newValidState);
    }

    private void setEditorBackground(boolean oldValidState, boolean newValidState) {
        if( oldValidState==newValidState )
            return;
        Control control = editor.getControl();
        
        if( control==null )
            return;
        
        if( oldValidState ){
            
            if( validBackgroundColor==null ){
                this.validBackgroundColor=control.getBackground();
            }
            if( validBackgroundColor==null ){
                this.validForegroundColor=control.getForeground();
            }
        }
        
        if( newValidState ){
            if( validBackgroundColor==null ){
                control.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
            }else{
                control.setBackground(validBackgroundColor);
            }
            if( validForegroundColor==null ){
                control.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
            }else{
                control.setForeground(validBackgroundColor);
            }
        }else{
            control.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
            control.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        }
    }

    private void setStatusLineMessage( boolean oldValidState, boolean newValidState ) {

        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();
        if( activeWorkbenchWindow==null )
            return;
        
        IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
        if( activePage==null )
            return;
        
        IWorkbenchPart activePart = activePage.getActivePart();
        if( activePart==null )
            return;
        
        IActionBars actionBars;
        if( activePart instanceof IViewPart ){
            actionBars=((IViewPart)activePart).getViewSite().getActionBars();
        }else if( activePart instanceof IEditorPart ){
            actionBars=((IEditorPart)activePart).getEditorSite().getActionBars();
        }else{
            return;
        }

        if( newValidState ){
            actionBars.getStatusLineManager().setErrorMessage(null);
        }else{
            actionBars.getStatusLineManager().setErrorMessage(editor.getErrorMessage());
        }
    }

}
