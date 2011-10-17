package net.refractions.udig.boundary;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Defines the changing functionality of the Boundary service.
 * 
 * @author pfeiffp
 */
public abstract class IBoundaryStrategy {

    /**
     * Returns the extent Should return an empty envelope for an "All" extent
     * 
     * @return ReferencedEnvelope
     */
    public abstract ReferencedEnvelope getExtent();

    /**
     * Returns a geometry of the current boundary selected
     * 
     * @return Geometry
     */
    public abstract Geometry getGeometry();

    /**
     * Returns the CRS of the current Boundary selected
     * 
     * @return
     */
    public abstract CoordinateReferenceSystem getCrs();

    /**
     * Returns the name of the Boundary strategy. This is used when adding to the combo to select
     * from.
     * 
     * @return String
     */
    public abstract String getName();

    /*
     * A list of listeners to be notified when the Strategy changes
     */
    protected Set<Listener> listeners = new CopyOnWriteArraySet<Listener>();
    
    /**
     * Allows notification of changes to the boundary represented by the
     * strategy. This is used for dynamic boundaries that change over time
     * (perhaps in response to a user using a tool to specifiy a new
     * clipping area).
     * <p>
     * The BoundaryServiceImpl will register a single listener in order
     * to track what is going on.
     * 
     * @param listener
     */
    public void addListener( Listener listener ) {
        if( listener == null ){
            throw new NullPointerException("BoundaryService listener required to be non null");
        }
        if( !listeners.contains(listener)){
            listeners.add(listener);
        }
    }
    /**
     * Remove a listener for boundary chages.
     * 
     * @param listener
     */
    public void removeListener( Listener listener ) {
        listeners.remove(listener);
    }
    
    /**
     * Notifies listener that the value of the filter has changed.
     */
    protected void notifyListeners(Object changed) {
        Event event = null;
        for( Listener listener : listeners ) {
            if( event == null ){
                event = new Event();
                event.data = changed;
            }
            try {
                if( listener != null ){
                    listener.handleEvent( event );
                }
            } catch (Exception e) {
                e.printStackTrace();
                UiPlugin.trace(UiPlugin.ID, listener.getClass(), e.getMessage(), e );
            }
        }
    }
}
