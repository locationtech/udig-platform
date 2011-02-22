/**
 * <copyright></copyright> $Id: RenderSwitch.java 21450 2006-09-15 21:43:17Z jeichar $
 */
package net.refractions.udig.project.internal.render.util;

import java.util.List;

import net.refractions.udig.project.IAbstractContext;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.AbstractContext;
import net.refractions.udig.project.internal.render.CompositeRenderContext;
import net.refractions.udig.project.internal.render.MultiLayerRenderer;
import net.refractions.udig.project.internal.render.RenderContext;
import net.refractions.udig.project.internal.render.RenderExecutor;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.internal.render.RenderPackage;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.internal.render.RendererCreator;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.render.ICompositeRenderContext;
import net.refractions.udig.project.render.IMultiLayerRenderer;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderManager;
import net.refractions.udig.project.render.IRenderer;
import net.refractions.udig.project.render.IViewportModel;
import net.refractions.udig.project.render.displayAdapter.IMapDisplayListener;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> The <b>Switch </b> for the model's inheritance hierarchy. It supports the
 * call {@link #doSwitch(EObject) doSwitch(object)}to invoke the <code>caseXXX</code> method for
 * each class of the model, starting with the actual class of the object and proceeding up the
 * inheritance hierarchy until a non-null result is returned, which is the result of the switch.
 * <!-- end-user-doc -->
 *
 * @see net.refractions.udig.project.internal.render.RenderPackage
 * @generated
 */
public class RenderSwitch {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * The cached model package <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    static RenderPackage modelPackage;

    /**
     * Creates an instance of the switch. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public RenderSwitch() {
        if (modelPackage == null) {
            modelPackage = RenderPackage.eINSTANCE;
        }
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result;
     * it yields that result. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    public Object doSwitch( EObject theEObject ) {
        return doSwitch(theEObject.eClass(), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result;
     * it yields that result. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch( EClass theEClass, EObject theEObject ) {
        if (theEClass.eContainer() == modelPackage) {
            return doSwitch(theEClass.getClassifierID(), theEObject);
        } else {
            List eSuperTypes = theEClass.getESuperTypes();
            return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch((EClass) eSuperTypes
                    .get(0), theEObject);
        }
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result;
     * it yields that result. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch( int classifierID, EObject theEObject ) {
        switch( classifierID ) {
        case RenderPackage.MULTI_LAYER_RENDERER: {
            MultiLayerRenderer multiLayerRenderer = (MultiLayerRenderer) theEObject;
            Object result = caseMultiLayerRenderer(multiLayerRenderer);
            if (result == null)
                result = caseRenderer(multiLayerRenderer);
            if (result == null)
                result = caseIMultiLayerRenderer(multiLayerRenderer);
            if (result == null)
                result = caseIRenderer(multiLayerRenderer);
            if (result == null)
                result = defaultCase(theEObject);
            return result;
        }
        case RenderPackage.RENDER_EXECUTOR: {
            RenderExecutor renderExecutor = (RenderExecutor) theEObject;
            Object result = caseRenderExecutor(renderExecutor);
            if (result == null)
                result = caseRenderer(renderExecutor);
            if (result == null)
                result = caseIRenderer(renderExecutor);
            if (result == null)
                result = defaultCase(theEObject);
            return result;
        }
        case RenderPackage.RENDER_MANAGER: {
            RenderManager renderManager = (RenderManager) theEObject;
            Object result = caseRenderManager(renderManager);
            if (result == null)
                result = caseIRenderManager(renderManager);
            if (result == null)
                result = defaultCase(theEObject);
            return result;
        }
        case RenderPackage.VIEWPORT_MODEL: {
            ViewportModel viewportModel = (ViewportModel) theEObject;
            Object result = caseViewportModel(viewportModel);
            if (result == null)
                result = caseIMapDisplayListener(viewportModel);
            if (result == null)
                result = caseIViewportModel(viewportModel);
            if (result == null)
                result = defaultCase(theEObject);
            return result;
        }
        case RenderPackage.RENDERER: {
            Renderer renderer = (Renderer) theEObject;
            Object result = caseRenderer(renderer);
            if (result == null)
                result = caseIRenderer(renderer);
            if (result == null)
                result = defaultCase(theEObject);
            return result;
        }
        default:
            return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>IRender Manager</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>IRender Manager</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseIRenderManager( IRenderManager object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>IViewport Model</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>IViewport Model</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseIViewportModel( IViewportModel object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>IComposite Render Context</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>IComposite Render Context</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseICompositeRenderContext( ICompositeRenderContext object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>IMulti Layer Renderer</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>IMulti Layer Renderer</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseIMultiLayerRenderer( IMultiLayerRenderer object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>IRender Context</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>IRender Context</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseIRenderContext( IRenderContext object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>IRenderer</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>IRenderer</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseIRenderer( IRenderer object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>ILayer</em>'. <!--
     * begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>ILayer</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseILayer( ILayer object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Manager</em>'. <!--
     * begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Manager</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseRenderManager( RenderManager object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>er</em>'. <!--
     * begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>er</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseRenderer( Renderer object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Renderer Creator</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Renderer Creator</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseRendererCreator( RendererCreator object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Viewport Model</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Viewport Model</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseViewportModel( ViewportModel object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Executor</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Executor</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseRenderExecutor( RenderExecutor object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Context</em>'. <!--
     * begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Context</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseRenderContext( RenderContext object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Composite Render Context</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Composite Render Context</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseCompositeRenderContext( CompositeRenderContext object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Comparable</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Comparable</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseComparable( Comparable object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Multi Layer Renderer</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Multi Layer Renderer</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseMultiLayerRenderer( MultiLayerRenderer object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Cloneable</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Cloneable</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseCloneable( Cloneable object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>IAbstract Context</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>IAbstract Context</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseIAbstractContext( IAbstractContext object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Abstract Context</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Abstract Context</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseAbstractContext( AbstractContext object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>IMap Display Listener</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>IMap Display Listener</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseIMapDisplayListener( IMapDisplayListener object ) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EObject</em>'. <!--
     * begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch, but this is the last case anyway. <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    public Object defaultCase( EObject object ) {
        return null;
    }

} // RenderSwitch
