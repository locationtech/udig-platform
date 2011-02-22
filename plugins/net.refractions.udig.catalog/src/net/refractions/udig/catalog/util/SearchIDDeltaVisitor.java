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
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IResolveDeltaVisitor;
import net.refractions.udig.catalog.URLUtils;
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
public class SearchIDDeltaVisitor implements IResolveDeltaVisitor {
    private IResolveDelta found;
    private URL id;
    public SearchIDDeltaVisitor( URL id ) {
        this.id = id;
        found = null;
    }
    /** Find available parents if provided handle */
    static List<URL> path( IResolve handle ) {
        IResolve handle2=handle;
        LinkedList<URL> path = new LinkedList<URL>();
        while( handle2 != null ) {
            path.addFirst(handle2.getIdentifier());
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
        if (delta.getKind() == Kind.NO_CHANGE) {
            return true; // no match here visit children
        }
        URL here = delta.getResolve().getIdentifier();
        if (here == null)
            return true; // visit children

        if (URLUtils.urlEquals(here, id, false) || here.toExternalForm().startsWith(id.toExternalForm())) {
            found = delta;
            return false; // found - it we don't need to find children
        }
        return true;
    }
    /**
     * Quick method that uses this visitor to search an event.
     * <p>
     * This serves as a good example of using a visitor.
     * </p>
     */
    public static IResolveDelta search( URL id, IResolveChangeEvent event ) {
        if (id == null || event == null)
            return null;

        IResolveDelta delta = event.getDelta();
        if (delta == null)
            return null;

        SearchIDDeltaVisitor visitor = new SearchIDDeltaVisitor(id);
        try {
            delta.accept(visitor);
            return visitor.getFound();
        } catch (IOException e) {
            return null; // visitor obviously could not find anything
        }
    }
}
