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
package net.refractions.udig.internal.aoi;

import net.refractions.udig.aoi.AOIListener;
import net.refractions.udig.aoi.IAOIService;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.operations.AbstractPropertyValue;
import net.refractions.udig.ui.operations.IOpFilterListener;
import net.refractions.udig.ui.operations.PropertyValue;

/**
 * This code advertises the name of the current AOI (Area of Interest) strategy so that tools
 * and operations can be enabled / disable as required.
 */
public class AOIStrategyPropertyValue extends AbstractPropertyValue<Object>
		implements PropertyValue<Object> {

    /** Watches the AOIService and will broadcast the name of the current strategy */
    AOIListener watcher = new AOIListener(){
        public void handleEvent( AOIListener.Event event ) {
            // notify people that are watching the property
            // that the value persisted is now changed
            // (the value is the name of the current IAOIStrategy)
            //
            notifyListeners( event );
        }
    };
    
	public AOIStrategyPropertyValue() {
	}

	/**
	 * This is used to compare the provided value to the current AOIService.
	 * <p>
	 * As an example the "AOI" property is checked with "Screen".
	 */
	@Override
	public boolean isTrue(Object object, String value) {
	    IAOIService aOIService = PlatformGIS.getAOIService();
		String name = aOIService.getProxy().getName();
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
            // this is the 1st listener we better watch the AOIService for change!
            IAOIService aOIService = PlatformGIS.getAOIService();
            aOIService.addListener( watcher );
        }
    }
    @Override
    public void removeListener( IOpFilterListener listener ) {
        super.removeListener(listener);
        if( listeners.isEmpty() ){
            // stop watching the AOI service
            IAOIService aOIService = PlatformGIS.getAOIService();
            aOIService.removeListener( watcher );
        }
    }
}
