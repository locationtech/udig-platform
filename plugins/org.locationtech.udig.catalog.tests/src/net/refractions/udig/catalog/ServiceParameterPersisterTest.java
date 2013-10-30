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
package net.refractions.udig.catalog;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class ServiceParameterPersisterTest {

    private static final String NODE_ID = "ServiceParameterPersisterTest"; //$NON-NLS-1$
    private TestPersister storer;
    private Preferences preferences;
    private NullProgressMonitor monitor;
    private TestPersister restorer;
    
    @Before
    public void setUp() throws Exception {
        storer = new TestPersister();
        restorer = new TestPersister();
        preferences = Platform.getPreferencesService().getRootNode().node(NODE_ID);
        monitor = new NullProgressMonitor();
    }

    @After
    public void tearDown() throws Exception {
        preferences.clear();
        preferences.removeNode();
    }

    @Test
    public void testPersistString() throws Exception {
        URL url = new URL("http://someurl.org"); //$NON-NLS-1$
        String id = new String("SomeString"); //$NON-NLS-1$
        TestService service = new TestService(url, id);

        assertPersistance(service);
    }

    private void assertPersistance( TestService service ) throws BackingStoreException,
            IOException, MalformedURLException {
        storer.store(monitor, preferences, Collections.singleton(service));

        restorer.restore(preferences);

        assertEquals(service.params.toString(), restorer.map.toString());
        
        
        ID id = restorer.id;
        assertEquals( new ID( service.url), id );
        //URL url = service.url;
        //assertTrue(URLUtils.urlEquals(service.url, restorer.id.toURL(), false));
    }

    @Test
    public void testPersistURL() throws Exception {
        URL url = new URL("http://someurl.org"); //$NON-NLS-1$
        URL id = new URL("http://anotherURL.org"); //$NON-NLS-1$
        TestService service = new TestService(url, id);

        assertPersistance(service);
    }

    @Test
    public void testPersistInteger() throws Exception {
        URL url = new URL("http://someurl.org"); //$NON-NLS-1$
        Integer id = Integer.valueOf(66);
        TestService service = new TestService(url, id);

        assertPersistance(service);

    }

    @Test
    public void testPersistFileURL() throws Exception {
        URL url = new URL("file:/c:\\someurl.org"); //$NON-NLS-1$
        URL id = new URL("file:/c:\\anotherURL.org"); //$NON-NLS-1$
        TestService service = new TestService(url, id);

        assertPersistance(service);
    }

    class TestPersister extends ServiceParameterPersister {
        ID id;
        Map<String, Serializable> map;

        public TestPersister() {
            super(CatalogPlugin.getDefault().getLocalCatalog(), CatalogPlugin.getDefault()
                    .getServiceFactory());
        }
        @Override
        protected void locateService( ID url, Map<String, Serializable> map,
                Map<String, Serializable> properties, Map<ID, Map<String, Serializable>> resourcePropertyMap ) {

            this.id = url;
            this.map = map;
        }
    }
    class TestService extends IService {

        static final String PARAM_VALUE = "paramValue"; //$NON-NLS-1$
        final Map<String, Serializable> params;
        final URL url;

        public TestService( URL id, Serializable paramValue ) {
            params = new HashMap<String, Serializable>();
            params.put(PARAM_VALUE, paramValue);
            this.url = id;
        }

        @Override
        public Map<String, Serializable> getConnectionParams() {
            return params;
        }

        @Override
        protected IServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
            return null;
        }

        @Override
        public List< ? extends IGeoResource> resources( IProgressMonitor monitor )
                throws IOException {
            return null;
        }

        public URL getIdentifier() {
            return url;
        }

        public Throwable getMessage() {
            return null;
        }

        public Status getStatus() {
            return null;
        }

    }
}
