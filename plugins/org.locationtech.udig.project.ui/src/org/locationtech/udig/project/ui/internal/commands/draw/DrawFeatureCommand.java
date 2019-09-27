/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal.commands.draw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.Symbolizer;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.project.ui.commands.AbstractDrawCommand;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.ui.Drawing;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.graphics.AWTGraphics;
import org.locationtech.udig.ui.graphics.AWTSWTImageUtils;
import org.locationtech.udig.ui.graphics.SWTGraphics;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Draws a feature on the screen.
 * 
 * @author jeichar
 * @since 0.9
 */
public class DrawFeatureCommand extends AbstractDrawCommand {

    private SimpleFeature feature;
    private static final Map<MathTransformKey, MathTransform> mtCache=new ConcurrentHashMap<MathTransformKey, MathTransform>();

    private Drawing drawing = Drawing.create();
    private CoordinateReferenceSystem featureCRS;

    Symbolizer[] syms;

    private Color color=Color.ORANGE;

    private MathTransform mt;
    /**
     * The location that the image should be drawn at.
     */
//    private Point imageLocation;
    /**
     * The image of the drawn feature.  
     * @see #preRender()
     */
    private Image image;
    private boolean errorReported;
    
    /**
     * @param feature
     * @param layer layer that feature is from
     * @throws IOException
     */
    public DrawFeatureCommand( SimpleFeature feature, ILayer layer ) throws IOException {
        this(feature, layer.getCRS());
    }

    /**
     * @param feature
     * @param crs
     */
    public DrawFeatureCommand( SimpleFeature feature, CoordinateReferenceSystem crs ) {
        this.feature = feature;
        if (crs == null)
            this.featureCRS = DefaultGeographicCRS.WGS84;
        else
            this.featureCRS = crs;
    }

    /**
     * @param feature
     */
    public DrawFeatureCommand( SimpleFeature feature ) {
        this(feature, feature.getFeatureType().getCoordinateReferenceSystem());
    }
    
    /**
     * Renders the feature to a image buffer so that drawing command will be fast.
     * If feature is large you should call this so that there isn't a big delay in the display
     * thread.
     * <p>
     * setMap() must be called before calling this method.
     * </p>
     * <p>
     * If this method is called then this object must be sent to the ViewportPane or be disposed
     * because a Image object is created that needs to be disposed.
     * </p>
     * 
     */
    public void preRender(){
        
        if( BUFFER_READY ){
        
            PlatformGIS.syncInDisplayThread(new Runnable(){
                public void run() {
                    renderInternal();
                }
            });
        }
    }
    
    private void renderInternal(){
        if( syms==null ) {
            syms = Drawing.getSymbolizers(((Geometry)feature.getDefaultGeometry()).getClass(), color,false);
        }
        MathTransform mt = getMathTransform(featureCRS);
        AffineTransform toScreen=getMap().getViewportModel().worldToScreenTransform();

        // calculate the size of the image and where it will be in the display
        Envelope envelope;
        try{
            ReferencedEnvelope bounds = new ReferencedEnvelope(feature.getBounds());
            envelope = bounds.transform(getMap().getViewportModel().getCRS(), true);
        }catch (Exception e) {
            envelope=new ReferencedEnvelope(feature.getBounds());
        }
        double[] screenbounds=new double[]{ 
                envelope.getMinX(), envelope.getMinY(), 
                envelope.getMaxX(), envelope.getMaxY(), 
                };
        toScreen.transform(screenbounds, 0, screenbounds, 0, 2);
        
//        imageLocation=new Point((int)(Math.min(screenbounds[0], screenbounds[2])), (int)(Math.min(screenbounds[1], screenbounds[3])) );
        
        int width = (int) Math.abs(screenbounds[2]-screenbounds[0]);
        int height = (int) Math.abs(screenbounds[3]-screenbounds[1]);
        //create transparent image
        image=AWTSWTImageUtils.createDefaultImage(Display.getDefault(), width, height);
        
        // draw feature
        SWTGraphics graphics=new SWTGraphics(image, Display.getDefault());
        
        drawing.drawFeature(graphics, feature,
                getMap().getViewportModel().worldToScreenTransform(envelope, new Dimension(width,height)), false, syms, mt);
        graphics.dispose();
    }
    
    /** I haven't been able to get the SWT image buffer going yet
     * So this flag is so I can quickly enable the unstable code for
     * development and disable it for committing my changes.
     */
    private static final boolean BUFFER_READY=false;

    /**
     * @see org.locationtech.udig.project.command.MapCommand#run()
     */
    public void run( IProgressMonitor monitor ) throws Exception {
        if( !BUFFER_READY || graphics instanceof AWTGraphics ){
            if( syms==null )
                syms = Drawing.getSymbolizers(((Geometry)feature.getDefaultGeometry()).getClass(), color, false);
            MathTransform mt = getMathTransform(featureCRS);
            drawing.drawFeature(graphics, feature,
                    getMap().getViewportModel().worldToScreenTransform(), false, syms, mt);
        }else{
            if( image==null ){
                preRender();
            }
//            graphics.drawImage(image, imageLocation.x, imageLocation.y);
        }
    }

    /**
     *
     * @return
     */
    private MathTransform getMathTransform(CoordinateReferenceSystem featureCRS) {
        MathTransformKey key=new MathTransformKey(featureCRS, getMap().getViewportModel().getCRS());
        mt=mtCache.get(key);
        if( mt==null ){
            try {
                mt = CRS.findMathTransform(featureCRS, getMap().getViewportModel().getCRS(), true);
            } catch (Exception e) {
                mt = null;
            }
            mtCache.put(key,mt);
        }
        return mt;
    }

    /**
     * @return Returns the color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color The color to set.
     */
    public void setColor( Color color ) {
        this.color = color;
    }
    /**
     * Allows the symbolizers to be set
     *
     * @param syms symbolizers to use to draw features.  
     */
    public void setSymbolizers( Symbolizer[] syms){
        if( syms==null )
            this.syms=new Symbolizer[0];
        else{

            this.syms=new Symbolizer[syms.length];
            System.arraycopy(syms, 0, this.syms, 0, this.syms.length);
        }
    }

    public Rectangle getValidArea() {
        if( feature!=null ){
            try {
                Envelope bounds = new ReferencedEnvelope(feature.getBounds()).transform(getMap().getViewportModel().getCRS(), true);
                double[] points=new double[]{bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY()};
                getMap().getViewportModel().worldToScreenTransform().transform(points, 0, points, 0, 2);
                return new Rectangle((int)points[0], (int)points[1], (int)Math.abs(points[2]-points[0]), (int)Math.abs(points[3]-points[1]));
            } catch (TransformException e) {
                if( !errorReported ){
                    errorReported = true;
                    ProjectUIPlugin.log("error calculating valid area, this will not be reported again", e);
                }
                return null;
            } catch (MismatchedDimensionException e) {
                if( !errorReported ){
                    errorReported = true;
                    ProjectUIPlugin.log("error calculating valid area, this will not be reported again", e);
                }
                return null;
            } catch (FactoryException e) {
                if( !errorReported ){
                    errorReported = true;
                    ProjectUIPlugin.log("error calculating valid area, this will not be reported again", e);
                }
                return null;
            }
        }
        return null;

    }

    public void setValid(boolean valid){
        super.setValid(valid);
        if( !valid )
            dispose();
    }
    
    protected void finalize(){
        dispose();
    }
    /**
     * Diposes of the image if it has been created.  Only needs to be called if the
     * command has not been sent to the {@link IMapDisplay}.
     */
    public void dispose(){
        if( image!=null && !image.isDisposed()){
            image.dispose();
            image=null;
        }
    }
    
    private static class MathTransformKey{
        final CoordinateReferenceSystem from, to;

        protected MathTransformKey(CoordinateReferenceSystem from, CoordinateReferenceSystem to){
            this.from=from;
            this.to=to;
        }
        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((from == null) ? 0 : from.hashCode());
            result = PRIME * result + ((to == null) ? 0 : to.hashCode());
            return result;
        }

        @Override
        public boolean equals( Object obj ) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final MathTransformKey other = (MathTransformKey) obj;
            if (from == null) {
                if (other.from != null)
                    return false;
            } else if (!from.equals(other.from))
                return false;
            if (to == null) {
                if (other.to != null)
                    return false;
            } else if (!to.equals(other.to))
                return false;
            return true;
        }
        
    }
}
