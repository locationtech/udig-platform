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
package org.locationtech.udig.mapgraphic.internal;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.mapgraphic.MapGraphicPlugin;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.render.impl.RendererImpl;
import org.locationtech.udig.project.render.ICompositeRenderContext;
import org.locationtech.udig.project.render.IMultiLayerRenderer;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.RenderException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * Renderer for MapGraphic layers
 */
public class MapGraphicRenderer extends RendererImpl implements IMultiLayerRenderer {

    public static final String BLACKBOARD_IMAGE_KEY = "CACHED_IMAGE"; //$NON-NLS-1$
    public static final String BLACKBOARD_IMAGE_BOUNDS_KEY = "CACHED_IMAGE_BOUNDS"; //$NON-NLS-1$
    
    @Override
    public String getName() {
        return super.getName();
    }
   
    
    /*
     * Renders the mapgraphic for the entire screen
     * 
     * @returns array[0] = new image; array[1] = image bounds
     */
     private Object[] backgroundRenderImage(ICompositeRenderContext context, List<IOException> exceptions){
        BufferedImage cache = new BufferedImage(context.getMapDisplay().getWidth(), context.getMapDisplay().getHeight(), BufferedImage.TYPE_INT_ARGB);      
        ReferencedEnvelope imageBounds = context.getViewportModel().getBounds();
        
        for( IRenderContext l : context.getContexts() ) {
            Graphics2D copy = (Graphics2D) cache.createGraphics();
            //final NonDisposableGraphics graphics = new NonDisposableGraphics(copy);
            try {
                if( !l.getLayer().isVisible() )
                    continue;
                MapGraphic mg = l.getGeoResource().resolve(MapGraphic.class, null);
                MapGraphicContext mgContext = new MapGraphicContextImpl(l, copy);
                mg.draw(mgContext);
            } catch (IOException e) {
                exceptions.add(e);
            }finally{
                copy.dispose();
            }
            setState(RENDERING);
        }
        return new Object[]{cache, imageBounds};   
    }

    /**
     * @see org.locationtech.udig.project.internal.render.impl.RendererImpl#render(java.awt.Graphics2D, IProgressMonitor)
     * @param destination
     */
    @Override
    public void render( Graphics2D destination, IProgressMonitor monitor ) {
        /* entry point for printing
         * cannot used cached image when printing; so 
         * draw directly to destination 
         */
        
        List<IOException> exceptions = new ArrayList<IOException>();
        for( IRenderContext l : getContext().getContexts() ) {
            Graphics2D copy = (Graphics2D) destination.create();
            // final NonDisposableGraphics graphics = new NonDisposableGraphics(copy);
            try {
                if (!l.getLayer().isVisible())
                    continue;
                MapGraphic mg = l.getGeoResource().resolve(MapGraphic.class, null);
                MapGraphicContext mgContext = new MapGraphicContextImpl(l, destination);
                mg.draw(mgContext);
            } catch (IOException e) {
                exceptions.add(e);
            } finally {
                copy.dispose();
            }
            setState(RENDERING);
        }
        if (!exceptions.isEmpty()) {
            // XXX: externalize this message
            RenderException exception = new RenderException(exceptions.size()
                    + " exceptions we raised while drawing map graphics", exceptions.get(0)); //$NON-NLS-1$
            exception.fillInStackTrace();
        }
        setState(DONE);
        
    }

    /**
     * @see org.locationtech.udig.project.internal.render.impl.RendererImpl#getContext()
     */
    @Override
    public synchronized ICompositeRenderContext getContext() {
        return (ICompositeRenderContext) super.getContext();
    }

    @Override
    public synchronized void setContext( IRenderContext newContext ) {
        super.setContext(newContext);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.project.render.Renderer#render(org.locationtech.jts.geom.Envelope)
     */
    @Override
    public void render( IProgressMonitor monitor ) {
        // this is the entry point for non printing
        /* For non-printing we will have a layer and can 
         * used the cached image on the layer.  
         */
        List<IOException> exceptions = new ArrayList<IOException>();
        ILayer layer = context.getLayer();
        IBlackboard blackboard = layer.getBlackboard();
        BlackboardItem cached = (BlackboardItem) blackboard.get(BLACKBOARD_IMAGE_KEY);
        if (cached != null){
            if (!cached.layersEqual(getContext().getLayers())){
                cached = null;
            }
        }
    
        if (cached == null){
            Object values[] = backgroundRenderImage(getContext(), exceptions);
            cached = new BlackboardItem((BufferedImage)values[0], (ReferencedEnvelope)values[1], getContext().getLayers());
            layer.getBlackboard().put(BLACKBOARD_IMAGE_KEY, cached);
            
        }
        BufferedImage cache = cached.image;
        ReferencedEnvelope imageBounds = cached.env;
        
        //we need to extract from the cache which is the size of the map display
        //the part of the image which is appropriate for the given bounds
        ReferencedEnvelope request = getContext().getImageBounds();

        double pixelperunitx = cache.getWidth() / imageBounds.getWidth() ;
        double pixelperunity = cache.getHeight() / imageBounds.getHeight();

        int lx = (int)Math.round((request.getMinX() - imageBounds.getMinX()) * pixelperunitx);
        int ly = (int)Math.round((request.getMaxY() - imageBounds.getMaxY()) * pixelperunity);

        AffineTransform transform = new AffineTransform(1f,0f,0f,1f,-lx,ly);
        Graphics2D denstination = getContext().getImage().createGraphics();
        denstination.drawImage(cache, transform, null);
        
        if (!exceptions.isEmpty()) {
            //XXX: externalize this message
            RenderException exception = new RenderException(exceptions.size()
                    + " exceptions we raised while drawing map graphics", exceptions.get(0)); //$NON-NLS-1$
            exception.fillInStackTrace();
        }
        setState(DONE);
    }

    Job refreshJob = new Job("RefreshJob"){ //$NON-NLS-1$
        @Override
        protected IStatus run( IProgressMonitor monitor ) {
            getContext().clearImage();
            try {
                
                render(monitor);
            } catch (Throwable e) {
                MapGraphicPlugin.log(null, e);
            }
            return Status.OK_STATUS;
        }

    };

    public void refreshImage() throws RenderException {
        refreshJob.schedule();
    }

    @Override
    public boolean isCacheable() {
    	return false;
    }
    
    @Override
    public void setState( int newState ) {
        
        if (newState == RENDER_REQUEST){
            //clear blackboard
            //TODO: make this only occur on layer.refresh() event.  Right now this works fine for the regular
            //renderer but does not work well for the tiled renderer as a render_request occurs for each
            //tile rendered.
            getContext().getLayer().getBlackboard().put(BLACKBOARD_IMAGE_KEY, null);
        }
        super.setState(newState);
    }


    //XXX: this cannot be the right solution.  It looks like this is trying to protect the
    // against the user disposing of the Graphics2D which should never do.  But is still is
    // a problem because this must be disposed of as well so nothing is really solved
    // hbullen
    @SuppressWarnings("unused")
    private static class NonDisposableGraphics extends Graphics2D{

        final Graphics2D graphics;

        public NonDisposableGraphics( final Graphics2D graphics ) {
            this.graphics = graphics;
        }

        public void addRenderingHints( Map< ? , ? > hints ) {
            graphics.addRenderingHints(hints);
        }

        public void clearRect( int x, int y, int width, int height ) {
            graphics.clearRect(x, y, width, height);
        }

        public void clip( Shape s ) {
            graphics.clip(s);
        }

        public void clipRect( int x, int y, int width, int height ) {
            graphics.clipRect(x, y, width, height);
        }

        public void copyArea( int x, int y, int width, int height, int dx, int dy ) {
            graphics.copyArea(x, y, width, height, dx, dy);
        }

        public Graphics create() {
            return graphics.create();
        }

        public Graphics create( int x, int y, int width, int height ) {
            return graphics.create(x, y, width, height);
        }

        public void dispose() {
            graphics.dispose();
        }

        public void draw( Shape s ) {
            graphics.draw(s);
        }

        public void draw3DRect( int x, int y, int width, int height, boolean raised ) {
            graphics.draw3DRect(x, y, width, height, raised);
        }

        public void drawArc( int x, int y, int width, int height, int startAngle, int arcAngle ) {
            graphics.drawArc(x, y, width, height, startAngle, arcAngle);
        }

        public void drawBytes( byte[] data, int offset, int length, int x, int y ) {
            graphics.drawBytes(data, offset, length, x, y);
        }

        public void drawChars( char[] data, int offset, int length, int x, int y ) {
            graphics.drawChars(data, offset, length, x, y);
        }

        public void drawGlyphVector( GlyphVector g, float x, float y ) {
            graphics.drawGlyphVector(g, x, y);
        }

        public void drawImage( BufferedImage img, BufferedImageOp op, int x, int y ) {
            graphics.drawImage(img, op, x, y);
        }

        public boolean drawImage( Image img, AffineTransform xform, ImageObserver obs ) {
            return graphics.drawImage(img, xform, obs);
        }

        public boolean drawImage( Image img, int x, int y, Color bgcolor, ImageObserver observer ) {
            return graphics.drawImage(img, x, y, bgcolor, observer);
        }

        public boolean drawImage( Image img, int x, int y, ImageObserver observer ) {
            return graphics.drawImage(img, x, y, observer);
        }

        public boolean drawImage( Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer ) {
            return graphics.drawImage(img, x, y, width, height, bgcolor, observer);
        }

        public boolean drawImage( Image img, int x, int y, int width, int height, ImageObserver observer ) {
            return graphics.drawImage(img, x, y, width, height, observer);
        }

        public boolean drawImage( Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer ) {
            return graphics.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
        }

        public boolean drawImage( Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer ) {
            return graphics.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
        }

        public void drawLine( int x1, int y1, int x2, int y2 ) {
            graphics.drawLine(x1, y1, x2, y2);
        }

        public void drawOval( int x, int y, int width, int height ) {
            graphics.drawOval(x, y, width, height);
        }

        public void drawPolygon( int[] xPoints, int[] yPoints, int nPoints ) {
            graphics.drawPolygon(xPoints, yPoints, nPoints);
        }

        public void drawPolygon( Polygon p ) {
            graphics.drawPolygon(p);
        }

        public void drawPolyline( int[] xPoints, int[] yPoints, int nPoints ) {
            graphics.drawPolyline(xPoints, yPoints, nPoints);
        }

        public void drawRect( int x, int y, int width, int height ) {
            graphics.drawRect(x, y, width, height);
        }

        public void drawRenderableImage( RenderableImage img, AffineTransform xform ) {
            graphics.drawRenderableImage(img, xform);
        }

        public void drawRenderedImage( RenderedImage img, AffineTransform xform ) {
            graphics.drawRenderedImage(img, xform);
        }

        public void drawRoundRect( int x, int y, int width, int height, int arcWidth, int arcHeight ) {
            graphics.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
        }

        public void drawString( AttributedCharacterIterator iterator, float x, float y ) {
            graphics.drawString(iterator, x, y);
        }

        public void drawString( AttributedCharacterIterator iterator, int x, int y ) {
            graphics.drawString(iterator, x, y);
        }

        public void drawString( String s, float x, float y ) {
            graphics.drawString(s, x, y);
        }

        public void drawString( String str, int x, int y ) {
            graphics.drawString(str, x, y);
        }

        public void fill( Shape s ) {
            graphics.fill(s);
        }

        public void fill3DRect( int x, int y, int width, int height, boolean raised ) {
            graphics.fill3DRect(x, y, width, height, raised);
        }

        public void fillArc( int x, int y, int width, int height, int startAngle, int arcAngle ) {
            graphics.fillArc(x, y, width, height, startAngle, arcAngle);
        }

        public void fillOval( int x, int y, int width, int height ) {
            graphics.fillOval(x, y, width, height);
        }

        public void fillPolygon( int[] xPoints, int[] yPoints, int nPoints ) {
            graphics.fillPolygon(xPoints, yPoints, nPoints);
        }

        public void fillPolygon( Polygon p ) {
            graphics.fillPolygon(p);
        }

        public void fillRect( int x, int y, int width, int height ) {
            graphics.fillRect(x, y, width, height);
        }

        public void fillRoundRect( int x, int y, int width, int height, int arcWidth, int arcHeight ) {
            graphics.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
        }

        public void finalize() {
            graphics.finalize(); // this was a warning in find bugz
        }

        public Color getBackground() {
            return graphics.getBackground();
        }

        public Shape getClip() {
            return graphics.getClip();
        }

        public Rectangle getClipBounds() {
            return graphics.getClipBounds();
        }

        public Rectangle getClipBounds( Rectangle r ) {
            return graphics.getClipBounds(r);
        }

        public Rectangle getClipRect() {
            return graphics.getClipBounds();
        }

        public Color getColor() {
            return graphics.getColor();
        }

        public Composite getComposite() {
            return graphics.getComposite();
        }

        public GraphicsConfiguration getDeviceConfiguration() {
            return graphics.getDeviceConfiguration();
        }

        public Font getFont() {
            return graphics.getFont();
        }

        public FontMetrics getFontMetrics() {
            return graphics.getFontMetrics();
        }

        public FontMetrics getFontMetrics( Font f ) {
            return graphics.getFontMetrics(f);
        }

        public FontRenderContext getFontRenderContext() {
            return graphics.getFontRenderContext();
        }

        public Paint getPaint() {
            return graphics.getPaint();
        }

        public Object getRenderingHint( Key hintKey ) {
            return graphics.getRenderingHint(hintKey);
        }

        public RenderingHints getRenderingHints() {
            return graphics.getRenderingHints();
        }

        public Stroke getStroke() {
            return graphics.getStroke();
        }

        public AffineTransform getTransform() {
            return graphics.getTransform();
        }

        public boolean hit( Rectangle rect, Shape s, boolean onStroke ) {
            return graphics.hit(rect, s, onStroke);
        }

        public boolean hitClip( int x, int y, int width, int height ) {
            return graphics.hitClip(x, y, width, height);
        }

        public void rotate( double theta, double x, double y ) {
            graphics.rotate(theta, x, y);
        }

        public void rotate( double theta ) {
            graphics.rotate(theta);
        }

        public void scale( double sx, double sy ) {
            graphics.scale(sx, sy);
        }

        public void setBackground( Color color ) {
            graphics.setBackground(color);
        }

        public void setClip( int x, int y, int width, int height ) {
            graphics.setClip(x, y, width, height);
        }

        public void setClip( Shape clip ) {
            graphics.setClip(clip);
        }

        public void setColor( Color c ) {
            graphics.setColor(c);
        }

        public void setComposite( Composite comp ) {
            graphics.setComposite(comp);
        }

        public void setFont( Font font ) {
            graphics.setFont(font);
        }

        public void setPaint( Paint paint ) {
            graphics.setPaint(paint);
        }

        public void setPaintMode() {
            graphics.setPaintMode();
        }

        public void setRenderingHint( Key hintKey, Object hintValue ) {
            graphics.setRenderingHint(hintKey, hintValue);
        }

        public void setRenderingHints( Map< ? , ? > hints ) {
            graphics.setRenderingHints(hints);
        }

        public void setStroke( Stroke s ) {
            graphics.setStroke(s);
        }

        public void setTransform( AffineTransform Tx ) {
            graphics.setTransform(Tx);
        }

        public void setXORMode( Color c1 ) {
            graphics.setXORMode(c1);
        }

        public void shear( double shx, double shy ) {
            graphics.shear(shx, shy);
        }

        public String toString() {
            return graphics.toString();
        }

        public void transform( AffineTransform Tx ) {
            graphics.transform(Tx);
        }

        public void translate( double tx, double ty ) {
            graphics.translate(tx, ty);
        }

        public void translate( int x, int y ) {
            graphics.translate(x, y);
        }
        
    }
    
    static class BlackboardItem{
        BufferedImage image;
        ReferencedEnvelope env;      
        Collection<ILayer> layers;
        
        public BlackboardItem(BufferedImage image, ReferencedEnvelope env, Collection<ILayer> layers){
            this.image = image;
            this.env = env;
            this.layers = layers; 
        }
        
        public boolean layersEqual(Collection<ILayer> layers){
            if (this.layers.size() != layers.size()) return false;
            for( Iterator<ILayer> iterator = this.layers.iterator(); iterator.hasNext(); ) {
                ILayer layer = (ILayer) iterator.next();
                if (!layers.contains(layer))return false;
            }
            return true;
        }
        
    }
    
}
