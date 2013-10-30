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
package org.locationtech.udig.project.tests.ui.internal.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;

import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;
import org.locationtech.udig.catalog.tests.DummyService;
import org.locationtech.udig.catalog.tests.ui.workflow.Runner;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.commands.CreateMapCommand;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.commands.OpenProjectElementCommand;
import org.locationtech.udig.project.ui.internal.MapEditorInput;
import org.locationtech.udig.project.ui.internal.MapEditorPart;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class OpenMapCommandTest extends AbstractProjectUITestCase {
	
	Map map;
	IService service;
	boolean done;
	
	@Before
	public void setUp() throws Exception {
		//get a georesource
		ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
		IServiceFactory sFactory 
			= CatalogPlugin.getDefault().getServiceFactory();
		
		HashMap params = new HashMap();
		params.put("dummy", DummyService.url);
		
		List<IService> services = sFactory.createService(params);
		service = services.get(0);
		
		CreateMapCommand cmCommand = 
			new CreateMapCommand(null,(List<IGeoResource>) service.resources(null),null);
		ProjectPlugin.getPlugin().getProjectRegistry().getDefaultProject()
			.sendSync(cmCommand);
		map = (Map) cmCommand.getCreatedMap();
		done = false;
	}
	
	@Ignore
	@Test
	public void test() throws Exception {
		Runner.Runnable[] runners = new Runner.Runnable[]{
			new Runner.Runnable() {
				public int run(IProgressMonitor monitor) {
					if (ApplicationGIS.getActiveMap() == null) 
						return Runner.REPEAT;
					
					done = true;
					return Runner.OK;
				}
			}
				
		};
		Runner runner = new Runner(runners,2000);
		runner.schedule();
		
		OpenProjectElementCommand omCommand = new OpenProjectElementCommand(map);
		omCommand.run(new NullProgressMonitor());
		
		Display display = Display.getCurrent();
		while(!display.isDisposed() && !done) {
			display.readAndDispatch();
		}
		
		assertEquals(map,ApplicationGIS.getActiveMap());
		
		
		MapEditorPart mapEditor = (MapEditorPart) PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		assertNotNull(mapEditor);
		
		MapEditorInput input = (MapEditorInput) mapEditor.getEditorInput();
		assertEquals(map, input.getProjectElement());
	}
}
