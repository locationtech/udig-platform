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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.LayerLegendItem;
import org.locationtech.udig.project.internal.ProjectPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Layer Legend Item</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.locationtech.udig.project.internal.impl.LayerLegendItemImpl#getLayer <em>Layer</em>}</li>
 * </ul>
 *
 * @generated
 */
public class LayerLegendItemImpl extends LegendItemImpl implements LayerLegendItem {
    /**
     * The cached value of the '{@link #getLayer() <em>Layer</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLayer()
     * @generated
     * @ordered
     */
    protected Layer layer;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected LayerLegendItemImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ProjectPackage.Literals.LAYER_LEGEND_ITEM;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Layer getLayer() {
        if (layer != null && layer.eIsProxy()) {
            InternalEObject oldLayer = (InternalEObject) layer;
            layer = (Layer) eResolveProxy(oldLayer);
            if (layer != oldLayer) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE,
                            ProjectPackage.LAYER_LEGEND_ITEM__LAYER, oldLayer, layer));
            }
        }
        return layer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Layer basicGetLayer() {
        return layer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setLayer(Layer newLayer) {
        Layer oldLayer = layer;
        layer = newLayer;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ProjectPackage.LAYER_LEGEND_ITEM__LAYER, oldLayer, layer));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case ProjectPackage.LAYER_LEGEND_ITEM__LAYER:
            if (resolve)
                return getLayer();
            return basicGetLayer();
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
        case ProjectPackage.LAYER_LEGEND_ITEM__LAYER:
            setLayer((Layer) newValue);
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
        case ProjectPackage.LAYER_LEGEND_ITEM__LAYER:
            setLayer((Layer) null);
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
        case ProjectPackage.LAYER_LEGEND_ITEM__LAYER:
            return layer != null;
        }
        return super.eIsSet(featureID);
    }

} //LayerLegendItemImpl
