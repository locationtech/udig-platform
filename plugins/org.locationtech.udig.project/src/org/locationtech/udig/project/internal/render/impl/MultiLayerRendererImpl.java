/**
 * <copyright></copyright> $Id$
 */
package org.locationtech.udig.project.internal.render.impl;

import org.locationtech.udig.project.internal.render.MultiLayerRenderer;
import org.locationtech.udig.project.internal.render.RenderPackage;
import org.locationtech.udig.project.render.RenderException;
import org.locationtech.udig.project.render.IRenderContext;

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
     * @generated
     */
    protected MultiLayerRendererImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RenderPackage.Literals.MULTI_LAYER_RENDERER;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void refreshImage() throws RenderException {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

} // MultiLayerRendererImpl
