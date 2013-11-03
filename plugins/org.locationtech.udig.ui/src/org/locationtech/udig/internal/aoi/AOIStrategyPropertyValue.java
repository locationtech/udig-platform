/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.internal.aoi;

import org.locationtech.udig.aoi.AOIListener;
import org.locationtech.udig.aoi.IAOIService;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.operations.AbstractPropertyValue;
import org.locationtech.udig.ui.operations.IOpFilterListener;
import org.locationtech.udig.ui.operations.PropertyValue;

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
