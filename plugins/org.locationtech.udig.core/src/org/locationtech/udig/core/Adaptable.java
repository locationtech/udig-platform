/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.core;

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
