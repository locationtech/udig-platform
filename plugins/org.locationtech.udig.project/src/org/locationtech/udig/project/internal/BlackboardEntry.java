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
 * TODO Purpose of org.locationtech.udig.project.internal
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 * @model
 */
public interface BlackboardEntry extends EObject {

	/**
	 * The key identifiying the object.
	 * 
	 * @uml.property name="key"
	 * @model
	 */
	String getKey();

	/**
	 * Sets the value of the '{@link org.locationtech.udig.project.internal.BlackboardEntry#getKey <em>Key</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Key</em>' attribute.
	 * @see #getKey()
	 * @generated
	 */
	void setKey(String value);

	/**
	 * Contents to persist.
	 * 
	 * @uml.property name="memento"
	 * @model
	 */
	String getMemento();

	/**
	 * Sets the value of the '{@link org.locationtech.udig.project.internal.BlackboardEntry#getMemento <em>Memento</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Memento</em>' attribute.
	 * @see #getMemento()
	 * @generated
	 */
	void setMemento(String value);

	/**
	 * Returns the cached type of the object being stored.
	 * 
	 * @uml.property name="objectClass"
	 * @model transient="true" volatile="true"
	 */
	Class getObjectClass();

	/**
	 * Sets the value of the '{@link org.locationtech.udig.project.internal.BlackboardEntry#getObjectClass <em>Object Class</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Object Class</em>' attribute.
	 * @see #getObjectClass()
	 * @generated
	 */
	void setObjectClass(Class value);

	/**
	 * Returns the cached object.
	 * <p>
	 * Value is generated using IPersister.load( Memento mem ).
	 * </p>
	 * 
	 * @return the object if it exists, or null if the cache is empty.
	 * @uml.property name="object"
	 * @model transient="true" volatile="true"
	 */
	Object getObject();

	/**
	 * Sets the value of the '{@link org.locationtech.udig.project.internal.BlackboardEntry#getObject <em>Object</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Object</em>' attribute.
	 * @see #getObject()
	 * @generated
	 */
	void setObject(Object value);

}
