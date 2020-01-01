/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render.impl;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.Trace;
import org.locationtech.udig.project.internal.render.RenderContext;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.internal.render.RendererDecorator;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.render.RenderException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.preference.IPreferenceStore;
import org.geotools.data.FeatureStore;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

public class TilingRenderer implements Renderer, RendererDecorator {

    private static final double SIGNIFICANT = 0.0001;

    protected Renderer child;

    protected ViewportModel oldViewport;

    protected Dimension oldDisplaySize;

    protected int dScreenW;

    protected int dScreenH;


    protected Envelope paintedAreaInWorld;

    public TilingRenderer( Renderer child ) {
        this.child = child;
    }

    public void render( IProgressMonitor monitor ) throws RenderException {
        IPreferenceStore store = ProjectPlugin.getPlugin().getPreferenceStore();
        boolean useTiling = store.getBoolean(PreferenceConstants.P_TILING_RENDERER); 
        
        if (getContext().getGeoResource().canResolve(FeatureStore.class) || !useTiling) {
            Envelope renderBounds = getRenderBounds();
            if (renderBounds == null) {
                getContext().clearImage();
            } else {
                Point min = getContext().worldToPixel(
                        new Coordinate(renderBounds.getMinX(), renderBounds.getMinY()));
                Point max = getContext().worldToPixel(
                        new Coordinate(renderBounds.getMaxX(), renderBounds.getMaxY()));
                int width = Math.abs(max.x - min.x);
                int height = Math.abs(max.y - min.y);
                Rectangle paintArea = new Rectangle(Math.min(min.x, max.x), Math.min(min.y, max.y),
                        width, height);

                getContext().clearImage(paintArea);
            }
            child.render(monitor);
        } else {
            checkState();
            setNewState();
            cacheRenderedImage();
            drawCachedTiles();
            approximateMissingTiles();
            drawNeededTiles(monitor);
            if( monitor.isCanceled() ){
                oldViewport=null;
                return;
            }

            oldViewport = (ViewportModel) EcoreUtil.copy(getContext().getViewportModelInternal());
            oldDisplaySize = getContext().getMapDisplay().getDisplaySize();
        }
    }

    /**
     * Draw the non-cached tiles
     * 
     * @param monitor
     * @throws RenderException
     */
    private void drawNeededTiles( IProgressMonitor monitor ) throws RenderException {
        Envelope currentBounds = getContext().getViewportModel().getBounds();
        if (paintedAreaInWorld.isNull()) {
            child.render( monitor);
            return;
        }
        if (currentBounds.getHeight() - paintedAreaInWorld.getHeight() > currentBounds.getWidth()
                - paintedAreaInWorld.getWidth()) {
            renderY(monitor, currentBounds, currentBounds.getMinX(), currentBounds.getMaxX());
            renderX(monitor, currentBounds, paintedAreaInWorld.getMinY(), paintedAreaInWorld
                    .getMaxY());
        } else {
            renderX(monitor, currentBounds, currentBounds.getMinY(), currentBounds.getMaxY());
            update();
            renderY(monitor, currentBounds, paintedAreaInWorld.getMinX(), paintedAreaInWorld
                    .getMaxX());
        }
    }

    private void renderX( IProgressMonitor monitor, Envelope currentBounds, double miny, double maxy )
            throws RenderException {
        if (paintedAreaInWorld.getWidth() < currentBounds.getWidth()) {
            double minx, maxx;
            if (paintedAreaInWorld.getMinX() <= currentBounds.getMinX()) {
                minx = currentBounds.getMaxX();
                maxx = paintedAreaInWorld.getMaxX();
            } else {
                minx = currentBounds.getMinX();
                maxx = paintedAreaInWorld.getMinX();
            }

            Envelope envelope = new Envelope(minx, maxx, miny, maxy);
            if (validEnvelope(envelope)) {
                child.setRenderBounds(envelope);
            	child.render( monitor);
            }
        }
    }

    private void renderY( IProgressMonitor monitor, Envelope currentBounds, double minx, double maxx )
            throws RenderException {
        if (paintedAreaInWorld.getHeight() < currentBounds.getHeight()) {
            double miny, maxy;
            if (paintedAreaInWorld.getMinY() <= currentBounds.getMinY()) {
                miny = currentBounds.getMaxY();
                maxy = paintedAreaInWorld.getMaxY();
            } else {
                miny = currentBounds.getMinY();
                maxy = paintedAreaInWorld.getMinY();
            }
            
            Envelope envelope = new Envelope(minx, maxx, miny, maxy);
            if (validEnvelope(envelope)) {
                child.setRenderBounds(envelope);
            	child.render(monitor);
            }            
        }
    }
    
    /**
     * Checks that the given envelope is actually valid. That is, it returns
     * false if the envelope has duplicate x or y coordinates (and thus is a
     * rectangle with width 0).
     * 
     * @param envelope
     * @return
     */
    protected boolean validEnvelope(Envelope envelope) {
    	Point lower = getContext().worldToPixel(new Coordinate(envelope.getMinX(), envelope.getMinY()));
    	Point upper = getContext().worldToPixel(new Coordinate(envelope.getMaxX(), envelope.getMaxY()));
    	
    	if (lower.x == upper.x || lower.y == upper.y) {
    		return false;
    	}
    	return true;
    }

    /**
     * Tell the owner that it can update the image.
     */
    private void update() {
        setState(RENDERING);
    }

    /**
     * Draws an approximation from the missing tiles.
     */
    private void approximateMissingTiles() {
        // TODO Auto-generated method stub

    }

    /**
     * Draws part or the whole of the image from the cached data.
     */
    private void drawCachedTiles() {
        if (getState() == NEVER || getState() == RENDER_REQUEST || getState() == RENDERING) {
            getContext().clearImage();
        } else if (!isZoomChanged()) {
            panImage();
        } else {
            getContext().clearImage();
        }
    }

    /**
     * If the image has been resized or panned then you can copy the area from the old image
     * quickly.
     */
    protected void panImage() {
        if (oldViewport == null)
            return;

        ProjectPlugin.trace(Trace.RENDER, getClass(), "Panning existing image", null); //$NON-NLS-1$

        Envelope current = getContext().getViewportModel().getBounds();
        Envelope old = oldViewport.getBounds();

        if( old.equals(current) )
            return;
        
        double worldPaintedMinX = old.getMinX();
        double worldPaintedMinY = old.getMinY();
        double worldPaintedMaxX = current.getMaxX();
        double worldPaintedMaxY = current.getMaxY();

        if (Math.abs(old.getMinX() - current.getMinX()) > SIGNIFICANT) {
            if (old.getMinX() > current.getMinX()) {
                worldPaintedMinX = current.getMaxX();
                worldPaintedMaxX = old.getMinX();
            } else {
                worldPaintedMinX = current.getMinX();
                worldPaintedMaxX = old.getMaxX();
            }
        }
        if (Math.abs(old.getMinY() - current.getMinY()) > SIGNIFICANT) {
            if (old.getMinY() > current.getMinY()) {
                worldPaintedMinY = old.getMinY();
                worldPaintedMaxY = current.getMaxY();
            } else {
                worldPaintedMinY = current.getMinY();
                worldPaintedMaxY = old.getMaxY();
            }
        }
        paintedAreaInWorld = new Envelope(worldPaintedMinX, worldPaintedMaxX, worldPaintedMinY,
                worldPaintedMaxY);

        AffineTransform at = getContext().worldToScreenTransform();

        double[] points = new double[]{old.getMinX(), old.getMaxY()};

        at.transform(points, 0, points, 0, 1);
        double oldminx = points[0];
        double oldminy = points[1];
        BufferedImage image = new BufferedImage(oldDisplaySize.width, oldDisplaySize.height,
                BufferedImage.TYPE_4BYTE_ABGR);
        image.createGraphics().drawImage(getContext().getImage(), 0, 0, null);
        getContext().clearImage();
        Graphics2D graphics = getContext().getImage().createGraphics();
        AffineTransform transform = AffineTransform.getTranslateInstance(oldminx, oldminy);

        graphics.drawRenderedImage(image, transform);

        graphics.dispose();

    }

    /**
     * Splits the existing image into tiles and caches them.
     */
    private void cacheRenderedImage() {
        // TODO Auto-generated method stub

    }

    /**
     * Makes calculations about what the needed areas are, how the new and old area intersect etc..
     * for use by the other methods
     */
    private void setNewState() {
        paintedAreaInWorld = new Envelope();

    }

    /**
     * This needs to go to the super class. The class throws an exception if the renderer is in
     * disposed state.
     */
    private void checkState() {
        if (getState() == IRenderer.DISPOSED)
            throw new IllegalStateException(Messages.TilingRenderer_disposedError); 
    }

    public void render( Graphics2D graphics, IProgressMonitor monitor ) throws RenderException {
        checkState();
        child.render(graphics, monitor);
    }

    boolean isZoomChanged() {
        Dimension displaySize = getContext().getRenderManager().getMapDisplay().getDisplaySize();
        if (oldViewport == null) {
            return true;
        }
        Envelope old = oldViewport.getBounds();
        Envelope curr = getContext().getViewportModel().getBounds();

        if (!displaySize.equals(oldDisplaySize)) {
            dScreenW = displaySize.width - oldDisplaySize.width;
            dScreenH = displaySize.height - oldDisplaySize.height;
        } else {
            dScreenW = -1;
            dScreenH = -1;
        }
        if (Math.abs(old.getWidth() - curr.getWidth()) > SIGNIFICANT && dScreenH == -1
                && dScreenW == -1)
            return true;

        if (Math.abs(old.getHeight() - curr.getHeight()) > SIGNIFICANT && dScreenH == -1
                && dScreenW == -1)
            return true;

        return false;
    }

    public RenderContext getContext() {
        return (RenderContext) child.getContext();
    }

    public void dispose() {
        checkState();
        purgeImageCache();
        child.dispose();
        setState(IRenderer.DISPOSED);
    }

    /**
     * Removes the cache from memory
     */
    private void purgeImageCache() {
        // TODO Auto-generated method stub

    }

    public Renderer getRenderer() {
        return child;
    }

    @Override
    public String toString() {
        return child.toString();
    }

    public EList eAdapters() {
        return child.eAdapters();
    }

    public int getState() {
        return child.getState();
    }

    public void setState( int newState ) {
        child.setState(newState);
    }

    public String getName() {
        return child.getName();
    }

    public void setName( String value ) {
        child.setName(value);
    }

    public void setContext( IRenderContext context ) {
        child.setContext(context);
    }

    public EClass eClass() {
        return child.eClass();
    }

    public Resource eResource() {
        return child.eResource();
    }

    public EObject eContainer() {
        return child.eContainer();
    }

    public EStructuralFeature eContainingFeature() {
        return child.eContainingFeature();
    }

    public EReference eContainmentFeature() {
        return child.eContainmentFeature();
    }

    public EList eContents() {
        return child.eContents();
    }

    public TreeIterator eAllContents() {
        return child.eAllContents();
    }

    public boolean eIsProxy() {
        return child.eIsProxy();
    }

    public EList eCrossReferences() {
        return child.eCrossReferences();
    }

    public Object eInvoke( EOperation operation, EList< ? > arguments )
            throws InvocationTargetException {
        return child.eInvoke(operation, arguments);
    }
    
    public Object eGet( EStructuralFeature feature ) {
        return child.eGet(feature);
    }

    public Object eGet( EStructuralFeature feature, boolean resolve ) {
        return child.eGet(feature, resolve);
    }

    public void eSet( EStructuralFeature feature, Object newValue ) {
        child.eSet(feature, newValue);
    }

    public boolean eIsSet( EStructuralFeature feature ) {
        return child.eIsSet(feature);
    }

    public void eUnset( EStructuralFeature feature ) {
        child.eUnset(feature);
    }

    public boolean eDeliver() {
        return child.eDeliver();
    }

    public void eSetDeliver( boolean deliver ) {
        child.eSetDeliver(deliver);
    }

    public void eNotify( Notification notification ) {
        child.eNotify(notification);
    }

    public boolean isCacheable() {
        return child.isCacheable();
    }

    public Envelope getRenderBounds() {
        return child.getRenderBounds();
    }

    public void setRenderBounds( Envelope boundsToRender ) {
        child.setRenderBounds(boundsToRender);
    }

    public void setRenderBounds( Rectangle screenArea ) {
        child.setRenderBounds(screenArea);
    }

}
