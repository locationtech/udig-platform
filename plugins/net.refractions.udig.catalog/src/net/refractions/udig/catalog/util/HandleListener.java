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
package net.refractions.udig.catalog.util;

import java.lang.ref.WeakReference;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveChangeListener;
import net.refractions.udig.catalog.IResolveDelta;
/**
 * Easy example that listens to a specific IResolve.
 * <p>
 * <p>
 * Will do its best to reduce the IResolveChangeEvent to:
 * <ul>
 * <li>start() - called after create with valid handle
 * <li>stop() - called before remove while handle is still valid
 * <li>dispose() - called after remove while handle is invalid
 * <li>refresh() - called when handle has changed
 * <li>replace() - called when handle is replaced
 * <li>reset() - called when handle is replaced, and replacement is unknown
 * </ul>
 * </p>
 * <p>
 * Note 1: The IResolveDelta does not always include information about *your* handle. If your parent
 * is being replaced - your direct replacement may not be available until you ask for it.
 * </p>
 * <p>
 * Translation repalce with a null newResolve = reset. This indicates you need to find your handle
 * again from first principles.
 * </p>
 * <p>
 * Note 2: HandleListener only holds onto a IResolve with a weak reference. Although this protects
 * you a little bit, you still need to remove zombie listeners from the catalog (they cannot do it
 * themselves).
 * </p>
 * <p>
 * However when the weak reference is cleaned up, we will notice and call dispose for you.
 * </p>
 * 
 * @author jgarnett
 * @since 0.9.0
 */
public abstract class HandleListener implements IResolveChangeListener {
    private WeakReference<IResolve> reference;

    public HandleListener( IResolve handle ) {
        reference = new WeakReference<IResolve>(handle);
    }

    /**
     * Resolve the handle - will call dispose if handle has passed out of scope.
     */
    public IResolve getHandle() {
        if (reference == null)
            return null;
        if (reference.enqueue()) {
            dispose();
            return null;
        }
        IResolve handle = reference.get();
        if (handle == null) {
            dispose();
        }
        return null;
    }
    /**
     * Can be called during a repalce, or reset to switch this listener over to the new handle.
     * 
     * @param handle
     * @return
     */
    public void setHandle( IResolve newHandle ) {
        reference.clear();
        reference = null;
        reference = new WeakReference<IResolve>(newHandle);
    }
    /** Called after create with valid handle */
    public abstract void start( IResolve handle );

    /** Called before remove while handle is still valid */
    public abstract void stop( IResolve handle );

    /** Called after remove while handle is invalid */
    public abstract void dispose();

    /** Called when handle has changed */
    public abstract void refresh( IResolve handle );

    /**
     * Handle is being replaced with newHandle.
     * <p>
     * Note: This method is called post change, the newHandle already exists in the catalog.
     * </p>
     * <p>
     * You have two options:
     * <ul>
     * <li>use setHandle( newHandle ) during your implemenation to <b>switch</b> over to the
     * newHandle
     * <li>remove this listener and set up a new one watching newHandle
     * </ul>
     * </p>
     */
    public abstract void replace( IResolve handle, IResolve newHandle );

    /**
     * This is a replace where the replacement is unknown.
     * <p>
     * This occurs when:
     * <ul>
     * <li>an IResolveChangeDelta indicates your is replaced
     * <li>an IResolveChangeDelta indicates you replaced
     * </ul>
     * </p>
     * <p>
     * In both cases you have two options:
     * <ul>
     * <li>Easy: find your replacement handle from first principles (ie look it up in the catalog)
     * <li>Harder: as an optimization you can try and look through the event information and locate
     * the delta for your parent. This delta may indicate your new parent, allowing you to locate
     * your "new" handle in a more timely fashion.
     * </ul>
     * </p>
     * 
     * @param handle
     */
    public abstract void reset( IResolve handle, IResolveChangeEvent event );

    /** Actual listener implemtnation */
    final public void changed( IResolveChangeEvent event ) {
        IResolve handle = getHandle();
        if (handle == null) {
            dispose();
            return;
        }
        if (event.getResolve() == handle) {
            // simple case event mentions handle
            switch( event.getType() ) {
            case PRE_DELETE:
                stop(handle);
                return;
            case PRE_CLOSE:
                dispose();
                return;
            case POST_CHANGE:
            default:
                refresh(handle);
                return;
            }
        }
        IResolveDelta match = SearchResolveDeltaVisitor.search(handle, event);
        if (match == null) {
            return; // this event does not effect us
        }
        if (match.getResolve() == handle) {
            // simple case delta mentions handle
            switch( match.getKind() ) {
            case NO_CHANGE:
                return; // one of our childs must of changed ...
            case ADDED:
                start(handle);
                return;
            case REMOVED:
                dispose();
                return;
            case REPLACED:
                if (match.getNewResolve() != null) {
                    replace(handle, match.getNewResolve());
                } else {
                    reset(handle, event);
                }
                return;
            case CHANGED:
            default:
                refresh(handle);
                return;
            }
        }
        // our parent has changed
        switch( match.getKind() ) {
        case NO_CHANGE:
        case ADDED:
            return; // these do not make sense in this context
        case REMOVED:
            dispose(); // parent is being removed
            return;
        case REPLACED: // parent is replaced, reset handle
            reset(handle, event);
            return;
        case CHANGED:
        default:
            refresh(handle);
            return;
        }
    }
}
