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
package org.locationtech.udig.project.internal.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.SchemaException;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.tests.CatalogTests;
import org.locationtech.udig.project.IProjectElement;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.ProjectRegistry;
import org.locationtech.udig.project.tests.support.AbstractProjectTestCase;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

public class PersistenceTest extends AbstractProjectTestCase {

    private static final int EXPECTED_FEAT_CNT_LAYER1 = 4;

    private static final int EXPECTED_FEAT_CNT_LAYER2 = 6;

    private static final String FIRST_MAP_NAME = "PersistenceTestFirstMap"; //$NON-NLS-1$

    private static final String FIRST_MAP_LAYERNAME = "PersistenceTestFirstMapLayer"; //$NON-NLS-1$

    private static final String SECOND_MAP_NAME = "PersistenceTestSecondMap"; //$NON-NLS-1$

    private static final String SECOND_MAP_LAYERNAME = "PersistenceTestSecondMapLayer"; //$NON-NLS-1$

    private static final String TYPE_NAME_1 = "PersistenceTestType1"; //$NON-NLS-1$

    private static final String TYPE_NAME_2 = "PersistenceTestType2"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "PersistenceTestProject";

    private Project project;

    @Before
    public void setUp() throws Exception {
        ProjectRegistry registry = ProjectPlugin.getPlugin().getProjectRegistry();
        String projectPath = Platform.getLocation().toString() + File.separatorChar + PROJECT_NAME;
        project = registry.getProject(projectPath);

        assertNotNull(project);

        createMapResourceAndLayer(project, FIRST_MAP_NAME, FIRST_MAP_LAYERNAME, TYPE_NAME_1,
                EXPECTED_FEAT_CNT_LAYER1);
        createMapResourceAndLayer(project, SECOND_MAP_NAME, SECOND_MAP_LAYERNAME, TYPE_NAME_2,
                EXPECTED_FEAT_CNT_LAYER2);

        assertEquals(2, project.getElements().size());
    }

    private void createMapResourceAndLayer(Project project, String mapName, String layerName,
            String typeName, int featureQuantity) throws IOException, SchemaException {
        Map map = ProjectFactory.eINSTANCE.createMap(project, mapName, null);
        IGeoResource resource = CatalogTests.createGeoResource(
                UDIGTestUtil.createDefaultTestFeatures(typeName, featureQuantity), false);
        Layer createdLayer = map.getLayerFactory().createLayer(resource);
        createdLayer.setName(layerName);
        map.getLayersInternal().add(createdLayer);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testSaveAndLoad() throws Exception {
        Collection<String> errorMessages = ProjectPlugin
                .saveProjects(Collections.singletonList(project));
        assertTrue(errorMessages.isEmpty());

        ResourceSet set = new ResourceSetImpl();
        Project loadedProject = (Project) set.getResource(project.eResource().getURI(), true)
                .getAllContents().next();
        assertFalse(loadedProject.eIsProxy());
        assertNotNull(loadedProject);
        int maps = 0;
        boolean foundFirstMap = false;
        boolean foundSecondMap = false;

        List<IProjectElement> resources = loadedProject.getElements();
        for (IProjectElement projectElement : resources) {
            Map map = (Map) projectElement;

            assertFalse(map.eIsProxy());
            assertEquals(1, map.getMapLayers().size());
            assertNotNull(map.getMapLayers().get(0).getGeoResources().get(0));
            FeatureSource resource = map.getMapLayers().get(0).getResource(FeatureSource.class,
                    new NullProgressMonitor());
            int featureCount = resource.getCount(Query.ALL);
            String featureTypeName = resource.getName().getLocalPart();

            String layerName = map.getLayersInternal().get(0).getName();
            if (map.getName().equals(FIRST_MAP_NAME)) {
                foundFirstMap = true;
                assertEquals(FIRST_MAP_LAYERNAME, layerName);
                assertEquals(TYPE_NAME_1, featureTypeName);
                assertEquals(EXPECTED_FEAT_CNT_LAYER1, featureCount);
            }
            if (map.getName().equals(SECOND_MAP_NAME)) {
                foundSecondMap = true;
                assertEquals(SECOND_MAP_LAYERNAME, layerName);
                assertEquals(TYPE_NAME_2, featureTypeName);
                assertEquals(EXPECTED_FEAT_CNT_LAYER2, featureCount);
            }
            maps++;
        }
        assertEquals(2, maps);
        assertTrue("First map not loaded correctly", foundFirstMap);
        assertTrue("Second map not loaded correctly", foundSecondMap);
    }
}
