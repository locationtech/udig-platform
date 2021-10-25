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

import org.locationtech.udig.project.IProject;

import org.eclipse.emf.ecore.EObject;

/**
 * The read/write interface for IProject.
 * 
 * @author Jesse
 * @since 1.0.0
 * @model
 */
public interface Project extends EObject, IProject {
	/**
	 * Returns an unmodifiable
	 */
	@Override
	public <E> List<E> getElements(Class<E> type);

	/**
	 * Returns a List with all elements in the project
	 * THis list is modifiable.
	 * 
	 * @return a list with all in the project
	 * @model type="ProjectElement" opposite="projectInternal"
	 */
	public List<ProjectElement> getElementsInternal();

	/**
	 * gets the name of the project
	 * 
	 * @return the name of the project
	 * @uml.property name="name"
	 * @model
	 */
	@Override
	public String getName();

	/**
	 * Sets the value of the '{@link org.locationtech.udig.project.internal.Project#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

}
