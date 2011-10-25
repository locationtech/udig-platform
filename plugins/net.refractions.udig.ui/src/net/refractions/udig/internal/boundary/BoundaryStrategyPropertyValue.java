/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
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
package net.refractions.udig.internal.boundary;

import net.refractions.udig.boundary.BoundaryListener;
import net.refractions.udig.boundary.IBoundaryService;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.operations.AbstractPropertyValue;
import net.refractions.udig.ui.operations.IOpFilterListener;
import net.refractions.udig.ui.operations.PropertyValue;

/**
 * This code advertises the name of the current boundary strategy so that tools
 * and operations can be enabled / disable as required.
 */
public class BoundaryStrategyPropertyValue extends AbstractPropertyValue<Object>
		implements PropertyValue<Object> {

    /** Watches the BoundaryService and will broadcast the name of the current strategy */
    BoundaryListener watcher = new BoundaryListener(){
        public void handleEvent( BoundaryListener.Event event ) {
            // notify people that are watching the property
            // that the value persisted is now changed
            // (the value is the name of the current IBoundaryStrategy)
            //
            notifyListeners( event.source.getName() );
        }
    };
    
	public BoundaryStrategyPropertyValue() {
	}

	/**
	 * This is used to compare the provided value to the current BoundaryService.
	 * <p>
	 * As an example the "boundary" property is checked with "Screen".
	 */
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
