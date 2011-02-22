/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.printing.model.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.core.Pair;
import net.refractions.udig.mapgraphic.MapGraphicChooserDialog;
import net.refractions.udig.mapgraphic.internal.MapGraphicRenderer;
import net.refractions.udig.mapgraphic.internal.MapGraphicResource;
import net.refractions.udig.mapgraphic.internal.MapGraphicService;
import net.refractions.udig.mapgraphic.style.LocationStyleContent;
import net.refractions.udig.printing.model.AbstractBoxPrinter;
import net.refractions.udig.printing.model.Box;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ILayerListener;
import net.refractions.udig.project.LayerEvent;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.LayerDecorator;
import net.refractions.udig.project.internal.LayerFactory;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.internal.render.CompositeRenderContext;
import net.refractions.udig.project.internal.render.RenderContext;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.internal.render.impl.CompositeRenderContextImpl;
import net.refractions.udig.project.internal.render.impl.ScaleUtils;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.BoundsStrategy;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PlatformUI;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * Allows a Map graphic to be embedded into a box separate from the MapBox.
 *
 * @author jesse
 * @since 1.1.0
 */
public class MapGraphicBoxPrinter extends AbstractBoxPrinter {

    private static final Layer NULL = new LayerDecorator(null);

    private Layer layer;
    private String warning;

    private ILayerListener layerListener = new ILayerListener(){

        public void refresh( LayerEvent event ) {
            setDirty(true);
        }

    };

    public void draw( Graphics2D graphics, IProgressMonitor monitor ) {
        if (warning != null) {
            drawWarning(graphics, warning);
            return;
        }

        if (layer == null) {
            queryForMapGraphic();
        }

        if (layer == null) {
            drawWarning(graphics, "Loading...");
        } else if (layer == NULL) {
            drawWarning(graphics, "A Decorator needs to be selected");
        } else {
            drawGraphic(graphics, monitor);
        }

    }

    /**
     * Draws the graphic.
     */
    private void drawGraphic( Graphics2D graphics, IProgressMonitor monitor ) {
        Pair<Map, Pair<Dimension, Double>> info = findMap();

        if (info == null) {
            drawWarning(graphics, "A map needs to be added to this page");
        }

        List<Layer> layers = info.getLeft().getLayersInternal();
        org.eclipse.draw2d.geometry.Dimension size = getBox().getSize();
        Rectangle rect = new Rectangle(size.width - 1, size.height - 1);
        layer.getStyleBlackboard().put(LocationStyleContent.ID, rect);
        layers.add(layer);

        MapGraphicRenderer renderer = new MapGraphicRenderer();
        CompositeRenderContext toUseForRendering = createRenderContext(info, layer);
        renderer.setContext(toUseForRendering);

        renderer.render(graphics, monitor);
    }

    private CompositeRenderContext createRenderContext( Pair<Map, Pair<Dimension, Double>> info, Layer layer ) {
        Map map = info.getLeft();
        Dimension size = info.getRight().getLeft();
        double scale = info.getRight().getRight();

        ViewportModel viewportModel = map.getViewportModelInternal();
        ReferencedEnvelope bounds = (ReferencedEnvelope) viewportModel.getBounds();
        BoundsStrategy boundsStrategy = new BoundsStrategy(scale);
        RenderContext context = ApplicationGIS.configureMapForRendering(map, size, 90,
                boundsStrategy, bounds);

        context.setLayerInternal(layer);
        context.setGeoResourceInternal(layer.getGeoResource());

        CompositeRenderContext toUseForRendering = new CompositeRenderContextImpl();
        toUseForRendering.addContexts(Collections.singleton(context));
        toUseForRendering.setRenderManagerInternal(context.getRenderManagerInternal());
        toUseForRendering.setMapInternal(context.getMapInternal());
        return toUseForRendering;
    }

    /**
     * @return Pair<CopyOfMap, Pair<MapBoxSize,ScaleDenominator>>
     */
    private Pair<Map, Pair<Dimension,Double>> findMap() {
        List<Box> boxes = getBox().getPage().getBoxes();
        for( Box box : boxes ) {
            if (box.getBoxPrinter() instanceof MapBoxPrinter) {
                MapBoxPrinter mapBoxPrinter = ((MapBoxPrinter) box.getBoxPrinter());
                Map map = mapBoxPrinter.getMap();
                Map copy = (Map) EcoreUtil.copy(map);

                // we need the original map and its box to correctly calculate the
                // scale so we must do it now
                Dimension size = new Dimension(box.getSize().width, box.getSize().height);
                ViewportModel viewportModel = map.getViewportModelInternal();

                ReferencedEnvelope bounds = (ReferencedEnvelope) viewportModel.getBounds();

                double scale = ScaleUtils.calculateScaleDenominator(bounds, size, 90);

                Pair<Dimension, Double> details = new Pair<Dimension, Double>(size, scale);
                return new Pair<Map, Pair<Dimension,Double>>(copy, details );
            }
        }
        return null;
    }

    /**
     * Warns the user that a MapGraphic needs to be set
     */
    private void drawWarning( Graphics2D graphics, String message ) {
        graphics.setColor(Color.BLACK);
        int height = graphics.getFontMetrics().getHeight();

        int base = (getBox().getSize().height - height) / 2 + height;

        graphics.drawString(message, 0, base);

    }

    @Override
    public void save( IMemento memento ) {
        memento.putTextData(URLUtils.urlToString(layer.getID(), false));
    }

    @Override
    public void load( IMemento memento ) {
        String url = memento.getTextData();

        try {
            IGeoResource resource = CatalogPlugin.getDefault().getLocalCatalog().getById(
                    IGeoResource.class, new URL(MapGraphicService.SERVICE_URL, url),
                    new NullProgressMonitor());
            setMapGraphic((MapGraphicResource) resource);
        } catch (IOException e) {
            // uh oh must be missing a plugin
            setMissingResourceWarning();
        }
    }

    private void setMissingResourceWarning() {
        warning = "The map graphic is missing from your installation, talk to the creator of this page and make sure you have the correct plugins.";
    }

    private void queryForMapGraphic() {
        final Display display = findDisplay();
        display.asyncExec(new Runnable(){
            public void run() {
                MapGraphicChooserDialog dialog = new MapGraphicChooserDialog(display
                        .getActiveShell(), false);
                dialog.open();
                if (dialog.getSelectedResources().isEmpty()) {
                    layer = NULL;
                } else {
                    IGeoResource resource = dialog.getSelectedResources().get(0);
                    setMapGraphic((MapGraphicResource) resource);
                }
                setDirty(true);

            }
        });
    }

    private Display findDisplay() {
        Display display = Display.getCurrent();
        if (display == null) {
            display = PlatformUI.getWorkbench().getDisplay();
        }
        return display;
    }

    public String getExtensionPointID() {
        return "net.refractions.udig.printing.ui.standardBoxes"; //$NON-NLS-1$
    }

    public void setMapGraphic( MapGraphicResource resource ) {
        LayerFactory factory = ProjectFactory.eINSTANCE.createLayerFactory();
        try {
            if (layer != null) {
                layer.removeListener(layerListener);
            }
            layer = factory.createLayer(resource);
            layer.addListener(layerListener);
        } catch (IOException e) {
            setMissingResourceWarning();
        }
        setDirty(true);
    }

    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adapter ) {
        if (adapter.isAssignableFrom(ILayer.class)) {
            return layer;
        }
        if (adapter.isAssignableFrom(IGeoResource.class)) {
            return getMapGraphic();
        }
        return null;
    }

    public MapGraphicResource getMapGraphic() {
        try {
            if (layer != null) {
                return layer.getResource(MapGraphicResource.class, new NullProgressMonitor());
            } else {
                return null;
            }
        } catch (IOException e) {
            // won't happen
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the layer contained in the box
     *
     * @return the layer contained in the box
     */
    public Layer getLayer() {
        return layer;
    }

}
