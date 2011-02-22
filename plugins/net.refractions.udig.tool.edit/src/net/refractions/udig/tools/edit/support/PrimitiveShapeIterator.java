/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PathIterator that wraps a primitive shape.
 *
 * @author jones
 * @since 1.1.0
 */
public class PrimitiveShapeIterator extends AbstractShapeIterator {

    private static final Map<PrimitiveShape, PrimitiveShapeIterator> map=new HashMap<PrimitiveShape, PrimitiveShapeIterator>();
    private static EditBlackboardListener bbListener=new EditBlackboardAdapter(){
        @SuppressWarnings("unchecked")
        @Override
        public void changed( EditBlackboardEvent event ) {
            switch( event.getType() ) {
            case CLEARED:
                ((EditBlackboard) event.getSource()).getListeners().remove(this);
                map.clear();
                break;
            case REMOVE_GEOMS:
                List<EditGeom> geoms=(List<EditGeom>) event.getOldValue();
                for( EditGeom geom: geoms ) {
                    map.remove(geom);
                }

                if( map.isEmpty() ){
                    ((EditBlackboard) event.getSource()).getListeners().remove(this);
                }
                break;
            default:
                break;
            }
        }

        @Override
        public void batchChange( List<EditBlackboardEvent> e ) {
            for( EditBlackboardEvent event : e ) {
                changed(event);
            }
        }
    };
    private boolean isPolygon;

    protected PrimitiveShapeIterator( PrimitiveShape shape ) {
        super(shape);
    }

    public static PrimitiveShapeIterator getPathIterator(PrimitiveShape shape){
        PrimitiveShapeIterator iter=map.get(shape);
        if ( iter==null ){
            iter=new PrimitiveShapeIterator(shape);
            shape.getEditBlackboard().getListeners().add( bbListener );
            map.put(shape, iter);
            iter.reset();
        }else{
            iter.reset();
        }
        return iter;
    }


    public int currentSegment( float[] coords ) {

        int result = super.currentSegment(coords);

        if( (isPolygon || shape.getEditGeom().getShapeType()==ShapeType.POLYGON) && result!=SEG_MOVETO ){

            if( !points.hasNext()  && isClosed(shape) ){
                result=SEG_CLOSE;
            }
        }

        return result;

    }

    // returns true if shape is closed
    protected boolean isClosed(PrimitiveShape shape) {
        return shape.getPoint(0).equals(shape.getPoint(shape.getNumPoints()-1));
    }

    public void setPolygon( boolean isPolygon ) {
        this.isPolygon=isPolygon;
    }

    /**
     * @return Returns the isPolygon.
     */
    public boolean isPolygon() {
        return isPolygon;
    }
}
