package net.refractions.udig.project.tests.ui.internal.commands;

import java.util.HashMap;
import java.util.List;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.tests.DummyService;
import net.refractions.udig.catalog.tests.ui.workflow.Runner;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.commands.CreateMapCommand;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.commands.OpenProjectElementCommand;
import net.refractions.udig.project.ui.internal.MapEditor;
import net.refractions.udig.project.ui.internal.MapEditorInput;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class OpenMapCommandTest extends AbstractProjectUITestCase {
	
	Map map;
	IService service;
	boolean done;
	
	@Override
	protected void setUp() throws Exception {
        super.setUp();
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
		
		
		MapEditor mapEditor = (MapEditor)PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		assertNotNull(mapEditor);
		
		MapEditorInput input = (MapEditorInput) mapEditor.getEditorInput();
		assertEquals(map, input.getProjectElement());
	}
}