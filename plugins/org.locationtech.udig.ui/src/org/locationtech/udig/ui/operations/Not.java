/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui.operations;

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

    @Override
    public String toString() {
        return "NOT "+filter;
    }
    
}
