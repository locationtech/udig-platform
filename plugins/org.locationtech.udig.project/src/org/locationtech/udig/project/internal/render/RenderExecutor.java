/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render;

import org.locationtech.udig.project.render.RenderException;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Each renderer has an executor that runs the renderer in a separate thread. 
 * There are currently 3 implementations. 
 * One for each type of renderer. (Composite/MultiLayer/Renderer). 
 * 
 * The CompositeRendererExecutor provides the incremental update functionality.
 * 
 * 
 * @author Jesse
 * @since 1.0.0
 * @model
 */
public interface RenderExecutor extends Renderer {
    /** The Extension point id declaring available RenderExecutors */
    String EXTENSION_ID = "org.locationtech.udig.project.renderExecutor"; //$NON-NLS-1$

    /** The name of the RenderExecutor class attribute in the Extension point */
    String EXECUTOR_ATTR = "executorClass"; //$NON-NLS-1$

    /** The name of the Renderer class attribute in the Extension point */
    String RENDERER_ATTR = "rendererClass"; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Renderer</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Renderer</em>' reference isn't clear, there really should be
     * more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Renderer</em>' reference.
     * @see #setRenderer(Renderer)
     * @see org.locationtech.udig.project.internal.render.RenderPackage#getRenderExecutor_Renderer()
     * @model resolveProxies="false" required="true" transient="true"
     * @generated
     */
    Renderer getRenderer();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.RenderExecutor#getRenderer <em>Renderer</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Renderer</em>' reference.
     * @see #getRenderer()
     * @generated
     */
    void setRenderer(Renderer value);

    /**
     * Method calls visitor.visit().
     * 
     * @param visitor the visitor object
     */
    void visit(ExecutorVisitor visitor);

    /**
     * This method does not use the monitor parameter. It is the same as calling render(bounds);
     * 
     * @see org.locationtech.udig.project.internal.render.Renderer#render(org.locationtech.jts.geom.Envelope,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    public void render(IProgressMonitor monitor) throws RenderException;

    /**
     * @see org.locationtech.udig.project.internal.render.Renderer#render(org.locationtech.jts.geom.Envelope,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    public void render() throws RenderException;

    /**
     * This method is called when the rendering is interrupted. If the rendering has to restart or
     * must stop. The dispose method called then the rendering thread is interrupted. Because the
     * dispose method is called while the rendering thread is still running it <b>MUST BE
     * THREADSAFE!!!! </b>
     */
    public void stopRendering();

}
