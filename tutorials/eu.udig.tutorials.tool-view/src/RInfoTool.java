import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.IAnimation;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.project.ui.internal.commands.draw.DrawShapeCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.SimpleTool;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Envelope;


public class RInfoTool extends SimpleTool {

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
	
	public RInfoTool() {
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
		FeatureIterator<SimpleFeature> features = null;
		try {
			AnimationUpdater.runTimer(getContext().getMapDisplay(), new Pulse(e.getPoint()));
			Envelope bbox = getContext().getBoundingBox(e.getPoint(), 4);
			ILayer layer = getContext().getSelectedLayer();
			getContext().getFeaturesInBbox(layer, bbox);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if(features != null) features.close();
		}
	}

}
