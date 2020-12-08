/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.rcp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.part.ViewPart;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.internal.ui.UDIGDropHandler;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.commands.AddLayersCommand;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.internal.MapSite;
import org.locationtech.udig.project.ui.internal.wizard.MapImport;
import org.locationtech.udig.project.ui.tool.IMapEditorSelectionProvider;
import org.locationtech.udig.project.ui.tool.ModalTool;
import org.locationtech.udig.project.ui.viewers.MapViewer;
import org.locationtech.udig.tools.internal.PanTool;
import org.locationtech.udig.tools.internal.Zoom;
import org.locationtech.udig.tutorials.tracking.glasspane.SeagullGlassPaneOp;

/**
 * A map view.
 *
 * @author Emily Gouge, Graham Davis (Refractions Research, Inc.)
 * @since 1.1.0
 * @version 1.3.0
 */
public class MapView extends ViewPart implements MapPart {

    public static String ID = "org.locationtech.udig.tutorials.rcp.mapView"; //$NON-NLS-1$

    private MapViewer mapviewer;

    private MapSite mapSite;

    private Map map;

    private SeagullGlassPaneOp seagullOp;

    public MapView() {
        super();
    }

    @Override
    public void createPartControl(Composite parent) {
        FillLayout fillLayout = new FillLayout();
        fillLayout.type = SWT.VERTICAL;
        parent.setLayout(fillLayout);

        mapviewer = new MapViewer(parent, this, SWT.SINGLE | SWT.DOUBLE_BUFFERED);

        mapSite = new MapSite(getViewSite(), this);
        // create a new empty map
        // if you are going to add layers do so now
        // prior to adding to the MapViewer
        //
        map = ProjectFactory.eINSTANCE.createMap();
        mapviewer.setMap(map);

        IMenuManager viewMenu = getViewSite().getActionBars().getMenuManager();
        viewMenu.add(new SetBackgroundFileAction());
        viewMenu.add(new SetBackgroundWMSCAction());
        viewMenu.add(new SetGlassSeagullsAction());

        IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
        toolbar.add(new SetPanToolAction());
        toolbar.add(new SetZoomExtentToolAction());
        toolbar.add(new SetPrintMapLayersToolAction());
        toolbar.add(new SetRefreshToolAction());
        toolbar.add(new SetZoomToMapToolAction());
    }

    class SetPrintMapLayersToolAction extends Action {
        public SetPrintMapLayersToolAction() {
            super("Print Map Layers"); //$NON-NLS-1$
        }

        @Override
        public void run() {
            if (map != null) {
                for (Layer layer : map.getLayersInternal()) {
                    System.out.println(layer + ", isvisible: " + layer.isVisible()); //$NON-NLS-1$
                }
            }
        }
    }

    class SetPanToolAction extends Action {
        public SetPanToolAction() {
            super("Pan"); //$NON-NLS-1$
        }

        private PanTool tool = new PanTool();

        @Override
        public void run() {
            setActive(tool);
        }
    }

    class SetZoomExtentToolAction extends Action {
        Zoom tool = new Zoom();

        public SetZoomExtentToolAction() {
            super("Zoom"); //$NON-NLS-1$
        }

        @Override
        public void run() {
            setActive(tool);
        }
    }

    class SetZoomToMapToolAction extends Action {
        public SetZoomToMapToolAction() {
            super("Zoom to Map"); //$NON-NLS-1$
        }

        @Override
        public void run() {
            ReferencedEnvelope bounds = map.getBounds(new NullProgressMonitor());
            map.sendCommandASync(new SetViewportBBoxCommand(bounds));
        }
    }

    class SetRefreshToolAction extends Action {
        public SetRefreshToolAction() {
            super("Refresh Map"); //$NON-NLS-1$
        }

        @Override
        public void run() {
            mapviewer.getRenderManager().refresh(null);
        }
    }

    class SetBackgroundFileAction extends Action {
        public SetBackgroundFileAction() {
            super("Add Background layer from file..."); //$NON-NLS-1$
        }

        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            Display display = Display.getCurrent();
            final ArrayList<File> files = new ArrayList<>();
            display.syncExec(new Runnable() {
                @Override
                public void run() {
                    FileDialog openDialog = new FileDialog(getSite().getShell(),
                            SWT.OPEN | SWT.MULTI);
                    String file = openDialog.open();
                    if (file == null)
                        return;
                    for (String name : openDialog.getFileNames()) {
                        files.add(new File(openDialog.getFilterPath(), name));
                    }
                }
            });
            if (files.isEmpty())
                return;
            List<IGeoResource> dataHandles = new ArrayList<>();
            ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();

            for (File file : files) {
                try {
                    URL url = file.toURI().toURL();
                    IService handle = catalog.acquire(url, null);
                    if (handle != null) {
                        // connected okay add all resources
                        List<IGeoResource> resources = (List<IGeoResource>) handle
                                .resources(new NullProgressMonitor());
                        dataHandles.addAll(resources);
                    }
                } catch (IOException eek) {
                    String message = "Could not add " + file; //$NON-NLS-1$
                    IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, message, eek);
                    Activator.getDefault().getLog().log(status);
                }
            }
            if (dataHandles.isEmpty()) {
                return; // nothing to add
            }
            map.sendCommandASync(new AddLayersCommand(dataHandles));
        }
    }

    class SetGlassSeagullsAction extends Action {
        public SetGlassSeagullsAction() {
            super("Add Glass Seagulls layer"); //$NON-NLS-1$
        }

        @Override
        public void run() {
            Display display = Display.getCurrent();
            if (seagullOp == null) {
                seagullOp = new SeagullGlassPaneOp();
            }
            // create a flock of seagulls on a glasspane
            try {
                seagullOp.op(display, map, new NullProgressMonitor());
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
        }

    }

    class SetBackgroundWMSCAction extends Action {
        public SetBackgroundWMSCAction() {
            super("Add Background layer..."); //$NON-NLS-1$
        }

        @Override
        public void run() {
            Display display = Display.getCurrent();
            display.syncExec(new Runnable() {
                @Override
                public void run() {
                    MapImport mapImport = new MapImport();
                    mapImport.getDialog().open();
                }
            });
        }
    }

    @Override
    public void setFocus() {
        mapviewer.getViewport().getControl().setFocus();
    }

    public void setModalTool(ModalTool tool) {
        tool.setActive(true);
    }

    @Override
    public Map getMap() {
        return mapviewer.getMap();
    }

    @Override
    public void dispose() {
        if (mapviewer != null && mapviewer.getViewport() != null && getMap() != null) {
            mapviewer.getViewport().removePaneListener(getMap().getViewportModelInternal());
        }
    }

    @Override
    public void openContextMenu() {
        mapviewer.openContextMenu();
    }

    @Override
    public void setFont(Control control) {
        mapviewer.setFont(control);
    }

    @Override
    public void setSelectionProvider(IMapEditorSelectionProvider selectionProvider) {
        mapviewer.setSelectionProvider(selectionProvider);
    }

    @Override
    public IStatusLineManager getStatusLineManager() {
        return getViewSite().getActionBars().getStatusLineManager();
    }

    ModalTool activeTool = null;

    public void setActive(ModalTool tool) {
        if (activeTool == tool) {
            return; // no change
        }
        if (activeTool != null) {
            activeTool.setActive(false);
            activeTool = null;
        }
        tool.setActive(true);
        activeTool = tool;
    }

    @Override
    public MapSite getMapSite() {
        return mapSite;
    }

    @Override
    public UDIGDropHandler getDropHandler() {
        // view has no drop support
        return null;
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean isDragging) {
        // ignore drag source
    }

    @Override
    public void setDirty(boolean isDirty) {
        // ignore dirty state
    }
}
