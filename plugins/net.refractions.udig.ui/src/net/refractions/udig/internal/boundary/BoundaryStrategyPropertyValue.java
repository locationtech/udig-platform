package net.refractions.udig.internal.boundary;

import java.io.IOException;

import net.refractions.udig.boundary.IBoundaryService;
import net.refractions.udig.core.AdapterUtil;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.operations.AbstractPropertyValue;
import net.refractions.udig.ui.operations.IOpFilterListener;
import net.refractions.udig.ui.operations.PropertyValue;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class BoundaryStrategyPropertyValue extends AbstractPropertyValue
		implements PropertyValue {

    /** Watches the BoundaryService and will broadcast the name of the current strategy */
    Listener watcher = new Listener(){
        public void handleEvent( Event event ) {
            notifyListeners( event.data );
        }        
    };
    
	public BoundaryStrategyPropertyValue() {
	}

	@Override
	public boolean isTrue(Object object, String value) {
	    IBoundaryService boundaryService = PlatformGIS.getBoundaryService();
		String name = boundaryService.getProxy().getName();
        return name.equals(value);
	}

	@Override
	public boolean canCacheResult() {
		return false;
	}

	@Override
	public boolean isBlocking() {
		return false;
	}
    @Override
    public void addListener( IOpFilterListener listener ) {
        super.addListener(listener);
        if( listeners.size()==1){
            // this is the 1st listener we better watch the BoundaryService for change!
            IBoundaryService boundaryService = PlatformGIS.getBoundaryService();
            boundaryService.addListener( watcher );
        }
    }
    @Override
    public void removeListener( IOpFilterListener listener ) {
        super.removeListener(listener);
        if( listeners.isEmpty() ){
            // stop watching the boundary service
            IBoundaryService boundaryService = PlatformGIS.getBoundaryService();
            boundaryService.removeListener( watcher );
        }
    }
}
