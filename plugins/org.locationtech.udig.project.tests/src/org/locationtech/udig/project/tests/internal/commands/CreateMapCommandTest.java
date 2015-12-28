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
package org.locationtech.udig.project.tests.internal.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.testsupport.CatalogTests;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.commands.CreateMapCommand;
import org.locationtech.udig.project.testsupport.AbstractProjectTestCase;

public class CreateMapCommandTest extends AbstractProjectTestCase {
	
	IService service;
	
	@Before
	public void setUp() throws Exception {
	    IGeoResource createGeoResource = CatalogTests.createGeoResource("dummy", 10, true);
	    service = createGeoResource.service(new NullProgressMonitor());
	}
	
	@SuppressWarnings("unchecked")
	@Test
    public void testWithProjectWithName() throws Exception {
		Project project = ProjectPlugin.getPlugin().getProjectRegistry()
			.getDefaultProject();
		
		CreateMapCommand cmCommand 
			= new CreateMapCommand("MyMap", (List<IGeoResource>) service.resources(null), project); //$NON-NLS-1$
		project.sendSync(cmCommand);
		
		Map map = (Map) cmCommand.getCreatedMap();
		assertNotNull(map);
		assertEquals(map.getProject(),project);
		assertEquals(map.getName(), "MyMap"); //$NON-NLS-1$
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
    public void testWithoutProjectWithoutName() throws Exception {
		Project project = ProjectPlugin.getPlugin().getProjectRegistry()
		.getDefaultProject();
	
		List<IGeoResource> members = (List<IGeoResource>) service.resources(null);
        CreateMapCommand cmCommand 
			= new CreateMapCommand(null, members, null);
		project.sendSync(cmCommand);
	
		Map map = (Map) cmCommand.getCreatedMap();
		assertNotNull(map);
		assertNotNull(map.getProject());
		
		assertEquals(map.getName(),members.get(0).getInfo(null).getTitle()); 
	
	}
}
