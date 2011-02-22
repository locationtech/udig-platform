/**
 * <copyright>
 * </copyright>
 *
 * $Id: BlackboardEntryImpl.java 16525 2005-10-27 01:38:05Z jeichar $
 */
package net.refractions.udig.project.internal.impl;

import net.refractions.udig.project.internal.BlackboardEntry;
import net.refractions.udig.project.internal.ProjectPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * TODO Purpose of net.refractions.udig.project.internal.impl
 * <p>
 * </p>
 *
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class BlackboardEntryImpl extends EObjectImpl implements BlackboardEntry {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getKey() <em>Key</em>}' attribute.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @see #getKey()
     * @generated
     * @ordered
     */
    protected static final String KEY_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getKey() <em>Key</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getKey()
     * @generated
     * @ordered
     */
    protected String key = KEY_EDEFAULT;

    /**
     * The default value of the '{@link #getMemento() <em>Memento</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getMemento()
     * @generated
     * @ordered
     */
    protected static final String MEMENTO_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getMemento() <em>Memento</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getMemento()
     * @generated
     * @ordered
     */
    protected String memento = MEMENTO_EDEFAULT;

    /**
     * The default value of the '{@link #getObjectClass() <em>Object Class</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getObjectClass()
     * @generated
     * @ordered
     */
    protected static final Class OBJECT_CLASS_EDEFAULT = null;

    /**
     * The default value of the '{@link #getObject() <em>Object</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getObject()
     * @generated
     * @ordered
     */
    protected static final Object OBJECT_EDEFAULT = null;

    /** reference to object * */
    private Object ref;

    /** class cache, disapears when app shuts down * */
    private Class objectClass;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected BlackboardEntryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return ProjectPackage.eINSTANCE.getBlackboardEntry();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String getKey() {
        return key;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setKey( String newKey ) {
        String oldKey = key;
        key = newKey;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.BLACKBOARD_ENTRY__KEY, oldKey, key));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String getMemento() {
        return memento;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setMemento( String newMemento ) {
        String oldMemento = memento;
        memento = newMemento;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.BLACKBOARD_ENTRY__MEMENTO, oldMemento, memento));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @uml.property name="objectClass"
     * @generated NOT
     */
    public Class getObjectClass() {
        return objectClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @uml.property name="objectClass"
     * @generated NOT
     */
    public void setObjectClass( Class newObjectClass ) {
        objectClass = newObjectClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public Object getObject() {
        return ref;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    public void setObject( Object newObject ) {
        ref = newObject;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Object eGet( EStructuralFeature eFeature, boolean resolve ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.BLACKBOARD_ENTRY__KEY:
            return getKey();
        case ProjectPackage.BLACKBOARD_ENTRY__MEMENTO:
            return getMemento();
        case ProjectPackage.BLACKBOARD_ENTRY__OBJECT_CLASS:
            return getObjectClass();
        case ProjectPackage.BLACKBOARD_ENTRY__OBJECT:
            return getObject();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void eSet( EStructuralFeature eFeature, Object newValue ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.BLACKBOARD_ENTRY__KEY:
            setKey((String) newValue);
            return;
        case ProjectPackage.BLACKBOARD_ENTRY__MEMENTO:
            setMemento((String) newValue);
            return;
        case ProjectPackage.BLACKBOARD_ENTRY__OBJECT_CLASS:
            setObjectClass((Class) newValue);
            return;
        case ProjectPackage.BLACKBOARD_ENTRY__OBJECT:
            setObject((Object) newValue);
            return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void eUnset( EStructuralFeature eFeature ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.BLACKBOARD_ENTRY__KEY:
            setKey(KEY_EDEFAULT);
            return;
        case ProjectPackage.BLACKBOARD_ENTRY__MEMENTO:
            setMemento(MEMENTO_EDEFAULT);
            return;
        case ProjectPackage.BLACKBOARD_ENTRY__OBJECT_CLASS:
            setObjectClass(OBJECT_CLASS_EDEFAULT);
            return;
        case ProjectPackage.BLACKBOARD_ENTRY__OBJECT:
            setObject(OBJECT_EDEFAULT);
            return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public boolean eIsSet( EStructuralFeature eFeature ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.BLACKBOARD_ENTRY__KEY:
            return KEY_EDEFAULT == null ? key != null : !KEY_EDEFAULT.equals(key);
        case ProjectPackage.BLACKBOARD_ENTRY__MEMENTO:
            return MEMENTO_EDEFAULT == null ? memento != null : !MEMENTO_EDEFAULT.equals(memento);
        case ProjectPackage.BLACKBOARD_ENTRY__OBJECT_CLASS:
            return OBJECT_CLASS_EDEFAULT == null
                    ? getObjectClass() != null
                    : !OBJECT_CLASS_EDEFAULT.equals(getObjectClass());
        case ProjectPackage.BLACKBOARD_ENTRY__OBJECT:
            return OBJECT_EDEFAULT == null ? getObject() != null : !OBJECT_EDEFAULT
                    .equals(getObject());
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String toString() {
        if (eIsProxy())
            return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (key: "); //$NON-NLS-1$
        result.append(key);
        result.append(", memento: "); //$NON-NLS-1$
        result.append(memento);
        result.append(')');
        return result.toString();
    }

} // BlackboardEntryImpl
