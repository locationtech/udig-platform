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
package net.refractions.udig.project;

import net.refractions.udig.project.internal.ProjectPackage;

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
