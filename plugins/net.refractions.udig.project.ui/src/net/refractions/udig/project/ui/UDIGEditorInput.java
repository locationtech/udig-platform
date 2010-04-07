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
package net.refractions.udig.project.ui;

import net.refractions.udig.project.IProjectElement;
import net.refractions.udig.project.element.IGenericProjectElement;
import net.refractions.udig.project.element.ProjectElementAdapter;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

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
