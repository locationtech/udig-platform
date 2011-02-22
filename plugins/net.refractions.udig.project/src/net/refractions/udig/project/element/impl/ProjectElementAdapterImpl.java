/**
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * $Id$
 */
package net.refractions.udig.project.element.impl;

import java.util.List;

import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.project.IProject;
import net.refractions.udig.project.element.ElementPackage;
import net.refractions.udig.project.element.IGenericProjectElement;
import net.refractions.udig.project.element.ProjectElementAdapter;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectPackage;

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
 * <ul>
 *   <li>{@link net.refractions.udig.project.element.impl.ProjectElementAdapterImpl#getName <em>Name</em>}</li>
 *   <li>{@link net.refractions.udig.project.element.impl.ProjectElementAdapterImpl#getProjectInternal <em>Project Internal</em>}</li>
 *   <li>{@link net.refractions.udig.project.element.impl.ProjectElementAdapterImpl#getBackingObject <em>Backing Object</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ProjectElementAdapterImpl extends EObjectImpl implements ProjectElementAdapter {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

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
    protected EClass eStaticClass() {
        return ElementPackage.Literals.PROJECT_ELEMENT_ADAPTER;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setName( String newName ) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ElementPackage.PROJECT_ELEMENT_ADAPTER__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Project getProjectInternal() {
        if (projectInternal != null && projectInternal.eIsProxy()) {
            InternalEObject oldProjectInternal = (InternalEObject) projectInternal;
            projectInternal = (Project) eResolveProxy(oldProjectInternal);
            if (projectInternal != oldProjectInternal) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE,
                            ElementPackage.PROJECT_ELEMENT_ADAPTER__PROJECT_INTERNAL,
                            oldProjectInternal, projectInternal));
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
    public NotificationChain basicSetProjectInternal( Project newProjectInternal,
            NotificationChain msgs ) {
        Project oldProjectInternal = projectInternal;
        projectInternal = newProjectInternal;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    ElementPackage.PROJECT_ELEMENT_ADAPTER__PROJECT_INTERNAL, oldProjectInternal,
                    newProjectInternal);
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
    public void setProjectInternal( Project newProjectInternal ) {
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
                    ElementPackage.PROJECT_ELEMENT_ADAPTER__PROJECT_INTERNAL, newProjectInternal,
                    newProjectInternal));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public IGenericProjectElement getBackingObject() {
        return backingObject;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public synchronized void setBackingObject( IGenericProjectElement newBackingObject ) {
        IGenericProjectElement oldBackingObject = backingObject;
        backingObject = newBackingObject;
        newBackingObject.setProjectElementAdapter(this);
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ElementPackage.PROJECT_ELEMENT_ADAPTER__BACKING_OBJECT, oldBackingObject,
                    backingObject));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eInverseAdd( InternalEObject otherEnd, int featureID,
            NotificationChain msgs ) {
        switch( featureID ) {
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
    public NotificationChain eInverseRemove( InternalEObject otherEnd, int featureID,
            NotificationChain msgs ) {
        switch( featureID ) {
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
    public Object eGet( int featureID, boolean resolve, boolean coreType ) {
        switch( featureID ) {
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
    public void eSet( int featureID, Object newValue ) {
        switch( featureID ) {
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
    public void eUnset( int featureID ) {
        switch( featureID ) {
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
    public boolean eIsSet( int featureID ) {
        switch( featureID ) {
        case ElementPackage.PROJECT_ELEMENT_ADAPTER__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        case ElementPackage.PROJECT_ELEMENT_ADAPTER__PROJECT_INTERNAL:
            return projectInternal != null;
        case ElementPackage.PROJECT_ELEMENT_ADAPTER__BACKING_OBJECT:
            return BACKING_OBJECT_EDEFAULT == null
                    ? backingObject != null
                    : !BACKING_OBJECT_EDEFAULT.equals(backingObject);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String toString() {
        if (eIsProxy())
            return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (name: "); //$NON-NLS-1$
        result.append(name);
        result.append(", backingObject: "); //$NON-NLS-1$
        result.append(backingObject);
        result.append(')');
        return result.toString();
    }

    public String getFileExtension() {
        List<IConfigurationElement> list = ExtensionPointList
                .getExtensionPointList(ProjectElementAdapter.EXT_ID);
        String extensionId = getBackingObject().getExtensionId();
        for( IConfigurationElement configurationElement : list ) {
            String id = configurationElement.getAttribute("id"); //$NON-NLS-1$
            if (id != null && id.equals(extensionId )) {
                return configurationElement.getAttribute("fileExtension"); //$NON-NLS-1$
            }
        }
        throw new IllegalArgumentException(extensionId + " was not a valid extension id"); //$NON-NLS-1$
    }

    public IProject getProject() {
        return getProjectInternal();
    }

    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adapter ) {
        if (getBackingObject() != null && adapter.isAssignableFrom(getBackingObject().getClass())) {
            return getBackingObject();
        }
        return null;
    }

} //ProjectElementAdapterImpl
