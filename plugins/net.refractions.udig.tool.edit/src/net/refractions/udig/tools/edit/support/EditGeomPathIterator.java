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

import net.refractions.udig.tools.edit.support.EditBlackboardEvent.EventType;


/**
 * Wraps a {@link net.refractions.udig.tools.edit.support.EditGeom} so that it can be drawn to a
 * Graphics2d object.
 * 
 * @author jones
 * @since 1.1.0
 */
public class EditGeomPathIterator extends AbstractPathIterator {

    protected boolean isPolygon;

    protected EditGeomPathIterator( EditGeom shape ) {
        super( shape );
    }
    
    public void dispose() {
    }
    
    private static final Map<EditBlackboard, EditBlackboardListener> listeners=new HashMap<EditBlackboard, EditBlackboardListener>();

    private static final Map<EditGeom, EditGeomPathIterator> map=new HashMap<EditGeom, EditGeomPathIterator>();

    public static EditGeomPathIterator getPathIterator(EditGeom geom){
        EditGeomPathIterator iter=map.get(geom);
        if ( iter==null ){
            iter=new EditGeomPathIterator(geom);
            EditBlackboardListener listener = listeners.get(geom.getEditBlackboard());
            if( listener==null ){
                listener=new EditBlackboardAdapter(){
                    @SuppressWarnings("unchecked")
                    @Override
                    public void changed( EditBlackboardEvent event ) {
                        if( event.getType()==EventType.REMOVE_GEOMS ){
                            List<EditGeom> geoms=(List<EditGeom>) event.getOldValue();
                            for( EditGeom geom: geoms ) {
                                map.remove(geom);
                            }
                        }
                    }
                    
                    @Override
                    public void batchChange( List<EditBlackboardEvent> e ) {
                        for( EditBlackboardEvent event : e ) {
                            changed(event);
                        }
                    }
                };
                listeners.put(geom.getEditBlackboard(), listener );
            }
            geom.getEditBlackboard().getListeners().add( listener );
            map.put(geom, iter);
        }
        return iter;
    }

    
    public int currentSegment( float[] coords ) {

        int result = super.currentSegment(coords);

        if( (isPolygon || geom.getShapeType()==ShapeType.POLYGON) && result!=SEG_MOVETO ){

            if( !points.hasNext()  && isClosed(currentShape) ){
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
