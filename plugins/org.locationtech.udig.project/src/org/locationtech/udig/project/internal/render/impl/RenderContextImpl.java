/**
 * <copyright></copyright> $Id$
 */
package org.locationtech.udig.project.internal.render.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.media.jai.JAI;
import javax.media.jai.TileCache;

import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.IllegalFilterException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.operation.matrix.GeneralMatrix;
import org.geotools.renderer.label.LabelCacheImpl;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.Trace;
import org.locationtech.udig.project.internal.impl.AbstractContextImpl;
import org.locationtech.udig.project.internal.render.RenderContext;
import org.locationtech.udig.project.internal.render.SelectionLayer;
import org.locationtech.udig.project.render.ILabelPainter;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.referencing.operation.MathTransform2D;

/**
 * The default implementation of the RenderContext interface.
 * <p>
 * This method is responsible for holding on to an Image for a
 * renderer to draw into. A renderer can supply an image; or
 * ask the render context to create one.
 * <p>
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
        
    /**
     * The size of the image (width and height in pixels)
     */
    protected Dimension imagesize = null;
    
    /**
     * The "world" bounds that the tile represents.
     */
    protected ReferencedEnvelope imageBounds = null;

    public static final BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);

    /**
     * Key used to retrieve the LabelPainter to and from the map blackboard.
     */
    private static final String LABEL_PAINTER = "LABEL_PAINTER"; //$NON-NLS-1$

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

    protected TileCache tempCache;

    /**
     * For context specific label painters; if null
     * then the label painter on the blackboard is used.
     * 
     * @see #getLabelPainter()
     * @see #setLabelPainterLocal(ILabelPainter)
     */
    private ILabelPainter labelPainterLocal;
    
    //private ILabelPainter labelPainter;
    
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
        
        if (impl.imagesize != null){
            this.imagesize = new Dimension(impl.imagesize);    
        }
        
        this.imageBounds = impl.imageBounds;
    }

    public synchronized TileCache getTileCache(){
        if( tempCache == null){
            tempCache =JAI.createTileCache();
            tempCache.setMemoryCapacity(16*1024*1024);
            tempCache.setMemoryThreshold(0.75f);
        }
        return tempCache;
    }
    /**
     * Sets the size of the image.  If set to null the size of the image will
     * be the same as the mapdisplay.
     * 
     *<p>This is used by the tile rendering system to use fixed tile sized images.
     *
     * @param d
     */
    public void setImageSize(Dimension d){
        this.imagesize = d;
    }
    /**
     * Provide a BufferedImage of the correct size for the map display.
     * 
     * @return BufferedImage for use by the Renderer
     */
    public BufferedImage getImage() {
        Dimension size = getImageSize();
        if( size == null || size.width < 1 || size.height <1 ){
            return dummyImage; // dummy image
        }
        return getImage(size.width, size.height); // will create if needed
    }
    
    /**
     * Updates the image associated with the context
     * to point to the new image.
     *
     * @param bi   new image
     */
   public synchronized void setImage( BufferedImage bi ) {
        this.image = bi;
    }
   
   
    /**
     * This method will create an image of the requested size.
     * <p>
     * The image will be created if needed; this implementation
     * will make use of the swtimage if it has been previously created
     * with a getSWTImage() and the sizes match.
     * </p>
     * @return a BufferedImage of the requested size (the image is cached) 
     */
    public synchronized BufferedImage getImage( int width, int height ) {
        if (width < 1 || height < 1){
            return dummyImage;
        }
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
//        IMapDisplay mapDisplay = getMapDisplay();
//        if( mapDisplay!=null ){
//        	clearImage(new Rectangle(0, 0, mapDisplay.getWidth(), mapDisplay.getHeight()));
//        }
        
        clearImage(new Rectangle(0, 0, getImageSize().width, getImageSize().height));
    }

    public Query getFeatureQuery() {
        Query query = getLayer().getQuery(getLayer() instanceof SelectionLayer);
        
        if( query.getFilter()==Filter.EXCLUDE ){
            return query; // nothing to draw get out of here!
        }
        
        FilterFactory ff=CommonFactoryFinder.getFilterFactory();
        Object editFilter=getLayer().getBlackboard().get(ProjectBlackboardConstants.MAP__RENDERING_FILTER);
        if (!(editFilter instanceof Filter) ){
            return query;
        }
        if( (editFilter instanceof Id) && ((Id)editFilter).getIDs().isEmpty() ){
        	return query;
        }
        
        Filter newFilter;
        try {
            if( query.getFilter()==Filter.INCLUDE){
                newFilter=ff.not((Filter) editFilter);
            }else{
                editFilter=ff.not((Filter) editFilter);
                newFilter=ff.and((Filter)query.getFilter(), (Filter) editFilter);
            }
        } catch (IllegalFilterException e) {
            return query;
        }
        // return new Query( query.getTypeName(), query.getNamespace(), newFilter, query.getMaxFeatures(), query.getPropertyNames(), query.getHandle());
        // Use copy constructor to allow for Query API to change over time
        Query newQuery = new Query( query );
        newQuery.setFilter( newFilter );
        return newQuery;
    }

    public synchronized void clearImage( Rectangle paintArea ) {
        if (ProjectPlugin.isDebugging(Trace.RENDER)) {
            ProjectPlugin.trace(getClass(), "", null); //$NON-NLS-1$
        }
        Graphics2D graphics = getImage().createGraphics();
        graphics.setBackground(new Color(0, 0, 0, 0));
//        graphics.setTransform(new AffineTransform());
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
        if( tempCache != null){
            tempCache.flush();
            tempCache = null;
        }
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
        
        //check image size
        if (imagesize == null){
            if (other.imagesize != null){
                return false;
            }
        }else{
            if (other.imagesize == null){
                return false;
            }else{
                if (imagesize.height != other.imagesize.height || imagesize.width != other.imagesize.width){
                    return false;
                }
            }
        }
        
        //check image bounds
        if (imageBounds == null){
            if (other.imageBounds != null){
                return false;
            }
        }else {
            if (!imageBounds.equals(other.imageBounds)){
                return false;
            }
        }
        
        return true;
    }

    
    
    /**
     * Sets the label painter to use with the context and only this context.  
     * 
     * <p>This method
     * assumes there is are multiple label painters for 
     * the map and this applies only to this context.  
     * It is used in the tiled rendering system - each tile has
     * its own label painter.
     * </p>
     * <p>
     * This is used to draw the labels for features.
     * </p>
     *
     * @param labelPainter
     */
    public void setLabelPainterLocal(ILabelPainter labelPainter) {
        this.labelPainterLocal = labelPainter;
    }
    

    
    /**
     * Gets the label painter.  
     * <p>
     * If the labelPainter is null then it look on the map
     * blackboard for a labelPainter. If it can't find one it creates one
     * and adds it to the map blackboard.
     * </p>
     * 
     */
    public ILabelPainter getLabelPainter() {
        synchronized (LABEL_PAINTER) {
            if (labelPainterLocal != null){
                return labelPainterLocal;
            }
        
            ILabelPainter labelPainter = (ILabelPainter) getMap().getBlackboard().get(LABEL_PAINTER);
            if (labelPainter == null){
                //create a new one and put it on the blackboard for others to use
                LabelCacheImpl defaultLabelCache = new LabelCacheImpl();
                labelPainter=new UDIGLabelCache(defaultLabelCache);
                getMap().getBlackboard().put(LABEL_PAINTER, labelPainter);
            }
            return labelPainter;
        }
    }

    /**
     * Sets the label painter to use with the context.  This method
     * assumes there is a single label painter for 
     * then entire map.
     * <p>
     * This is used to draw the labels for features.
     * </p>
     *
     * @param labelPainter
     */
    public synchronized void setLabelPainter(ILabelPainter labelPainter){
        getMap().getBlackboard().put(LABEL_PAINTER, labelPainter);
    }
    
    /**
     * Returns the bounds represented by this render context.
     */
    public ReferencedEnvelope getImageBounds() {
        if (imageBounds == null ){
            //returns the bounds of the viewport model
            return getViewportModel().getBounds();
        }
        //return the image bounds
        return imageBounds;
    }

    /**
     * Returns the size of the image to be generated for display.  If imagesize is null then it returns the display size from
     * the map display.
     * 
     * <p>This is used by the tile rendering system so a tile can have a fixed size.
     *
     * @return
     */
    public Dimension getImageSize(){
        if (imagesize == null){
            IMapDisplay mapDisplay = getMapDisplay();
            if( mapDisplay == null ){
                return null;
            }
            return mapDisplay.getDisplaySize();
        }else{
            return imagesize;
        }
    }
    /**
     * Sets the image bounds represented by this context.  If set to null then the bounds of the image is assumed to match the bounds
     * of the viewport model.
     */
    public void setImageBounds( ReferencedEnvelope bounds ) {
        this.imageBounds = bounds;
    }
    
    /**
     * Converts a coordinate expressed on the image
     * back to real world coordinates.
     * 
     *  <p>A convenience method.
     * 
     * @param x horizontal coordinate on device space (image)
     * @param y vertical coordinate on device space (image)
     * 
     * @return The correspondent real world coordinate
     */
    @Override
    public Coordinate pixelToWorld( int x, int y ){
        return ScaleUtils.pixelToWorld(x, y, getImageBounds(), getImageSize());
    }
    
    
    /**
     * Gets up the affine transform that will transform from the world to the display of size
     * destination. A convenience method. This method is independent of the CRS.
     * 
     * @return a transform that maps from real world coordinates to the screen
     */
    @Override
    public AffineTransform worldToScreenTransform() { 
        return ScaleUtils.worldToScreenTransform(getImageBounds(), getImageSize());
    }

    /**
     * Returns the pixel on the screen for a given coordinate in world space.
     * 
     * @param coord A coordinate in world space.
     * @return The pixel on the screen that the world coordinate is drawn on.
     * @see Point
     * @see Coordinate
     */
    @Override
    public Point worldToPixel( Coordinate coord ) {
        return ScaleUtils.worldToPixel(coord, getImageBounds(), getImageSize());
    }

    @Override
    public MathTransform2D worldToScreenMathTransform() {
        GeneralMatrix matrix = new GeneralMatrix(worldToScreenTransform());
        try {
            return (MathTransform2D) ReferencingFactoryFinder.getMathTransformFactory(null)
                    .createAffineTransform(matrix);
        } catch (Exception e) {
            return null;
        }
    }
    
} // RenderContextImpl
