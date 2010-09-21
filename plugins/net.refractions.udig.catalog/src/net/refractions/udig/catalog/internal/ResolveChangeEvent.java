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
package net.refractions.udig.catalog.internal;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveDelta;

/**
 * Everything change change change ...
 * 
 * @author jgarnett
 * @since 0.6.0
 */
public class ResolveChangeEvent implements IResolveChangeEvent {
    private Object source;
    private Type type;
    private IResolveDelta delta; // may be null for some events
    private IResolve handle; // may be null for some events

    /**
     * Construct <code>CatalogChangeEvent</code>.
     * 
     * @param source Source of event, in case you care
     * @param type Type constant from ICatalogChangeEvent
     * @param delta Describes the change
     */
    public ResolveChangeEvent( Object source, Type type, IResolveDelta delta ) {
        this.source = source;
        this.type = type;
        this.delta = delta;
        if (source instanceof IResolve) {
            handle = (IResolve) source;
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ResolveChangeEvent ("); //$NON-NLS-1$
        buffer.append(type);

        if (delta != null) {
            buffer.append(","); //$NON-NLS-1$
            buffer.append(delta);

        }
        if (handle != null) {
            buffer.append(","); //$NON-NLS-1$
            buffer.append(handle.getIdentifier());
        }

        return buffer.toString();
    }

    /**
     * @see net.refractions.udig.catalog.ICatalogChangeEvent#getDelta()
     */
    public IResolveDelta getDelta() {
        return delta;
    }

    /**
     * @see net.refractions.udig.catalog.ICatalogChangeEvent#getResource()
     */
    public IResolve getResolve() {
        return handle;
    }

    /**
     * @see net.refractions.udig.catalog.ICatalogChangeEvent#getSource()
     */
    public Object getSource() {
        return source;
    }

    /**
     * @see net.refractions.udig.catalog.ICatalogChangeEvent#getType()
     */
    public Type getType() {
        return type;
    }
}