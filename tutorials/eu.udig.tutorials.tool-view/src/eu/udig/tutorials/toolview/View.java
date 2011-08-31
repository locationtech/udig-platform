package eu.udig.tutorials.toolview;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.mapgraphic.internal.MapGraphicService;
import net.refractions.udig.project.ui.internal.DefaultMapViewPart;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.DataUtilities;

/**
 * The main view port.  Adds a shapefile to the View and configures the view with the tools and context menu 
 * (when selection tool is active)
 * @version 1.3.0
 */
public class View extends DefaultMapViewPart {
	public static final String ID = "X.view";


	@Override
	protected void createResources(List<IGeoResource> resources, IProgressMonitor monitor) throws IOException {
		addAlertsMapgraphic(monitor, resources);

		FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		dialog.setFilterExtensions(new String[]{"*.shp"});
		String path = dialog.open();
		File file = new File( path );
		URL url = DataUtilities.fileToURL(file);
		
		addShpService(url,resources,monitor);
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
	
	private void addShpService(URL url,List<IGeoResource> resources, IProgressMonitor monitor) throws IOException {
		IService service = CatalogPlugin.getDefault().getLocalCatalog().acquire(url,monitor);
		
		resources.addAll(service.resources(monitor));
	}

	@Override
	protected boolean acquireToolbar() {
		return true;
	}
	
	@Override
	public IStatusLineManager getStatusLineManager() {
		return getViewSite().getActionBars().getStatusLineManager();
	}
}