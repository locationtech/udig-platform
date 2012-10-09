/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.browser;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author mleslie
 * @since 1.0.0
 */
public class EditorPlaceholder implements IEditorPart {

    IEditorPart getEditorPart() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getActivePage().getActiveEditor();
    }
    
    public IEditorInput getEditorInput() {
        return getEditorPart() != null ? getEditorPart().getEditorInput()
            : null;
    }

    public IEditorSite getEditorSite() {
        return getEditorPart() != null ? getEditorPart().getEditorSite()
            : null;
    }

    public void init(IEditorSite site, IEditorInput input) 
        throws PartInitException {
        
        if (getEditorPart() != null) 
                getEditorPart().init(site, input);
    }

    public void addPropertyListener(IPropertyListener listener) {
        if (getEditorPart() != null) 
            getEditorPart().addPropertyListener(listener);
    }

    public void createPartControl(Composite parent) {
        if (getEditorPart() != null)
            getEditorPart().createPartControl(parent);
    }

    public void dispose() {
        if (getEditorPart() != null)
            getEditorPart().dispose();
    }

    public IWorkbenchPartSite getSite() {
        return getEditorPart() != null ? getEditorPart().getSite() 
                : null;
    }

    public String getTitle() {
        return getEditorPart() != null ? getEditorPart().getTitle() 
                : null;
    }

    public Image getTitleImage() {
        return getEditorPart() != null ? getEditorPart().getTitleImage() 
                : null;
    }

    public String getTitleToolTip() {
        return getEditorPart() != null ? getEditorPart().getTitleToolTip() 
                : null;
    }

    public void removePropertyListener(IPropertyListener listener) {
        if (getEditorPart() != null)
            getEditorPart().removePropertyListener(listener);
    }

    public void setFocus() {
        if (getEditorPart() != null)
            getEditorPart().setFocus();
    }

    public Object getAdapter(Class adapter) {
        return getEditorPart() != null 
            ? getEditorPart().getAdapter(adapter)
            : null;
    }

    public void doSave(IProgressMonitor monitor) {
        if (getEditorPart() != null)
            getEditorPart().doSave(monitor);
    }

    public void doSaveAs() {
        if (getEditorPart() != null)
            getEditorPart().doSaveAs();
    }

    public boolean isDirty() {
        return getEditorPart() != null ? getEditorPart().isDirty()
                : false;
    }

    public boolean isSaveAsAllowed() {
        return getEditorPart() != null ? getEditorPart().isSaveAsAllowed()
                : false;
    }

    public boolean isSaveOnCloseNeeded() {
        return getEditorPart() != null 
            ? getEditorPart().isSaveOnCloseNeeded()
            : false;
    }
    
}
