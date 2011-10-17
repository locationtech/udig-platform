/**
 * 
 */
package net.refractions.udig.internal.boundary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.boundary.BoundaryProxy;
import net.refractions.udig.boundary.IBoundaryService;
import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;
import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
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
    
    /** This is the boundary extension point processed to get BoundaryStrategy entries */
    private static final String EXT_ID = "net.refractions.udig.ui.boundary";

    /**
     * the id of the all strategy (ie the default)
     */
    public static final String STRATEGY_ALL_ID = "net.refractions.udig.ui.boundaryAll";
    
    /*
     * A list of all the strategies
     */
    protected List<BoundaryProxy> proxyList = new ArrayList<BoundaryProxy>();

    protected Listener watcher = new Listener(){
        public void handleEvent( Event event ) {
            notifyListeners(event);
        }
    };
    /*
     * A list of listeners to be notified when the Strategy changes
     */
    protected Set<Listener> listeners = new CopyOnWriteArraySet<Listener>();
    
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
    
    private BoundaryProxy currentStrategy;

    public BoundaryServiceImpl() {
        
        // process the extension point here to get the list of Strategies
        ExtensionPointProcessor processBoundaryItems = new ExtensionPointProcessor(){
            @Override
            public void process( IExtension extension, IConfigurationElement element ) throws Exception {
               BoundaryProxy proxy = new BoundaryProxy(element);
               proxyList.add(proxy);
               /*String className = element.getAttribute("class");
               if( currentClassName != null && currentClassName.equals( className )){
                   initialStrategy = strategy;
               }*/
            }
        };
        ExtensionPointUtil.process( UiPlugin.getDefault(), EXT_ID,  processBoundaryItems );

        this.setStrategy(this.getDefault());
    }

    @Override
    public ReferencedEnvelope getExtent() {
        return this.currentStrategy.getExtent();
    }

    @Override
    public void setStrategy( BoundaryProxy strategy ) {
        if( this.currentStrategy == strategy ){
            return; // no change
        }
        if( this.currentStrategy != null ){
            this.currentStrategy.removeListener(watcher);
        }
        this.currentStrategy = strategy;
        if( this.currentStrategy != null ){
            this.currentStrategy.addListener(watcher);
        }
        notifyListeners(strategy.getName());
    }

    @Override
    public Geometry getGeometry() {
        return this.currentStrategy.getGeometry();
    }

    @Override
    public CoordinateReferenceSystem getCrs() {
        return this.currentStrategy.getCrs();
    }

    @Override
    public BoundaryProxy getProxy() {
        return this.currentStrategy;
    }
    
    @Override
    public BoundaryProxy getDefault() {
        BoundaryProxy test = this.findProxy(STRATEGY_ALL_ID);
        return this.findProxy(STRATEGY_ALL_ID);
    }

    @Override
    public List<BoundaryProxy> getProxyList() {
        return Collections.unmodifiableList( proxyList );
    }

    @Override
    public BoundaryProxy findProxy( String id ) {
        for (BoundaryProxy boundaryProxy: proxyList) {
            if (boundaryProxy.getId().equals(id)) {
                return boundaryProxy;
            }
        }
        return null;
    }
}
