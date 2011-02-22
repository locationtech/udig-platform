/**
 * <copyright></copyright> $Id: RenderFactory.java 21423 2006-09-14 19:17:05Z jeichar $
 */
package net.refractions.udig.project.internal.render;

import org.eclipse.emf.ecore.EFactory;

/**
 * TODO Purpose of net.refractions.udig.project.internal.render
 * <p>
 * </p>
 *
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public interface RenderFactory extends EFactory {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    RenderFactory eINSTANCE = new net.refractions.udig.project.internal.render.impl.RenderFactoryImpl();

    /**
     * Returns a new object of class '<em>Manager</em>'. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
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
     * Returns a new object of class '<em>Viewport Model</em>'. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @return a new object of class '<em>Viewport Model</em>'.
     * @generated
     */
    ViewportModel createViewportModel();

    /**
     * Returns a new object of class '<em>Executor</em>'. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
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
     * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
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

} // RenderFactory
