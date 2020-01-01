/* 
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.ProjectPackage;

/**
 * Communicate Layer state to the user.
 * <p>
 * Note we may need to squirl away the clock number in the layer blackboard to keep it spinning
 * smoothly.
 * </p>
 * 
 * @author jgarnett
 * @since 0.6.0
 */
public class LayerStatusDecorator implements ILightweightLabelDecorator {

    private final Map<ILayer, Integer> currentlyDisplayedStatus=Collections.synchronizedMap(new WeakHashMap<ILayer, Integer>());
    private final Set<ILabelProviderListener> listeners = new CopyOnWriteArraySet<ILabelProviderListener>();
    private Adapter adapterImpl = new AdapterImpl(){
        public void notifyChanged( Notification msg ) {
            if (msg.getNotifier() instanceof Layer) {
                final Layer layer = (Layer) msg.getNotifier();
                if( adapterImpl==null ){
                    layer.eAdapters().remove(this);
                    return;
                }
                if (msg.getFeatureID(Layer.class) != ProjectPackage.LAYER__STATUS)
                    return;
                Integer integer = currentlyDisplayedStatus.get(layer);
                if ( integer!=null && msg.getNewIntValue() == integer.intValue())
                    return;
                refresh(layer);
            }
        }
    };

    void refresh( Layer layer ) {
        if (listeners.isEmpty())
            return;
        LabelProviderChangedEvent event = new LabelProviderChangedEvent(this, layer);
        for( ILabelProviderListener listener : listeners ) {
            listener.labelProviderChanged(event);
        }
    }
    /**
     * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object,
     *      org.eclipse.jface.viewers.IDecoration)
     */
    @SuppressWarnings("unchecked")
    public synchronized void decorate( Object element, IDecoration decoration ) {
        Layer layer = (Layer) element; // should be safe, extention point does the instanceof
                       
        // check
        ImageDescriptor ovr = statusIcon(layer);
        if (ovr != null)
            decoration.addOverlay(ovr, IDecoration.BOTTOM_LEFT);

        // decoration.addOverlay( ProjectUIPlugin.getDefault().getImageDescriptor( ISharedImages.SELECT_UDR ),
        // IDecoration.UNDERLAY );

        if (!layer.eAdapters().contains(adapterImpl))
            layer.eAdapters().add(adapterImpl);
    }

    private ImageDescriptor statusIcon( Layer layer ) {
        currentlyDisplayedStatus.put(layer, layer.getStatus());
        switch( layer.getStatus() ) {
        case ILayer.WARNING:
            return ProjectUIPlugin.getDefault().getImageDescriptor(ISharedImages.WARN_OVR);

        case ILayer.ERROR:
            return ProjectUIPlugin.getDefault().getImageDescriptor(ISharedImages.ERROR_OVR);

        case ILayer.MISSING:
            return ProjectUIPlugin.getDefault().getImageDescriptor(ISharedImages.HUH_OVR);

        case ILayer.UNCONFIGURED:
            return ProjectUIPlugin.getDefault().getImageDescriptor(ISharedImages.UNCONFIGURED_OVR);

        case ILayer.WAIT:
            return ProjectUIPlugin.getDefault().getImageDescriptor(ISharedImages.WAIT_OVR);

        case ILayer.WORKING:
            return ProjectUIPlugin.getDefault().getImageDescriptor(ISharedImages.CLOCK0_OVR);
        /*
         * { int clock = 0; if( layer.getProperties().contains("clock")){ clock =
         * layer.getProperties().getInteger( "clock" ); } clock++; if ( clock > 7 ) clock = 0;
         * layer.getProperties().putInteger( "clock", clock ); switch( clock ){ case 0: return
         * ProjectUIPlugin.getDefault().getImageDescriptor( ISharedImages.CLOCK0_OVR ); case 1: return ProjectUIPlugin.getDefault().getImageDescriptor(
         * ISharedImages.CLOCK1_OVR ); case 2: return ProjectUIPlugin.getDefault().getImageDescriptor(
         * ISharedImages.CLOCK2_OVR ); case 3: return ProjectUIPlugin.getDefault().getImageDescriptor(
         * ISharedImages.CLOCK3_OVR ); case 4: return ProjectUIPlugin.getDefault().getImageDescriptor(
         * ISharedImages.CLOCK4_OVR ); case 5: return ProjectUIPlugin.getDefault().getImageDescriptor(
         * ISharedImages.CLOCK5_OVR ); case 6: return ProjectUIPlugin.getDefault().getImageDescriptor(
         * ISharedImages.CLOCK6_OVR ); case 7: return ProjectUIPlugin.getDefault().getImageDescriptor(
         * ISharedImages.CLOCK7_OVR ); default: return ProjectUIPlugin.getDefault().getImageDescriptor(
         * ISharedImages.WAIT_OVR ); } }
         */
        case ILayer.DONE:
        default:
            return null; // we are done - not status needed
        }
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void addListener( ILabelProviderListener listener ) {
        listeners.add(listener);
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {
        // clean up 
        listeners.clear();
        // set adapterImpl to null so it will clean itself up if called again.
        adapterImpl=null;
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
     *      java.lang.String)
     */
    public boolean isLabelProperty( Object element, String property ) {
        // System.out.println( "layer status update sees: " + property );
        return true; // "status".equals( property );
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void removeListener( ILabelProviderListener listener ) {
        listeners.remove(listener);
    }

}
