/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.support;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * PathIterator for a simple primitive shape.
 * @author jones
 * @since 1.1.0
 */
public abstract class AbstractShapeIterator implements PathIterator{

    private PrimitiveShape pointShape;

    protected PrimitiveShape shape;

    protected Iterator<Point> points;
    protected Point currentPoint, nextPoint;

    private PrimitiveShape trueShape;
    
    protected AbstractShapeIterator( PrimitiveShape shape ) {
        this.trueShape=shape;
    }
    
    public synchronized Shape toShape(){
        reset();
        GeneralPath path=new GeneralPath();
        path.append( this, false );
        return path;
    }
    
    public synchronized void reset(){
        currentPoint=null;
        if( trueShape.getNumPoints()==1 )
            shape=getPointShape();
        else
            shape=trueShape;
        points=shape.iterator();
        nextPoint=null;
    }
    
    private synchronized PrimitiveShape getPointShape() {
        if( pointShape==null ){
            pointShape = new PrimitiveShape(trueShape.getEditGeom());
            Point point = trueShape.getPoint(0);
            pointShape.getEditGeom().initializing=true;
            Point valueOf = Point.valueOf(point.getX()-2, point.getY()-2);
            pointShape.getMutator().addPoint(valueOf, null);
            valueOf = Point.valueOf(point.getX()+2, point.getY()-2);
            pointShape.getMutator().addPoint(valueOf, null);
            valueOf = Point.valueOf(point.getX()+2, point.getY()+2);
            pointShape.getMutator().addPoint(valueOf, null);
            valueOf = Point.valueOf(point.getX()-2, point.getY()+2);
            pointShape.getMutator().addPoint(valueOf, null);
            valueOf = Point.valueOf(point.getX()-2, point.getY()-2);
            pointShape.getMutator().addPoint(valueOf, null);
            pointShape.getEditGeom().initializing=false;
        }
        
        return pointShape;
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
        
        if( points.hasNext() ){
            nextPoint=points.next();
            return false;
        }
        
        return true;
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

}
