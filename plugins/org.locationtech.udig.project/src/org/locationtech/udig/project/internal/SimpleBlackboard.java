/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.project.BlackboardEvent;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IBlackboardListener;

/**
 * A simple wrapper on a Map, used for tempoary collaboration.
 * 
 * @author jgarnett
 * @since 0.6.0
 */
public class SimpleBlackboard implements IBlackboard {
    /**
     * @uml.property name="map"
     * @uml.associationEnd qualifier="key:java.lang.Object java.lang.Object"
     */
    Map<String, Object> map;

    /**
     * Construct <code>SimpleBlackboard</code>.
     */
    public SimpleBlackboard() {
        map = new HashMap<String, Object>();
    }

    /**
     * Construct <code>SimpleBlackboard</code>.
     */
    public SimpleBlackboard( Map<String, Object> map ) {
        this.map = map;
    }

    /**
     * @see net.refractions.udig.project.IBlackboard#get(java.lang.String)
     */
    public Object get( String key ) {
        return map.get(key);
    }

    /**
     * @see net.refractions.udig.project.IBlackboard#put(java.lang.String, java.lang.Object)
     */
    public void put( String key, Object value ) {
        Object oldValue = map.put(key, value);
        BlackboardEvent event=new BlackboardEvent(this, key, oldValue, value);
        for( IBlackboardListener l : listeners ) {
            try{
            l.blackBoardChanged(event);
            } catch (Exception e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            }
        }
    }
    public Object remove( String key ) {
        Object oldValue = map.remove(key);
        BlackboardEvent event=new BlackboardEvent(this, key, oldValue, null);
        for( IBlackboardListener l : listeners ) {
            try{
            l.blackBoardChanged(event);
            } catch (Exception e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            }
        }
        return oldValue;
    }
    /**
     * @see net.refractions.udig.project.IBlackboard#getFloat(java.lang.String)
     */
    public Float getFloat( String key ) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value instanceof Float) {
                return (Float) value;
            }
        }
        return null;
    }

    /**
     * @see net.refractions.udig.project.IBlackboard#getInteger(java.lang.String)
     */
    public Integer getInteger( String key ) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value instanceof Integer) {
                return (Integer) value;
            }
        }
        return null;
    }

    /**
     * @see net.refractions.udig.project.IBlackboard#getString(java.lang.String)
     */
    public String getString( String key ) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value instanceof String) {
                return (String) value;
            }
        }
        return null;
    }

    /**
     * @see net.refractions.udig.project.IBlackboard#putFloat(java.lang.String, float)
     */
    public void putFloat( String key, float value ) {
        put(key, value);
    }

    /**
     * @see net.refractions.udig.project.IBlackboard#putInteger(java.lang.String, int)
     */
    public void putInteger( String key, int value ) {
        put(key, value);
    }

    /**
     * @see net.refractions.udig.project.IBlackboard#putString(java.lang.String, java.lang.String)
     */
    public void putString( String key, String value ) {
        put(key, value);
    }

    /**
     * @see net.refractions.udig.project.IBlackboard#clear()
     */
    public void clear() {
        map.clear();

        for( IBlackboardListener l : listeners ) {
            try{
                l.blackBoardCleared(this);
            } catch (Exception e) {
                ProjectPlugin.log("", e); //$NON-NLS-1$
            }
        }
    }

    public void flush() {
        // do nothing, does not support persistance
    }

    /*
     * @see net.refractions.udig.project.Blackboard#contains(java.lang.String)
     */
    public boolean contains( String key ) {
        return map.containsKey(key);
    }


    CopyOnWriteArraySet<IBlackboardListener> listeners=new CopyOnWriteArraySet<IBlackboardListener>(); 
    public boolean addListener( IBlackboardListener listener ) {
        return listeners.add(listener);
    }
    

    public boolean removeListener( IBlackboardListener listener ) {
        return listeners.remove(listener);
    }


    public void addAll( IBlackboard blackboard ) {
        Set<String> keySet = blackboard.keySet();
        for( String key : keySet ) {
            put(key, blackboard.get(key));
        }
    }

    public Set<String> keySet() {
        return this.map.keySet();
    }

}
