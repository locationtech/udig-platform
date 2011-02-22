/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.ui.internal.commands.draw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.ui.Drawing;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.graphics.AWTGraphics;
import net.refractions.udig.ui.graphics.SWTGraphics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.geotools.feature.Feature;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.Symbolizer;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Draws a feature on the screen.
 *
 * @author jeichar
 * @since 0.9
 */
public class DrawFeatureCommand extends AbstractDrawCommand {

    private Feature feature;
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

    /**
     * @param feature
     * @param layer layer that feature is from
     * @throws IOException
     */
    public DrawFeatureCommand( Feature feature, ILayer layer ) throws IOException {
        this(feature, layer.getCRS());
    }

    /**
     * @param feature
     * @param crs
     */
    public DrawFeatureCommand( Feature feature, CoordinateReferenceSystem crs ) {
        this.feature = feature;
        if (crs == null)
            this.featureCRS = DefaultGeographicCRS.WGS84;
        else
            this.featureCRS = crs;
    }

    /**
     * @param feature
     */
    public DrawFeatureCommand( Feature feature ) {
        this(feature, feature.getFeatureType().getDefaultGeometry().getCoordinateSystem());
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
        if( syms!=null )
            syms = Drawing.getSymbolizers(feature.getDefaultGeometry().getClass(), color,false);
        MathTransform mt = getMathTransform(featureCRS);
        AffineTransform toScreen=getMap().getViewportModel().worldToScreenTransform();

        // calculate the size of the image and where it will be in the display
        Envelope envelope;
        try{
            envelope = JTS.transform(feature.getBounds(), null, mt, 10);
        }catch (Exception e) {
            envelope=feature.getBounds();
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
        image=SWTGraphics.createDefaultImage(Display.getDefault(), width, height);

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
     * @see net.refractions.udig.project.command.MapCommand#run()
     */
    public void run( IProgressMonitor monitor ) throws Exception {
        if( !BUFFER_READY || graphics instanceof AWTGraphics ){
            if( syms==null )
                syms = Drawing.getSymbolizers(feature.getDefaultGeometry().getClass(), color, false);
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
                Envelope bounds = JTS.transform(feature.getBounds(), getMathTransform(featureCRS));
                double[] points=new double[]{bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY()};
                getMap().getViewportModel().worldToScreenTransform().transform(points, 0, points, 0, 2);
                return new Rectangle((int)points[0], (int)points[1], (int)Math.abs(points[2]-points[0]), (int)Math.abs(points[3]-points[1]));
            } catch (TransformException e) {
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
