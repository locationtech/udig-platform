package net.refractions.udig.render.internal.wms.basic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ProjectBlackboardConstants;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.ICompositeRenderContext;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.render.wms.basic.WMSPlugin;
import net.refractions.udig.style.sld.SLDContent;
import net.refractions.udig.style.wms.WMSStyleContent;
import net.refractions.udig.ui.ProgressManager;
import net.refractions.udig.ui.graphics.SLDs;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.StyleImpl;
import org.geotools.styling.Style;
import org.geotools.util.Range;


/**
 * Used to describe the rendering abilities of BasicWMSRenderer2.
 * @author Richard Gould
 */
public class BasicWMSMetrics2 extends AbstractRenderMetrics {

	
    /*
     * list of styles the basic wms renderer is expecting to find and use
     */
    private static List<String> listExpectedStyleIds(){
        ArrayList<String> styleIds = new ArrayList<String>();
        styleIds.add(WMSStyleContent.WMSSTYLE);
        styleIds.add(SLDContent.ID);
        styleIds.add(ProjectBlackboardConstants.LAYER__DATA_QUERY);
        return styleIds;
    }
    
	/**
     * Construct <code>BasicWMSMetrics2</code>.
     *
     * @param context2
     * @param factory
     */
    public BasicWMSMetrics2( IRenderContext context2, BasicWMSMetricsFactory2 factory) {
        super(context2, factory, listExpectedStyleIds());
    }

    public Renderer createRenderer() {
        Renderer renderer=new BasicWMSRenderer2();
        renderer.setContext(context);
        return renderer;
    }
    
    public IRenderContext getRenderContext() {
        return context;
    }

    /**
     * @see net.refractions.udig.project.render.IRenderMetrics#getRenderMetricsFactory()
     */
    public IRenderMetricsFactory getRenderMetricsFactory() {
        return factory;
    }

    public boolean canAddLayer( ILayer layer ) {
        
        if( !layer.hasResource(Layer.class) )
            return false;
        
        try {
            if( !layer.findGeoResource(Layer.class).parent(ProgressManager.instance().get()).
                    equals(getRenderContext().getGeoResource().parent(ProgressManager.instance().get())) )
                    return false;
        } catch (IOException e2) {
            return false;
        }
        
        
        double opacity = Double.NaN;
        
        ICompositeRenderContext context1= (ICompositeRenderContext) context;
        
        
        IRenderContext[] contexts=context1.getContexts().toArray(new IRenderContext[context1.getContexts().size()]);
        Arrays.sort(contexts);
        List<Layer> owsLayers=new ArrayList<Layer>(); 
        IService currentService;
        try {
            owsLayers.add(layer.getResource(Layer.class, new NullProgressMonitor()));
            currentService = layer.getResource(IService.class, null);
        } catch (IOException e1) {
            WMSPlugin.log("", e1); //$NON-NLS-1$
            return false;
        }
        for( IRenderContext renderContext: contexts) {
            ILayer previousLayer = renderContext.getLayer();

            try {
                owsLayers.add(previousLayer.getResource(Layer.class, new NullProgressMonitor()));
                IService previousService = previousLayer.getResource(IService.class, null);
                if (currentService != previousService) {
                    return false;
                }
            } catch (IOException e) {
                WMSPlugin.log("Error while retrieving service.", e); //$NON-NLS-1$
                return false;
            }
            
            if( BasicWMSRenderer2.findRequestCRS(owsLayers, context.getCRS(), context.getMap())==null )
                return false;
            
            Style style = (Style) previousLayer.getStyleBlackboard().get(SLDContent.ID);
            if (style != null) {
                opacity = SLDs.rasterOpacity(SLDs.rasterSymbolizer(style));
            }
        }
                
        Style style = (Style) layer.getStyleBlackboard().get(SLDContent.ID);
        if (style == null && Double.isNaN(opacity)) {
            return true;
        }        
        
        double result = SLDs.rasterOpacity(SLDs.rasterSymbolizer(style));
        
        if (result == opacity) {
            return true;
        }
        return false;
    }

    // XXX: Consider doing more SLD when WMS post is a go
    public boolean canStyle( String SyleID, Object value ) {
        if( value == null ) return false;
        if( value instanceof Style)     
            return !Double.isNaN( SLDs.rasterOpacity( (Style) value ) );
        
        if( value instanceof StyleImpl && getRenderContext().getGeoResource().canResolve(Layer.class) ){
            try {
                Layer layer = getRenderContext().getGeoResource().resolve(Layer.class, ProgressManager.instance().get());
                if( layer.getStyles().contains(value) ){
                    return true;
                }else{
                    return false;
                }
            } catch (IOException e) {
                return false;
            }
        }
        
        return false;
    }

    public boolean isOptimized() {
        return false;
    }

    public Set<Range<Double>> getValidScaleRanges() {
        HashSet<Range<Double>> ranges = new HashSet<Range<Double>>();
        try {
            Layer layer = context.getGeoResource().resolve(Layer.class, ProgressManager.instance().get());
            double min = layer.getScaleDenominatorMin();
            double max = layer.getScaleDenominatorMax();
            if( invalidScale(min) && invalidScale(max) ){
                return ranges;
            }
            
            
            if( invalidScale(min)){
                min = Double.MIN_VALUE;
            }
            if( invalidScale(max) ){
                max = Double.MAX_VALUE;
            }
            if( min < max ){
                ranges.add(new Range(Double.class, min, max));
            }
            else {
                ranges.add(new Range(Double.class, max, min));
            }
        } catch (IOException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
        return ranges;
    }

    private boolean invalidScale( double value ) {
        return Double.isInfinite(value) || Double.isNaN(value);
    }
}
