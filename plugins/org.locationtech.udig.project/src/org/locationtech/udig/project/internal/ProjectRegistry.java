/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * TODO Purpose of org.locationtech.udig.project.internal
 * <p>
 * </p>
 *
 * @author Jesse
 * @since 1.0.0
 * @model
 */
public interface ProjectRegistry extends EObject {

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
     * Sets the value of the '{@link org.locationtech.udig.project.internal.ProjectRegistry#getCurrentProject <em>Current Project</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Current Project</em>' reference.
     * @see #getCurrentProject()
     * @generated
     */
    void setCurrentProject(Project value);

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
    public Project getProject(URI uri);

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
    public Project getProject(String projectPath);

    /**
     * @model type="Project" containment="false" resolveProxies="true"
     * @return All the projects registered with the ProjectRegistry.  This is a muteable list.
     */
    public List<Project> getProjects();

}
