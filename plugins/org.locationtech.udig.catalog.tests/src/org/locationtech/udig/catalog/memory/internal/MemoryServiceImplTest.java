package org.locationtech.udig.catalog.memory.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.locationtech.udig.ui.tests.support.UDIGTestUtil.createDefaultTestFeatures;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.DataStore;
import org.geotools.feature.SchemaException;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.memory.ActiveMemoryDataStore;
import org.opengis.feature.simple.SimpleFeature;

public class MemoryServiceImplTest {

    IService memoryServiceImpl;

    ActiveMemoryDataStore ds;

    @SuppressWarnings("restriction")
    @Before
    public void setUp() throws IOException {
        memoryServiceImpl = new MemoryServiceImpl(
                new URL("http://localhost#MemoryServiceImplTest"));
        ds = (ActiveMemoryDataStore) memoryServiceImpl.resolve(DataStore.class,
                new NullProgressMonitor());
        assertNotNull(ds.getTypeNames());
        assertEquals(0, ds.getTypeNames().length);

        List<IResolve> members = memoryServiceImpl.members(new NullProgressMonitor());
        assertNotNull(members);
        assertEquals(0, members.size());
    }

    @Test
    public void refeshMemberListOnNewTypeNameInDS() throws IOException, SchemaException {
        SimpleFeature[] testFeaturesType1 = createDefaultTestFeatures("MemoryTestFeatureType1", 1);
        ds.addFeatures(testFeaturesType1);
        assertEquals("MemoryTestFeatureType1", ds.getTypeNames()[0]);

        List<IResolve> members = memoryServiceImpl.members(new NullProgressMonitor());
        assertNotNull(members);
        assertEquals(1, members.size());
        assertTrue(members.get(0).getID().toString().endsWith("MemoryTestFeatureType1"));

        // add another one
        SimpleFeature[] testFeaturesType2 = createDefaultTestFeatures("MemoryTestFeatureType2", 1);
        ds.addFeatures(testFeaturesType2);
        assertEquals("MemoryTestFeatureType1", ds.getTypeNames()[0]);
        assertEquals("MemoryTestFeatureType2", ds.getTypeNames()[1]);

        members = memoryServiceImpl.members(new NullProgressMonitor());
        assertNotNull(members);
        assertEquals(2, members.size());
        assertTrue(members.get(0).getID().toString().endsWith("MemoryTestFeatureType1"));
        assertTrue(members.get(1).getID().toString().endsWith("MemoryTestFeatureType2"));
    }
}
