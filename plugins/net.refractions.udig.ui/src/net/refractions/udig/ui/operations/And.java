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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Not element in the operation extension point.
 * @author jones
 * @since 1.1.0
 */
public class And implements OpFilter {
    private List<OpFilter> filters=new ArrayList<OpFilter>();
    
    /**
     * @return Returns the filters.
     */
    public List<OpFilter> getFilters() {
        return filters;
    }

    public boolean accept( Object object ) {
        for( OpFilter filter : filters ) {
            if( !filter.accept(object) )
                return false;
        }
        return true;
    }

    public void addListener( IOpFilterListener listener ) {
        for( OpFilter filter : filters ) {
            filter.addListener(listener);
        }
    }

    public boolean canCacheResult() {
        for( OpFilter filter : filters ) {
            if( !filter.canCacheResult() )
                return false;
        }

        return true;
    }

    public boolean isBlocking() {
        for( OpFilter filter : filters ) {
            if( filter.isBlocking() )
                return true;
        }

        return false;
    }

    public void removeListener( IOpFilterListener listener ) {
        for( OpFilter filter : filters ) {
            filter.removeListener(listener);
        }
    }

}
