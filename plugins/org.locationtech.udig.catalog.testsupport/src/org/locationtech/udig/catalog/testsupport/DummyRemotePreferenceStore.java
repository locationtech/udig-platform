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
package org.locationtech.udig.catalog.testsupport;

import java.util.HashMap;
import java.util.Map;

import org.locationtech.udig.catalog.RemotePreferenceStore;


/**
 * Simple memory implementation of a RemotePreferenceStore for testing purposes only.
 *  
 * @author chorner
 * @since 1.1.0
 */
public class DummyRemotePreferenceStore extends RemotePreferenceStore {

    public Map<String, String> preferences = new HashMap<String, String>();
    
    @Override
    public String getValue( String name ) {
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
