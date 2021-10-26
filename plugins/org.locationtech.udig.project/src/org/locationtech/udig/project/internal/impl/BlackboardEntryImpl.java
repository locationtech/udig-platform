/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.locationtech.udig.project.internal.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.locationtech.udig.project.internal.BlackboardEntry;
import org.locationtech.udig.project.internal.ProjectPackage;

/**
 * TODO Purpose of org.locationtech.udig.project.internal.impl
 * <p>
 * </p>
 *
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class BlackboardEntryImpl extends EObjectImpl implements BlackboardEntry {
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
    @Override
    protected EClass eStaticClass() {
        return ProjectPackage.Literals.BLACKBOARD_ENTRY;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getKey() {
        return key;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setKey(String newKey) {
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
                    ProjectPackage.BLACKBOARD_ENTRY__MEMENTO, oldMemento, memento));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @uml.property name="objectClass"
     * @generated NOT
     */
    @Override
    public Class getObjectClass() {
        return objectClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @uml.property name="objectClass"
     * @generated NOT
     */
    @Override
    public void setObjectClass(Class newObjectClass) {
        objectClass = newObjectClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public Object getObject() {
        return ref;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public void setObject(Object newObject) {
        ref = newObject;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case ProjectPackage.BLACKBOARD_ENTRY__KEY:
            return getKey();
        case ProjectPackage.BLACKBOARD_ENTRY__MEMENTO:
            return getMemento();
        case ProjectPackage.BLACKBOARD_ENTRY__OBJECT_CLASS:
            return getObjectClass();
        case ProjectPackage.BLACKBOARD_ENTRY__OBJECT:
            return getObject();
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
            setObject(newValue);
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
        case ProjectPackage.BLACKBOARD_ENTRY__KEY:
            setKey(KEY_EDEFAULT);
            return;
        case ProjectPackage.BLACKBOARD_ENTRY__MEMENTO:
            setMemento(MEMENTO_EDEFAULT);
            return;
        case ProjectPackage.BLACKBOARD_ENTRY__OBJECT_CLASS:
            setObjectClass((Class) null);
            return;
        case ProjectPackage.BLACKBOARD_ENTRY__OBJECT:
            setObject(OBJECT_EDEFAULT);
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
        case ProjectPackage.BLACKBOARD_ENTRY__KEY:
            return KEY_EDEFAULT == null ? key != null : !KEY_EDEFAULT.equals(key);
        case ProjectPackage.BLACKBOARD_ENTRY__MEMENTO:
            return MEMENTO_EDEFAULT == null ? memento != null : !MEMENTO_EDEFAULT.equals(memento);
        case ProjectPackage.BLACKBOARD_ENTRY__OBJECT_CLASS:
            return getObjectClass() != null;
        case ProjectPackage.BLACKBOARD_ENTRY__OBJECT:
            return OBJECT_EDEFAULT == null ? getObject() != null
                    : !OBJECT_EDEFAULT.equals(getObject());
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy())
            return super.toString();

        StringBuilder result = new StringBuilder(super.toString());
        result.append(" (key: "); //$NON-NLS-1$
        result.append(key);
        result.append(", memento: "); //$NON-NLS-1$
        result.append(memento);
        result.append(')');
        return result.toString();
    }

} // BlackboardEntryImpl
