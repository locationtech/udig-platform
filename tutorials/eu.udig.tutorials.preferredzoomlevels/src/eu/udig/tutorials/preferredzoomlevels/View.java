package eu.udig.tutorials.preferredzoomlevels;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.internal.DefaultMapViewPart;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IStatusLineManager;

/**
 * 
 * @version 1.3.0
 */
public class View extends DefaultMapViewPart {
	@SuppressWarnings("nls")
    public static final String ID = "eu.udig.tutorials.preferredzoomlevels.view";

	@Override
	protected void createResources(List<IGeoResource> resources, 
			IProgressMonitor monitor) throws IOException {
		@SuppressWarnings("nls")
        URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path("data/face.shp"), Collections.emptyMap());
		IService r = CatalogPlugin.getDefault().getLocalCatalog().acquire(FileLocator.resolve(url), monitor);
		resources.add(r.resources(monitor).get(0));
	}
	
	@Override
	protected void initMap(Map createdMap) {
		TreeSet<Double> scales = new TreeSet<Double>();
		scales.add(1000.0);
		scales.add(10000.0);
		scales.add(100000.0);
		scales.add(1000000.0);
		scales.add(10000000.0);
		scales.add(100000000.0);
		scales.add(1000000000.0);
		createdMap.getViewportModelInternal().setPreferredScaleDenominators(scales);
		super.initMap(createdMap);
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