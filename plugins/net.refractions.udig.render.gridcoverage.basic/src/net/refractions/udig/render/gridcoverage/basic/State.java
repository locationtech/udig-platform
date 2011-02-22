package net.refractions.udig.render.gridcoverage.basic;

import java.awt.Rectangle;

import net.refractions.udig.project.render.IRenderContext;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Encapsulates the state required to render a GridCoverage
 *
 * @author Jesse
 * @since 1.1.0
 */
public class State {

    public float opacity;

    public double minScale;

    public double maxScale;

    public IRenderContext context;

    public Envelope bounds;

    public Rectangle displayArea;

    public State(IRenderContext context, Envelope bbox,
    	Rectangle displayArea, float opacity, double minScale,
    	double maxScale) {
        this.opacity = opacity;
        this.minScale = minScale;
        this.maxScale = maxScale;
        this.context = context;
        this.bounds = bbox;
        this.displayArea = displayArea;
    }

}
