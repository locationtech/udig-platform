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
 */
package org.locationtech.udig.project.internal.impl;

import java.util.Collection;
import java.util.List;
import org.locationtech.udig.project.ILegendItem;
import org.locationtech.udig.project.internal.Folder;
import org.locationtech.udig.project.internal.LegendItem;
import org.locationtech.udig.project.internal.ProjectPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Folder</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.locationtech.udig.project.internal.impl.FolderImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.locationtech.udig.project.internal.impl.FolderImpl#isShown <em>Shown</em>}</li>
 *   <li>{@link org.locationtech.udig.project.internal.impl.FolderImpl#getIcon <em>Icon</em>}</li>
 *   <li>{@link org.locationtech.udig.project.internal.impl.FolderImpl#getItems <em>Items</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class FolderImpl extends EObjectImpl implements Folder {
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
     * The default value of the '{@link #isShown() <em>Shown</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isShown()
     * @generated
     * @ordered
     */
    protected static final boolean SHOWN_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isShown() <em>Shown</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isShown()
     * @generated
     * @ordered
     */
    protected boolean shown = SHOWN_EDEFAULT;

    /**
     * The default value of the '{@link #getIcon() <em>Icon</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIcon()
     * @generated
     * @ordered
     */
    protected static final ImageDescriptor ICON_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getIcon() <em>Icon</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIcon()
     * @generated
     * @ordered
     */
    protected ImageDescriptor icon = ICON_EDEFAULT;

    /**
     * The cached value of the '{@link #getItems() <em>Items</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getItems()
     * @generated
     * @ordered
     */
    protected EList<ILegendItem> items;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected FolderImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ProjectPackage.Literals.FOLDER;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public List<ILegendItem> getItems() {
        if (items == null) {
            items = new EObjectContainmentEList<ILegendItem>(ILegendItem.class, this,
                    ProjectPackage.FOLDER__ITEMS);
        }
        return items;
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
     * Look up the MapImpl that is containing this legend item.
     * 
     * @return MapImpl or null if we have not been added to a map yet.
     */
    public MapImpl getMapInternal() {
        InternalEObject container = eContainer;
        while (container != null && !(container instanceof MapImpl)) {
            container = container.eInternalContainer();
        }
        return (MapImpl) container;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.FOLDER__NAME,
                    oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ImageDescriptor getIcon() {
        return icon;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setIcon(ImageDescriptor newIcon) {
        ImageDescriptor oldIcon = icon;
        icon = newIcon;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.FOLDER__ICON,
                    oldIcon, icon));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isShown() {
        return shown;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setShown(boolean newShown) {
        boolean oldShown = shown;
        shown = newShown;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.FOLDER__SHOWN,
                    oldShown, shown));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID,
            NotificationChain msgs) {
        switch (featureID) {
        case ProjectPackage.FOLDER__ITEMS:
            return ((InternalEList<?>) getItems()).basicRemove(otherEnd, msgs);
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
        case ProjectPackage.FOLDER__NAME:
            return getName();
        case ProjectPackage.FOLDER__SHOWN:
            return isShown();
        case ProjectPackage.FOLDER__ICON:
            return getIcon();
        case ProjectPackage.FOLDER__ITEMS:
            return getItems();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case ProjectPackage.FOLDER__NAME:
            setName((String) newValue);
            return;
        case ProjectPackage.FOLDER__SHOWN:
            setShown((Boolean) newValue);
            return;
        case ProjectPackage.FOLDER__ICON:
            setIcon((ImageDescriptor) newValue);
            return;
        case ProjectPackage.FOLDER__ITEMS:
            getItems().clear();
            getItems().addAll((Collection<? extends ILegendItem>) newValue);
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
        case ProjectPackage.FOLDER__NAME:
            setName(NAME_EDEFAULT);
            return;
        case ProjectPackage.FOLDER__SHOWN:
            setShown(SHOWN_EDEFAULT);
            return;
        case ProjectPackage.FOLDER__ICON:
            setIcon(ICON_EDEFAULT);
            return;
        case ProjectPackage.FOLDER__ITEMS:
            getItems().clear();
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
        case ProjectPackage.FOLDER__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        case ProjectPackage.FOLDER__SHOWN:
            return shown != SHOWN_EDEFAULT;
        case ProjectPackage.FOLDER__ICON:
            return ICON_EDEFAULT == null ? icon != null : !ICON_EDEFAULT.equals(icon);
        case ProjectPackage.FOLDER__ITEMS:
            return items != null && !items.isEmpty();
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
        if (baseClass == ILegendItem.class) {
            switch (derivedFeatureID) {
            default:
                return -1;
            }
        }
        if (baseClass == LegendItem.class) {
            switch (derivedFeatureID) {
            case ProjectPackage.FOLDER__NAME:
                return ProjectPackage.LEGEND_ITEM__NAME;
            case ProjectPackage.FOLDER__SHOWN:
                return ProjectPackage.LEGEND_ITEM__SHOWN;
            case ProjectPackage.FOLDER__ICON:
                return ProjectPackage.LEGEND_ITEM__ICON;
            default:
                return -1;
            }
        }
        return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
        if (baseClass == ILegendItem.class) {
            switch (baseFeatureID) {
            default:
                return -1;
            }
        }
        if (baseClass == LegendItem.class) {
            switch (baseFeatureID) {
            case ProjectPackage.LEGEND_ITEM__NAME:
                return ProjectPackage.FOLDER__NAME;
            case ProjectPackage.LEGEND_ITEM__SHOWN:
                return ProjectPackage.FOLDER__SHOWN;
            case ProjectPackage.LEGEND_ITEM__ICON:
                return ProjectPackage.FOLDER__ICON;
            default:
                return -1;
            }
        }
        return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
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

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (name: "); //$NON-NLS-1$
        result.append(name);
        result.append(", shown: "); //$NON-NLS-1$
        result.append(shown);
        result.append(", icon: "); //$NON-NLS-1$
        result.append(icon);
        result.append(')');
        return result.toString();
    }

} //FolderImpl
