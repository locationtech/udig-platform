/**
 * <copyright></copyright> $Id$
 */
package org.locationtech.udig.project.internal.render;

import org.locationtech.udig.project.internal.render.impl.TiledCompositeRendererImpl;

import org.eclipse.emf.ecore.EFactory;

/**
 * TODO Purpose of org.locationtech.udig.project.internal.render
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public interface RenderFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    RenderFactory eINSTANCE = org.locationtech.udig.project.internal.render.impl.RenderFactoryImpl
            .init();

    /**
     * Returns a new object of class '<em>Manager</em>'.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @return a new object of class '<em>Manager</em>'.
     * @generated
     */
    RenderManager createRenderManager();

    /**
     * Creates a RenderManager that is a viewer of the map, not part of the map model itself. One
     * could be used to view a map from a different point of view.
     * 
     * @return a RenderManager that is a viewer of the map.
     */
    public RenderManager createRenderManagerViewer();

    /**
     * Returns a new object of class '<em>Viewport Model</em>'.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @return a new object of class '<em>Viewport Model</em>'.
     * @generated
     */
    ViewportModel createViewportModel();

    /**
     * Returns a new object of class '<em>Executor</em>'.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @return a new object of class '<em>Executor</em>'.
     * @generated
     */
    RenderExecutor createRenderExecutor();

    /**
     * Creates a ViewportModel that is a viewer of the map, not part of the map model itself. One
     * could be used to view a map from a different point of view.
     * 
     * @return a ViewportModel that is a viewer of the map.
     */
    public ViewportModel createViewportModelViewer();

    /**
     * Returns a new object of class '<em>Executor</em>'. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @return a new object of class '<em>Executor</em>'.
     * @generated NOT
     */
    RenderExecutor createRenderExecutor( Renderer renderer );

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    RenderPackage getRenderPackage();

    /**
     * Creates a CompositeRenderer object
     * 
     * @return a CompositeRenderer object
     */
    MultiLayerRenderer createCompositeRenderer();

    /**
     * Creates a CompositeRenderer object
     * 
     * @return a CompositeRenderer object
     */
    TiledCompositeRendererImpl createTiledCompositeRenderer();

} // RenderFactory
