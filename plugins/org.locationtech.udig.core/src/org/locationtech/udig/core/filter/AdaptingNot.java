/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.core.filter;

import org.opengis.filter.Filter;
import org.opengis.filter.Not;

/**
 * AdaptingFilter that implements Not interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingNot extends AdaptingFilter<Not> implements Not {

    AdaptingNot( Not filter ) {
        super(filter);
    }

    public Filter getFilter() {
        return wrapped.getFilter();
    }

}
