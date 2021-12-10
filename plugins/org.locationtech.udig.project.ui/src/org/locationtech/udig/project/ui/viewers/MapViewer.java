/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.viewers;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.command.navigation.SetViewportBBoxCommand;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.internal.RenderManagerDynamic;
import org.locationtech.udig.project.ui.internal.TiledRenderManagerDynamic;
import org.locationtech.udig.project.ui.internal.render.displayAdapter.impl.ViewportPaneSWT;
import org.locationtech.udig.project.ui.internal.render.displayAdapter.impl.ViewportPaneTiledSWT;
import org.locationtech.udig.project.ui.internal.tool.ToolContext;
import org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.tool.IMapEditorSelectionProvider;
import org.locationtech.udig.project.ui.tool.ModalTool;

/**
 * A concrete viewer based on a ViewportPane widget.
 * <p>
 * In order to facilitate experimentation with a range of GIS widgets we have created a JFace
 * "viewer" for working with a Map.
 * </p>
 * This component implements MapPart allowing your view to make use of eclipse delegate facilities.
 *
 * @author Jody Garnett
 * @since 1.1.0
 * @version 1.2.3
 */
public class MapViewer {
    /** This viewer's ViewportPane control. */
    protected ViewportPane viewport;

    /** This is the current map. */
    protected Map map;

    /** This is the workbench part displaying the map; it should implement MapPart or I will cry. */
    private IWorkbenchPart part;

    /** Context menu or view menu or something nice for the user to look at */
    private Menu menu;

    /** Allows to get information about the active Map tool */
    private ModalTool activeTool;

    /** Allows a tool to communicate with stuff - ie a "tool site" */
    private ToolContextImpl toolcontext;

    /** Draws the map */
    private RenderManager renderManager;

    /** Preferred scales like open layers (used for zoom in, zoom out etc...) */
    private double[] resolutions;

    /** This viewer's map part */
    private MapPart mapPart;

    /** This is for testing only DO NOT USE OTHERWISE */
    public boolean isTesting;

    /**
     * Creates a map viewer on a newly-created viewport pane under the given parent.
     * <p>
     * The viewport pane is created using the SWT style bits <code>MULTI, H_SCROLL, V_SCROLL,</code>
     * and <code>BORDER</code>. The viewer has no input, no content provider, a default label
     * provider, no sorter, and no filters. The table has no columns.
     * <p>
     * The default SWT.DOUBLE_BUFFERED for the canvas and SWT.MULTI for a tiled renderer
     *
     * @param parent the parent control
     */
    public MapViewer(Composite parent, MapPart mapPart) {
        this(parent, mapPart, SWT.DOUBLE_BUFFERED | SWT.MULTI);
    }

    /**
     * Creates a table viewer on a newly-created table control under the given parent. The table
     * control is created using the given style bits. The viewer has no input, no content provider,
     * a default label provider, no sorter, and no filters. The table has no columns.
     *
     * @param parent the parent control
     * @param mapPart the map part of this viewer.
     * @param style Use SWT.SINGLE or SWT.MULTI to control the use of tiles, SWT.DOUBLE_BUFFERED and
     *        SWT.NO_BACKGROUND can be used to configure the canvas.
     */
    public MapViewer(Composite parent, MapPart mapPart, int style) {
        Assert.isNotNull(mapPart, "MapPart required");

        this.mapPart = mapPart;
        if ((style & SWT.MULTI) == SWT.MULTI) {
            viewport = new ViewportPaneTiledSWT(parent, style, this.mapPart);
        } else if ((style & SWT.SINGLE) == SWT.SINGLE) {
            viewport = new ViewportPaneSWT(parent, style, this.mapPart);
        } else {
            viewport = new ViewportPaneSWT(parent, this.mapPart);
        }
    }

    /**
     * Used to internal "MapPart" to the provided WorkbenchPart (any status messages etc.. would be
     * sent to the view or editor provided).
     * <p>
     * This is an *optional* step; if you can call this method setSelectionProvider will work.
     * </p>
     *
     * @param part Editor or View workbench part
     */
    public void init(IWorkbenchPart part) {
        this.part = part;
        this.mapPart = (MapPart) part;
    }

    /**
     * Access to the ViewportPane
     * <p>
     * The Viewport is usually an SWT widget; to be sure check getControl().
     * </p>
     *
     * @return viewport used to display the map
     */
    public ViewportPane getViewport() {
        return viewport;
    }

    /**
     * Access to the control (often this is the same as getViewport().
     * <p>
     * You use this to work with DnD or context menu integration tasks.
     * </p>
     *
     * @return control May be null if SWT not used, the viewport does not extend Control, or if
     *         disposed.
     *
     */
    public Control getControl() {
        if (viewport instanceof Control) {
            return ((Control) viewport);
        }
        return null;
    }

    /**
     * The render manager responsible for rendering items in this view.
     *
     * @return
     */
    public RenderManager getRenderManager() {
        return renderManager;
    }

    /**
     * This is the Map; you can send addLayer commands and so forth to this.
     */
    public Map getMap() {
        return map;
    }

    /**
     * Used to ask the widget to display the provided map.
     * <p>
     * This method will register a listener on the provided map and the viewer will refresh as the
     * map state changes.
     *
     * @param map to display; or null for none.
     */
    public void setMap(Map map) {
        if (this.map == map) {
            return;
        }

        // remove previous listeners if they exist
        if (this.map != null) {
            renderManager = this.map.getRenderManagerInternal();
            viewport.removePaneListener(this.map.getViewportModelInternal());
            renderManager.setMapInternal(null);
            renderManager.setMapDisplay(null);
            viewport.setRenderManager(null);
        }

        // add the new map
        this.map = map;

        if (this.map == null)
            return;

        if (map.getRenderManager() == null) {
            if (viewport instanceof ViewportPaneTiledSWT) {
                map.setRenderManagerInternal(new TiledRenderManagerDynamic());
            } else {
                map.setRenderManagerInternal(new RenderManagerDynamic());
            }
        }
        renderManager = map.getRenderManagerInternal();
        viewport.setRenderManager(renderManager);
        renderManager.setMapDisplay(viewport);
        viewport.addPaneListener(map.getViewportModelInternal());
    }

    /**
     * Context menu to be made visible as needed by tools.
     *
     * @param contextMenu
     */
    public void setMenu(Menu contextMenu) {
        this.menu = contextMenu;
    }

    public Menu getMenu() {
        return menu;
    }

    public IStatusLineManager getStatusLineManager() {
        IWorkbenchPartSite site = part.getSite();
        if (site instanceof IViewSite) {
            IViewSite viewSite = (IViewSite) site;
            return viewSite.getActionBars().getStatusLineManager();
        } else if (site instanceof IEditorSite) {
            IEditorSite editorSite = (IEditorSite) site;
            return editorSite.getActionBars().getStatusLineManager();
        }
        throw new NullPointerException("Unable to determine StatusLineManager");
    }

    /**
     * Will open the menu provided by getMenu().
     * <p>
     * This method is called by tools to open the context menu
     * </p>
     */
    public void openContextMenu() {
        final Menu contextMenu = getMenu();
        final Widget control = getControl();
        if (control != null && !control.isDisposed() && contextMenu != null) {
            control.getDisplay().asyncExec(new Runnable() {
                @Override
                public void run() {
                    if (control == null || control.isDisposed()) {
                        return;
                    }
                    contextMenu.setVisible(true);
                }
            });
        }
    }

    /**
     * Accept the provided tool; call setContext; and activate the tool.
     * <p>
     * The tool is responsible for registering any mouse listeners it requires when setActive( true
     * ) is called. When the tool is replaced setActive( false ) will be called allowing you to
     * clean up your listeners.
     * </p>
     *
     * @param tool
     */
    public void setModalTool(ModalTool tool) {
        if (activeTool != null) {
            // ask the current tool to stop listening etc...
            activeTool.setActive(false);
            activeTool = null;
        }
        if (tool == null) {
            return;
        }
        activeTool = tool;
        activeTool.setContext(getToolContext());
        activeTool.setActive(true);
    }

    /**
     * @return tool context (used to teach tools about our MapViewer facilities)
     */
    protected synchronized ToolContext getToolContext() {
        if (toolcontext == null) {
            toolcontext = new ToolContextImpl();
            toolcontext.setMapInternal(map);
            toolcontext.setRenderManagerInternal(map.getRenderManagerInternal());
        }
        return toolcontext;
    }

    public void setFont(Control control) {
        Display display = control.getDisplay();
        FontData[] data = display.getFontList("courier", true); //$NON-NLS-1$
        if (data.length < 1) {
            data = control.getFont().getFontData();
        }
        for (int i = 0; i < data.length; i++) {
            if (Platform.OS_MACOSX == Platform.getOS())
                data[i].setHeight(12);
            else
                data[i].setHeight(10);
        }
        control.setFont(new Font(control.getDisplay(), data));
    }

    public void setSelectionProvider(IMapEditorSelectionProvider selectionProvider) {
        if (selectionProvider == null) {
            throw new NullPointerException("selection provider must not be null!"); //$NON-NLS-1$
        }
        selectionProvider.setActiveMap(map, mapPart);
        if (part != null) {
            part.getSite().setSelectionProvider(selectionProvider);
        }
    }

    public void dispose() {
        if (this.viewport != null) {
            if (getMap() != null && getMap().getViewportModelInternal() != null) {
                this.viewport.removePaneListener(getMap().getViewportModelInternal());
            }
            this.viewport.dispose();
            this.viewport = null;
            this.map = null;
            this.renderManager = null;
            this.part = null;
            this.menu = null;
        }
    }

    /**
     * For resolutions consider:
     *
     * <pre>
     * <code>
     * WMSTileSet tileSet = (res.resolve( WMSTileSet.class, null ));
     * theResolutions = tileSet.getResolutions();
     * </code>
     * </pre>
     */
    public void setResolutions(double[] resolutions) {
        this.resolutions = resolutions;
        // this should construct a zoom model based on resolution
        // (the default zoom model can be based on common scales)
        // zoomTo will allow people to switch between zoom levels.
        //
        // when these models are defined they will live
        // in the viewport model.
    }

    /**
     * Will zoom to the appropriate level (if a zoom model has been provided by setResolutions
     * call).
     *
     * @param level
     */
    public void zoomTo(final int level) {
        if (resolutions == null) {
            return;
        }
        Display.getCurrent().asyncExec(new Runnable() {
            @Override
            public void run() {
                ViewportModel model = map.getViewportModelInternal();

                double nextresolution = resolutions[level];

                // find the center of the screen
                double centerx = model.getCenter().x;
                double centery = model.getCenter().y;

                // using the next resolution, screen size, and center of the
                // screen
                // compute a bounding box
                double pixelwidth = viewport.getWidth();
                double newunitwidth = nextresolution * pixelwidth;
                double xmin = centerx - (newunitwidth / 2.0);
                double xmax = centerx + (newunitwidth / 2.0);

                double newunitheight = nextresolution * viewport.getHeight();
                double ymin = centery - (newunitheight / 2.0);
                double ymax = centery + (newunitheight / 2.0);

                // new bounding box
                ReferencedEnvelope re = new ReferencedEnvelope(xmin, xmax, ymin, ymax,
                        model.getCRS());

                // create a navigation command to update the viewport
                map.sendCommandSync(new SetViewportBBoxCommand(re));
            }
        });
    }

}
