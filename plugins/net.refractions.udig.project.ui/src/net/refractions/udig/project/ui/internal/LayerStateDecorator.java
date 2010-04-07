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

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.project.internal.Layer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;

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

    private Adapter hack = new AdapterImpl(){
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
        decoration.addOverlay(Images.getDescriptor(ImageConstants.WRITE_OVR));

        if (!layer.eAdapters().contains(hack)) {
            layer.eAdapters().add(hack);
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
