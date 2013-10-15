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

import java.awt.Shape;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.tools.edit.support.EditBlackboardEvent.EventType;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Path;

/**
 * Extends {@link EditGeomPathIterator} so that 
 * @author jones
 * @since 1.1.0
 */
public class CurrentEditGeomPathIterator extends EditGeomPathIterator {
    enum State{
        waiting, onCurrent, done, onlyPointInShape;
    };
    private Point location;
    State currentState;
    private PrimitiveShape shape;

    protected CurrentEditGeomPathIterator( EditGeom shape ) {
        super(shape);
    }
    

    private static final Map<EditBlackboard, EditBlackboardListener> listeners=new HashMap<EditBlackboard, EditBlackboardListener>();

    private static final Map<EditGeom, CurrentEditGeomPathIterator> map=new HashMap<EditGeom, CurrentEditGeomPathIterator>();

    public static CurrentEditGeomPathIterator getPathIterator(EditGeom geom){
        CurrentEditGeomPathIterator iter=map.get(geom);
        if ( iter==null ){
            iter=new CurrentEditGeomPathIterator(geom);
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

    
    @Override
    public boolean isDone() {
        boolean superDone = super.isDone();
        
        
        return superDone && (currentState==State.done);
    }

    private void prepareToPath() {
        if( location==null )
            currentState=State.done;
        else if( shape.getNumPoints()==0 ){
           currentState=State.onlyPointInShape; 
        }else{
            currentState=State.waiting;
        }
    }
    
    
    @Override
    public Shape toShape() {
        prepareToPath();
        return super.toShape();
    }

    public Path toPath(Device device) {
        prepareToPath();
        return super.toPath(device);
    }
    
    @Override
    protected Shape createPoint() {
        return null;
    }
    
    @Override
    protected Path createPointPath(Device device) {
        return null;
    }
    
    @Override
    public void next() {
        if ( !super.isDone() ){
            super.next();   
            return;
        }
        if ( currentState==State.waiting )
            currentState=State.onCurrent;
        else
            currentState=State.done;
    }
    
    @Override
    public int currentSegment( float[] coords ) {
        
        if ( !super.isDone() )
            return super.currentSegment(coords);
        
        if( currentState==State.onCurrent || currentState==State.onlyPointInShape){
            coords[0]=location.getX();
            coords[1]=location.getY();
            if( currentState==State.onlyPointInShape ){
                return SEG_MOVETO;
            }
        }
        if ( currentState==State.done && isPolygon() )
            return SEG_CLOSE;

        return SEG_LINETO;
            
    }

    /**
     * @return Returns the location.
     */
    public Point getLocation() {
        return location;
    }

    /**
     * @param location The location to set.
     * @param shape if null then 
     */
    public void setLocation( Point location, PrimitiveShape shape ) {
        if( shape!=null){
            this.location = location;
            this.shape=shape;
        }else{
            this.location=null;
            this.shape=null;
        }
    }


}
