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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.locationtech.udig.tools.edit.EditPlugin;

import org.locationtech.jts.geom.Envelope;

/**
 * Holds onto an geometry for editing, offering up PrimitiveShapes.
 * 
 * 
 * Models a geometry as required by the PixelCoordMap (what is PixelCoordMap?)
 * 
 * @author jones
 * @since 1.1.0
 */
public class EditGeom implements Iterable<PrimitiveShape> {

    private final PrimitiveShape shell;
    private final List<PrimitiveShape> holes = Collections
            .synchronizedList(new ArrayList<PrimitiveShape>());
    EditBlackboard owner;
    private final AtomicReference<ShapeType> shapeType = new AtomicReference<ShapeType>();
    {
        shapeType.set(ShapeType.UNKNOWN);
    }
    private final AtomicReference<String> featureID = new AtomicReference<String>();
    private AtomicReference<Boolean> changed = new AtomicReference<Boolean>();
    boolean initializing;
    
    /**
     * The latest up-to-date bounds of the feature in datastore.
     */
    private Envelope featureBBox = null;

    /**
     * It is recommended to use constructor with bounds.
     * 
     * @param owner
     * @param featureId2
     */
    public EditGeom( EditBlackboard owner, String featureId2 ) {
        this.owner = owner;
        shell = new PrimitiveShape(this);
        this.featureID.set(featureId2);
        changed.set(false);
    }
    
    /**
     * 
     * 
     * @param owner
     * @param featureId
     * @param featureBounds
     */
    public EditGeom( EditBlackboard owner, String featureId, Envelope featureBounds ) {
        this.owner = owner;
        shell = new PrimitiveShape(this);
        this.featureID.set(featureId);
        changed.set(false);
        this.featureBBox = featureBounds;
    }

    public EditGeom( EditGeom geom ) {
        shell = new PrimitiveShape(geom.getShell());
        for( PrimitiveShape hole : geom.getHoles() ) {
            holes.add(new PrimitiveShape(hole));
        }
        owner = geom.owner;
    }

    public PrimitiveShape getShell() {
        return shell;
    }
    /**
     * User is expected to add holes as needed.
     * 
     * @return
     */
    public List<PrimitiveShape> getHoles() {
        return holes;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(featureID.get());
        buffer.append(" "); //$NON-NLS-1$
        buffer.append(shapeType);
        buffer.append(" "); //$NON-NLS-1$
        buffer.append(shell.toString());
        buffer.append("{"); //$NON-NLS-1$        
        for( int i = 0, numHoles = this.holes.size(); i < numHoles; i++ ) {
            buffer.append(holes.get(i).toString());
        }
        buffer.append("}"); //$NON-NLS-1$
        return buffer.toString();
    }

    public PrimitiveShape newHole() {
        PrimitiveShape hole = new PrimitiveShape(this);
        hole.type="HOLE"; //$NON-NLS-1$
        holes.add(hole);
        return hole;
    }

    /**
     * @return Returns the featureID.
     */
    public AtomicReference<String> getFeatureIDRef() {
        return featureID;
    }

    /**
     * @return Returns the shape type.
     */
    public ShapeType getShapeType() {
        return shapeType.get();
    }

    /**
     * This is a thread-safe method
     * 
     * @param type The new shape type.
     */
    public void setShapeType( ShapeType shapeType ) {
        this.shapeType.set(shapeType);
    }

    public Iterator<PrimitiveShape> iterator() {
        return new Iterator<PrimitiveShape>(){

            boolean shellAccessed = false;
            Iterator<PrimitiveShape> holeIter = holes.iterator();
            public boolean hasNext() {
                return !shellAccessed || holeIter.hasNext();
            }

            public PrimitiveShape next() {
                if (!shellAccessed) {
                    shellAccessed = true;
                    return shell;
                }

                return holeIter.next();
            }

            public void remove() {
                throw new UnsupportedOperationException("Remove not allowed"); //$NON-NLS-1$
            }

        };
    }

    /**
     * @return Returns the owner.
     */
    public EditBlackboard getEditBlackboard() {
        return owner;
    }
    
    /**
     * Returns an old bounding box of the feature whose geometry is wrapped by EditGeom
     * before any editing.
     * 
     * @return 
     *      Returns an old bounding box of the feature
     */
    public Envelope getFeatureEnvelope(){
        return featureBBox;
    }

    /**
     * Indicates that this geometry has been modified since it has been in the blackboard.
     * 
     * @return true if the geometry has been modified while on the blackboard.
     */
    public boolean isChanged() {
        return changed.get();
    }

    /**
     * This method signals the in-memory state of EditGeom with respect to the actual
     * feature in datastore.
     * <p>
     *  <li><code>true</code> value means that the geometry is changed but not up-to-date
     *  with datastore's feature geometry.
     *  <li><code>false</code> value means that the geometry is up-to-date
     *  with datastore's feature geometry.
     * 
     * 
     * 
     * @param changed
     */
    public void setChanged( boolean changed ) {
        if(this.changed.get() && !changed){
          synchronized(owner){
              Envelope envelope = new Envelope();
              for( int i = 0; i < shell.getNumCoords(); i++ ) {
                  envelope.expandToInclude(shell.getCoord(i));
              }
              featureBBox = envelope;
          }
        }
        
        this.changed.set(changed);

    }
    public Selection createSelection() {
        return new EditGeomSelection(this);
    }
    public void assertValid() {
        if( !EditPlugin.isDebugging(EditPlugin.RUN_ASSERTIONS))
            return;
        for( PrimitiveShape shape : this ) {
            shape.assertValid();
        }
    }

    public boolean hasVertex( Point point ) {
        if( shapeType.get()==ShapeType.POLYGON ){
            for( PrimitiveShape shape : this ) {
                if( shape.hasVertex(point) )
                    return true;
            }
            return false;
        }else{
            return shell.hasVertex(point);
        }
    }

    /**
     * Gets the closest edge in the geometry to the point.
     *
     * @param point reference point
     * @param treatUnknownAsPolygon declares whether to treat geometries of type UNKNOWN as a polygon 
     * @return
     */
    public ClosestEdge getClosestEdge( Point point, boolean treatUnknownAsPolygon ) {
        assert shapeType.get()!=ShapeType.POINT;
        ClosestEdge closest = shell.getClosestEdge(point, treatUnknownAsPolygon);
        if( shapeType.get()==ShapeType.POLYGON  || 
                (shapeType.get()== ShapeType.UNKNOWN && treatUnknownAsPolygon) ){
            
            if( shell.contains(point, treatUnknownAsPolygon) ){
                for( PrimitiveShape shape : holes ) {
                    ClosestEdge current = shape.getClosestEdge(point, treatUnknownAsPolygon);
                    if ( closest==null )
                        closest=current;
                    else if( current!=null && current.distanceToEdge<closest.distanceToEdge )
                        closest=current;
                }
            }
        }
        return closest;
    }

    public Point overVertex( Point point, int radius ) {
        for( PrimitiveShape shape : this ) {
            Point p=shape.overVertex(point, radius);
            if( p!=null )
                return p;
        }
        return null;
    }

}
