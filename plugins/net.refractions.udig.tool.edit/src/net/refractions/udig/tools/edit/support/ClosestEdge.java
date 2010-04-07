package net.refractions.udig.tools.edit.support;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Describes the relationship between an arbitrary point to an edge in a {@link EditGeom}
 * 
 * @author jones
 * @since 1.1.0
 */
public class ClosestEdge{
    final double distanceToEdge;
    final Point pointOnLine;
    final int indexOfPrevious;
    final EditGeom shape;
    final PrimitiveShape part;
    Coordinate addedCoord;
    
    public ClosestEdge(double distanceToEdge, int indexOfPrevious, Point pointOnLine, 
            PrimitiveShape part) {
        this.distanceToEdge=distanceToEdge;
        this.indexOfPrevious=indexOfPrevious;
        this.pointOnLine=pointOnLine;
        this.shape=part.getEditGeom();
        this.part=part;
        this.addedCoord=part.getEditBlackboard().toCoord(pointOnLine);
    }
    
    /**
     * @return Returns the distanceToEdge.
     */
    public double getDistanceToEdge() {
        return distanceToEdge;
    }

    /**
     * @return Returns the indexOfPrevious.
     */
    public int getIndexOfPrevious() {
        return indexOfPrevious;
    }

    /**
     * @return Returns the part.
     */
    public PrimitiveShape getPart() {
        return part;
    }

    /**
     * @return Returns the pointOnLine.
     */
    public Point getPointOnLine() {
        return pointOnLine;
    }
    /**
     * @return Returns the coordinate the will/has been added.
     */
    public Coordinate getAddedCoord() {
        return addedCoord;
    }
    /**
     * @return Returns the shape.
     */
    public EditGeom getGeom() {
        return shape;
    }


    
    @Override
    public String toString() {
        StringBuffer buffer=new StringBuffer();
        buffer.append("distance:"); //$NON-NLS-1$
        buffer.append(distanceToEdge);
        buffer.append(", point:"); //$NON-NLS-1$
        buffer.append(pointOnLine);
        buffer.append(", previous:"); //$NON-NLS-1$
        buffer.append(indexOfPrevious);
        
        return buffer.toString();
    }
}