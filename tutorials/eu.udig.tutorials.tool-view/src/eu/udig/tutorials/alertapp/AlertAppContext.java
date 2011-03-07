package eu.udig.tutorials.alertapp;

import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;

import org.geotools.data.FeatureSource;

/**
 * Normally a uDig MapPart asks the enabled modal tool for a selection and sets that selections
 * as the selection of the MapPart.  In this example we only want operations that we define
 * to be able to work on the MapPart so we have a context that all of our operations expect
 * as the selection and therefore only our operations will appear in the context menu.
 *  
 * @author jeichar
 */
public class AlertAppContext {

	private View view;
	private List<ILayer> layers;
	private ILayer vectorLayer;
	private ILayer showAlertsLayer;

	public AlertAppContext(View view) {
		this.view = view;
		this.layers = this.view.getMap().getMapLayers();

		for (ILayer layer : layers) {
			if(layer.hasResource(FeatureSource.class)) {
				this.vectorLayer = layer;
			} else if(layer.hasResource(ShowAlertsMapGraphic.class)) {
				this.showAlertsLayer = layer;
			}
		}
	}

	public ILayer getShowAlertsLayer() {
		return showAlertsLayer;
	}
	
	public ILayer getVectorLayer() {
		return vectorLayer;
	}

	public IMap getMap() {
		return view.getMap();
	}

}
