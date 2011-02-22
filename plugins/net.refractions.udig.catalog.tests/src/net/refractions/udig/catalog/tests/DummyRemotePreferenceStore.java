package net.refractions.udig.catalog.tests;

import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.RemotePreferenceStore;


/**
 * Simple memory implementation of a RemotePreferenceStore for testing purposes only.
 *
 * @author chorner
 * @since 1.1.0
 */
public class DummyRemotePreferenceStore extends RemotePreferenceStore {

    public Map<String, String> preferences = new HashMap<String, String>();

    @Override
    protected String getValue( String name ) {
        return preferences.get(name);
    }

    @Override
    public void putValue( String name, String value ) {
        preferences.put(name, value);
    }

    @Override
    public boolean isKey( String name ) {
        return preferences.containsKey(name);
    }

}
