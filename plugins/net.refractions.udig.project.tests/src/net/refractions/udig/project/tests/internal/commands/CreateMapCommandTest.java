package net.refractions.udig.project.tests.internal.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.tests.DummyService;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.commands.CreateMapCommand;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;

import org.junit.Before;
import org.junit.Test;

public class CreateMapCommandTest extends AbstractProjectTestCase {
	
	IService service;
	
	@Before
	public void setUp() throws Exception {
		//get a georesource
		IServiceFactory sFactory 
			= CatalogPlugin.getDefault().getServiceFactory();
		
		HashMap<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("dummy", DummyService.url); //$NON-NLS-1$
		
		List<IService> services = sFactory.createService(map);
		service = services.get(0);
//        catalog.add(service);
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