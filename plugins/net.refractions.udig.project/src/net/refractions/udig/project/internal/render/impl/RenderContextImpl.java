/**
 * <copyright></copyright> $Id$
 */
package net.refractions.udig.project.internal.render.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ProjectBlackboardConstants;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.Trace;
import net.refractions.udig.project.internal.impl.AbstractContextImpl;
import net.refractions.udig.project.internal.render.RenderContext;
import net.refractions.udig.project.internal.render.SelectionLayer;
import net.refractions.udig.project.render.ILabelPainter;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;

import org.geotools.data.DefaultQuery;
import org.geotools.data.Query;
import org.geotools.filter.FidFilter;
import org.geotools.filter.Filter;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;
import org.geotools.filter.FilterType;
import org.geotools.filter.IllegalFilterException;
import org.geotools.renderer.lite.LabelCacheDefault;

/**
 * The default implementation of the RenderContext interface.
 *
 * @author Jesse
 * @since 1.0.0
 */
public class RenderContextImpl extends AbstractContextImpl implements RenderContext {

    /**
     * The cached value of the '{@link #getImage() <em>Image</em>}' attribute.
     *
     * @see #getImage()
     */
    protected volatile BufferedImage image = null;

    public static final BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);

    private static final String LABEL_PAINTER = "LABEL_PAINTER";

    /**
     * The cached value of the '{@link #getLayerInternal() <em>Layer Internal</em>}' reference.
     *
     * @see #getLayerInternal()
     */
    protected Layer layerInternal = null;

    /**
     * The cached value of the '{@link #getGeoResourceInternal() <em>Geo Resource Internal</em>}'
     * attribute.
     *
     * @see #getGeoResourceInternal()
     */
    protected IGeoResource geoResourceInternal = null;

    private boolean selection;

    public RenderContextImpl() {
        super();
    }

    public RenderContextImpl( boolean selection ) {
        super();
        this.selection = selection;
    }

    public RenderContextImpl( RenderContextImpl impl ) {
        super(impl);
        setGeoResourceInternal(impl.getGeoResourceInternal());
        setLayerInternal(impl.getLayerInternal());
    }

    public BufferedImage getImage() {
        int width = getMapDisplay().getWidth(), height = getMapDisplay().getHeight();
        return getImage(width, height);
    }

    public synchronized BufferedImage getImage( int width, int height ) {

        if (width < 1 || height < 1)
            return dummyImage;

        if (image == null || image.getWidth() < width || image.getHeight() < height) {
            synchronized (this) {
                if (image == null || image.getWidth() < width || image.getHeight() < height) {
            		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                }

            }
        }
        return image;
    }

    public Query getQuery( ILayer layer ) {
        return layer.getQuery(selection);
    }

    /**
     * <p>
     * Default behavior is to return 0 if no layers are associated otherwise returns the zorder of
     * the first layer in the list.
     * </p>
     *
     * @return Default behavior is to return 0 if no layers are associated otherwise returns the
     *         zorder of the first layer in the list. <!-- end-user-doc -->
     */
    public int getZorder() {
        if (getLayer() == null) {
            return 0;
        }
        return getLayer().getZorder();
    }

    public boolean isVisible() {
        return getLayer().isVisible();
    }

    public Layer getLayerInternal() {
        return layerInternal;
    }

    public void setLayerInternal( Layer newLayerInternal ) {
        layerInternal = newLayerInternal;
    }

    public IGeoResource getGeoResourceInternal() {
        return geoResourceInternal;
    }

    public void setGeoResourceInternal( IGeoResource newGeoResourceInternal ) {
        geoResourceInternal = newGeoResourceInternal;
    }


    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("[layer: "); //$NON-NLS-1$
        if( layerInternal instanceof SelectionLayer)
            result.append("Selection "); //$NON-NLS-1$
        result.append(layerInternal==null?"null":layerInternal.getName()); //$NON-NLS-1$
        result.append(", geoResource: "); //$NON-NLS-1$
        result.append(geoResourceInternal==null?"null":geoResourceInternal.getIdentifier().toString()); //$NON-NLS-1$
        result.append(']');
        return result.toString();
    }

    public boolean hasContent( Point screenLocation ) {
         BufferedImage image = getImage();

         int alpha = image.getAlphaRaster().getSample(screenLocation.x, screenLocation.y, 0);

         return alpha>0;

        // ColorModel cm = image.getColorModel();
        // SampleModel sm = image.getSampleModel();
        // WritableRaster raster = image.getRaster();
        //
        //
        // Object array;
        // switch( sm.getTransferType() ){
        // case DataBuffer.TYPE_BYTE:
        // array = raster.getPixel( screenLocation.x, screenLocation.y, new
        // byte[
        // raster.getNumBands() ] );
        // break;
        // case DataBuffer.TYPE_USHORT:
        // array = raster.getPixel( screenLocation.x, screenLocation.y, new
        // short[
        // raster.getNumBands() ] );
        // break;
        // case DataBuffer.TYPE_INT:
        // array = raster.getPixel( screenLocation.x, screenLocation.y, new int[
        // raster.getNumBands() ] );
        // break;
        // default:
        // return false; // I give up
        // }
        // cm.getAlpha( array );
//        return false;
    }

    public BufferedImage copyImage( Rectangle rectangle ) {
        return null;
    }
    public int compareTo( RenderContext o ) {
        if( o==null )
            return 1;
        if( o==this )
            return 0;
        int result = getLayer().compareTo(o.getLayer());

        // don't have same rendermanager then they are not the same.
        if( result==0 && getRenderManager()!=o.getRenderManager() )
            return 1;
        return result;
    }

    public void init( RenderContext renderContext ) {
        setMapInternal(renderContext.getMapInternal());
        setRenderManagerInternal(renderContext.getRenderManagerInternal());
        setLayerInternal(renderContext.getLayerInternal());
        setGeoResourceInternal(renderContext.getGeoResourceInternal());
    }

    public void clearImage() {
        IMapDisplay mapDisplay = getMapDisplay();
        if( mapDisplay!=null ){
        	clearImage(new Rectangle(0, 0, mapDisplay.getWidth(), mapDisplay.getHeight()));
        }
    }

    public Query getFeatureQuery() {
        Query query = getLayer().getQuery(getLayer() instanceof SelectionLayer);
        if( query.getFilter()==Filter.ALL )
            return query;
        FilterFactory ff=FilterFactoryFinder.createFilterFactory();
        Object editFilter=getLayer().getBlackboard().get(ProjectBlackboardConstants.MAP__RENDERING_FILTER);
        if (!(editFilter instanceof Filter) ){
            return query;
        }
        if( (editFilter instanceof FidFilter) && ((FidFilter)editFilter).getFids().length==0 ){
        	return query;
        }

        Filter newFilter;
        try {
            if( query.getFilter()==Filter.NONE){
                newFilter=ff.createLogicFilter((Filter) editFilter, FilterType.LOGIC_NOT);
            }else{
                editFilter=ff.createLogicFilter((Filter) editFilter, FilterType.LOGIC_NOT);
                newFilter=ff.createLogicFilter(query.getFilter(), (Filter) editFilter, FilterType.LOGIC_AND);
            }
        } catch (IllegalFilterException e) {
            return query;
        }
        return new DefaultQuery( query.getTypeName(), query.getNamespace(), newFilter, query.getMaxFeatures(), query.getPropertyNames(), query.getHandle());
    }

    public synchronized void clearImage( Rectangle paintArea ) {
        if (ProjectPlugin.isDebugging(Trace.RENDER)) {
            ProjectPlugin.trace(getClass(), "", null); //$NON-NLS-1$
        }
        Graphics2D graphics = getImage().createGraphics();
        graphics.setBackground(new Color(0, 0, 0, 0));
        graphics.setTransform(new AffineTransform());
        graphics.clearRect(paintArea.x, paintArea.y, paintArea.width, paintArea.height);
        graphics.dispose();
    }

    public void setStatus( int status ) {
        if (layerInternal != null && !(layerInternal instanceof SelectionLayer))
            layerInternal.setStatus(status);
    }

    public ILayer getLayer() {
        return getLayerInternal();
    }

    public IGeoResource getGeoResource() {
        return getGeoResourceInternal();
    }

    public int getStatus() {
        if( getLayer()==null )
            return -1;
        return getLayer().getStatus();
    }

    public String getStatusMessage() {
        if( getLayer()==null )
            return null;
        return getLayer().getStatusMessage();
    }

    public void setStatusMessage( String message ) {
        if (getLayerInternal() != null)
            getLayerInternal().setStatusMessage(message);
    }

    public void dispose() {
        image = null;
    }

    public RenderContextImpl copy() {
        return new RenderContextImpl(this);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((geoResourceInternal == null) ? 0 : geoResourceInternal.hashCode());
        result = PRIME * result + ((layerInternal == null) ? 0 : layerInternal.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if( !(obj instanceof RenderContextImpl) )
            return false;
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final RenderContextImpl other = (RenderContextImpl) obj;
        if (geoResourceInternal == null) {
            if (other.geoResourceInternal != null)
                return false;
        } else if (!geoResourceInternal.equals(other.geoResourceInternal))
            return false;
        if (layerInternal == null) {
            if (other.layerInternal != null)
                return false;
        } else if (!layerInternal.equals(other.layerInternal))
            return false;
        return true;
    }

    public synchronized ILabelPainter getLabelPainter() {
        ILabelPainter labelPainter = (ILabelPainter) getMap().getBlackboard().get(LABEL_PAINTER);
        if ( labelPainter==null ){
            LabelCacheDefault defaultLabelCache = new LabelCacheDefault();
            labelPainter=new UDIGLabelCache(defaultLabelCache);
            getMap().getBlackboard().put(LABEL_PAINTER, labelPainter);
        }

        return labelPainter;
    }
} // RenderContextImpl
