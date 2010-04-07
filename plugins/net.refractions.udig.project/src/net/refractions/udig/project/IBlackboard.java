/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project;

import java.util.Set;

/**
 * Provide Temporary for shared collaboration between renderers and the user.
 * <p>
 * To emphais the tempoary nature we are not storing objects at this time, please see
 * StyleBlackboard for the direction we wish to take this class. API should this interface be
 * included?
 * </p>
 * Q: Should this class be cloneable? API explain the use of 'keys' and any uniqueness constraints
 * (Set or List?) API X getX(key) ... should this be X get(key) and let the compiler firgure out
 * which one to call based on the signature? API moreover, you might want to consider on 'Objects'
 * and the use of <T> in the API, using generics to avoid multiple get and put methods. API looks
 * alot like a Map ... either document a comparison or add an extends clause?
 * 
 * @author Jody Garnett
 * @since 0.7.0
 */
public interface IBlackboard {
    /**
     * Adds a listener to the blackboard.  Duplicates are not permitted.
     *
     * @param listener a listener that will be notified when the blackboard changes
     * @return <tt>true</tt> if the collection changed as a result of the call.
     */
    boolean addListener(IBlackboardListener listener);
    /**
     * Removes a listener
     *
     * @param listener the listener to be removed.
     * @return <tt>true</tt> if the collection contained the specified
     *         element.
     */
    boolean removeListener(IBlackboardListener listener);
    /**
     * Check if blackboard has an entry for key.
     * 
     * @return <code>true</code> if a value is known for key
     */
    boolean contains( String key );

    /**
     * Returns the object value of the given key.
     * 
     * @param key the key
     * @return the value, or null if the key was not found
     */
    public Object get( String key );

    /**
     * Sets the value of the given key.
     * 
     * @param key the key
     * @param value the value
     */
    public void put( String key, Object value );

    /**
     * Returns the floating point value of the given key.
     * 
     * @param key the key
     * @return the value, or <code>null</code> if the key was not found or was found but was not a
     *         floating point number
     */
    public Float getFloat( String key );

    /**
     * Returns the integer value of the given key.
     * 
     * @param key the key
     * @return the value, or <code>null</code> if the key was not found or was found but was not
     *         an integer
     */
    public Integer getInteger( String key );

    /**
     * Returns the string value of the given key.
     * 
     * @param key the key
     * @return the value, or <code>null</code> if the key was not found
     */
    public String getString( String key );

    /**
     * Sets the value of the given key to the given floating point number.
     * 
     * @param key the key
     * @param value the value
     */
    public void putFloat( String key, float value );

    /**
     * Sets the value of the given key to the given integer.
     * 
     * @param key the key
     * @param value the value
     */
    public void putInteger( String key, int value );

    /**
     * Sets the value of the given key to the given string.
     * 
     * @param key the key
     * @param value the value
     */
    public void putString( String key, String value );
    
    /**
    * Removes the value identified by key from the blackboard.
    * 
    * @param key the key
    * @return The object removed from the blackboard, or null if no such entry exists.
    */
   Object remove( String key );

    /**
     * Clear the contents of this blackboard.
     * <p>
     * This is a clear() method, not a dispose(), resource handling is not provided.
     * </p>
     * <p>
     * It is not recommend that client code store *real* (such as Images) resources on the
     * blackboard. This is not a ImageCache that will magically handle resource management for you.
     * </p>
     */
    public void clear();

    /**
     * Flush the contents of this blackboard.
     * <p>
     * This signals the blackboard to save any contents, and clear any cached object references.
     * </p>
     */
    public void flush();
    
    /**
     * adds all the contents of the source blackboard to the destination blackboard
     *
     * @param blackboard
     */
    void addAll( IBlackboard blackboard );
    
    /**
     * return the set of keys on the blackboard.
     *
     * @return
     */
    Set<String> keySet();
}