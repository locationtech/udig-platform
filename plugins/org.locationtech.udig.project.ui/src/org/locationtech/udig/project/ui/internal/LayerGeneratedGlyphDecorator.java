/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import static org.locationtech.udig.project.internal.provider.LayerItemProvider.GENERATED_ICON;
import static org.locationtech.udig.project.internal.provider.LayerItemProvider.GENERATED_NAME;

import java.io.IOException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.geotools.data.FeatureSource;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.Symbolizer;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.ITransientResolve;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.ui.ImageCache;
import org.locationtech.udig.ui.graphics.Glyph;
import org.locationtech.udig.ui.graphics.SLDs;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;

/**
 * Generate glyph/title - fetch from WMS or derrive from StyleBlackboard.
 * <p>
 * This is a complete heavyweight decorator - there is no messing around with this one. It has its
 * own thread, and will pay attention to events.
 * </p>
 * <p>
 * Generated Content is placed in layer properties:
 * <ul>
 * <li>displayName:
 * <li>displayGlyph:
 * </ul>
 * </p>
 * <p>
 * Generation only kicks in if getGlyph or getName return null.
 * </p>
 * 
 * @author jgarnett
 * @since 0.7.0
 */
public class LayerGeneratedGlyphDecorator implements ILabelDecorator {

    /**
     * Queue of layers needing to be refreshed.
     * <p>
     * Does not allow duplicates to be added.
     * </p>
     */
    LinkedList<Layer> queue = new LinkedList<Layer>(){
        /** <code>serialVersionUID</code> field */
        private static final long serialVersionUID = 3834874663317747760L;

        public void add( int index, Layer aLayer ) {
            if (!contains(aLayer))
                super.add(index, aLayer);
        }

        public boolean add( Layer aLayer ) {
            if (!contains(aLayer))
                return super.add(aLayer);
            return false;
        }

        public void addFirst( Layer aLayer ) {
            if (!contains(aLayer))
                super.addFirst(aLayer);

        }

        public void addLast( Layer aLayer ) {
            if (!contains(aLayer))
                super.addFirst(aLayer);
        }
    };

    private volatile boolean disposed=false;

    /**
     * Piccaso generates pcitures for layers in the queue.
     * <p>
     * This is the sole provider of dynamic artwork for layers. Piccaso will block contacting
     * external servers and so on.
     * </p>
     * <p>
     * If this gets to be a pain we may switch to the dutch school model, perhaps even
     * impressionests based on a sample feature.
     * </p>
     * <p>
     * Any artwork is provided as an ImageDescriptor using the key GENERATED_ICON. This will be
     * turned into an Image by the decorateImage method as required.
     * </p>
     */
    Job picasso = new Job(Messages.LayerGeneratedGlyphDecorator_jobName){ 

        @SuppressWarnings("unchecked")
        public IStatus run( IProgressMonitor monitor ) {
            Layer layer = null;
            SERVICE: while( !disposed  ) {
                synchronized (queue) {
                    if (queue.isEmpty()) {
                        return Status.OK_STATUS;
                    }

                    try {
                        layer = queue.removeFirst();
                        if (!layer.eAdapters().contains(adapterImpl)) {
                            layer.eAdapters().add(adapterImpl);
                        }
                    } catch (NoSuchElementException noLayer) {
                        continue SERVICE;
                    }
                }
                try {
                    boolean notifyIcon = refreshIcon(layer);
                    
                    boolean notifyLabel = refreshLabel(layer);

                    if (notifyIcon || notifyLabel) {
                        refresh(layer);
                    }
                } catch (Throwable t) {
                    // must catch all icon errors or this thread will die :-P
                }
            }
            return Status.OK_STATUS;

        }

        /**
         * Refresh icon if required, true if label was changed.
         * <p>
         * Icon will be placed on GENERATED_ICON
         * </p>
         * 
         * @param layer
         * @param notify
         * @return true if label was changed
         */
        private boolean refreshIcon( Layer layer ) {
            try {
                ImageDescriptor icon = generateIcon(layer);
                if (icon != null) {
                    layer.getProperties().put(GENERATED_ICON, icon);
                    return true;
                }
            } catch (Throwable problem) {
                ProjectUIPlugin.getDefault().getLog().log(
                        new Status(IStatus.WARNING, ProjectUIPlugin.ID, IStatus.INFO,
                                "Could not generate layer glyph " + layer, problem)); //$NON-NLS-1$
                // layer.setStatus(Layer.WARNING);
            }
            return false;
        }

        /**
         * Refresh icon if required, true if icon was changed.
         * 
         * @param layer
         * @param notify
         * @return ture if icon was changed
         */
        private boolean refreshLabel( Layer layer ) {
            String label = label(layer);

            if (label == null || label.length() == 0) {
                try {
                    String gen = generateLabel(layer);
                    // System.out.println( "generated "+ gen );
                    if (gen != null) {
                        layer.getProperties().putString(GENERATED_NAME, gen);
                        return true;
                    }
                } catch (Throwable problem) {
                    ProjectUIPlugin.getDefault().getLog().log(
                            new Status(IStatus.WARNING, ProjectUIPlugin.ID, IStatus.INFO,
                                    "Could not generate name for " + layer, problem)); //$NON-NLS-1$
                    // layer.setStatus(Layer.WARNING);
                }
            }
            return false;
        }
    };

    private static LayerGeneratedGlyphDecorator instance = null;

    public static LayerGeneratedGlyphDecorator getInstance() {
        return instance;
    }

    public LayerGeneratedGlyphDecorator() {
        picasso.setSystem(true);
        picasso.setPriority(Job.DECORATE);
        picasso.schedule();
        instance=this;
    }

    Set<ILabelProviderListener> listeners = new CopyOnWriteArraySet<ILabelProviderListener>();

    Adapter adapterImpl = new AdapterImpl(){
        public void notifyChanged( Notification msg ) {
            if (msg.getNotifier() instanceof Layer) {
                final Layer layer = (Layer) msg.getNotifier();
                if (queue == null) {
                    // we can stop listening now nobody cares
                    layer.eAdapters().remove(this);
                    return;
                }
                switch( msg.getFeatureID(Layer.class) ) {
                case ProjectPackage.LAYER__ICON:
                case ProjectPackage.LAYER__STYLE_BLACKBOARD:
                case ProjectPackage.LAYER__NAME:
                case ProjectPackage.LAYER__GEO_RESOURCES:
                    layer.getProperties().put(GENERATED_ICON, null);
                    layer.getProperties().put(GENERATED_NAME, null);
                    refresh(layer);
                    break;
                }
            }
        }
    };

    void refresh( Layer layer ) {
        if (listeners.isEmpty())
            return;
        LabelProviderChangedEvent event = new LabelProviderChangedEvent(this, layer);
        for( ILabelProviderListener listener : listeners ) {
            listener.labelProviderChanged(event);
        }
    }

    /** Cache of images by resource id */
    static ImageCache cache = new ImageCache();

    /**
     * A non null answer when layer has a good label.
     * <p>
     * Where a good/real means:
     * <ul>
     * <li>label.getLabel() != null
     * <li>label.getProperties().getSTring( GENERATED_NAME ) != null
     * </p>
     * <p>
     * This method does not block and used used by the decorateText and our thread to test/acquire
     * the right text. If null is returned the thread will be started in the hopes of producing
     * something.
     * <p>
     * 
     * @returns Label for layer, or <code>null</code> if unavailable
     */
    static String label( Layer layer ) {
        String label = layer.getName();
        if (label != null && label.length() > 0){
            return label; // layer has a user supplied name
        }
        label = layer.getProperties().getString(GENERATED_NAME);
        if (label != null) {
            return label; // we have already generated one
        }
        return null;
    }

    /**
     * Generate label.
     * <p>
     * This is used to generate a value for layer.getProperties().getString( GENERATED_NAME ).
     * <p>
     * The generated label from Resource.getInfo().getTitle(). This method will block and should not
     * be called from the event thread.
     * </p>
     * 
     * @return gernated layer, or <code>null</code> if none can be determined
     */
    public static String generateLabel( Layer layer ) {
        String name = layer.getName();
        if( name != null ){
            return name; // user supplied name for the win!
        }
        IGeoResource resource = layer.getGeoResource();
        if (resource == null){
            return null;
        }
        String title = resource.getTitle(); // this is from the non-blocking cache!
        if( title == null){
            // fine - no title let us try to connect to find one
            IGeoResourceInfo info = null;
            try {
                info = resource.getInfo(null);
                title = info.getTitle();
                if( title == null ){
                    title = info.getName();
                }
            } catch (IOException e) {
            }
        }
        String layerName = title;
        if( layerName == null ){
            layerName = resource.getID().toBaseFile();
        }
        // Add qualifier if present        
        //String qualifier = resource.getID().getTypeQualifier();
        //if( qualifier != null ){
        //    layerName += "("+qualifier+")";
        //}
        // Side note: Original label, made by item provider uses,
        // resource.getIdentifier() which is non blocking
        //
    	if( layer.hasResource(ITransientResolve.class)){
    		layerName += " *";
    	}
    	return layerName;
    }

    /**
     * A non null answer when layer has a good gylph.
     * <p>
     * Where a good/real means:
     * <ul>
     * <li>label.getGylph() != null
     * <li>label.getProperties().get( GENERATED_GYLPH ) != null
     * </p>
     * <p>
     * This method does not block and used used by the decorateImage and our thread to test/acquire
     * the right image. If <code>null</code> is returned the thread will be started in the hopes
     * of producing something.
     * <p>
     * 
     * @returns Image for layer, or <code>null</code> if unavailable Image icon( Layer layer ) {
     *          ImageDescriptor glyph = layer.getGlyph(); if (glyph != null) return
     *          cache.getImage(glyph); Image genglyph = (Image)
     *          layer.getProperties().get(GENERATED_ICON); if (genglyph != null &&
     *          !genglyph.isDisposed() ) return genglyph; // we have already generated one return
     *          null; }
     */

    /**
     * Genearte label and place in label.getProperties().getSTring( GENERATED_NAME ).
     * <p>
     * Label is genrated from Resource.
     * </p>
     * 
     * @return gernated layer
     */
    public static ImageDescriptor generateIcon( Layer layer ) {
        StyleBlackboard style = layer.getStyleBlackboard();

        if (style != null && !style.getContent().isEmpty()) {
            ImageDescriptor icon = generateStyledIcon(layer);
            if (icon != null)
                return icon;
        }
        ImageDescriptor icon = generateDefaultIcon(layer);
        if (icon != null)
            return icon;
        return null;
    }

    /**
     * Generate icon based on style information.
     * <p>
     * Will return null if an icom based on the current style could not be generated. You may
     * consult generateDefaultIcon( layer ) for a second opionion based on just the layer
     * information.
     * 
     * @param layer
     * @return ImageDecriptor for layer, or null in style could not be indicated
     */
    public static ImageDescriptor generateStyledIcon( Layer layer ) {
        StyleBlackboard blackboard = layer.getStyleBlackboard();
        if (blackboard == null)
            return null;

        Style sld = (Style) blackboard.lookup(Style.class); // or
        // blackboard.get(
        // "org.locationtech.udig.style.sld"
        // );
        if (sld != null) {
            Rule rule = getRule(sld);
            return generateStyledIcon(layer, rule);
        }
        if (layer.hasResource(WebMapServer.class)) {
            return null; // do not support styling for wms yet
        }
        return null;
    }

    private static Rule getRule( Style sld ) {
        Rule rule = null;
        int size = 0;

        for( FeatureTypeStyle style : sld.featureTypeStyles() ) {
            for( Rule potentialRule : style.rules() ) {
                if (potentialRule != null) {
                    Symbolizer[] symbs = potentialRule.getSymbolizers();
                    for( int m = 0; m < symbs.length; m++ ) {
                        if (symbs[m] instanceof PointSymbolizer) {
                            int newSize = SLDs.pointSize((PointSymbolizer) symbs[m]);
                            if (newSize > 16 && size != 0) {
                                // return with previous rule
                                return rule;
                            }
                            size = newSize;
                            rule = potentialRule;
                        } else {
                            return potentialRule;
                        }
                    }
                }
            }
        }
        return rule;
    }

    public static ImageDescriptor generateStyledIcon( ILayer layer, Rule rule ) {
        if (layer.hasResource(FeatureSource.class) && rule != null) {
            SimpleFeatureType type = layer.getSchema();
            GeometryDescriptor geom = type.getGeometryDescriptor();
            if (geom != null) {
                Class geom_type = geom.getType().getBinding();
                if (geom_type == Point.class || geom_type == MultiPoint.class) {
                    return Glyph.point(rule);
                } else if (geom_type == LineString.class || geom_type == MultiLineString.class) {
                    return Glyph.line(rule);
                } else if (geom_type == Polygon.class || geom_type == MultiPolygon.class) {
                    return Glyph.polygon(rule);
                } else if (geom_type == Geometry.class || geom_type == GeometryCollection.class) {
                    return Glyph.geometry(rule);
                }
            }
        }
        IGeoResource resource = layer.findGeoResource(FeatureSource.class);
        if (resource == null)
            return null;
        IGeoResourceInfo info;
        try {
            info = resource.getInfo(null);
        } catch (IOException e) {
            info = null;
        }
        if (info != null) {
            ImageDescriptor infoIcon = info.getImageDescriptor();
            if (infoIcon != null)
                return infoIcon;
        }
        if (resource.canResolve(GridCoverageReader.class)) {
            ImageDescriptor icon = Glyph.grid(null, null, null, null);
            if (icon != null)
                return icon;
        }
        if (resource.canResolve(FeatureSource.class)) {
            ImageDescriptor icon = Glyph.geometry(rule);
            if (icon != null)
                return icon;
        }
        return null;
    }

    /**
     * Generate icon based on simple layer type information without style.
     * <p>
     * The following information is checked:
     * <ul>
     * <li>All WMS resources known to the layer - they often have default icon
     * <li>FeatureSoruce known to the layer - icon can be based on SimpleFeatureType
     * <li>IGeoResourceInfo type information
     * </ul>
     * </p>
     * 
     * @param layer
     * @return Icon based on layer, null if unavailable
     */
    static ImageDescriptor generateDefaultIcon( Layer layer ) {
        // check for a WMS layer first as it has a pretty icon
        if (layer.hasResource(WebMapServer.class) && layer.hasResource(ImageDescriptor.class)) {
            try {
                ImageDescriptor legendGraphic = layer.getResource(ImageDescriptor.class, null);
                if (legendGraphic != null)
                    return legendGraphic;
            } catch (IOException notAvailable) {
                // should not really have happened
            }
        }
        // lets try for featuretype based glyph
        //
        if (layer.hasResource(FeatureSource.class)) {

            SimpleFeatureType type = layer.getSchema();
            GeometryDescriptor geom = type.getGeometryDescriptor();
            if (geom != null) {
                Class geom_type = geom.getType().getBinding();
                if (geom_type == Point.class || geom_type == MultiPoint.class) {
                    return Glyph.point(null, null);
                } else if (geom_type == LineString.class || geom_type == MultiLineString.class) {
                    return Glyph.line(null, SLDs.NOTFOUND);
                } else if (geom_type == Polygon.class || geom_type == MultiPolygon.class) {
                    return Glyph.polygon(null, null, SLDs.NOTFOUND);
                } else if (geom_type == Geometry.class || geom_type == GeometryCollection.class) {
                    return Glyph.geometry(null, null);
                } else {
                    return Glyph.geometry(null, null);
                }
            }
        }
        
        //
        // Resource based glyph?
        //
        IGeoResourceInfo info = null;
        try {
            if( !layer.getGeoResources().isEmpty() ){
                info = layer.getGeoResources().get(0).getInfo(null);
            }
        } catch (IOException e) {
            //
        }
        if (info != null) {
            ImageDescriptor infoIcon = info.getImageDescriptor();
            if (infoIcon != null)
                return infoIcon;
        }

        if (layer.hasResource(GridCoverageReader.class)) {
            ImageDescriptor icon = Glyph.grid(null, null, null, null);
            if (icon != null)
                return icon;
        }
        if (layer.hasResource(FeatureSource.class)) {
            ImageDescriptor icon = Glyph.geometry(null, null);
            if (icon != null)
                return icon;
        }
        return null; // default probided by lable provider will have to do
    }

    /**
     * @see org.eclipse.jface.viewers.ILabelDecorator#decorateText(java.lang.String,
     *      java.lang.Object)
     */
    public String decorateText( String text, Object element ) {
        if (!(element instanceof Layer))
            return null;
        Layer layer = (Layer) element;
        try {
            String label = label(layer); // test for label name or generated
            // name

            if (label != null && label.length() != 0)
                return label;

            synchronized (queue) {
                if (!queue.contains(layer)) {
                    queue.add(layer); // thread will wake up and generate us a
                    // layer
                    picasso.schedule();
                }
            }
        } catch (Throwable problem) {
            ProjectUIPlugin.getDefault().getLog().log(
                    new Status(IStatus.WARNING, ProjectUIPlugin.ID, IStatus.INFO,
                            "Generated name unavailable " + layer, problem)); //$NON-NLS-1$
        }
        return null; // use existing default from item provider
    }

    /**
     * We are not allowed to block, test if generation is needed and start up the queue.
     * <p>
     * State Table of Image \ Image Descriptor:
     * 
     * <pre><code>
     *            | null         | icon                
     *   ---------+--------------+---------------------+
     *   disposed | queue        | image =             |
     *    or null |   layer      |  icon.createImage() |
     *   ---------+--------------+---------------------+
     *   image    | both         | image               |
     *            +--------------+---------------------+
     * </code></pre>
     * 
     * This attempts to reduce the amount of flicker experienced as the layer figures out its glyph
     * in the face of many events.
     * </p>
     * <p>
     * Everyone gives us events - who gives us icons?
     * <ul>
     * <li>If the user has given the layer an icon we don't need to generate anything.
     * <li>piccaso will wait on the queue and generate icons, and refresh the decorator.
     * <li>We will get the refresh and generate an Image from the Icon, we can use this image when
     * we are nexted refreshed.
     * <li>A random eclipse code will dispose our Images, and refrsh us (We can still generate our
     * images from the saved icon).
     * <li>TODO Review : The listener will watch for changes to layer,if any look interesting the icon
     * will be cleared and we will be refreshed. We still have our image so their will be no
     * downtime while waiting for piccaso to make us a new Icon.
     * </ul>
     * So what happens for a layer that we cannot generate a icon for? We will place it in the queue
     * *every* time. Who knows maybe style or something will change and we can do better then the
     * default.
     * </p>
     * 
     * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(org.eclipse.swt.graphics.Image,
     *      java.lang.Object)
     */
    public Image decorateImage( Image origionalImage, Object element ) {
        if (!(element instanceof Layer))
            return null;

        Layer layer = (Layer) element;
        if (layer.getIcon() != null)
            return null; // don't replace user's glyph

        ImageDescriptor icon = (ImageDescriptor) layer.getProperties().get(GENERATED_ICON);
        if (icon == null) { // we need to generate our icon - check every time
            // it may now be possible
            synchronized (queue) {
                queue.add(layer); // thread will wake up and generate us a
                // layer
                picasso.schedule();
            }
        }
        return null; // next time through the origionalImage will be based on
        // our icon
        /*
         * Image image = (Image) layer.getProperties().get(GENERATED_IMAGE); if( image != null ){
         * if( !image.isDisposed()){ //return image; // we have an image already to go } image =
         * null; // forget this image it is dead layer.getProperties().put( GENERATED_IMAGE, null ); }
         * if( icon != null ){ Image newImage = icon.createImage(); // returns null on error if(
         * newImage != null ){ layer.getProperties().put( GENERATED_IMAGE, newImage ); return
         * newImage; } icon = null; // icon did not work - better clear it and try again.
         * layer.getProperties().put( GENERATED_ICON, null ); } return null; // use default from
         * item provider (often based on GeoResource type)
         */
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void addListener( ILabelProviderListener listener ) {
        listeners.add(listener);
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {
        picasso.cancel();
        Thread.yield();
        disposed=true;
        queue.clear();

        // clean up 
        if (listeners != null) {
            listeners.clear();
            listeners = null;
        }
        if (cache != null) {
            cache.dispose();
            cache = null;
        }
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
     *      java.lang.String)
     */
    public boolean isLabelProperty( Object element, String property ) {
        return true;
        /*
         * return "glyph".equals( property ) || "styleBlackboard".equals( property ) ||
         * "name".equals( property ) || "geoResources".equals( property );
         */
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void removeListener( ILabelProviderListener listener ) {
        listeners.remove(listener);
    }

}
