/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
    @Override
    public String toString() {
        StringBuilder build = new StringBuilder();
        build.append("AND (");
        for( OpFilter filter : filters ) {
            build.append( filter );
            build.append(",");
        }
        build.setCharAt(build.length()-1, ')');
        return build.toString();
    }

}
