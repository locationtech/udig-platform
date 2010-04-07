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
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.interceptor.LayerInterceptor;
import net.refractions.udig.project.interceptor.MapInterceptor;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.Trace;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * Wraps a EList and makes sure that when a layer is added the layer interceptor is fired and a deep
 * adapter is added.
 * 
 * @author Jesse
 * @since 1.1.0
 */
class LayersList2 extends SynchronizedEObjectWithInverseResolvingEList  {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 4584718175140573610L;

    private Collection<Adapter>     deepAdapters     = new CopyOnWriteArraySet<Adapter>();


    @SuppressWarnings("unchecked")
    public LayersList2( Class dataClass, InternalEObject owner, int featureID, int inverseFeatureID ) {
        super(dataClass, owner, featureID, inverseFeatureID);
    }

    /**
     * adds the adapter to all layers and the context model
     * 
     * @param adapter adapter to add to all layers.
     */
    @SuppressWarnings("unchecked")
    public void addDeepAdapter( Adapter adapter ) {
        deepAdapters.add(adapter);
        if( !owner.eAdapters().contains(adapter))
            owner.eAdapters().add(adapter);
        for( Object object : this ) {
            Layer layer=(Layer) object;
            if( !layer.eAdapters().contains(adapter))
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

    @SuppressWarnings("unchecked")
    @Override
    protected void didAdd( int index, Object newObject ) {
        super.didAdd(index, newObject);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public NotificationChain inverseAdd( Object object, NotificationChain notifications ) {
        NotificationChain notificationChain = super.inverseAdd(object, notifications);
        if( ProjectPlugin.isDebugging(Trace.MODEL))
            ProjectPlugin.trace(getClass(), "inverseAdd() = "+((Layer)object).getID()+" to map "+getMap().getName(), null);  //$NON-NLS-1$ //$NON-NLS-2$

        runAddInterceptors(object);
        return notificationChain;
    }
    
    @Override
    protected Object assign( int index, Object object ) {
            if (!(object instanceof Layer))
                throw new AssertionError("Can only add " + Layer.class.getName() + " to a map.  Was: " //$NON-NLS-1$ //$NON-NLS-2$
                        + object.getClass().getName());
        
            if( ProjectPlugin.isDebugging(Trace.MODEL))
                ProjectPlugin.trace(getClass(), "Adding "+((Layer)object).getID()+" to map "+getMap().getName()+" at location: "+index, null);  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
            Object object2 = super.assign(index, object);
            if( ProjectPlugin.isDebugging(Trace.MODEL))
                ProjectPlugin.trace(getClass(), "Resulting list="+this, null);  //$NON-NLS-1$
            
            return object2;
    }

    private IMap getMap() {
        if( owner instanceof IMap )
            return (IMap)owner;
        if( owner.eContainer() instanceof IMap )
            return (IMap) owner.eContainer();
        throw new IllegalStateException("Owner should be IMap or ContextModel"); //$NON-NLS-1$
    }

    @Override
    protected Object doRemove( int index ) {
        Object toRemove = get(index);
        runRemoveInterceptor(toRemove);

        if( ProjectPlugin.isDebugging(Trace.MODEL))
            ProjectPlugin.trace(getClass(), "Removing "+((Layer)toRemove).getID()+" from map "+getMap().getName(), null);  //$NON-NLS-1$ //$NON-NLS-2$
        
        return super.doRemove(index);
    }

    @Override
    protected void doClear() {
        Object[] toRemove = toArray();
        removeAllInterceptors(Arrays.asList(toRemove));

        if( ProjectPlugin.isDebugging(Trace.MODEL))
            ProjectPlugin.trace(getClass(), "Removing all layers from map:"+getMap().getName(), null);  //$NON-NLS-1$
        
        super.doClear();
    }

    @SuppressWarnings("unchecked")
    private void removeAllInterceptors( Collection c ) {
        for( Iterator iter = c.iterator(); iter.hasNext(); ) {
            Layer element = (Layer) iter.next();
            runLayerInterceptor(element, "layerRemoved"); //$NON-NLS-1$
            element.eAdapters().removeAll(deepAdapters);
        }
    }

    @SuppressWarnings("unchecked")
    private void runAddInterceptors( Object element ) {
        Layer layer = (Layer) element;
        for (Adapter deepAdapter : deepAdapters) {
			if(!layer.eAdapters().contains(deepAdapter))
				layer.eAdapters().add(deepAdapter);
		}
        runLayerInterceptor(layer, LayerInterceptor.ADDED_ID); 
    }

    @SuppressWarnings("unchecked")
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
        List<IConfigurationElement> list = ExtensionPointList.getExtensionPointList(LayerInterceptor.EXTENSION_ID);
        for( IConfigurationElement element : list ) {
            if( element.getName().equals(configurationName) ){
                String attribute = element.getAttribute("name"); //$NON-NLS-1$
                try {
                    LayerInterceptor interceptor=(LayerInterceptor) element.createExecutableExtension("class"); //$NON-NLS-1$
                    interceptor.run(layer);
                } catch (CoreException e) {
                    ProjectPlugin.log( "Error creating class: "+element.getAttribute("class")+" part of layer interceptor: "+attribute, e);  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                } catch (Throwable t){
                    ProjectPlugin.log("error running interceptor: "+attribute, t);  //$NON-NLS-1$
                }
            }
        }
    }

    private void runDeprecatedInterceptors( Layer layer, String configurationName ) {
        List<IConfigurationElement> interceptors = ExtensionPointList
                .getExtensionPointList(MapInterceptor.MAP_INTERCEPTOR_EXTENSIONPOINT);
        for( IConfigurationElement element : interceptors ) {
            if (!configurationName.equals(element.getName()))
                continue;
            try {
                LayerInterceptor interceptor = (LayerInterceptor) element
                        .createExecutableExtension("class"); //$NON-NLS-1$
                interceptor.run(layer);
            } catch (Throwable e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            }
        }
    }

}
