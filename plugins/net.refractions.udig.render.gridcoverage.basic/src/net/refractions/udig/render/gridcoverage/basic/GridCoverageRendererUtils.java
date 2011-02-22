package net.refractions.udig.render.gridcoverage.basic;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.Collections;

import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;


import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.project.render.IRenderContext;

import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.processing.DefaultProcessor;
import org.geotools.factory.Hints;
import org.geotools.filter.Expression;
import org.geotools.renderer.lite.GridCoverageRenderer;
import org.geotools.renderer.lite.RendererUtilities;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;


public class GridCoverageRendererUtils {

    public static GridCoverageRenderer createRenderer(GridCoverage coverage,
	    CoordinateReferenceSystem targetCRS) {
	CoordinateReferenceSystem coverageCRS = coverage.getCoordinateReferenceSystem();
	// Hack for 2.2.x GridCoverage (our custom reprojection code)
	//TODO: remove for GeoTools 2.3.x
	if (coverageCRS!=null && targetCRS!=null && !coverageCRS.equals(targetCRS)) {
	    coverage = projectTo(coverage, targetCRS, null);
	}
	return new GridCoverageRenderer(coverage, targetCRS);
    }

    /**
     * @deprecated soon to be private
     * @param coverage
     * @param crs
     * @param geometry
     * @return
     */
    public static GridCoverage projectTo(final GridCoverage coverage,
	    final CoordinateReferenceSystem crs, final GridGeometry2D geometry) {
	RenderingHints hints = new RenderingHints(Hints.LENIENT_DATUM_SHIFT,
		Boolean.TRUE);
	DefaultProcessor processor = new DefaultProcessor(hints);

	ParameterValueGroup params = processor
		.getOperation("Resample").getParameters(); //$NON-NLS-1$
	params.parameter("Source").setValue(coverage); //$NON-NLS-1$
	if (geometry != null) {
	    params.parameter("GridGeometry").setValue(geometry); //$NON-NLS-1$
	}
	if (crs != null) {
	    params.parameter("CoordinateReferenceSystem").setValue(crs); //$NON-NLS-1$
	}
	return (GridCoverage) processor.doOperation(params);
    }

    public static void paintGraphic(GridCoverageRenderer renderer, Graphics2D graphics, State state) {
        // setup composite
        Composite oldComposite = graphics.getComposite();
        graphics.setComposite(AlphaComposite.getInstance(
        	AlphaComposite.SRC_OVER, state.opacity));

        // setup affine transform for on screen rendering
        Rectangle displayArea = state.displayArea;

        AffineTransform at = RendererUtilities.worldToScreenTransform(
        	state.bounds, displayArea);
        AffineTransform tempTransform = graphics.getTransform();
        AffineTransform atg = new AffineTransform(tempTransform);
        atg.concatenate(at);
        graphics.setTransform(atg);

        RenderingHints hints = new RenderingHints(Collections.EMPTY_MAP);
        hints.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED));
        hints.add(new RenderingHints(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE));
        hints.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED));
        hints.add(new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED));
        hints.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR));
        hints.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE));
        hints.add(new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF));
        hints.add(new RenderingHints(JAI.KEY_INTERPOLATION,new InterpolationNearest()));
        graphics.addRenderingHints(hints);

        renderer.paint(graphics);

        // reset previous configuration
        graphics.setComposite(oldComposite);
        graphics.setTransform(tempTransform);
    }

    /**
     * Extract symbolizer parameters from the style blackboard
     */
    public static State getRenderState(IRenderContext context) {
        StyleBlackboard styleBlackboard = (StyleBlackboard) context.getLayer()
        	.getStyleBlackboard();
        Style style = (Style) styleBlackboard.lookup(Style.class);
        double minScale = Double.MIN_VALUE;
        double maxScale = Double.MAX_VALUE;
        float opacity = 1.0f;
        if (style != null) {
            try {
        	Rule rule = style.getFeatureTypeStyles()[0].getRules()[0];
        	minScale = rule.getMinScaleDenominator();
        	maxScale = rule.getMaxScaleDenominator();
        	if (rule.getSymbolizers()[0] instanceof RasterSymbolizer) {
        	    RasterSymbolizer rs = (RasterSymbolizer) rule
        		    .getSymbolizers()[0];
        	    opacity = getOpacity(rs);
        	}

            } catch (Exception e) {
        	ProjectPlugin.getPlugin().log(e);
            }
        } else {
            opacity = 1;
            minScale = 0;
            maxScale = Double.MAX_VALUE;
        }

        Rectangle displayArea = new Rectangle(context.getMapDisplay()
        	.getWidth(), context.getMapDisplay().getHeight());

        return new State(context, context.getViewportModel().getBounds(),
        	displayArea, opacity, minScale, maxScale);
    }

    public static float getOpacity(RasterSymbolizer sym) {
        float alpha = 1.0f;
        Expression exp = sym.getOpacity();
        if (exp == null)
            return alpha;
        Object obj = exp.getValue(null);
        if (obj == null)
            return alpha;
        Number num = null;
        if (obj instanceof Number)
            num = (Number) obj;
        if (num == null)
            return alpha;
        return num.floatValue();
    }

    public static Rectangle getPaintArea(Envelope imageBounds,
            Envelope mapBounds, Rectangle mapRect) {
        if (imageBounds.equals(mapBounds)) {
            return mapRect;
        }
        if (!mapBounds.intersects(imageBounds)) {
            //don't bother, there is nothing to draw
            return new Rectangle(0, 0, 0, 0);
        }
        int w = 0;
        int h = 0;
        int x = 0;
        int y = 0;
        //cases:
        // 1. Map is completely contained inside the image
        // 2. Image is completely contained inside the map
        // 3. Map shows the edge of the image
        if (imageBounds.contains(mapBounds)) { //case 1
            return mapRect;
        } else if (mapBounds.contains(imageBounds)) { //case 2
            double horizScale = imageBounds.getWidth() / mapBounds.getWidth();
            double vertScale = imageBounds.getHeight() / mapBounds.getHeight();
            double horizOffset = imageBounds.getMinX() - mapBounds.getMinX();
            double vertOffset = imageBounds.getMinY() - mapBounds.getMinY();
            w = (int) (mapRect.width * horizScale);
            h = (int) (mapRect.height * vertScale);
            x = (int) (mapRect.x + (horizOffset * mapRect.width / mapBounds
        	    .getWidth()));
            y = (int) (mapRect.y + mapRect.getMaxY() - h - (vertOffset
        	    * mapRect.height / mapBounds.getHeight()));
            return new Rectangle(x, y, w, h);
        } else { //case 3
            //image boundary!
            return mapRect;
        }
    }

}
