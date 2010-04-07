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
package net.refractions.udig.project.ui.internal;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.ProjectPackage;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;

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
    private Adapter hack = new AdapterImpl(){
        public void notifyChanged( Notification msg ) {
            if (msg.getNotifier() instanceof Layer) {
                final Layer layer = (Layer) msg.getNotifier();
                if( hack==null ){
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

        // decoration.addOverlay( Images.getDescriptor( ImageConstants.SELECT_UDR ),
        // IDecoration.UNDERLAY );

        if (!layer.eAdapters().contains(hack))
            layer.eAdapters().add(hack);
    }

    private ImageDescriptor statusIcon( Layer layer ) {
        currentlyDisplayedStatus.put(layer, layer.getStatus());
        switch( layer.getStatus() ) {
        case ILayer.WARNING:
            return Images.getDescriptor(ImageConstants.WARN_OVR);

        case ILayer.ERROR:
            return Images.getDescriptor(ImageConstants.ERROR_OVR);

        case ILayer.MISSING:
            return Images.getDescriptor(ImageConstants.HUH_OVR);

        case ILayer.UNCONFIGURED:
            return Images.getDescriptor(ImageConstants.UNCONFIGURED_OVR);

        case ILayer.WAIT:
            return Images.getDescriptor(ImageConstants.WAIT_OVR);

        case ILayer.WORKING:
            return Images.getDescriptor(ImageConstants.CLOCK0_OVR);
        /*
         * { int clock = 0; if( layer.getProperties().contains("clock")){ clock =
         * layer.getProperties().getInteger( "clock" ); } clock++; if ( clock > 7 ) clock = 0;
         * layer.getProperties().putInteger( "clock", clock ); switch( clock ){ case 0: return
         * Images.getDescriptor( ImageConstants.CLOCK0_OVR ); case 1: return Images.getDescriptor(
         * ImageConstants.CLOCK1_OVR ); case 2: return Images.getDescriptor(
         * ImageConstants.CLOCK2_OVR ); case 3: return Images.getDescriptor(
         * ImageConstants.CLOCK3_OVR ); case 4: return Images.getDescriptor(
         * ImageConstants.CLOCK4_OVR ); case 5: return Images.getDescriptor(
         * ImageConstants.CLOCK5_OVR ); case 6: return Images.getDescriptor(
         * ImageConstants.CLOCK6_OVR ); case 7: return Images.getDescriptor(
         * ImageConstants.CLOCK7_OVR ); default: return Images.getDescriptor(
         * ImageConstants.WAIT_OVR ); } }
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
        // should clean up after hack
        listeners.clear();
        // set hack to null so it will clean itself up if called again.
        hack=null;
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
