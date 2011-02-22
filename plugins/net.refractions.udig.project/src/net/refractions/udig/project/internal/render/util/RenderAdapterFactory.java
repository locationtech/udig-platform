/**
 * <copyright></copyright> $Id: RenderAdapterFactory.java 21450 2006-09-15 21:43:17Z jeichar $
 */
package net.refractions.udig.project.internal.render.util;

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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;


/**
 * TODO Purpose of net.refractions.udig.project.internal.render.util
 * <p>
 * </p>
 *
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class RenderAdapterFactory extends AdapterFactoryImpl {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public static final String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * The cached model package. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    static RenderPackage modelPackage;

    /**
     * Creates an instance of the adapter factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public RenderAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = RenderPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object. <!-- begin-user-doc
     * --> This implementation returns <code>true</code> if the object is either the model's
     * package or is an instance object of the model. <!-- end-user-doc -->
     *
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    public boolean isFactoryForType( Object object ) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject) object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch the delegates to the <code>createXXX</code> methods. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected RenderSwitch modelSwitch = new RenderSwitch(){
        public Object caseIRenderManager( IRenderManager object ) {
            return createIRenderManagerAdapter();
        }
        public Object caseIViewportModel( IViewportModel object ) {
            return createIViewportModelAdapter();
        }
        public Object caseICompositeRenderContext( ICompositeRenderContext object ) {
            return createICompositeRenderContextAdapter();
        }
        public Object caseIMultiLayerRenderer( IMultiLayerRenderer object ) {
            return createIMultiLayerRendererAdapter();
        }
        public Object caseIRenderContext( IRenderContext object ) {
            return createIRenderContextAdapter();
        }
        public Object caseIRenderer( IRenderer object ) {
            return createIRendererAdapter();
        }
        public Object caseILayer( ILayer object ) {
            return createILayerAdapter();
        }
        public Object caseCompositeRenderContext( CompositeRenderContext object ) {
            return createCompositeRenderContextAdapter();
        }
        public Object caseMultiLayerRenderer( MultiLayerRenderer object ) {
            return createMultiLayerRendererAdapter();
        }
        public Object caseRenderContext( RenderContext object ) {
            return createRenderContextAdapter();
        }
        public Object caseRenderExecutor( RenderExecutor object ) {
            return createRenderExecutorAdapter();
        }
        public Object caseRenderManager( RenderManager object ) {
            return createRenderManagerAdapter();
        }
        public Object caseViewportModel( ViewportModel object ) {
            return createViewportModelAdapter();
        }
        public Object caseComparable( Comparable object ) {
            return createComparableAdapter();
        }
        public Object caseRenderer( Renderer object ) {
            return createRendererAdapter();
        }
        public Object caseRendererCreator( RendererCreator object ) {
            return createRendererCreatorAdapter();
        }
        public Object caseIMapDisplayListener( IMapDisplayListener object ) {
            return createIMapDisplayListenerAdapter();
        }
        public Object caseCloneable( Cloneable object ) {
            return createCloneableAdapter();
        }
        public Object caseIAbstractContext( IAbstractContext object ) {
            return createIAbstractContextAdapter();
        }
        public Object caseAbstractContext( AbstractContext object ) {
            return createAbstractContextAdapter();
        }
        public Object defaultCase( EObject object ) {
            return createEObjectAdapter();
        }
    };

    /**
     * Creates an adapter for the <code>target</code>. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     *
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    public Adapter createAdapter( Notifier target ) {
        return (Adapter) modelSwitch.doSwitch((EObject) target);
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.render.IRenderManager <em>IRender Manager</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see net.refractions.udig.project.render.IRenderManager
     * @generated
     */
    public Adapter createIRenderManagerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.render.IViewportModel <em>IViewport Model</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see net.refractions.udig.project.render.IViewportModel
     * @generated
     */
    public Adapter createIViewportModelAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.render.ICompositeRenderContext <em>IComposite Render Context</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see net.refractions.udig.project.render.ICompositeRenderContext
     * @generated
     */
    public Adapter createICompositeRenderContextAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.render.IMultiLayerRenderer <em>IMulti Layer Renderer</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see net.refractions.udig.project.render.IMultiLayerRenderer
     * @generated
     */
    public Adapter createIMultiLayerRendererAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.render.IRenderContext <em>IRender Context</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see net.refractions.udig.project.render.IRenderContext
     * @generated
     */
    public Adapter createIRenderContextAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.render.IRenderer <em>IRenderer</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see net.refractions.udig.project.render.IRenderer
     * @generated
     */
    public Adapter createIRendererAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.ILayer <em>ILayer</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see net.refractions.udig.project.ILayer
     * @generated
     */
    public Adapter createILayerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link net.refractions.udig.project.render.RenderManager <em>Manager</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.render.RenderManager
     * @generated
     */
    public Adapter createRenderManagerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.internal.render.Renderer <em>Renderer</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.render.Renderer
     * @generated
     */
    public Adapter createRendererAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link net.refractions.udig.project.render.RendererCreator <em>er Creator</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.render.RendererCreator
     * @generated
     */
    public Adapter createRendererCreatorAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link net.refractions.udig.project.render.ViewportModel <em>Viewport Model</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.render.ViewportModel
     * @generated
     */
    public Adapter createViewportModelAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link net.refractions.udig.project.render.RenderExecutor <em>Executor</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.render.RenderExecutor
     * @generated
     */
    public Adapter createRenderExecutorAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link net.refractions.udig.project.render.RenderContext <em>Context</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.render.RenderContext
     * @generated
     */
    public Adapter createRenderContextAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.internal.render.CompositeRenderContext <em>Composite Render Context</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.render.CompositeRenderContext
     * @generated
     */
    public Adapter createCompositeRenderContextAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link Comparable <em>Comparable</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see Comparable
     * @generated
     */
    public Adapter createComparableAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.internal.render.MultiLayerRenderer <em>Multi Layer Renderer</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.render.MultiLayerRenderer
     * @generated
     */
    public Adapter createMultiLayerRendererAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link Cloneable <em>Cloneable</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see Cloneable
     * @generated
     */
    public Adapter createCloneableAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.IAbstractContext <em>IAbstract Context</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see net.refractions.udig.project.IAbstractContext
     * @generated
     */
    public Adapter createIAbstractContextAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link net.refractions.udig.project.AbstractContext <em>Abstract Context</em>}'. <!--
     * begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.AbstractContext
     * @generated
     */
    public Adapter createAbstractContextAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.render.displayAdapter.IMapDisplayListener <em>IMap Display Listener</em>}'.
     * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore
     * cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
     * end-user-doc -->
     *
     * @return the new adapter.
     * @see net.refractions.udig.project.render.displayAdapter.IMapDisplayListener
     * @generated
     */
    public Adapter createIMapDisplayListenerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case. <!-- begin-user-doc --> This default
     * implementation returns null. <!-- end-user-doc -->
     *
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }

} // RenderAdapterFactory
