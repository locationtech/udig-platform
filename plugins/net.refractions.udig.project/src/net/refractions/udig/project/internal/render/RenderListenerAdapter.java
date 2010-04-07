/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.render;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

/**
 * An Adapter for RenderExecutors that allow other objects to listen to state changes that occur in
 * the RenderExecutor.
 * <p>
 * See the example for how to use an this class as a listener.
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Listener for RenderExecutor state events</li>
 * <li>Call renderDone, renderStarting, or renderUpdate when an event occurs that can be mapped to
 * one of the methods.</li>
 * </ul>
 * </p>
 * <p>
 * Example Use:
 * 
 * <pre><code>
 * renderExecutor.eAdapters().add(new RenderListenerAdapter(){
 *     protected void renderStarting() {
 *         //Enter starting behaviour
 *     }
 * 
 *     protected void renderUpdate() {
 *         //Enter update behaviour
 *     }
 * 
 *     protected void renderDone() {
 *         //Enter done behaviour
 *     }
 * });
 * </code></pre>
 * 
 * </p>
 * 
 * @author jeichar
 * @since 0.3
 */
public class RenderListenerAdapter extends AdapterImpl {
    /**
     * @see org.eclipse.emf.common.notify.impl.AdapterImpl#isAdapterForType(java.lang.Object)
     */
    public boolean isAdapterForType( Object type ) {
        return RenderListenerAdapter.class == type;
    }

    /**
     * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged( Notification msg ) {
        if (msg.getFeatureID(Renderer.class) == RenderPackage.RENDERER__STATE) {
            switch( msg.getNewIntValue() ) {
            case Renderer.STARTING:
                renderStarting();
                break;
            case Renderer.RENDERING: {
                renderUpdate();
                break;
            }
            case Renderer.DONE: {
                renderDone();
                break;
            }
            case Renderer.DISPOSED: {
                renderDisposed(msg);
                break;
            }
            case Renderer.RENDER_REQUEST: {
                renderRequest();
                break;
            }
            }
        }
    }

    /**
     * Subclasses may implement if they are interested in being notified when a renderer needs to be
     * rerendered
     */
    protected void renderRequest() {
        // do nothing
    }

    /**
     * Subclasses may implement if they are interested in being notified when a renderer has been
     * disposed
     * 
     * @param msg
     */
    protected void renderDisposed( Notification msg ) {
        // do nothing
    }

    /**
     * Subclasses may implement if they are interested in receiving renderStarting events
     */
    protected void renderStarting() {
        // do nothing
    }

    /**
     * Subclasses may implement if they are interested in receiving renderUpdate events
     */
    protected void renderUpdate() {
        // do nothing
    }

    /**
     * Subclasses may implement if they are interested in receiving renderDone events
     */
    protected void renderDone() {
        // do nothing
    }
}
