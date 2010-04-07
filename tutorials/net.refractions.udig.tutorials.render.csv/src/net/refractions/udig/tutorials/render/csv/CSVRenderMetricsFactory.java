package net.refractions.udig.tutorials.render.csv;

import java.io.IOException;
import java.util.ArrayList;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.project.render.IRenderer;
import net.refractions.udig.tutorials.catalog.csv.CSV;

public class CSVRenderMetricsFactory implements IRenderMetricsFactory {

    /**
     * We are willing to render the context if it can provide us with a CSV API.
     * <p>
     * As first written this renderer is going to be limited to DefaultGeographicCRS.WGS; you can
     * enforce this by added the following code:
     * <pre><code>
     * if (!CRS.equalsIgnoreMetadata(context.getCRS(), DefaultGeographicCRS.WGS84)) {
     *     return false; // we only are rendering WGS84 right now
     * }
     * </code></pre>
     * @return true if any resource can resolve to a CSV.
     */
    public boolean canRender( IRenderContext context ) throws IOException {
        for( IGeoResource resource : context.getLayer().getGeoResources() ) {
            if (resource.canResolve(CSV.class)) {
                return true;
            }
        }
        return false;
    }
    /** 
     * Used to create an object of class net.refractions.udig.project.render.IRenderMetrics.
     * <p>
     * This class will evaulate how well our renderer can handle the provided context.
     * @param context Content of a Layer and GeoResource to be drawn
     * @return AbstractRenderMetrics indicating how well we can draw the provided context
     */
    public AbstractRenderMetrics createMetrics( IRenderContext context ) {
        return new CSVRenderMetrics(context, this);
    }

    public Class< ? extends IRenderer> getRendererType() {
        return CSVRenderer.class;
    }

}
