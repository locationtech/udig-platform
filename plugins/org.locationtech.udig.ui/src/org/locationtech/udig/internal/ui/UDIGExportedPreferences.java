/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.internal.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.preferences.IExportedPreferences;
import org.eclipse.core.runtime.preferences.IPreferenceNodeVisitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Uses a scoped preferences to store preferences.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class UDIGExportedPreferences implements IExportedPreferences {

    private IPreferenceStore store;
    private CopyOnWriteArraySet<INodeChangeListener> nodeChangeListeners=new CopyOnWriteArraySet<>();
    private CopyOnWriteArraySet<IPreferenceChangeListener> preferenceChangeListeners=new CopyOnWriteArraySet<>();
    private String nodePath;
    private Map<String, String> map=new HashMap<>();
    private Map<String, UDIGExportedPreferences> children=new HashMap<>();

    public UDIGExportedPreferences(IPreferenceStore store, String nodePath){
        if( nodePath.contains(":") ) //$NON-NLS-1$
            throw new IllegalArgumentException("Node name cannot contain a ':'"); //$NON-NLS-1$
        if( nodePath.contains("$") ) //$NON-NLS-1$
            throw new IllegalArgumentException("Node name cannot contain a '$'"); //$NON-NLS-1$
        this.store=store;
        this.nodePath=nodePath;
        try{
            sync();
        }catch (Exception e) {
            LoggingSupport.log(UiPlugin.getDefault(), e);
        }
    }

    @Override
    public boolean isExportRoot() {
        return false;
    }

    @Override
    public void addNodeChangeListener( INodeChangeListener listener ) {
        nodeChangeListeners.add(listener);
    }

    @Override
    public void removeNodeChangeListener( INodeChangeListener listener ) {
        nodeChangeListeners.remove(listener);
    }

    @Override
    public void addPreferenceChangeListener( IPreferenceChangeListener listener ) {
        preferenceChangeListeners.add(listener);
    }

    @Override
    public void removePreferenceChangeListener( IPreferenceChangeListener listener ) {
        preferenceChangeListeners.remove(listener);
    }

    @Override
    public void removeNode() throws BackingStoreException {
        store.setToDefault(nodePath);
    }

    @Override
    public Preferences node( String path ) {
        String string = nodePath+"/"+path; //$NON-NLS-1$
        if( children.get(path)!=null )
            return children.get(path);

        UDIGExportedPreferences preferences=new UDIGExportedPreferences(store, string);
        children.put(path, preferences);
        return preferences;
    }

    @Override
    public void accept( IPreferenceNodeVisitor visitor ) throws BackingStoreException {
        visitor.visit(this);
    }

    @Override
    public void put( String key, String value ) {
        map.put(key,value);
    }

    @Override
    public String get( String key, String def ) {
        return map.get(key);
    }

    @Override
    public void remove( String key ) {
        map.remove(key);
    }

    @Override
    public void clear() throws BackingStoreException {
        map.clear();
    }

    @Override
    public void putInt( String key, int value ) {
        map.put(key, String.valueOf(value));
    }

    @Override
    public int getInt( String key, int def ) {
        String string = map.get(key);
        if( string==null )
            return def;
        return Integer.parseInt(string);
    }

    @Override
    public void putLong( String key, long value ) {
        map.put(key, String.valueOf(value));
    }

    @Override
    public long getLong( String key, long def ) {
        String string = map.get(key);
        if( string==null )
            return def;
        return Long.parseLong(string);
    }

    @Override
    public void putBoolean( String key, boolean value ) {
        map.put(key, String.valueOf(value));
    }

    @Override
    public boolean getBoolean( String key, boolean def ) {
        String string = map.get(key);
        if( string==null )
            return def;
        return Boolean.parseBoolean(string);    }

    @Override
    public void putFloat( String key, float value ) {
        map.put(key, String.valueOf(value));
    }

    @Override
    public float getFloat( String key, float def ) {
        String string = map.get(key);
        if( string==null )
            return def;
        return Float.parseFloat(string);    }

    @Override
    public void putDouble( String key, double value ) {
        map.put(key, String.valueOf(value));
    }

    @Override
    public double getDouble( String key, double def ) {
        String string = map.get(key);
        if( string==null )
            return def;
        return Double.parseDouble(string);    }

    @Override
    public void putByteArray( String key, byte[] value ) {
        map.put(key, String.valueOf(value));
    }

    @Override
    public byte[] getByteArray( String key, byte[] def ) {
        String string = map.get(key);
        if( string==null )
            return def;
        return string.getBytes();
    }

    @Override
    public String[] keys() throws BackingStoreException {
        return map.keySet().toArray(new String[0]);
    }

    @Override
    public String[] childrenNames() throws BackingStoreException {
        return children.keySet().toArray(new String[0]);
    }

    @Override
    public Preferences parent() {
        return null;
    }

    @Override
    public boolean nodeExists( String pathName ) throws BackingStoreException {
        String s = store.getString(nodePath+"/"+pathName); //$NON-NLS-1$
        return s.length()>0;
    }

    @Override
    public String name() {
        int lastIndexOf = nodePath.lastIndexOf('/');
        if(lastIndexOf==-1)
            return ""; //$NON-NLS-1$
        return nodePath.substring(lastIndexOf);
    }

    @Override
    public String absolutePath() {
        return nodePath;
    }

    @Override
    public void flush() throws BackingStoreException {
        StringBuilder builder = null;
        for( Map.Entry<String,String> entry : map.entrySet() ) {
            if( builder==null )
                builder=new StringBuilder();
            else
                builder.append(":"); //$NON-NLS-1$
            builder.append(entry.getKey());
            builder.append(":"); //$NON-NLS-1$
            builder.append(entry.getValue());
        }
        if( builder!=null )
        store.putValue(nodePath, builder.toString());

        StringBuilder encodedChildren=null;
        for( String entry : children.keySet() ) {
            if( encodedChildren==null )
                encodedChildren=new StringBuilder();
            else
                encodedChildren.append(":"); //$NON-NLS-1$
            encodedChildren.append(nodePath+"/"+entry); //$NON-NLS-1$
        }
        if( encodedChildren!=null )
            store.putValue(nodePath+"$children", encodedChildren.toString()); //$NON-NLS-1$

        for( UDIGExportedPreferences p : children.values() ) {
            if( p!=null )
                p.flush();
        }
    }

    @Override
    public void sync() throws BackingStoreException {
        String[] data=store.getString(nodePath).split(":"); //$NON-NLS-1$
        if (data.length > 1) {
            for( int i = 0; i < data.length; i++ ) {
                String key = data[i];
                i++;
                String value=null;
                if( data.length>i)
                    value = data[i];
                map.put(key, value);
            }
        }
        String[] encodedChildren=store.getString(nodePath+"$children").split(":");  //$NON-NLS-1$//$NON-NLS-2$
        if (encodedChildren.length>1){
            for( String string : encodedChildren ) {
                if( string.length()>0){
                    String[] name=string.split("/"); //$NON-NLS-1$
                    children.put(name[name.length-1], null);
                }
            }
        }
    }

}
