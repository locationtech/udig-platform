/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.SafeRunnable;

/**
 * An abstract preference store implementation for remote preference stores. This class is quite
 * similar to <code>org.eclipse.jface.preference.PreferenceStore</code>, with the exception that
 * it loads and saves its values from a remote source on startup and shutdown, respectively.
 * 
 * @author chorner
 */
public abstract class RemotePreferenceStore implements IPersistentPreferenceStore {

    /**
     * List of registered listeners (element type:
     * <code>IPropertyChangeListener</code>). These listeners are to be
     * informed when the current value of a preference changes.
     */
    private Set<IPropertyChangeListener> listeners = new CopyOnWriteArraySet<IPropertyChangeListener>();

    /**
     * The locally stored copy of the map from preference name to preference value
     * (represented as strings).
     */
    private Map<String, String> localProperties;
    
    /**
     * The mapping from preference name to default preference value (represented
     * as strings); <code>null</code> if none.
     */
    private Map<String, String> defaultProperties;

    /**
     * Indicates whether a value as been changed by <code>setToDefault</code>
     * or <code>setValue</code>; initially <code>false</code>.
     */
    private boolean dirty = false;
    
    /**
     * Indicates that the remote store has been initially contacted.
     */
    private boolean ready = false;

    /**
     * Obtains the remote value for the specified preference.  Subclasses must implement.
     *
     * @param name key
     * @return String value
     */
    protected abstract String getValue( String name );
    
    /**
     * Stores a remote value for the preference with the specified key. Subclasses must implement.
     */
    public abstract void putValue( String name, String value );
    
    /**
     * Determines if the key specified exists in the remote store.
     *
     * @param name key of the preference
     * @return true if key exists
     */
    public abstract boolean isKey( String name );
    
    public RemotePreferenceStore() {
        defaultProperties = new HashMap<String, String>();
        localProperties = new HashMap<String, String>();
    }
    
    /**
     * This is the initial hit of the remote store. All methods that read or write will check to
     * ensure we've interacted with the store prior to doing anything.
     */
    protected void load() {
        //grab all the preferences (initial load so we can detect local modifications)
        String[] names = preferenceNames();
        for (int i = 0; i < names.length; i++) {
            String value = getValue(names[i]);
            if (value != null) {
                localProperties.put(names[i], value);
            }
        }
        ready = true;
    }

    public void save() throws IOException {
        String[] names = preferenceNames();
        for (int i = 0; i < names.length; i++) {
            String remoteValue = getValue(names[i]);
            String localValue = toString(localProperties.get(names[i]));
            String defaultValue = toString(defaultProperties.get(names[i]));
            if (localValue == null) {
                if (defaultValue == null) {
                    throw new IOException("Default property value not defined"); //$NON-NLS-1$
                } else {
                    putValue(names[i], defaultValue); //revert to default
                }
            } else if (remoteValue == null) {
                putValue(names[i], localValue); //first save
            } else {
                if (!remoteValue.equals(localValue)) {
                    putValue(names[i], localValue); // regular save
                }
            }
        }
        dirty = false;
    }
    
    /**
     * Had to add this method as the Property object was being unreliable in returning its contents
     * using the getProperty method.  We also want null to be null, rather than not "null".
     */
    private String toString( Object object ) {
        if (object == null)
            return null;
        if (object instanceof Boolean) {
            if (((Boolean) object).booleanValue()) {
                return IPreferenceStore.TRUE;
            } else {
                return IPreferenceStore.FALSE;
            }
        }
        return String.valueOf(object);
    }

    public void addPropertyChangeListener( IPropertyChangeListener listener ) {
        listeners.add(listener);
    }

    public boolean contains( String name ) {
        if (name == null)
            return false;
        if (localProperties.containsKey(name) || defaultProperties.containsKey(name))
            return true;
        // check the remote store too
        Object remoteValue = getValue(name);
        if (remoteValue == null)
            return false;
        else
            return true;
    }

    public void firePropertyChangeEvent( String name, Object oldValue, Object newValue ) {
        // Do we need to fire an event.
        if ((oldValue == null || !oldValue.equals(newValue))) {
            final PropertyChangeEvent pe = new PropertyChangeEvent(this, name,
                    oldValue, newValue);
            for( final IPropertyChangeListener l : listeners ) {
                SafeRunnable.run(new SafeRunnable(JFaceResources.getString("PreferenceStore.changeError")) { //$NON-NLS-1$
                        public void run() {
                            l.propertyChange(pe);
                        }
                });
            }
        }
    }

    public boolean getBoolean( String name ) {
        if (!ready) load();
        return getBoolean(localProperties, name);
    }

    private boolean getBoolean(Map<String, String> p, String name) {
        String value = p != null ? p.get(name) : null;
        if (value == null)
            return BOOLEAN_DEFAULT_DEFAULT;
        if (value.equals(IPreferenceStore.TRUE))
            return true;
        return false;
    }
    
    public boolean getDefaultBoolean( String name ) {
        return getBoolean(defaultProperties, name);
    }

    public double getDefaultDouble( String name ) {
        return getDouble(defaultProperties, name);
    }

    private double getDouble( Map<String, String> p, String name ) {
        String value = p != null ? p.get(name) : null;
        if (value == null)
            return DOUBLE_DEFAULT_DEFAULT;
        double ival = DOUBLE_DEFAULT_DEFAULT;
        try {
            ival = new Double(value).doubleValue();
        } catch (NumberFormatException e) {
        }
        return ival;
    }

    public float getDefaultFloat( String name ) {
        return getFloat(defaultProperties, name);
    }

    private float getFloat( Map<String, String> p, String name ) {
        String value = p != null ? p.get(name) : null;
        if (value == null)
            return FLOAT_DEFAULT_DEFAULT;
        float ival = FLOAT_DEFAULT_DEFAULT;
        try {
            ival = new Float(value).floatValue();
        } catch (NumberFormatException e) {
        }
        return ival;
    }

    public int getDefaultInt( String name ) {
        return getInt(defaultProperties, name);
    }

    private int getInt( Map<String, String> p, String name ) {
        String value = p != null ? p.get(name) : null;
        if (value == null)
            return INT_DEFAULT_DEFAULT;
        int ival = 0;
        try {
            ival = Integer.parseInt(value);
        } catch (NumberFormatException e) {
        }
        return ival;
    }

    public long getDefaultLong( String name ) {
        return getLong(defaultProperties, name);
    }

    private long getLong( Map<String, String> p, String name ) {
        String value = p != null ? p.get(name) : null;
        if (value == null)
            return LONG_DEFAULT_DEFAULT;
        long ival = LONG_DEFAULT_DEFAULT;
        try {
            ival = Long.parseLong(value);
        } catch (NumberFormatException e) {
        }
        return ival;
    }

    public String getDefaultString( String name ) {
        return getString(defaultProperties, name);
    }

    private String getString( Map<String, String> p, String name ) {
        String value = p != null ? p.get(name) : null;
        if (value == null)
            return STRING_DEFAULT_DEFAULT;
        return value;
    }

    public double getDouble( String name ) {
        if (!ready) load();
        return getDouble(localProperties, name);
    }

    public float getFloat( String name ) {
        if (!ready) load();
        return getFloat(localProperties, name);
    }

    public int getInt( String name ) {
        if (!ready) load();
        return getInt(localProperties, name);
    }

    public long getLong( String name ) {
        if (!ready) load();
        return getLong(localProperties, name);
    }

    public String getString( String name ) {
        if (!ready) load();
        return getString(localProperties, name);
    }

    public boolean isDefault( String name ) {
        //does not check remote store
        return (!localProperties.containsKey(name) && defaultProperties.containsKey(name));
    }

    public boolean needsSaving() {
        if (!ready) load();
        return dirty;
    }

    /**
     * Returns an enumeration of all preferences known to this store which have
     * current values other than their default value.
     * 
     * @return an array of preference names
     */
    public String[] preferenceNames() {
        Set<String> names = defaultProperties.keySet();
        return (String[]) names.toArray(new String[names.size()]);
    }
    
    public void removePropertyChangeListener( IPropertyChangeListener listener ) {
        listeners.remove(listener);
    }

    public void setDefault( String name, double value ) {
        setValue(defaultProperties, name, value);
    }

    public void setDefault( String name, float value ) {
        setValue(defaultProperties, name, value);
    }

    public void setDefault( String name, int value ) {
        setValue(defaultProperties, name, value);
    }

    public void setDefault( String name, long value ) {
        setValue(defaultProperties, name, value);
    }

    public void setDefault( String name, String value ) {
        setValue(defaultProperties, name, value);
    }

    public void setDefault( String name, boolean value ) {
        setValue(defaultProperties, name, value);
    }

    private void setValue( Map<String, String> p, String name, double value ) {
        Assert.isTrue(p != null && name != null);
        p.put(name, toString(new Double(value)));
    }

    private void setValue( Map<String, String> p, String name, float value ) {
        Assert.isTrue(p != null && name != null);
        p.put(name, toString(new Float(value)));
    }

    private void setValue( Map<String, String> p, String name, int value ) {
        Assert.isTrue(p != null && name != null);
        p.put(name, toString(Integer.valueOf(value)));
    }

    private void setValue( Map<String, String> p, String name, long value ) {
        Assert.isTrue(p != null && name != null);
        p.put(name, toString(Long.valueOf(value)));
    }

    private void setValue( Map<String, String> p, String name, String value ) {
        Assert.isTrue(p != null && name != null && value != null);
        p.put(name, value);
    }

    private void setValue( Map<String, String> p, String name, boolean value ) {
        Assert.isTrue(p != null && name != null);
        p.put(name, toString(Boolean.valueOf(value)));
    }

    public void setToDefault( String name ) {
        Object oldValue = localProperties.get(name);
        localProperties.remove(name);
        dirty = true;
        Object newValue = null;
        if (defaultProperties != null)
            newValue = defaultProperties.get(name);
        firePropertyChangeEvent(name, oldValue, newValue);
    }

    public void setValue( String name, double value ) {
        double oldValue = getDouble(name);
        if (oldValue != value) {
            setValue(localProperties, name, value);
            dirty = true;
            firePropertyChangeEvent(name, new Double(oldValue), new Double(value));
        }
    }

    public void setValue( String name, float value ) {
        float oldValue = getFloat(name);
        if (oldValue != value) {
            setValue(localProperties, name, value);
            dirty = true;
            firePropertyChangeEvent(name, new Float(oldValue), new Float(value));
        }
    }

    public void setValue( String name, int value ) {
        int oldValue = getInt(name);
        if (oldValue != value) {
            setValue(localProperties, name, value);
            dirty = true;
            firePropertyChangeEvent(name, Integer.valueOf(oldValue), Integer.valueOf(value));
        }
    }

    public void setValue( String name, long value ) {
        long oldValue = getLong(name);
        if (oldValue != value) {
            setValue(localProperties, name, value);
            dirty = true;
            firePropertyChangeEvent(name, Long.valueOf(oldValue), Long.valueOf(value));
        }
    }

    public void setValue( String name, String value ) {
        String oldValue = getString(name);
        if (oldValue == null || !oldValue.equals(value)) {
            setValue(localProperties, name, value);
            dirty = true;
            firePropertyChangeEvent(name, oldValue, value);
        }
    }

    public void setValue( String name, boolean value ) {
        boolean oldValue = getBoolean(name);
        if (oldValue != value) {
            setValue(localProperties, name, value);
            dirty = true;
            firePropertyChangeEvent(name, Boolean.valueOf(oldValue), Boolean.valueOf(value));
        }
    }

}
