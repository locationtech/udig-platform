package eu.udig.tutorials.alertapp;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.mapgraphic.internal.MapGraphicService;
import net.refractions.udig.project.ui.internal.DefaultMapViewPart;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

/**
 * The main view port.  Adds a shapefile to the View and configures the view with the tools and context menu 
 * (when selection tool is active)
 */
public class View extends DefaultMapViewPart {
	public static final String ID = "X.view";


	@Override
	protected void createResources(List<IGeoResource> resources, IProgressMonitor monitor) throws IOException {
		addAlertsMapgraphic(monitor, resources);

		FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		dialog.setFilterExtensions(new String[]{"shp"});
		String file = dialog.open();
		addShpService(file,resources,monitor);
	}
	private void addAlertsMapgraphic(IProgressMonitor monitor,
			List<IGeoResource> resources) throws IOException {
		IService service = CatalogPlugin.getDefault().getLocalCatalog().acquire(MapGraphicService.SERVICE_URL,monitor);
		String desiredIdString = MapGraphicService.SERVICE_URL+"#"+ShowAlertsMapGraphic.EXTENSION_ID;
		for (IGeoResource resource : service.resources(null)) {
			String idString = resource.getID().toString();
			if(idString.equals(desiredIdString)) {
				resources.add(resource);
				return;
			}
		}
		throw new IllegalStateException("Unable to find " + desiredIdString + " mapgraphic");
	}
	
	private void addShpService(String path,List<IGeoResource> resources, IProgressMonitor monitor) throws IOException {
		IService service = CatalogPlugin.getDefault().getLocalCatalog().acquire(new URL("file://"+path),monitor);
		
		resources.addAll(service.resources(monitor));
	}

	@Override
	protected boolean acquireToolbar() {
		return true;
	}
	
}