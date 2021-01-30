/**
 * <copyright></copyright> $Id$
 */
package org.locationtech.udig.project.internal.impl;

import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.project.internal.StyleEntry;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
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
    private Class styleClass = null; // no default style class provided

    /**
     * Indicates whether the entry is <em>selected</em>
     * @see StyleBlackboard#setSelected(String[])
     */
    private volatile boolean selected = false;

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
    @Override
    protected EClass eStaticClass() {
        return ProjectPackage.Literals.STYLE_ENTRY;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getID() {
        return iD;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setID(String newID) {
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
    @Override
    public String getMemento() {
        return memento;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setMemento(String newMemento) {
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
    @Override
    public Object getStyle() {
        return style;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @uml.property name="style"
     * @generated NOT
     */
    @Override
    public void setStyle(Object newStyle) {
        style = newStyle;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @uml.property name="styleClass"
     * @generated NOT
     */
    @Override
    public Class getStyleClass() {
        return styleClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @uml.property name="styleClass"
     * @generated NOT
     */
    @Override
    public void setStyleClass(Class newStyleClass) {
        styleClass = newStyleClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case ProjectPackage.STYLE_ENTRY__ID:
            return getID();
        case ProjectPackage.STYLE_ENTRY__MEMENTO:
            return getMemento();
        case ProjectPackage.STYLE_ENTRY__STYLE:
            return getStyle();
        case ProjectPackage.STYLE_ENTRY__STYLE_CLASS:
            return getStyleClass();
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
        case ProjectPackage.STYLE_ENTRY__ID:
            setID((String) newValue);
            return;
        case ProjectPackage.STYLE_ENTRY__MEMENTO:
            setMemento((String) newValue);
            return;
        case ProjectPackage.STYLE_ENTRY__STYLE:
            setStyle(newValue);
            return;
        case ProjectPackage.STYLE_ENTRY__STYLE_CLASS:
            setStyleClass((Class) newValue);
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
            setStyleClass((Class) null);
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
        case ProjectPackage.STYLE_ENTRY__ID:
            return ID_EDEFAULT == null ? iD != null : !ID_EDEFAULT.equals(iD);
        case ProjectPackage.STYLE_ENTRY__MEMENTO:
            return MEMENTO_EDEFAULT == null ? memento != null : !MEMENTO_EDEFAULT.equals(memento);
        case ProjectPackage.STYLE_ENTRY__STYLE:
            return STYLE_EDEFAULT == null ? getStyle() != null : !STYLE_EDEFAULT.equals(getStyle());
        case ProjectPackage.STYLE_ENTRY__STYLE_CLASS:
            return getStyleClass() != null;
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated NOT
     */
    @Override
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

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

} // StyleEntryImpl
