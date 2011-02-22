/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.catalog.objectproperty;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveChangeListener;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IResolveChangeEvent.Type;
import net.refractions.udig.ui.operations.AbstractPropertyValue;

public class ObjectPropertyCatalogListener implements IResolveChangeListener {

    Object evaluationObject;
    IGeoResource resource;
    private AbstractPropertyValue<? extends Object> owner;
    private AtomicBoolean isEvaluating;

    public ObjectPropertyCatalogListener( Object layer, IGeoResource resource,
            AtomicBoolean isEvaluating, AbstractPropertyValue<? extends Object> owner ) {
        this.evaluationObject = layer;
        this.resource = resource;
        this.isEvaluating=isEvaluating;
        this.owner=owner;
    }

    public void changed( IResolveChangeEvent event ) {
        if( isEvaluating.get() )
            return;
        if( event.getType() == Type.POST_CHANGE ){
            if( event.getResolve().getIdentifier().equals(resource.getIdentifier()) ){
                if( isEvaluating.get() )
                    return;
                owner.notifyListeners(evaluationObject);
            }else{
                process(event.getDelta());
            }
        }
    }

    private boolean process( IResolveDelta delta ) {
        if( isEvaluating.get() )
            return true;

        if( delta.getResolve().getIdentifier().equals( resource.getIdentifier() ) ){
            owner.notifyListeners(evaluationObject);
            return true;
        }

        List<IResolveDelta> children = delta.getChildren();

        for( IResolveDelta delta2 : children ) {
            if( isEvaluating.get() )
                return true;

            if( process(delta2) )
                return true;
        }
        return false;
    }

}
