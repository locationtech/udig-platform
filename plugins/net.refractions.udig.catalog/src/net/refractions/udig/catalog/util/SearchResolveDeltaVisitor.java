/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.catalog.util;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IResolveDeltaVisitor;
import net.refractions.udig.catalog.IResolveDelta.Kind;

/**
 * Easy of use visitor for search IResolveDeltas.
 * <p>
 * After a run found == Delta that best matches the provided handle.
 * <p>
 * Will be null if no matches were found that were interesting. Where: interesting != NO_CHANGE
 * </p>
 * <p>
 * You can use this code as an example of a good IResolveDeltaVisitor.
 * </p>
 * 
 * @author jgarnett
 * @since 0.6.0
 */
public class SearchResolveDeltaVisitor implements IResolveDeltaVisitor {
    private IResolveDelta found;
    private List<IResolve> path;

    public SearchResolveDeltaVisitor( IResolve handle ) {
        path = path(handle);
        found = null;
    }
    /** Find available parents if provided handle */
    static List<IResolve> path( IResolve handle ) {
        IResolve handle2=handle;

        LinkedList<IResolve> path = new LinkedList<IResolve>();
        while( handle2 != null ) {
            path.addFirst(handle2);
            try {
                handle2 = handle2.parent(null);
            } catch (IOException e) {
                handle2 = null; // no more parents
            }
        }
        return path;
    }
    /**
     * Best match IResolveDelta for handle, may be null if search came up empty.
     * 
     * @return Best match IResolveDelta for handle
     */
    public IResolveDelta getFound() {
        return found;
    }
    public boolean visit( IResolveDelta delta ) {
        if (delta.getKind() != Kind.NO_CHANGE && path.contains(delta.getResolve())) {
            found = delta;
        }
        return true;
    }
    /**
     * Quick method that uses this visitor to search an event.
     * <p>
     * This serves as a good example of using a visitor.
     * </p>
     */
    public static IResolveDelta search( IResolve handle, IResolveChangeEvent event ) {
        if (handle == null || event == null)
            return null;

        IResolveDelta delta = event.getDelta();
        if (delta == null)
            return null;

        SearchResolveDeltaVisitor visitor = new SearchResolveDeltaVisitor(handle);
        try {
            delta.accept(visitor);
            return visitor.getFound();
        } catch (IOException e) {
            return null; // visitor obviously could not find anything
        }
    }
}