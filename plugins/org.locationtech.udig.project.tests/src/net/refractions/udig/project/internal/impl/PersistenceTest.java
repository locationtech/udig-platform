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
package net.refractions.udig.project.internal.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.ProjectRegistry;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class PersistenceTest extends AbstractProjectTestCase {

    final String firstMapName = "FirstMap"; //$NON-NLS-1$
    final String firstMapLayerName = "FirstMapLayer"; //$NON-NLS-1$
    final String secondMapName = "SecondMap"; //$NON-NLS-1$
    final String secondMapLayerName = "SecondMapLayer"; //$NON-NLS-1$
    final String type1Name="type1"; //$NON-NLS-1$
    final String type2Name="type2"; //$NON-NLS-1$

    private Project project;
	private File file;
	private IGeoResource resource1;
	private IGeoResource resource2;

	@SuppressWarnings("unchecked")
	@Before
    public void setUp() throws Exception {
        ProjectRegistry registry = ProjectPlugin.getPlugin().getProjectRegistry();
        List<Project> projects = registry.getProjects();
        registry.getProjects().removeAll(projects);
        
        EList list=registry.eResource().getResourceSet().getResources();
        Set<Resource> toRemove=new HashSet<Resource>();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Resource element = (Resource) iter.next();
            if( element!=registry.eResource() ){
                element.unload();
                toRemove.add(element);
            }
        }
        
        project=registry.getDefaultProject();
		file = new File(project.eResource().getURI().toFileString());
		if( file.exists() ){
			if( file.isFile() ){
				file.delete();
				File parent=file.getParentFile();
				File[] files=parent.listFiles();
				for (File file : files) {
					file.delete();
				}
				parent.delete();
			}
			file.delete();
		}

        registry.eResource().getResourceSet().getResources().removeAll(toRemove);
        
        project=registry.getProject(FileLocator.toFileURL(Platform.getInstanceLocation().getURL()).getFile());

		resource1 = MapTests.createGeoResource(UDIGTestUtil.createDefaultTestFeatures(type1Name,4),false);
		Map map = MapTests.createNonDynamicMapAndRenderer(resource1, new Dimension(512,512));
        map.setName(firstMapName); 
		map.getLayersInternal().get(0).setName(firstMapLayerName); 
		
		resource2 = MapTests.createGeoResource(UDIGTestUtil.createDefaultTestFeatures(type2Name,6),false); 
		map=MapTests.createNonDynamicMapAndRenderer(resource2, new Dimension(512,512));
		map.setName(secondMapName); 
		map.getLayersInternal().get(0).setName(secondMapLayerName); 
	}

	@After
	public void tearDown() throws Exception {
		if( file.exists() ){
			if( file.isFile() ){
				file.delete();
				File parent=file.getParentFile();
				File[] files=parent.listFiles();
				for (File file : files) {
					file.delete();
				}
				parent.delete();
			}
			file.delete();
		}
	}
	
	@Ignore
	@Test
    public void testSaveAndLoad() throws Exception {
		EList list=project.eResource().getResourceSet().getResources();
		
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Resource element = (Resource) iter.next();
            try{
                element.save(null);
            }catch (Exception e) {
            }
            if( !element.getContents().contains(ProjectPlugin.getPlugin().getProjectRegistry()) )
                element.unload();
		}
		
		ResourceSet set=new ResourceSetImpl();
		Project project=(Project) set.getResource(URI.createURI("file://"+file.getAbsolutePath()), true).getAllContents().next(); //$NON-NLS-1$
		assertFalse(project.eIsProxy());
		assertNotNull(project);
		int maps=0;
		boolean foundFirstMap=false;
		boolean foundSecondMap=false;
		
		List resources=project.getElements();
		for (Iterator iter = resources.iterator(); iter.hasNext();) {
			Map map=(Map) iter.next();
	
			assertFalse(map.eIsProxy());
			assertEquals(1, map.getLayersInternal().size());
			assertNotNull(map.getLayersInternal().get(0).getGeoResources().get(0));
			assertNotNull(map.getLayersInternal().get(0).getResource(FeatureSource.class, new NullProgressMonitor()));
	
			if( map.getName().equals(firstMapName)){ 
				foundFirstMap=true;
				assertEquals( firstMapLayerName, map.getLayersInternal().get(0).getName()); 
				FeatureSource<SimpleFeatureType, SimpleFeature> source=map.getLayersInternal().get(0).getResource(FeatureSource.class, null);				
				assertEquals( 4, source.getCount(Query.ALL));
				assertEquals( firstMapLayerName, map.getLayersInternal().get(0).getName()); 
			}
			if( map.getName().equals(secondMapName)){ 
				foundSecondMap=true;
				assertEquals( secondMapLayerName, map.getLayersInternal().get(0).getName()); 
				FeatureSource<SimpleFeatureType, SimpleFeature> source=map.getLayersInternal().get(0).getResource(FeatureSource.class, null);				
				assertEquals( 6, source.getCount(Query.ALL));
				assertEquals( secondMapLayerName, map.getLayersInternal().get(0).getName());
			}
			maps++;
		}
		assertEquals(2,maps);
		assertTrue(foundFirstMap);
		assertTrue(foundSecondMap);
	}
}
