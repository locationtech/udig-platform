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
package org.locationtech.udig.project.ui.internal.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.IProjectElement;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectElement;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.ProjectRegistry;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.window.Window;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

public class DeleteTest extends AbstractProjectUITestCase {

	private Project project;

	@Before
	public void setUp() throws Exception {
		SimpleFeature [] features=UDIGTestUtil.createDefaultTestFeatures("Tests",4); //$NON-NLS-1$
		IGeoResource resource = MapTests.createGeoResource(features, true);
		Map map=MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(512,512));
		project=map.getProjectInternal();
	}
	
	@After
	public void tearDown() throws Exception {
		ProjectRegistry registry = ProjectPlugin.getPlugin().getProjectRegistry();
		Resource registryResource=registry.eResource();
		List<Project> project=new ArrayList<Project>();
		project.addAll(registry.getProjects());
		
		for (Project project2 : project) {
			registry.getProjects().remove(project2);
		}
		if( registryResource == null )
			return;
		Iterator iter=registryResource.getResourceSet().getResources().iterator();
		while ( iter.hasNext() ){
			Resource resource=(Resource) iter.next();
			if( resource==registry.eResource() )
				continue;
			resource.unload();
			File file = new File(resource.getURI().toFileString());
			if( file.exists() )
				file.delete();
		}
	}
	
	/*
	 * Test method for 'org.locationtech.udig.project.ui.internal.actions.Delete.operate(SimpleFeature)'
	 */
	@Test
	public void testOperateFeature() {

	}

	/*
	 * Test method for 'org.locationtech.udig.project.ui.internal.actions.Delete.operate(Layer)'
	 */
	@Test
	public void testOperateLayer() {

	}

	/*
	 * Test method for 'org.locationtech.udig.project.ui.internal.actions.Delete.operate(ProjectElement)'
	 */
	@Test
	public void testOperateProjectElement() {
		ProjectElement element = project.getElementsInternal().get(0);
		Resource resource=element.eResource();
		DeleteAccessor deleteAction=new DeleteAccessor();
		deleteAction.runDoDelete(element, false, Window.OK);
		
		assertNull(element.getProject());
		assertNull(element.eResource());
		
		assertEquals(0, resource.getContents().size());
		assertFalse(resource.isLoaded());
	}

	/*
	 * Test method for 'org.locationtech.udig.project.ui.internal.actions.Delete.operate(Project)'
	 */
	@Ignore
	@Test
	public void testOperateProject() throws Exception {
		
		DeleteAccessor deleteAction=new DeleteAccessor();
		URI projecturi=project.eResource().getURI();
		Iterator iter=project.eResource().getResourceSet().getResources().iterator();
		while ( iter.hasNext() ){
			((Resource) iter.next()).save(null);
		}
        //test cancel delete
		deleteAction.runDoDelete(project,false, Window.CANCEL);
		ProjectRegistry registry = ProjectPlugin.getPlugin().getProjectRegistry();
		assertEquals(1, registry.getProjects().size());
		assertTrue( registry.getProjects().contains(project) );
		assertNotNull(project.eResource());
		assertEquals(project, project.getElementsInternal().get(0).getProject());

        Resource resource = project.eResource();
        IProjectElement elem = project.getElements().get(0);
        
        // Test remove project but leave files
		deleteAction.runDoDelete(project,false, Window.OK);
		assertEquals(0, registry.getProjects().size());
		assertTrue( new File(projecturi.toFileString()).exists() );
        assertFalse(resource.isLoaded());
        
		Project project=registry.getProject(projecturi);
		assertEquals(1, project.getElementsInternal().size());
        assertNotSame(elem, project.getElements().get(0));
		assertEquals(project, project.getElementsInternal().get(0).getProject());
        
        // Test delete from file System
        deleteAction.runDoDelete(project,true, Window.OK);
        assertEquals(0, registry.getProjects().size());
        assertFalse( new File(projecturi.toFileString()).exists() );
	}

	class DeleteAccessor extends Delete {
		public DeleteAccessor() {
            super(true);
        }
        public void runDoDelete(Project project, boolean deleteProjectFiles, int returncode){
			doDelete(project, deleteProjectFiles, returncode);
		}
		public void runDoDelete(ProjectElement element, boolean deleteProjectFiles, int returncode){
			doDelete(element, deleteProjectFiles, returncode);
		}
	}
	
}
