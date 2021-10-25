/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal;

import org.eclipse.emf.ecore.EObject;

/**
 * Encapsulates an entry in the StyleBlackboard.  
 * 
 * @author Jesse
 * @since 1.0.0
 * @model
 */
public interface StyleEntry extends EObject {
	/**
	 * The ID is a unique identifier required by the Renderer and StyleConfigurator.
	 * 
	 * @uml.property name="iD"
	 * @model
	 */
	String getID();

	/**
	 * Sets the value of the '{@link org.locationtech.udig.project.internal.StyleEntry#getID <em>ID</em>}' attribute.
	 * <!-- begin-user-doc --> The ID is a unique identifier required by the Renderer and
	 * StyleConfigurator. <!-- end-user-doc -->
	 * @param value the new value of the '<em>ID</em>' attribute.
	 * @see #getID()
	 * @generated
	 */
	void setID(String value);

	/**
	 * Contents to persist, genrated StyleContent.save( Memento mem, value ).
	 * 
	 * @uml.property name="memento"
	 * @model
	 */
	String getMemento();

	/**
	 * Sets the value of the '{@link org.locationtech.udig.project.internal.StyleEntry#getMemento <em>Memento</em>}' attribute.
	 * <!-- begin-user-doc --> Contents to persist, genrated StyleContent.save( Memento
	 * mem, value ). <!-- end-user-doc -->
	 * @param value the new value of the '<em>Memento</em>' attribute.
	 * @see #getMemento()
	 * @generated
	 */
	void setMemento(String value);

	/**
	 * Returns the cached style object.
	 * <p>
	 * Value is generated using StyleContent.load( Memento mem ).
	 * </p>
	 * 
	 * @return the object if it exists, or null if the cache is empty.
	 * @uml.property name="style"
	 * @model transient="true" volatile="true"
	 */
	public Object getStyle();

	/**
	 * Sets the value of the '{@link org.locationtech.udig.project.internal.StyleEntry#getStyle <em>Style</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Style</em>' attribute.
	 * @see #getStyle()
	 * @generated
	 */
	void setStyle(Object value);

	/**
	 * Returns the cached class of the style object. Used for doing class based lookups on the style
	 * blackboard.
	 * 
	 * @uml.property name="styleClass"
	 * @model transient="true" volatile="true"
	 */
	public Class getStyleClass();

	/**
	 * Sets the value of the '{@link org.locationtech.udig.project.internal.StyleEntry#getStyleClass <em>Style Class</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Style Class</em>' attribute.
	 * @see #getStyleClass()
	 * @generated
	 */
	void setStyleClass(Class value);

	/**
	 * Indicates whether the entry is <em>selected</em>.
	 *
	 * @param selected true if the entry is selected.
	 * @see StyleBlackboard#setSelected(String[])
	 */
	void setSelected(boolean selected);

	/**
	 * Indicates whether the entry is <em>selected</em>.
	 *
	 * @return true if the entry is selected.
	 * @see StyleBlackboard#setSelected(String[])
	 */
	boolean isSelected();
}
