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
package org.locationtech.udig.project;

import org.locationtech.udig.project.internal.ProjectPackage;

/**
 * An event indicating a attribute of the EditManager has changed.
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class EditManagerEvent extends UDIGEvent {
    /** Indicates the current edit feature has changed. 
    * The old value will be the last edit feature, 
    * the new value will be the currently/newly edit feature
    */
    public static final int EDIT_FEATURE = ProjectPackage.EDIT_MANAGER__EDIT_FEATURE;
    /** Indicates the current edit layer has changed. 
     * The old value will be the last edit layer, 
     * the new value will be the currently/newly edit layer
     */
    public static final int EDIT_LAYER = ProjectPackage.EDIT_MANAGER__EDIT_LAYER_INTERNAL;
    /** Indicates the currently selected layer has changed. 
     * The old value will be the last selected layer, 
     * the new value will be the currently/newly selected layer
     */
    public static final int SELECTED_LAYER = ProjectPackage.EDIT_MANAGER__SELECTED_LAYER;
    /** Indicates the transaction is about to be committed.  Old and new values will be null. */
    public static final int PRE_COMMIT = -2;
    /** Indicates the transaction has been committed.  Old and new values will be null. */
    public static final int POST_COMMIT = -1;
    /** Indicates the transaction is about to be rolledback.  Old and new values will be null. */
    public static final int PRE_ROLLBACK = -3;
    /** Indicates the transaction has been rolledback.  Old and new values will be null. */
    public static final int POST_ROLLBACK = -4;

    private int type;
    /**
     * Construct <code>EditManagerEvent</code>.
     * 
     * @param source the object that raised the event.
     * @param type the type of event this object represents.
     * @param newValue the new value, if this applies.
     * @param oldValue the old value, if this applies.
     */
    public EditManagerEvent( IEditManager source, int type, Object newValue, Object oldValue ) {
        super(source, newValue, oldValue);
        this.type=type;
    }

    /**
     * Gets the type of the event.
     * 
     * @return the type of the event.
     * @see #EDIT_FEATURE
     * @see #EDIT_LAYER
     * @see #SELECTED_LAYER
     * @see #PRE_COMMIT
     * @see #POST_COMMIT
     * @see #PRE_ROLLBACK
     * @see #POST_ROLLBACK
     * @uml.property name="type"
     */
    public int getType() {
        return type;
    }
    
    @Override
    public IEditManager getSource() {
        return (IEditManager) source;
    }
}
