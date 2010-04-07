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
package net.refractions.udig.ui.operations;

import net.refractions.udig.core.AdapterUtil;

/**
 * Default impl of OpFilter
 * @author jones
 * @since 1.1.0
 */
public class AdaptsToFilter implements OpFilter {

    private String targetClass;

    /**
     * @param targetClass Class that the object must resolve to.
     */
    public AdaptsToFilter( String targetClass) {
        this.targetClass=targetClass;
    }
    
    public boolean accept( Object object ) {
        return AdapterUtil.instance.canAdaptTo(targetClass, object);
    }

    public boolean canCacheResult() {
        return true;
    }

    public boolean isBlocking() {
        return false;
    }

    public void addListener( IOpFilterListener listener ) {
        // do nothing
    }

    public void removeListener( IOpFilterListener listener ) {
        // do nothing
    }

}
