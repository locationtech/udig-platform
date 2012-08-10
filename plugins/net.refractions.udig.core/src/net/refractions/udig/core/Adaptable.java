/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.core;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;


/**
 * Quick implementation of IAdaptable, may be subclassed if required.
 * <p>
 * Often used to convert a selection to an IAdaptable. Design of this class was
 * informed by AdaptableForwarder.
 * <p>
 * The implementation correctly delegates to {@link Platform#getAdapterManager} so
 * there is no need for further processing.
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.2
 */
public class Adaptable implements IAdaptable {
    protected Object element;
    public Adaptable( Object element ){
        if( element instanceof Adaptable ){
            Adaptable adaptor = (Adaptable) element;
            element = adaptor.element;
        }
        else {
            this.element = element;
        }
    }
    @SuppressWarnings("rawtypes") // rawtypes used by super class - we cannot fix
    public Object getAdapter(Class type) {
        if( type.isInstance( element ) ){
            return type.cast( element );
        }
        if (element instanceof IAdaptable){
            IAdaptable adaptable = (IAdaptable) element;
            Object adapter = adaptable.getAdapter( type );
            if( adapter != null ){
                return type.cast( adapter);
            }
        }
        // last ditch attempt!
        IAdapterManager manager = Platform.getAdapterManager();
        return manager.getAdapter( element, type );
    }
}