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
