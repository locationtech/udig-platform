/**
 * 
 */
package net.refractions.udig.internal.boundary;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.boundary.IBoundaryService;
import net.refractions.udig.boundary.IBoundaryStrategy;
import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.ui.operations.IOpFilterListener;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This is the default implementation of BoundaryService; it delegates to the internal strategy
 * object.
 * 
 * @author pfeiffp
 */
public class BoundaryServiceImpl implements IBoundaryService {
    protected Set<Listener> listeners=new CopyOnWriteArraySet<Listener>();
    
    @Override
    public void addListener( Listener listener ) {
        if( listener == null ){
            throw new NullPointerException("BoundaryService listener required to be non null");
        }
        listeners.add(listener);
    }

    @Override
    public void removeListener( Listener listener ) {
        listeners.add(listener);
    }
    
    /**
     * Notifies listener that the value of the filter has changed.
     */
    private void notifyListeners(Object changed) {
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
    
    private IBoundaryStrategy boundaryStrategy;

    public BoundaryServiceImpl() {
        this.boundaryStrategy = new BoundaryStrategyAll();
        // process the extension point here to get the list of 
    }

    @Override
    public ReferencedEnvelope getExtent() {
        return this.boundaryStrategy.getExtent();
    }

    @Override
    public void setStrategy( IBoundaryStrategy boundaryStrategy ) {
        this.boundaryStrategy = boundaryStrategy;
        notifyListeners(boundaryStrategy.getName());
    }

    @Override
    public Geometry getBoundary() {
        return this.boundaryStrategy.getBoundary();
    }

    @Override
    public CoordinateReferenceSystem getCrs() {
        return this.boundaryStrategy.getCrs();
    }

    @Override
    public IBoundaryStrategy getCurrentStrategy() {
        return this.boundaryStrategy;
    }
    
    @Override
    public IBoundaryStrategy getDefault() {
        return new BoundaryStrategyAll();
    }
}
