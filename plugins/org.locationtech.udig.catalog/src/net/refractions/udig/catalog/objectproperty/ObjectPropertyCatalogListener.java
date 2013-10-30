/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.objectproperty;

import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveChangeListener;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.util.SearchIDDeltaVisitor;
import net.refractions.udig.ui.operations.AbstractPropertyValue;

/**
 * IResolveChangeListener that will watch a single GeoResource for changes and pass them over to the
 * PropertyValue system used to enable and disable operations.
 * <p>
 * This class is a bridge between Catalog event notifications and PropertyValue notifications (ie
 * true or false). It would be nice to express all this stuff as "core expressions" as introduced in
 * the recent versions of the RCP; as right now this notification is only limited to Operations.
 * <p>
 */
public class ObjectPropertyCatalogListener implements IResolveChangeListener {

    Object evaluationObject;

    /**
     * This is the IGeoResource we are "watching", note we are only going to compare the identifier
     * since IGeoResource is a "handle" (ie transient data object).
     * <p>
     * We may of discovered this GeoResource using a search (and gotten back on instance), but want
     * to watch for changes in the local catalog (another instance) - so the check based on
     * identifier really is needed.
     */
    private URL resourceId;

    /**
     * The PropertyValue (ie true / false) used to enable or distable user interface stuff.
     */
    private AbstractPropertyValue< ? extends Object> owner;
    private AtomicBoolean isEvaluating;

    public ObjectPropertyCatalogListener( Object layer, IGeoResource resource,
            AtomicBoolean isEvaluating, AbstractPropertyValue< ? extends Object> owner ) {
        this.evaluationObject = layer;
        this.resourceId = resource.getIdentifier();
        this.isEvaluating = isEvaluating;
        this.owner = owner;
    }
    /**
     * Accept a change notification from catalog - we will process if our resource was modified.
     */
    public void changed( IResolveChangeEvent event ) {
        if (isEvaluating.get()){
            return;
        }
        IResolveDelta found = SearchIDDeltaVisitor.search( resourceId, event);
        if( found != null ){
            owner.notifyListeners(evaluationObject);
        }
        // Original code that only noticed changes *on* the IResolve, not the parent
        /*
        if (event.getType() == Type.POST_CHANGE) {
            if (event.getResolve().getIdentifier().equals(resource.getIdentifier())) {
                if (isEvaluating.get()) {
                    return;
                }
                owner.notifyListeners(evaluationObject);
            } else {
                 process(event.getDelta());
            }
        }
        */
    }

    /**
     * Internal method used to kick the associated PropertyValue.
     * <p>
     * The PropertyValue will calculate a new true or false value
     * depending on what it wants to do.
     *
     * @param delta Catalog change delta
     * @return true if our resource was updated, false if this delta did not concern us
     */
    /*
    private boolean process( IResolveDelta delta ) {
        if (isEvaluating.get()){
            // we are already busy
            return true;
        }

        if (delta.getResolve().getIdentifier().equals(resourceId)) {
            // we are directly indicated
            owner.notifyListeners(evaluationObject);
            return true;
        }

        // Go through all the changed children and check ...
        // this code is easier to understand when written with
        // a visitor rather than a for loop
        List<IResolveDelta> children = delta.getChildren();
        for( IResolveDelta delta2 : children ) {
            if (isEvaluating.get()){
                // we are already busy - is not threaded code fun?
                return true;
            }
            if (process(delta2)){
                return true;
            }
        }
        return false;
    }
    */

}