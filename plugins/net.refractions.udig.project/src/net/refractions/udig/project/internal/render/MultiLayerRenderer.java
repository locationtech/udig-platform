/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.render;

import net.refractions.udig.project.render.IMultiLayerRenderer;
import net.refractions.udig.project.render.RenderException;

/**
 * The CompositeRenderer is a container for all the Renderers of a viewport.
 * <p>
 * Produces a Image out of a series of renderers, provides viewport pane notification when content
 * changes.
 * </p>
 * <p>
 * <li>Renderer must ensure that the layers are combined in the correct zorder. The largest zorder
 * must be drawn first.</li>
 * <li>Combines all outputs from contained renderers into a buffered image</li>
 * <li>Image creation is provided by RenderContext</li>
 * <li>Double Buffering is handled by someone else (viewport pane)</li>
 * <li>CompositeRenderer must register with its contained renderers so it can merge the rendered
 * layers again.</li>
 * <li>The events the render stack must listen for are setState(DONE) events. The following code
 * illustrates how this can be done:
 * 
 * <pre><code>
 * executor.eAdapters().add(new RenderListenerAdapter(){
 *     //renderDone is called when setState(DONE) is called
 *     protected void renderDone() {
 *         synchronized (CompositeRendererImpl.this) {
 *             setState(DONE);
 *         }
 *     }
 * });
 * </code></pre>
 * 
 * <li>Call setState(RENDERING) when a redraw is required.</li>
 * </p>
 * The Default implementation simply draws the layers overtop one another to merge the Layers.
 * CompositeRenderer
 * </p>
 * 
 * @author jeichar
 * @model abstract="true"
 */
public interface MultiLayerRenderer extends Renderer, IMultiLayerRenderer {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * Called when the map has changed.
     * <p>
     * this method is guaranteed not to block
     * </p>
     * <p>
     * Usually the image will merge the images for each layer. However in some cases, such as for a
     * WMS, the image will have to be recreated. This should be a quick operation.
     * </p>
     * <p>
     * Note: This command differs from render. render() forces a full rerendering of the data
     * whereas refreshImage does not require that the renderer access the data again.
     * 
     * @throws RenderException
     * @model
     */
    void refreshImage() throws RenderException;
}
