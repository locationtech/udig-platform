/**
 * <copyright></copyright> $Id$
 */
package org.locationtech.udig.project.internal.render.util;

import java.util.List;

import org.locationtech.udig.project.IAbstractContext;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.AbstractContext;
import org.locationtech.udig.project.internal.render.CompositeRenderContext;
import org.locationtech.udig.project.internal.render.MultiLayerRenderer;
import org.locationtech.udig.project.internal.render.RenderContext;
import org.locationtech.udig.project.internal.render.RenderExecutor;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.RenderPackage;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.internal.render.RendererCreator;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.render.ICompositeRenderContext;
import org.locationtech.udig.project.render.IMultiLayerRenderer;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderManager;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.render.IViewportModel;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplayListener;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc --> The <b>Switch </b> for the model's inheritance hierarchy. It supports the
 * call {@link #doSwitch(EObject) doSwitch(object)}to invoke the <code>caseXXX</code> method for
 * each class of the model, starting with the actual class of the object and proceeding up the
 * inheritance hierarchy until a non-null result is returned, which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.locationtech.udig.project.internal.render.RenderPackage
 * @generated
 */
public class RenderSwitch<T> extends Switch<T> {
    /**
     * The cached model package <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    static RenderPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public RenderSwitch() {
        if (modelPackage == null) {
            modelPackage = RenderPackage.eINSTANCE;
        }
    }

    /**
     * Checks whether this is a switch for the given package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @parameter ePackage the package in question.
     * @return whether this is a switch for the given package.
     * @generated
     */
    @Override
    protected boolean isSwitchFor( EPackage ePackage ) {
        return ePackage == modelPackage;
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    @Override
    protected T doSwitch( int classifierID, EObject theEObject ) {
        switch( classifierID ) {
        case RenderPackage.MULTI_LAYER_RENDERER: {
            MultiLayerRenderer multiLayerRenderer = (MultiLayerRenderer) theEObject;
            T result = caseMultiLayerRenderer(multiLayerRenderer);
            if (result == null) result = caseRenderer(multiLayerRenderer);
            if (result == null) result = caseIMultiLayerRenderer(multiLayerRenderer);
            if (result == null) result = caseIRenderer(multiLayerRenderer);
            if (result == null) result = defaultCase(theEObject);
            return result;
        }
        case RenderPackage.RENDER_EXECUTOR: {
            RenderExecutor renderExecutor = (RenderExecutor) theEObject;
            T result = caseRenderExecutor(renderExecutor);
            if (result == null) result = caseRenderer(renderExecutor);
            if (result == null) result = caseIRenderer(renderExecutor);
            if (result == null) result = defaultCase(theEObject);
            return result;
        }
        case RenderPackage.RENDER_MANAGER: {
            RenderManager renderManager = (RenderManager) theEObject;
            T result = caseRenderManager(renderManager);
            if (result == null) result = caseIRenderManager(renderManager);
            if (result == null) result = defaultCase(theEObject);
            return result;
        }
        case RenderPackage.VIEWPORT_MODEL: {
            ViewportModel viewportModel = (ViewportModel) theEObject;
            T result = caseViewportModel(viewportModel);
            if (result == null) result = caseIMapDisplayListener(viewportModel);
            if (result == null) result = caseIViewportModel(viewportModel);
            if (result == null) result = defaultCase(theEObject);
            return result;
        }
        case RenderPackage.RENDERER: {
            Renderer renderer = (Renderer) theEObject;
            T result = caseRenderer(renderer);
            if (result == null) result = caseIRenderer(renderer);
            if (result == null) result = defaultCase(theEObject);
            return result;
        }
        default:
            return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>IRender Manager</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IRender Manager</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIRenderManager( IRenderManager object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>IViewport Model</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IViewport Model</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIViewportModel( IViewportModel object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>IMulti Layer Renderer</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IMulti Layer Renderer</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIMultiLayerRenderer( IMultiLayerRenderer object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>IRender Context</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IRender Context</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIRenderContext( IRenderContext object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>IRenderer</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IRenderer</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIRenderer( IRenderer object ) {
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
    public T caseILayer( ILayer object ) {
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
    public T caseRenderManager( RenderManager object ) {
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
    public T caseRenderer( Renderer object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Viewport Model</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Viewport Model</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseViewportModel( ViewportModel object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Executor</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Executor</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseRenderExecutor( RenderExecutor object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Comparable</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Comparable</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseComparable( Comparable object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Multi Layer Renderer</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Multi Layer Renderer</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseMultiLayerRenderer( MultiLayerRenderer object ) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>IMap Display Listener</em>'.
     * <!-- begin-user-doc --> This implementation returns null; returning a non-null result will
     * terminate the switch. <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>IMap Display Listener</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseIMapDisplayListener( IMapDisplayListener object ) {
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
    @Override
    public T defaultCase( EObject object ) {
        return null;
    }

} // RenderSwitch
