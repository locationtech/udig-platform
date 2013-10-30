/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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