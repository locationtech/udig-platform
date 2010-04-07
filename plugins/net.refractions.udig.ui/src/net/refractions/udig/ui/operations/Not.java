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

/**
 * Represents the Not element in the operation extension point.
 * @author jones
 * @since 1.1.0
 */
public class Not implements OpFilter {

    private OpFilter filter;

    public Not(OpFilter filter) {
        this.filter=filter;
    }
    
    public boolean accept( Object object ) {
        return !filter.accept(object);
    }


    public void addListener( IOpFilterListener listener ) {
            filter.addListener(listener);
    }

    public boolean canCacheResult() {
        return filter.canCacheResult();
    }

    public boolean isBlocking() {
        return filter.isBlocking();
    }

    public void removeListener( IOpFilterListener listener ) {
        filter.removeListener(listener);
    }


    
}
