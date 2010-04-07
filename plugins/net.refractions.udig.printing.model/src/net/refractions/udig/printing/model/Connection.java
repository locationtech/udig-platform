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
package net.refractions.udig.printing.model;

/**
 * A Connection represents some sort of relation between two Boxes.
 * 
 * For example, a scalebar needs to be related to a map in order to draw
 * itself properly. A Connection can be used to visualize this relation in the
 * PageEditor. 
 * 
 * @author rgould
 * @since 0.6.0
 * @model
 */
public interface Connection extends Element{
    /**
     * @return true if this connection is actually connected, false otherwise
     * @model
     */
    public boolean isConnected();

	/**
     * Sets the value of the '{@link net.refractions.udig.printing.model.Connection#isConnected <em>Connected</em>}' attribute.
     * <!-- begin-user-doc -->
     * Sets the connected state of this connection to the value provided 
     * <!-- end-user-doc --> 
     * @param value the new value of the '<em>Connected</em>' attribute.
     * @see #isConnected()
     * @generated
     */
    void setConnected(boolean value);

    /**
     * The Box returned represents a object that uses the target of the
     * connection somehow.
     * 
     * @see Box
     * @return the Box that is the source of this connection
     * @model
     */
    public Box getSource();

	/**
     * Sets the value of the '{@link net.refractions.udig.printing.model.Connection#getSource <em>Source</em>}' reference.
     * <!-- begin-user-doc -->
     * Sets this connection's source Box 
     * This will affect the connection by notifying any listeners of the change.
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Source</em>' reference.
     * @see #getSource()
     * @generated
     */
    void setSource(Box value);

    /**
     * This represents a link between a Box and this class. The target of a
     * connection is often used by the source to retrieve information, or
     * maybe for manipulation.
     * 
     * @see Box
     * @return the Box that is the target of this connection
     * @model
     */
    public Box getTarget();

	/**
     * Sets the value of the '{@link net.refractions.udig.printing.model.Connection#getTarget <em>Target</em>}' reference.
     * <!-- begin-user-doc -->
     * Sets this connection's source Box 
     * This represents a link between a Box and this class. The target of a
     * connection is often used by the source to retrieve information, or
     * maybe for manipulation.
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Target</em>' reference.
     * @see #getTarget()
     * @generated
     */
    void setTarget(Box value);

    /**
     * Disconnects this connection from its source and target
     */
    public void disconnect();

    /**
     * Reconnects this connection to its target and source, as if it
     * was just created.
     */
    public void reconnect();

    /**
     * Reconnects this connection to a new source Box and a new target Box
     * 
     * this is the same as:
     * <pre> 
     * {
     * 	setSource(source);
     * 	setTarget(target);
     * 	reconnect();
     * }
     * </pre>
     * @param source the Connection's new source
     * @param target the Connection's new target
     */
    public void reconnect( Box source, Box target );
}
