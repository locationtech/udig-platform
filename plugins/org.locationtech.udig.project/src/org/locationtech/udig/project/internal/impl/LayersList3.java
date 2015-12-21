/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, 2015 Refractions Research Inc. and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import org.locationtech.udig.core.internal.ExtensionPointList;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.interceptor.LayerInterceptor;
import org.locationtech.udig.project.interceptor.MapInterceptor;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.Trace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * Wraps a EList and makes sure that when a layer is added the layer interceptor is fired and a deep
 * adapter is added.
 * <p>
 * This is used to go from an object (Map or ContextModel) to a list of layers (it does not set up a reverse relationship)
 * so you will need to figure out how to call layer.setMap yourself.
 * 
 * @author Jesse
 * @author Frank Gasdorf
 * @author Erdal Karaca
 * 
 * @since 1.1.0
 */
class LayersList3 extends SynchronizedEObjectResolvingEList<Layer> {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 4584718175140573610L;

    private Collection<Adapter> deepAdapters = new CopyOnWriteArraySet<Adapter>();

    public LayersList3( Class<Layer> dataClass, InternalEObject owner, int featureID ) {
        super(dataClass, owner, featureID );
    }

    /**
     * adds the adapter to all layers and the context model
     * 
     * @param adapter adapter to add to all layers.
     */
    public void addDeepAdapter( final Adapter adapter ) {
        deepAdapters.add(adapter);
        if (!owner.eAdapters().contains(adapter))
            owner.eAdapters().add(adapter);

        syncedIteration(new IEListVisitor<Layer>(){
            public void visit( final Layer layer ) {
                if (!layer.eAdapters().contains(adapter)) {
                    layer.eAdapters().add(adapter);
                }
            }
        });

    }

    /**
     * removes the adapter from all layers.
     * 
     * @param toRemove
     */
    public void removeDeepAdapter( final Adapter toRemove ) {
        deepAdapters.remove(toRemove);
        owner.eAdapters().remove(toRemove);

        syncedIteration(new IEListVisitor<Layer>() {
            public void visit(final Layer layer) {
                layer.eAdapters().remove(toRemove);
            }
        });
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
        
        // iterating over instances of LayersList2 must be synced
        if (c instanceof LayersList3) {
            ((LayersList3) c).syncedIteration(new IEListVisitor<Layer>(){
                public void visit( final Layer element ) {
                    runLayerInterceptorAndRemove(element);
                }
            });
        } else {
            for( final Iterator iter = c.iterator(); iter.hasNext(); ) {
                final Layer element = (Layer) iter.next();
                runLayerInterceptorAndRemove(element);
            }
        }
    }

    private void runAddInterceptors( Object element ) {
        Layer layer = (Layer) element;
        for( Adapter deepAdapter : deepAdapters ) {
            if (!layer.eAdapters().contains(deepAdapter))
                layer.eAdapters().add(deepAdapter);
        }
        runLayerInterceptor(layer, LayerInterceptor.ADDED_ID);
    }

    private void runRemoveInterceptor( Object remove ) {
        if (remove == null || !contains(remove))
            return;
        Layer layer = (Layer) remove;
        runLayerInterceptor(layer, LayerInterceptor.REMOVED_ID);
        (layer).eAdapters().removeAll(deepAdapters);
    }

    private void runLayerInterceptor( Layer layer, String configurationName ) {
        runNonDeprecatedInterceptors(layer, configurationName);
        runDeprecatedInterceptors(layer, configurationName);
    }

    private void runNonDeprecatedInterceptors( Layer layer, String configurationName ) {
        List<IConfigurationElement> list = ExtensionPointList
                .getExtensionPointList(LayerInterceptor.EXTENSION_ID);
        for( IConfigurationElement element : list ) {
            if (element.getName().equals(configurationName)) {
                String attribute = element.getAttribute("name"); //$NON-NLS-1$
                try {
                    LayerInterceptor interceptor = (LayerInterceptor) element
                            .createExecutableExtension("class"); //$NON-NLS-1$
                    interceptor.run(layer);
                } catch (CoreException e) {
                    ProjectPlugin
                            .log(
                                    "Error creating class: " + element.getAttribute("class") + " part of layer interceptor: " + attribute, e); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                } catch (Throwable t) {
                    ProjectPlugin.log("error running interceptor: " + attribute, t); //$NON-NLS-1$
                }
            }
        }
    }

    private void runDeprecatedInterceptors( Layer layer, String configurationName ) {
        List<IConfigurationElement> interceptors = ExtensionPointList
                .getExtensionPointList(MapInterceptor.MAP_INTERCEPTOR_EXTENSIONPOINT);
        for( IConfigurationElement element : interceptors ) {
            if (!configurationName.equals(element.getName())) {
                continue;
            }
            try {
                LayerInterceptor interceptor = (LayerInterceptor) element
                        .createExecutableExtension("class"); //$NON-NLS-1$
                interceptor.run(layer);
            } catch (Throwable e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            }
        }
    }

    private void runLayerInterceptorAndRemove( final Layer element ) {
        runLayerInterceptor(element, "layerRemoved"); //$NON-NLS-1$
        element.eAdapters().removeAll(deepAdapters);
    }
}
