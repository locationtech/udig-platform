/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.printing.model.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PlatformUI;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.core.Pair;
import org.locationtech.udig.mapgraphic.MapGraphicChooserDialog;
import org.locationtech.udig.mapgraphic.internal.MapGraphicRenderer;
import org.locationtech.udig.mapgraphic.internal.MapGraphicResource;
import org.locationtech.udig.mapgraphic.internal.MapGraphicService;
import org.locationtech.udig.mapgraphic.style.LocationStyleContent;
import org.locationtech.udig.printing.model.AbstractBoxPrinter;
import org.locationtech.udig.printing.model.Box;
import org.locationtech.udig.printing.model.Page;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ILayerListener;
import org.locationtech.udig.project.LayerEvent;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.LayerDecorator;
import org.locationtech.udig.project.internal.LayerFactory;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.render.CompositeRenderContext;
import org.locationtech.udig.project.internal.render.RenderContext;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.internal.render.impl.CompositeRenderContextImpl;
import org.locationtech.udig.project.internal.render.impl.ScaleUtils;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.BoundsStrategy;

/**
 * Allows a Map graphic to be embedded into a box separate from the MapBox.
 *
 * @author jesse
 * @since 1.1.0
 */
public class MapGraphicBoxPrinter extends AbstractBoxPrinter {

    private static final int DEFAULTDPI = 90;

    private int usedDpi = 90;

    private float scaleFactor = Float.NaN;

    private static final Layer NULL = new LayerDecorator(null);

    private Layer layer;

    private String warning;

    private ILayerListener layerListener = new ILayerListener() {

        @Override
        public void refresh(LayerEvent event) {
            setDirty(true);
        }

    };

    private boolean inPreviewMode;

    public MapGraphicBoxPrinter() {
        System.out.println();
    }

    public MapGraphicBoxPrinter(Page page) {
        if (page != null) {
            scaleFactor = (float) page.getSize().width / (float) page.getPaperSize().height;
        }
    }

    @Override
    public void draw(Graphics2D graphics, IProgressMonitor monitor) {
        super.draw(graphics, monitor);
        if (Float.isNaN(scaleFactor)) {
            List<Box> boxes = getBox().getPage().getBoxes();
            for (Box box : boxes) {
                Object adapter = box.getBoxPrinter().getAdapter(Map.class);
                if (adapter != null) {
                    scaleFactor = (float) box.getSize().width / (float) box.getPaperSize().height;
                    break;
                }
            }
        }

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
    private void drawGraphic(Graphics2D graphics, IProgressMonitor monitor) {
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

    @Override
    public void createPreview(Graphics2D graphics, IProgressMonitor monitor) {
        inPreviewMode = true;
        draw(graphics, monitor);
        setDirty(false);
        inPreviewMode = false;
    }

    private CompositeRenderContext createRenderContext(Pair<Map, Pair<Dimension, Double>> info,
            Layer layer) {
        Map map = info.getLeft();
        Dimension size = info.getRight().getLeft();
        double scale = info.getRight().getRight();

        ViewportModel viewportModel = map.getViewportModelInternal();
        ReferencedEnvelope bounds = viewportModel.getBounds();
        BoundsStrategy boundsStrategy = new BoundsStrategy(scale);

        RenderContext context = null;
        if (inPreviewMode && !Float.isNaN(scaleFactor)) {
            float dpiFloat = DEFAULTDPI * scaleFactor;
            context = ApplicationGIS.configureMapForRendering(map, size, (int) dpiFloat,
                    boundsStrategy, bounds);
        } else {
            context = ApplicationGIS.configureMapForRendering(map, size, DEFAULTDPI, boundsStrategy,
                    bounds);
        }

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
    private Pair<Map, Pair<Dimension, Double>> findMap() {
        List<Box> boxes = getBox().getPage().getBoxes();
        for (Box box : boxes) {
            if (box.getBoxPrinter() instanceof MapBoxPrinter) {
                MapBoxPrinter mapBoxPrinter = ((MapBoxPrinter) box.getBoxPrinter());
                Map map = mapBoxPrinter.getMap();
                Map copy = EcoreUtil.copy(map);

                // we need the original map and its box to correctly calculate the
                // scale so we must do it now
                Dimension size = new Dimension(box.getSize().width, box.getSize().height);
                ViewportModel viewportModel = map.getViewportModelInternal();

                ReferencedEnvelope bounds = viewportModel.getBounds();

                double scale = ScaleUtils.calculateScaleDenominator(bounds, size, 90);

                Pair<Dimension, Double> details = new Pair<>(size, scale);
                return new Pair<>(copy, details);
            }
        }
        return null;
    }

    /**
     * Warns the user that a MapGraphic needs to be set
     */
    private void drawWarning(Graphics2D graphics, String message) {
        graphics.setColor(Color.BLACK);
        int height = graphics.getFontMetrics().getHeight();

        int base = (getBox().getSize().height - height) / 2 + height;

        graphics.drawString(message, 0, base);

    }

    @Override
    public void save(IMemento memento) {
        memento.putTextData(URLUtils.urlToString(layer.getID(), false));
    }

    @Override
    public void load(IMemento memento) {
        String url = memento.getTextData();

        try {
            IGeoResource resource = CatalogPlugin.getDefault().getLocalCatalog().getById(
                    IGeoResource.class, new ID(new URL(MapGraphicService.SERVICE_URL, url)),
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
        display.asyncExec(new Runnable() {
            @Override
            public void run() {
                MapGraphicChooserDialog dialog = new MapGraphicChooserDialog(
                        display.getActiveShell(), false);
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

    @Override
    public String getExtensionPointID() {
        return "org.locationtech.udig.printing.ui.standardBoxes"; //$NON-NLS-1$
    }

    public void setMapGraphic(MapGraphicResource resource) {
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

    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter) {
        if (adapter.isAssignableFrom(ILayer.class)) {
            return layer;
        }
        if (adapter.isAssignableFrom(IGeoResource.class)) {
            return getMapGraphic();
        }
        return Platform.getAdapterManager().getAdapter(this, adapter);
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

    public void setStyleBlackboardKey(String key, Object value) {
        if (layer == null) {
            throw new IllegalStateException(
                    "Please set the map graphic before calling this method."); //$NON-NLS-1$
        }
        layer.getStyleBlackboard().put(key, value);
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
