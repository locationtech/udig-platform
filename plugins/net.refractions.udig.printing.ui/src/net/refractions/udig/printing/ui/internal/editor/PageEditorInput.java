/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.printing.ui.internal.editor;

import net.refractions.udig.printing.model.Page;
import net.refractions.udig.project.ui.UDIGEditorInput;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;

/**
 * Provides for editing of a Page object within uDig
 * @author Richard Gould
 * @since 0.3
 */
public class PageEditorInput extends UDIGEditorInput {

    /**
     * TODO summary sentence for exists ...
     * 
     * @see org.eclipse.ui.IEditorInput#exists()
     * @return
     */
    public boolean exists() {
        return false;
    }

    /**
     * TODO summary sentence for getImageDescriptor ...
     * 
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     * @return
     */
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    /**
     * TODO summary sentence for getPersistable ...
     * 
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     * @return
     */
    public IPersistableElement getPersistable() {
        return null;
    }

    /**
     * TODO summary sentence for getToolTipText ...
     * 
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     * @return
     */
    public String getToolTipText() {
        return ((Page) getProjectElement()).getName();
    }

    /**
     * TODO summary sentence for getAdapter ...
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     * @param adapter
     * @return
     */
    public Object getAdapter( Class adapter ) {
        return null;
    }

    /**
     * TODO summary sentence for getName ...
     * 
     * @see org.eclipse.ui.IEditorInput#getName()
     * @return
     */
    public String getName() {
        return ((Page) getProjectElement()).getName();
    }

}
