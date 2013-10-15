/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.support;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.refractions.udig.ui.graphics.AWTSWTImageUtils;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Path;

/**
 * An abstract class for drawing {@link net.refractions.udig.tools.edit.support.EditGeom} objects
 * 
 * @author jones
 * @since 1.1.0
 */
public abstract class AbstractPathIterator implements PathIterator{

    protected final EditGeom geom;
    private Iterator<PrimitiveShape> shapes;
    protected Iterator<Point> points;
    protected Point currentPoint, nextPoint;
    
    protected PrimitiveShape currentShape;

    protected AbstractPathIterator( EditGeom shape ) {
        this.geom = shape;
    }

    /**
     * Returns a shape that can be draw.
     * <p>It is recommended to call getShape() every time a draw is needed because the shape does not update if
     * the EditGeom is updated between draws.  getShape is guaranteed to get a shape that is representative of
     * the current state of geom.</p>
     *
     * @return
     */
    public Shape toShape() {
        prepareToPath();

        if( geom.getShell().getNumPoints()==1 ){
            Shape createPoint = createPoint();
            if( createPoint!=null )
                return createPoint;
        }
        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        
        path.append(this, false);
        
        return path;
    }

    public Path toPath(Device device) {
        prepareToPath();
        
        if( geom.getShell().getNumPoints()==1 ){
            Path createPoint = createPointPath(device);
            if( createPoint!=null )
                return createPoint;
        }
        
        return AWTSWTImageUtils.createPath(this, device);
    }


    private void prepareToPath() {
        currentPoint=null;
        points=null;
        nextPoint=null;
        currentShape=null;
        shapes=geom.iterator();
    }
    


    /**
     * Called if there is only a single point in the geom.  Default behaviour
     * is to return a small square.  If null is returned then this PathIterator
     * will be used to create a geometry.
     *
     * @return a shape to draw or null if PathIterator should be used to create a shape.
     */
    protected Shape createPoint() {
        Point point=geom.getShell().getPoint(0);
        return new Rectangle(point.getX()-2, point.getY()-2, 4, 4 );
    }

    protected Path createPointPath(Device device) {
        Point point=geom.getShell().getPoint(0);
        Path path=new Path(device);
        path.addRectangle(point.getX()-2, point.getY()-2, 4, 4);
        return path;
    }

    public int getWindingRule() {
        return WIND_EVEN_ODD;
    }

    public boolean isDone() {
        return isDoneInternal();
    }
    
    private boolean isDoneInternal() {
        if( nextPoint!=null )
            return false;
        
        if( points!=null && points.hasNext() ){
            nextPoint=points.next();
            return false;
        }
        
        // points is null or is done so get next shape
        while( shapes.hasNext()){
            currentShape=shapes.next();
            
            points=currentShape.iterator();
            
            if( points.hasNext() ){
                nextPoint=points.next();
                break;
            }
        }
        
        return nextPoint==null;
    }

    public void next() {
        try{
        nextPoint=null;
        if( points.hasNext() ){
            if ( isDone() ){
                return;
            }
                currentPoint=nextPoint;
        }else{
            isDone();
            currentPoint=null;
        }
        }catch(NoSuchElementException e ){
            throw new NoSuchElementException(
                    "Shape is done, make sure to check isDone before calling next()"); //$NON-NLS-1$
        }
        
    
    }
    
    public int currentSegment( float[] coords ) {

        int result=SEG_LINETO;

        if (currentPoint==null){
            coords[0] = nextPoint.getX();
            coords[1] = nextPoint.getY();
            return SEG_MOVETO;
        }
        
        coords[0] = currentPoint.getX();
        coords[1] = currentPoint.getY();

        return result;

    }

    public int currentSegment( double[] coords ) {
        float[] floats=new float[coords.length];
        int result = currentSegment(floats);
        System.arraycopy(floats, 0, coords, 0, coords.length);
        return result;
    }

}
