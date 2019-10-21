/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.locationtech.udig.project.internal.Layer;

/**
 * Listen to state changes such as isDirty.
 * <p>
 * This would be better handled as a series of lightweight decorators if such things can be aranged.
 * </p>
 * 
 * @author jgarnett
 * @since 0.7.0
 */
public class LayerStateDecorator implements ILightweightLabelDecorator {

    private Adapter adapterImpl = new AdapterImpl(){
        public void notifyChanged( Notification msg ) {
            if (msg.getNotifier() instanceof Layer) {
                Layer layer = (Layer) msg.getNotifier();
                refresh(layer); // just refresh for now
            }
        }
    };
    Set<ILabelProviderListener> listeners = new CopyOnWriteArraySet<ILabelProviderListener>();

    void refresh( Layer layer ) {
        if (listeners.isEmpty())
            return;
        LabelProviderChangedEvent event = new LabelProviderChangedEvent(this, layer);

        for( ILabelProviderListener listener : listeners ) {
            listener.labelProviderChanged(event);
        }
    }
    void refresh() {
        if (listeners.isEmpty())
            return;
        LabelProviderChangedEvent event = new LabelProviderChangedEvent(this);

        for( ILabelProviderListener listener : listeners ) {
            listener.labelProviderChanged(event);
        }
    }
    /**
     * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object,
     *      org.eclipse.jface.viewers.IDecoration)
     */
    @SuppressWarnings("unchecked")
    public void decorate( Object element, IDecoration decoration ) {
        Layer layer = (Layer) element; // should be safe, extention point does the instanceof
                                            // check
        decoration.addOverlay(ProjectUIPlugin.getDefault().getImageDescriptor(ISharedImages.WRITE_OVR));

        if (!layer.eAdapters().contains(adapterImpl)) {
            layer.eAdapters().add(adapterImpl);
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
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
     *      java.lang.String)
     */
    public boolean isLabelProperty( Object element, String property ) {
        return true; // "state".equals( property ); 
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void removeListener( ILabelProviderListener listener ) {
        listeners.remove(listener);
    }
}
