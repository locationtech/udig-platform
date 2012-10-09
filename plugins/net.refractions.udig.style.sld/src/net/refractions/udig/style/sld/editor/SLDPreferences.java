/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.style.sld.editor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferenceNodeVisitor;
import org.eclipse.core.runtime.preferences.IScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * 
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author chorner
 * @since 1.1.M1
 * @see org.eclipse.core.runtime.preferences.IEclipsePreferences
 */
public class SLDPreferences implements IEclipsePreferences, IScope {

    protected Map<String,Object> children;
    protected final String name;
    protected final SLDPreferences parent;
    
    public SLDPreferences() {
        this(null, null);
    }
    
    public SLDPreferences(SLDPreferences parent, String name) {
        super();
        this.parent = parent;
        this.name = name;
    }
    
    public void addNodeChangeListener( INodeChangeListener listener ) {
    }

    public void removeNodeChangeListener( INodeChangeListener listener ) {
    }

    public void addPreferenceChangeListener( IPreferenceChangeListener listener ) {
    }

    public void removePreferenceChangeListener( IPreferenceChangeListener listener ) {
    }

    public void removeNode() throws BackingStoreException {
    }

    public Preferences node( String path ) {
        return null;
    }

    public void accept( IPreferenceNodeVisitor visitor ) throws BackingStoreException {
    }

    public void put( String key, String value ) {
    }

    public String get( String key, String def ) {
        return null;
    }

    public void remove( String key ) {
    }

    public void clear() throws BackingStoreException {
    }

    public void putInt( String key, int value ) {
    }

    public int getInt( String key, int def ) {
        return 0;
    }

    public void putLong( String key, long value ) {
    }

    public long getLong( String key, long def ) {
        return 0;
    }

    public void putBoolean( String key, boolean value ) {
    }

    public boolean getBoolean( String key, boolean def ) {
        return false;
    }

    public void putFloat( String key, float value ) {
    }

    public float getFloat( String key, float def ) {
        return 0;
    }

    public void putDouble( String key, double value ) {
    }

    public double getDouble( String key, double def ) {
        return 0;
    }

    public void putByteArray( String key, byte[] value ) {
    }

    public byte[] getByteArray( String key, byte[] def ) {
        return null;
    }

    public String[] keys() throws BackingStoreException {
        return null;
    }

    public String[] childrenNames() throws BackingStoreException {
        return null;
    }

    public Preferences parent() {
        return null;
    }

    public boolean nodeExists( String pathName ) throws BackingStoreException {
        return false;
    }

    public String name() {
        return name;
    }

    public String absolutePath() {
        return null;
    }

    public void flush() throws BackingStoreException {
    }

    public void sync() throws BackingStoreException {
    }

    public IEclipsePreferences create( IEclipsePreferences parent, String name ) {
        return null;
    }

    protected synchronized IEclipsePreferences addChild(String childName, IEclipsePreferences child) {
        //Thread safety: synchronize method to protect modification of children field
        if (children == null)
            children = Collections.synchronizedMap(new HashMap<String,Object>());
        children.put(childName, child == null ? (Object) childName : child);
        return child;
    }
    
}
