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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.internal.shp.ShpGeoResourceImpl;
import org.locationtech.udig.catalog.internal.shp.ShpServiceImpl;

/**
 * @author FGasdorf
 *
 */
public class ShpGeoResourceInfoTest {

    ShpServiceImpl serviceInfo;

    final Map<String, Serializable> connectionParamsMap = new HashMap<>();

    @Before
    public void before() {
        connectionParamsMap.clear();
        URL shpResourceURL = ShpGeoResourceInfoTest.class.getResource("/test-data/test.shp");
        connectionParamsMap.put(ShapefileDataStoreFactory.URLP.key, shpResourceURL);
        // to avoid loading bundle for test-case
        connectionParamsMap.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, false);
        connectionParamsMap.put(ShapefileDataStoreFactory.DBFCHARSET.key,
                Charset.defaultCharset().name());
        serviceInfo = new ShpServiceImpl(shpResourceURL, connectionParamsMap);
    }

    @Test
    public void nullOrEmptyElementNotInKeywordList() throws InterruptedException, ExecutionException {
        Callable<IGeoResourceInfo> infoFuture = createCallable();
        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
        Future<IGeoResourceInfo> future = newSingleThreadExecutor.submit(infoFuture);
        IGeoResourceInfo info = future.get();
        Set<String> keywords = info.getKeywords();
        assertEquals(3, keywords.size());
        Iterator<String> iterator = keywords.iterator();
        while (iterator.hasNext()) {
            String keyword = iterator.next();
            assertFalse("expected an non null & non empty keyword :"
                    + Arrays.toString(keywords.toArray()), StringUtils.isEmpty(keyword));
        }
    }

    /**
     * @return
     */
    private Callable<IGeoResourceInfo> createCallable() {
        return new Callable<IGeoResourceInfo>() {
            @Override
            public IGeoResourceInfo call() throws Exception {
                ShpGeoResourceImpl shpGeoResourceImpl = new ShpGeoResourceImpl(serviceInfo, "test");
                return shpGeoResourceImpl.getInfo(null);
            }
        };
    }

    /**
     * Issue https://github.com/locationtech/udig-platform/issues/678
     */
    @Test()
    public void getSchemaNoNullPointerWhileNamespaceOfFeatureTypeNotSet() throws InterruptedException, ExecutionException {
        Callable<IGeoResourceInfo> infoFuture = createCallable();
        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
        Future<IGeoResourceInfo> future = newSingleThreadExecutor.submit(infoFuture);
        IGeoResourceInfo info = future.get();
        URI schema = info.getSchema();
        assertNull(schema);
    }
}
