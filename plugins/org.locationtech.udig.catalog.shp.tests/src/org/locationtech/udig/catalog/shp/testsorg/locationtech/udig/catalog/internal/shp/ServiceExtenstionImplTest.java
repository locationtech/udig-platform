/**
 * uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2022, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.catalog.shp.testsorg.locationtech.udig.catalog.internal.shp;

import static java.io.File.createTempFile;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.shp.ShpServiceExtension;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author FGasdorf
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceExtenstionImplTest {

    private static final String OLD_GETOOLS_URL_KEY = "shapefile url";
    private final ShpServiceExtension shpServiceExtension = new ShpServiceExtension();

    private File tempShapeFile;

    @Before
    public void before() throws IOException {
        tempShapeFile = createTempFile("test", ".shp");
    }

    @Test
    public void createParamMapIsNullForUnsupportedType() throws MalformedURLException {
        File f = new File("file/that/does/not/exists.unsupported");
        Map<String, Serializable> paramMap = shpServiceExtension.createParams(f.toURI().toURL());
        assertNull(paramMap);
    }

    @Test
    public void createParamMapIsNullIfFileNotExists() throws MalformedURLException {
        File f = new File("file/that/does/not/exists.shp");
        Map<String, Serializable> paramMap = shpServiceExtension.createParams(f.toURI().toURL());
        assertNull(paramMap);
    }

    @Test
    public void createServiceIsNullParameterMapNotCausesNPEandReturnNullService()
            throws MalformedURLException {
        Map<String, Serializable> nullMap = null;
        assertNull(shpServiceExtension.createService(tempShapeFile.toURI().toURL(), nullMap));
    }

    @Test
    public void createServiceWithOldUrlParameterKeyTransformedToNew() {
        URL resource = this.getClass().getResource("/test-data/test.shp");

        Map<String, Serializable> paramMap = new HashMap<>();
        paramMap.put(OLD_GETOOLS_URL_KEY, resource);
        paramMap.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, false);
        paramMap.put(ShapefileDataStoreFactory.DBFCHARSET.key, Charset.defaultCharset().name());

        IService service = shpServiceExtension.createService(null, paramMap);
        assertServiceConnectionParams(service.getConnectionParams());
    }

    /**
     * @param connectionParams
     */
    private void assertServiceConnectionParams(Map<String, Serializable> connectionParams) {
        assertTrue(connectionParams.containsKey(ShapefileDataStoreFactory.URLP.key));
        assertTrue(connectionParams.containsKey(ShapefileDataStoreFactory.DBFCHARSET.key));
        assertFalse(connectionParams.containsKey(OLD_GETOOLS_URL_KEY));
    }

    @Test
    public void createServiceGivenURLasString() {
        URL resource = this.getClass().getResource("/test-data/test.shp");

        Map<String, Serializable> paramMap = new HashMap<>();
        paramMap.put(ShapefileDataStoreFactory.URLP.key, resource.toString());
        paramMap.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, false);
        paramMap.put(ShapefileDataStoreFactory.DBFCHARSET.key, Charset.defaultCharset().name());

        IService service = shpServiceExtension.createService(null, paramMap);

        assertTrue(service.canResolve(ShapefileDataStore.class));
        assertServiceConnectionParams(service.getConnectionParams());
    }

}
