package eu.udig.tutorials.toolview;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.IAnimation;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.project.ui.internal.commands.draw.DrawShapeCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.project.ui.tool.SimpleTool;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.Touches;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Finds all features under the mouse pointer (and surrounding 4 pixels) and all features that touch those features.
 * For each feature a ReferencedEnvelope is added to the {@link ShowAlertsMapGraphic}'s layer blackboard.
 * 
 * This class also show a rectangle around the mouse pointer and an animation of a circle zooming to a point when the mouse is
 * clicked.
 *  
 * @author jeichar
 */
public class SendAlertTool extends SimpleTool {

	public final static String EXTENSION_ID = "eu.udig.tutorials.tool-view.sendalerttool";
	
	/**
	 * Animation of a circle zooming to a point.
	 */
	private static class Pulse extends AbstractDrawCommand implements IAnimation{

		private Point center;
		private int size = 100;
		private int last = 100;
		public Pulse(Point center) {
			this.center = center;
		}
		@Override
		public Rectangle getValidArea() {
			Rectangle shapeBounds = shapeBounds(last);
			shapeBounds.grow(10, 10);
			return shapeBounds;
		}
		private Rectangle shapeBounds(int size) {
			return new Rectangle(center.x-(size/2),center.y-(size/2),size,size);
		}

		@Override
		public void run(IProgressMonitor monitor) throws Exception {
			graphics.setColor(new Color(200, 200, 0, 100));
			graphics.setStroke(ViewportGraphics.LINE_SOLID, 3);
			Rectangle shapeBounds = shapeBounds(size);
			graphics.drawOval(shapeBounds.x, shapeBounds.y, shapeBounds.width, shapeBounds.height);
		}
		@Override
		public short getFrameInterval() {
			return 100;
		}
		@Override
		public void nextFrame() {
			last = size;
			size -= 10;
		}
		@Override
		public boolean hasNext() {
			return size > 0;
		}
		
	}
	
	private DrawShapeCommand affectedArea = new DrawShapeCommand();
	
	public SendAlertTool() {
		super(MOUSE|MOTION);
		affectedArea.setFill(new Color(200, 200,0,50));
		affectedArea.setPaint(new Color(200, 200,0,255));

	}
	@Override
	public void setActive(boolean active) {
		if(active) {
			affectedArea.setValid(true);
			getContext().sendASyncCommand(affectedArea);
		} else {
			affectedArea.setValid(false);
			refreshAffectedArea();
		}
		super.setActive(active);
	}
	private void refreshAffectedArea() {
		Rectangle validArea = affectedArea.getValidArea();

		if(validArea!=null) {
			getContext().getViewportPane().repaint(validArea.x-2, validArea.y-2, validArea.width+4, validArea.height+4);
		}
	}
	
	@Override
	protected void onMouseExited(MapMouseEvent e) {
		if(affectedArea.isValid()) {
			affectedArea.setValid(false);
			refreshAffectedArea();
		} 
		super.onMouseExited(e);
	}
	
	@Override
	protected void onMouseEntered(MapMouseEvent e) {
		Rectangle area = new Rectangle(e.x-10,e.y-10,20,20);
		affectedArea.setShape(area);
		if(!affectedArea.isValid()) {
			affectedArea.setValid(true);
			getContext().sendASyncCommand(affectedArea);
		}
		refreshAffectedArea();
		super.onMouseEntered(e);
	}
	
	@Override
	protected void onMouseMoved(MapMouseEvent e) {
		Rectangle originalArea = affectedArea.getValidArea();
		
		Rectangle area = new Rectangle(e.x-10,e.y-10,20,20);
		affectedArea.setShape(area);
		Rectangle validArea;
		Rectangle newArea = affectedArea.getValidArea();
		if(originalArea == null) {
			validArea = newArea; 
		} else {
			validArea = newArea.union(originalArea);
		}
		getContext().getViewportPane().repaint(validArea.x-2, validArea.y-2, validArea.width+4, validArea.height+4);
	}
	
	@Override
	protected void onMouseReleased(MapMouseEvent e) {
		IToolContext toolContext = getContext();

		// start animation
		AnimationUpdater.runTimer(toolContext.getMapDisplay(), new Pulse(e.getPoint()));
		
		ILayer mapGraphicLayer = findMapGraphicLayer(toolContext);
		
		FeatureIterator<SimpleFeature> features = null;
		try {
			Envelope bbox = toolContext.getBoundingBox(e.getPoint(), 4);
			ILayer selectedLayer = toolContext.getSelectedLayer();
			features = toolContext.getFeaturesInBbox(selectedLayer, bbox).features();
			
			ArrayList<ReferencedEnvelope> alerts = new ArrayList<ReferencedEnvelope>();
			ReferencedEnvelope bounds = new ReferencedEnvelope();
			while(features.hasNext()) {
				SimpleFeature feature = features.next();
				addAlertsForFeature(feature, alerts, bounds);
			}

			mapGraphicLayer.getBlackboard().put(ShowAlertsMapGraphic.ALERTS_KEY, alerts);
			
			mapGraphicLayer.refresh(bounds);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if(features != null) features.close();
		}
	}
	private void addAlertsForFeature(SimpleFeature feature,
			ArrayList<ReferencedEnvelope> alerts, ReferencedEnvelope bounds) {
		FeatureIterator<SimpleFeature> features = null;
		try {
			addBounds(feature, alerts, bounds);
			features = getAffectedFeatures(feature);
			while(features.hasNext()) {
				addBounds(features.next(), alerts, bounds);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if(features != null) features.close();
		}
	}
	private void addBounds(SimpleFeature feature,
			ArrayList<ReferencedEnvelope> alerts, ReferencedEnvelope bounds) {
		ReferencedEnvelope featureBounds = new ReferencedEnvelope(feature.getBounds());
		bounds.expandToInclude(featureBounds);
		alerts.add(featureBounds);
	}
	@SuppressWarnings("unchecked")
	private FeatureIterator<SimpleFeature> getAffectedFeatures(SimpleFeature feature) throws IOException {
		FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
		String geomAttName = feature.getFeatureType().getGeometryDescriptor().getLocalName();
		PropertyName geomPropertyExpression = filterFactory.property(geomAttName);
		Literal literalGeomExpression = filterFactory.literal(feature.getDefaultGeometry());
		Touches filter = filterFactory.touches(geomPropertyExpression, literalGeomExpression);
		
		IProgressMonitor monitor = 
			getContext().getActionBars().getStatusLineManager().getProgressMonitor();
		FeatureSource<SimpleFeatureType,SimpleFeature> resource = 
			getContext().getSelectedLayer().getResource(FeatureSource.class, monitor);
		
		return resource.getFeatures(filter).features();
	}
	
	
	private ILayer findMapGraphicLayer(IToolContext toolContext) {
		for( ILayer layer : toolContext.getMapLayers()) {
			if(layer.hasResource(ShowAlertsMapGraphic.class)) {
				return layer;
			}
		}
		throw new IllegalStateException("This tool should not be enabled if the ShowAlertsMapGraphic is not in map");
	}

}
