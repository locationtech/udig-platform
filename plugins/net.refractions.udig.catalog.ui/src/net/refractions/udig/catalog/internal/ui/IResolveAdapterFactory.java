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
package net.refractions.udig.catalog.internal.ui;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.core.IBlockingAdaptable;

import org.eclipse.core.runtime.IAdapterFactory;

/**
 * AdapterFactory that adapts IResolves to IAdaptables
 * <p>
 * Please be aware that although this will allow getAdapter to negotiate
 * the IResolve methods; the result uses IBlocableAdapter and actually
 * performs IO resulting in the possibility of long delays and IOExceptions.
 * 
 * @author jeichar
 * @since 0.9
 */
public class IResolveAdapterFactory implements IAdapterFactory {

    /**
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
     */
    @SuppressWarnings("rawtypes")
    public Object getAdapter( Object adaptableObject, Class adapterType ) {
        if (IBlockingAdaptable.class.isAssignableFrom(adapterType)
                && adaptableObject instanceof IResolve)
            return new IResolveAdaptable((IResolve) adaptableObject);
        return null;
    }

    /**
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
     */
    @SuppressWarnings("rawtypes")
    public Class[] getAdapterList() {
        return new Class[]{IBlockingAdaptable.class};
    }

}
