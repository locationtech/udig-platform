/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.printing.ui.internal.editor;

import org.locationtech.udig.printing.model.Page;
import org.locationtech.udig.project.ui.UDIGEditorInput;

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
