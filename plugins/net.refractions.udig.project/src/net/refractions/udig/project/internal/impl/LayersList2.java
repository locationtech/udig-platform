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
package net.refractions.udig.project.internal.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.interceptor.InterceptorSupport;
import net.refractions.udig.project.interceptor.LayerInterceptor;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.Trace;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * Wraps a EList and makes sure that when a layer is added the layer interceptor is fired and a deep
 * adapter is added.
 * <p>
 * This is used to go from an object (Map or ContextModel) to a list of layers; and set up the inverse relationship
 * so that layer.getMap() works.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class LayersList2 extends SynchronizedEObjectWithInverseResolvingEList<Layer> {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 4584718175140573610L;

    private Collection<Adapter> deepAdapters = new CopyOnWriteArraySet<Adapter>();

    public LayersList2( Class<Layer> dataClass, InternalEObject owner, int featureID, int inverseFeatureID ) {
        super(dataClass, owner, featureID, inverseFeatureID);
    }

    /**
     * adds the adapter to all layers and the context model
     * 
     * @param adapter adapter to add to all layers.
     */
    public void addDeepAdapter( Adapter adapter ) {
        deepAdapters.add(adapter);
        if (!owner.eAdapters().contains(adapter))
            owner.eAdapters().add(adapter);
        for( Object object : this ) {
            Layer layer = (Layer) object;
            if (!layer.eAdapters().contains(adapter))
                layer.eAdapters().add(adapter);
        }
    }

    /**
     * removes the adapter from all layers.
     * 
     * @param toRemove
     */
    public void removeDeepAdapter( Adapter toRemove ) {
        deepAdapters.remove(toRemove);
        owner.eAdapters().remove(toRemove);
        for( Object object : this ) {
            ((Layer) object).eAdapters().remove(toRemove);
        }
    }

    @Override
    protected void didAdd( int index, Layer newObject ) {
        super.didAdd(index, newObject);
    }

    @Override
    public NotificationChain inverseAdd( Layer object, NotificationChain notifications ) {
        NotificationChain notificationChain = super.inverseAdd(object, notifications);
        if (ProjectPlugin.isDebugging(Trace.MODEL))
            ProjectPlugin
                    .trace(
                            getClass(),
                            "inverseAdd() = " + ((Layer) object).getID() + " to map " + getMap().getName(), null); //$NON-NLS-1$ //$NON-NLS-2$

        runAddInterceptors(object);
        return notificationChain;
    }

    @Override
    protected Layer assign( int index, Layer object ) {
        if (!(object instanceof Layer))
            throw new AssertionError("Can only add " + Layer.class.getName() + " to a map.  Was: " //$NON-NLS-1$ //$NON-NLS-2$
                    + object.getClass().getName());

        if (ProjectPlugin.isDebugging(Trace.MODEL))
            ProjectPlugin
                    .trace(
                            getClass(),
                            "Adding " + ((Layer) object).getID() + " to map " + getMap().getName() + " at location: " + index, null); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
        Layer object2 = super.assign(index, object);
        if (ProjectPlugin.isDebugging(Trace.MODEL))
            ProjectPlugin.trace(getClass(), "Resulting list=" + this, null); //$NON-NLS-1$

        return object2;
    }
    /**
     * Used to look up the map when describing events during tracing 
     * @return current map
     */
    private IMap getMap() {
        if (owner instanceof IMap)
            return (IMap) owner;
        if (owner.eContainer() instanceof IMap)
            return (IMap) owner.eContainer();
        throw new IllegalStateException("Owner should be IMap or ContextModel"); //$NON-NLS-1$
    }

    @Override
    protected Layer doRemove( int index ) {
        Object toRemove = get(index);
        runRemoveInterceptor(toRemove);

        if (ProjectPlugin.isDebugging(Trace.MODEL))
            ProjectPlugin
                    .trace(
                            getClass(),
                            "Removing " + ((Layer) toRemove).getID() + " from map " + getMap().getName(), null); //$NON-NLS-1$ //$NON-NLS-2$

        return super.doRemove(index);
    }

    @Override
    protected void doClear() {
        Object[] toRemove = toArray();
        removeAllInterceptors(Arrays.asList(toRemove));

        if (ProjectPlugin.isDebugging(Trace.MODEL))
            ProjectPlugin.trace(getClass(),
                    "Removing all layers from map:" + getMap().getName(), null); //$NON-NLS-1$

        super.doClear();
    }

    private void removeAllInterceptors( Collection<?> c ) {
        for( Iterator<?> iter = c.iterator(); iter.hasNext(); ) {
            Layer element = (Layer) iter.next();
            InterceptorSupport.runLayerInterceptor(element, "layerRemoved"); //$NON-NLS-1$
            element.eAdapters().removeAll(deepAdapters);
        }
    }

    private void runAddInterceptors( Object element ) {
        Layer layer = (Layer) element;
        for( Adapter deepAdapter : deepAdapters ) {
            if (!layer.eAdapters().contains(deepAdapter))
                layer.eAdapters().add(deepAdapter);
        }
        InterceptorSupport.runLayerInterceptor(layer, LayerInterceptor.ADDED_ID);
    }

    private void runRemoveInterceptor( Object remove ) {
        if (remove == null || !contains(remove))
            return;
        Layer layer = (Layer) remove;
        InterceptorSupport.runLayerInterceptor(layer, LayerInterceptor.REMOVED_ID);
        layer.eAdapters().removeAll(deepAdapters);
    }

}
