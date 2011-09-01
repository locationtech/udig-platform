package eu.udig.tutorials.toolview;

import java.awt.Color;
import java.awt.Shape;
import java.util.List;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * Render all Referenced envelopes on the map as red semi-transparent rectangles.  The SendAlertTool will place
 * the alerts on the blackboard
 */
public class ShowAlertsMapGraphic implements MapGraphic {
	
	public static final String ALERTS_KEY = "ALERTS";
	public static final String EXTENSION_ID = "eu.udig.tutorials.tool-view.showalertmapgraphic";

	@Override
	public void draw(MapGraphicContext context) {
		@SuppressWarnings("unchecked")
		List<ReferencedEnvelope> alerts = (List<ReferencedEnvelope>) context.getLayer().getBlackboard().get(ALERTS_KEY);
		if(alerts == null) return;
		
		ViewportGraphics graphics = context.getGraphics();
		
		for (ReferencedEnvelope referencedEnvelope : alerts) {
			Shape shape = context.toShape(referencedEnvelope);
			graphics.setColor(new Color(255,0,0,50));
			graphics.fill(shape);
			graphics.setColor(Color.RED);
			graphics.draw(shape);
		}
	}

}
