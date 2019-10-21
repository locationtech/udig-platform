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
package org.locationtech.udig.project.ui;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.locationtech.udig.project.IProjectElement;
import org.locationtech.udig.project.element.IGenericProjectElement;
import org.locationtech.udig.project.element.ProjectElementAdapter;

/**
 * So the project explorer could generically open any editor.
 * 
 * @author jones
 * @since 0.3
 */
public abstract class UDIGEditorInput implements IEditorInput {
    protected IProjectElement projectElement;
    private String editorID;

    /**
     * @return Returns the projectElement.
     */
    public IProjectElement getProjectElement() {
        return projectElement;
    }
    /**
     * @param projectElement The projectElement to set.
     */
    public void setProjectElement( IProjectElement projectElement ) {
        this.projectElement = projectElement;
    }
    
    @Override
    public boolean equals(Object arg0) {
    	if (arg0 instanceof UDIGEditorInput) {
			UDIGEditorInput input = (UDIGEditorInput) arg0;
			return this.getProjectElement().equals(input.getProjectElement());
		}
    	return false;
    }
    
    @Override
    public int hashCode() {
    	return getProjectElement().hashCode();
    }
    /**
     * The id of the default editor for this type of input.
     * @return The id of the default editor for this type of input.
     */
    public String getEditorId() {
        return editorID;
    }
    /**
     * @param editorID The editorID to set.
     */
    public void setEditorId( String editorID ) {
        this.editorID = editorID;
    }

	public boolean exists() {
		return true;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if( adapter.isAssignableFrom(projectElement.getClass())){
			return projectElement;
		}
		if( projectElement instanceof ProjectElementAdapter){
			IGenericProjectElement genericProjectElement = ((ProjectElementAdapter) projectElement).getBackingObject();
			if( adapter.isAssignableFrom(genericProjectElement.getClass())){
				return genericProjectElement;
			}
		}
		return null;
	}

}
