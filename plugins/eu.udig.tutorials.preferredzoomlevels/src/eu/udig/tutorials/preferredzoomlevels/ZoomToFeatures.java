package eu.udig.tutorials.preferredzoomlevels;

import java.io.IOException;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.command.navigation.SetViewportBBoxCommand;
import net.refractions.udig.project.ui.tool.AbstractActionTool;
import net.refractions.udig.ui.ProgressManager;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.filter.Filter;

/**
 * This is mostly for testing zoom to an area when preferred zoom levels are enabled
 * @author jeichar
 *
 */
public class ZoomToFeatures extends AbstractActionTool {

	@Override
	public void run() {
		ILayer selectedLayer = getContext().getSelectedLayer();
		Filter selectionFilter = selectedLayer.getFilter();
		try {
			SimpleFeatureSource source = selectedLayer.getResource(SimpleFeatureSource.class, ProgressManager.instance().get());
			ReferencedEnvelope bounds = source.getFeatures(selectionFilter).getBounds();
			getContext().sendASyncCommand(new SetViewportBBoxCommand(bounds, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
