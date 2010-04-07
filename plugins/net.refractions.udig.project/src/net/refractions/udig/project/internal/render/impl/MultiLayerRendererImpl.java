/**
 * <copyright></copyright> $Id$
 */
package net.refractions.udig.project.internal.render.impl;

import net.refractions.udig.project.internal.render.MultiLayerRenderer;
import net.refractions.udig.project.internal.render.RenderPackage;
import net.refractions.udig.project.render.IRenderContext;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Multi Layer Renderer</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public abstract class MultiLayerRendererImpl extends RendererImpl implements MultiLayerRenderer {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected MultiLayerRendererImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected EClass eStaticClass() {
        return RenderPackage.eINSTANCE.getMultiLayerRenderer();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void refreshImage() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Object eGet( EStructuralFeature eFeature, boolean resolve ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case RenderPackage.MULTI_LAYER_RENDERER__STATE:
            return new Integer(getState());
        case RenderPackage.MULTI_LAYER_RENDERER__NAME:
            return getName();
        case RenderPackage.MULTI_LAYER_RENDERER__CONTEXT:
            return getContext();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void eSet( EStructuralFeature eFeature, Object newValue ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case RenderPackage.MULTI_LAYER_RENDERER__STATE:
            setState(((Integer) newValue).intValue());
            return;
        case RenderPackage.MULTI_LAYER_RENDERER__NAME:
            setName((String) newValue);
            return;
        case RenderPackage.MULTI_LAYER_RENDERER__CONTEXT:
            setContext((IRenderContext) newValue);
            return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void eUnset( EStructuralFeature eFeature ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case RenderPackage.MULTI_LAYER_RENDERER__STATE:
            setState(STATE_EDEFAULT);
            return;
        case RenderPackage.MULTI_LAYER_RENDERER__NAME:
            setName(NAME_EDEFAULT);
            return;
        case RenderPackage.MULTI_LAYER_RENDERER__CONTEXT:
            setContext((IRenderContext) null);
            return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public boolean eIsSet( EStructuralFeature eFeature ) {
        switch( eDerivedStructuralFeatureID(eFeature) ) {
        case RenderPackage.MULTI_LAYER_RENDERER__STATE:
            return state != STATE_EDEFAULT;
        case RenderPackage.MULTI_LAYER_RENDERER__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        case RenderPackage.MULTI_LAYER_RENDERER__CONTEXT:
            return context != null;
        }
        return eDynamicIsSet(eFeature);
    }

} // MultiLayerRendererImpl
