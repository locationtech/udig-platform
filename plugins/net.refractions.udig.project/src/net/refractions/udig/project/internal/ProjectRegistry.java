/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * TODO Purpose of net.refractions.udig.project.internal
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 * @model
 */
public interface ProjectRegistry extends EObject {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$
	public static final String PROJECT_FILE = "project.uprj";

    /**
     * Will create or return the default project. This project is called: workspace/newProject.udig
     * (in english versions)
     * 
     * @return the default project
     */
    public Project getDefaultProject();

    /**
     * Returns the last selected/modified project
     * 
     * @return the last selected/modified project
     * @model transient="true"
     */
    public Project getCurrentProject();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.internal.ProjectRegistry#getCurrentProject <em>Current Project</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Current Project</em>' reference.
     * @see #getCurrentProject()
     * @generated
     */
    void setCurrentProject( Project value );

    /**
     * Returns the Project that is associated with the filename.
     * <p>
     * If the Project is not part of the ProjectRegistry it is loaded.
     * </p>
     * <p>
     * <ul>
     * <li>Loads projects when required.</li>
     * </ul>
     * 
     * @model
     * @param uri The file name of the Project
     * @return The Project that maps to the file indicated by name
     */
    public Project getProject( URI uri );

    /**
     * Returns the Project that is associated with the file path.
     * <p>
     * If the Project is not part of the ProjectRegistry it is loaded.
     * </p>
     * <p>
     * <ul>
     * <li>Loads projects when required.</li>
     * </ul>
     * 
     * @model
     * @param projectPath The file path of the Project
     * @return The Project that maps to the file indicated by name
     */
    public Project getProject( String projectPath );

    /**
     * @model type="Project" containment="false" resolveProxies="true"
     * @return All the projects registered with the ProjectRegistry.  This is a muteable list.
     */
    public List<Project> getProjects();

}
