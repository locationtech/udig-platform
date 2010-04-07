/**
 * <copyright></copyright> $Id$
 */
package net.refractions.udig.project.internal.impl;

import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.project.internal.StyleEntry;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * Default implementation 
 * 
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class StyleEntryImpl extends EObjectImpl implements StyleEntry {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getID() <em>ID</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getID()
     * @generated
     * @ordered
     */
    protected static final String ID_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getID() <em>ID</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getID()
     * @generated
     * @ordered
     */
    protected String iD = ID_EDEFAULT;

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
     * The default value of the '{@link #getStyle() <em>Style</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getStyle()
     * @generated
     * @ordered
     */
    protected static final Object STYLE_EDEFAULT = null;

    /**
     * The default value of the '{@link #getStyleClass() <em>Style Class</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getStyleClass()
     * @generated
     * @ordered
     */
    protected static final Class STYLE_CLASS_EDEFAULT = null;

    /**
     * The cached style.
     * 
     * @generated NOT
     */
    protected Object style = STYLE_EDEFAULT;

    /**
     * The cached style class.
     * 
     * @generated NOT
     */
    private Class styleClass = STYLE_CLASS_EDEFAULT;

    /**
     * Indicates whether the entry is <em>selected</em>
     * @see StyleBlackboard#setSelected(String[])
     */
    private volatile boolean selected=false;
    
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected StyleEntryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return ProjectPackage.eINSTANCE.getStyleEntry();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String getID() {
        return iD;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setID( String newID ) {
        String oldID = iD;
        iD = newID;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.STYLE_ENTRY__ID,
                    oldID, iD));
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
                    ProjectPackage.STYLE_ENTRY__MEMENTO, oldMemento, memento));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @uml.property name="style"
     * @generated NOT
     */
    public Object getStyle() {
        return style;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @uml.property name="style"
     * @generated NOT
     */
    public void setStyle( Object newStyle ) {
        style = newStyle;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @uml.property name="styleClass"
     * @generated NOT
     */
    public Class getStyleClass() {
        return styleClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @uml.property name="styleClass"
     * @generated NOT
     */
    public void setStyleClass( Class newStyleClass ) {
        styleClass = newStyleClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Object eGet( EStructuralFeature eFeature, boolean resolve ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.STYLE_ENTRY__ID:
            return getID();
        case ProjectPackage.STYLE_ENTRY__MEMENTO:
            return getMemento();
        case ProjectPackage.STYLE_ENTRY__STYLE:
            return getStyle();
        case ProjectPackage.STYLE_ENTRY__STYLE_CLASS:
            return getStyleClass();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void eSet( EStructuralFeature eFeature, Object newValue ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case ProjectPackage.STYLE_ENTRY__ID:
            setID((String) newValue);
            return;
        case ProjectPackage.STYLE_ENTRY__MEMENTO:
            setMemento((String) newValue);
            return;
        case ProjectPackage.STYLE_ENTRY__STYLE:
            setStyle((Object) newValue);
            return;
        case ProjectPackage.STYLE_ENTRY__STYLE_CLASS:
            setStyleClass((Class) newValue);
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
        case ProjectPackage.STYLE_ENTRY__ID:
            setID(ID_EDEFAULT);
            return;
        case ProjectPackage.STYLE_ENTRY__MEMENTO:
            setMemento(MEMENTO_EDEFAULT);
            return;
        case ProjectPackage.STYLE_ENTRY__STYLE:
            setStyle(STYLE_EDEFAULT);
            return;
        case ProjectPackage.STYLE_ENTRY__STYLE_CLASS:
            setStyleClass(STYLE_CLASS_EDEFAULT);
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
        case ProjectPackage.STYLE_ENTRY__ID:
            return ID_EDEFAULT == null ? iD != null : !ID_EDEFAULT.equals(iD);
        case ProjectPackage.STYLE_ENTRY__MEMENTO:
            return MEMENTO_EDEFAULT == null ? memento != null : !MEMENTO_EDEFAULT.equals(memento);
        case ProjectPackage.STYLE_ENTRY__STYLE:
            return STYLE_EDEFAULT == null ? getStyle() != null : !STYLE_EDEFAULT.equals(getStyle());
        case ProjectPackage.STYLE_ENTRY__STYLE_CLASS:
            return STYLE_CLASS_EDEFAULT == null ? getStyleClass() != null : !STYLE_CLASS_EDEFAULT
                    .equals(getStyleClass());
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated NOT
     */
    public String toString() {
        if (eIsProxy())
            return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (iD: "); //$NON-NLS-1$
        result.append(iD);
        result.append(", memento: "); //$NON-NLS-1$
        result.append(memento);
        result.append(", selected: "); //$NON-NLS-1$
        result.append(selected);
        result.append(')');
        return result.toString();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected( boolean selected ) {
        this.selected = selected;
    }

} // StyleEntryImpl
