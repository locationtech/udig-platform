/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 * $Id$
 */
package org.locationtech.udig.project.element.impl;

import java.util.List;

import org.locationtech.udig.core.internal.ExtensionPointList;
import org.locationtech.udig.project.IProject;
import org.locationtech.udig.project.element.ElementPackage;
import org.locationtech.udig.project.element.IGenericProjectElement;
import org.locationtech.udig.project.element.ProjectElementAdapter;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectPackage;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Project Element Adapter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.locationtech.udig.project.element.impl.ProjectElementAdapterImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.locationtech.udig.project.element.impl.ProjectElementAdapterImpl#getProjectInternal <em>Project Internal</em>}</li>
 *   <li>{@link org.locationtech.udig.project.element.impl.ProjectElementAdapterImpl#getBackingObject <em>Backing Object</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ProjectElementAdapterImpl extends EObjectImpl implements ProjectElementAdapter {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getProjectInternal() <em>Project Internal</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProjectInternal()
	 * @generated
	 * @ordered
	 */
	protected Project projectInternal;

	/**
	 * The default value of the '{@link #getBackingObject() <em>Backing Object</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBackingObject()
	 * @generated
	 * @ordered
	 */
	protected static final IGenericProjectElement BACKING_OBJECT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBackingObject() <em>Backing Object</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBackingObject()
	 * @generated
	 * @ordered
	 */
	protected IGenericProjectElement backingObject = BACKING_OBJECT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ProjectElementAdapterImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ElementPackage.Literals.PROJECT_ELEMENT_ADAPTER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ElementPackage.PROJECT_ELEMENT_ADAPTER__NAME, oldName,
					name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Project getProjectInternal() {
		if (projectInternal != null && projectInternal.eIsProxy()) {
			InternalEObject oldProjectInternal = (InternalEObject) projectInternal;
			projectInternal = (Project) eResolveProxy(oldProjectInternal);
			if (projectInternal != oldProjectInternal) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							ElementPackage.PROJECT_ELEMENT_ADAPTER__PROJECT_INTERNAL, oldProjectInternal,
							projectInternal));
			}
		}
		return projectInternal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Project basicGetProjectInternal() {
		return projectInternal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProjectInternal(Project newProjectInternal, NotificationChain msgs) {
		Project oldProjectInternal = projectInternal;
		projectInternal = newProjectInternal;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					ElementPackage.PROJECT_ELEMENT_ADAPTER__PROJECT_INTERNAL, oldProjectInternal, newProjectInternal);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setProjectInternal(Project newProjectInternal) {
		if (newProjectInternal != projectInternal) {
			NotificationChain msgs = null;
			if (projectInternal != null)
				msgs = ((InternalEObject) projectInternal).eInverseRemove(this,
						ProjectPackage.PROJECT__ELEMENTS_INTERNAL, Project.class, msgs);
			if (newProjectInternal != null)
				msgs = ((InternalEObject) newProjectInternal).eInverseAdd(this,
						ProjectPackage.PROJECT__ELEMENTS_INTERNAL, Project.class, msgs);
			msgs = basicSetProjectInternal(newProjectInternal, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					ElementPackage.PROJECT_ELEMENT_ADAPTER__PROJECT_INTERNAL, newProjectInternal, newProjectInternal));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public IGenericProjectElement getBackingObject() {
		return backingObject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public synchronized void setBackingObject(IGenericProjectElement newBackingObject) {
		IGenericProjectElement oldBackingObject = backingObject;
		backingObject = newBackingObject;
		newBackingObject.setProjectElementAdapter(this);
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					ElementPackage.PROJECT_ELEMENT_ADAPTER__BACKING_OBJECT, oldBackingObject, backingObject));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ElementPackage.PROJECT_ELEMENT_ADAPTER__PROJECT_INTERNAL:
			if (projectInternal != null)
				msgs = ((InternalEObject) projectInternal).eInverseRemove(this,
						ProjectPackage.PROJECT__ELEMENTS_INTERNAL, Project.class, msgs);
			return basicSetProjectInternal((Project) otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ElementPackage.PROJECT_ELEMENT_ADAPTER__PROJECT_INTERNAL:
			return basicSetProjectInternal(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ElementPackage.PROJECT_ELEMENT_ADAPTER__NAME:
			return getName();
		case ElementPackage.PROJECT_ELEMENT_ADAPTER__PROJECT_INTERNAL:
			if (resolve)
				return getProjectInternal();
			return basicGetProjectInternal();
		case ElementPackage.PROJECT_ELEMENT_ADAPTER__BACKING_OBJECT:
			return getBackingObject();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case ElementPackage.PROJECT_ELEMENT_ADAPTER__NAME:
			setName((String) newValue);
			return;
		case ElementPackage.PROJECT_ELEMENT_ADAPTER__PROJECT_INTERNAL:
			setProjectInternal((Project) newValue);
			return;
		case ElementPackage.PROJECT_ELEMENT_ADAPTER__BACKING_OBJECT:
			setBackingObject((IGenericProjectElement) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case ElementPackage.PROJECT_ELEMENT_ADAPTER__NAME:
			setName(NAME_EDEFAULT);
			return;
		case ElementPackage.PROJECT_ELEMENT_ADAPTER__PROJECT_INTERNAL:
			setProjectInternal((Project) null);
			return;
		case ElementPackage.PROJECT_ELEMENT_ADAPTER__BACKING_OBJECT:
			setBackingObject(BACKING_OBJECT_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case ElementPackage.PROJECT_ELEMENT_ADAPTER__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
		case ElementPackage.PROJECT_ELEMENT_ADAPTER__PROJECT_INTERNAL:
			return projectInternal != null;
		case ElementPackage.PROJECT_ELEMENT_ADAPTER__BACKING_OBJECT:
			return BACKING_OBJECT_EDEFAULT == null ? backingObject != null
					: !BACKING_OBJECT_EDEFAULT.equals(backingObject);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (name: "); //$NON-NLS-1$
		result.append(name);
		result.append(", backingObject: "); //$NON-NLS-1$
		result.append(backingObject);
		result.append(')');
		return result.toString();
	}

	@Override
	public String getFileExtension() {
		List<IConfigurationElement> list = ExtensionPointList.getExtensionPointList(ProjectElementAdapter.EXT_ID);
		String extensionId = getBackingObject().getExtensionId();
		for (IConfigurationElement configurationElement : list) {
			String id = configurationElement.getAttribute("id"); //$NON-NLS-1$
			if (id != null && id.equals(extensionId)) {
				return configurationElement.getAttribute("fileExtension"); //$NON-NLS-1$
			}
		}
		throw new IllegalArgumentException(extensionId + " was not a valid extension id"); //$NON-NLS-1$
	}

	@Override
	public IProject getProject() {
		return getProjectInternal();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (getBackingObject() != null && adapter.isAssignableFrom(getBackingObject().getClass())) {
			return getBackingObject();
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List getElements(Class type) {
		return getBackingObject().getElements(type);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List getElements() {
		return getBackingObject().getElements();
	}

} //ProjectElementAdapterImpl
