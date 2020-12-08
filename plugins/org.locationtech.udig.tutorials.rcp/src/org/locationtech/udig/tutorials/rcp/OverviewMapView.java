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
package org.locationtech.udig.tutorials.rcp;

import org.locationtech.udig.project.command.navigation.SetViewportBBoxCommand;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.internal.ui.UDIGDropHandler;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.command.navigation.SetViewportBBoxCommand;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.internal.MapSite;
import org.locationtech.udig.project.ui.internal.wizard.MapImport;
import org.locationtech.udig.project.ui.tool.IMapEditorSelectionProvider;
import org.locationtech.udig.project.ui.tool.ModalTool;
import org.locationtech.udig.project.ui.viewers.MapEditDomain;
import org.locationtech.udig.project.ui.viewers.MapViewer;
import org.locationtech.udig.tools.internal.PanTool;
import org.locationtech.udig.tools.internal.Zoom;
import org.locationtech.udig.tutorials.tracking.glasspane.SeagullGlassPaneOp;
import org.locationtech.udig.tutorials.tracking.glasspane.TrackSeagullOp;

/**
 * A sample map view that contains an overview map in the lower left corner.
 * <p>
 * This overview map tracks the main map and displays a blue box that outlines the area you are
 * currently zoomed to.
 * </p>
 *
 * @author Emily Gouge
 * @since 1.2.0
 * @version 1.3.0
 */
public class OverviewMapView extends ViewPart implements MapPart {

    public static final String ID = "org.locationtech.udig.tutorials.rcp.mapViewOverview"; //$NON-NLS-1$

    private MapViewer mapviewer; // main map viewer

    private MapSite mapSite;

    private OverviewMapViewer overviewmapviewer; // overview map viewer

    private MapEditDomain editDomain;

    public OverviewMapView() {
        super();
    }

    @Override
    public Map getMap() {
        return mapviewer.getMap();
    }

    @Override
    public void openContextMenu() {
        mapviewer.openContextMenu();
    }

    @Override
    public void setFont(Control textArea) {
        mapviewer.getViewport().getControl().setFocus();
    }

    @Override
    public void setSelectionProvider(IMapEditorSelectionProvider selectionProvider) {
        mapviewer.setSelectionProvider(selectionProvider);
    }

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new FormLayout());

        // create two maps
        final Map overviewmap = ProjectFactory.eINSTANCE.createMap();
        final Map mainmap = ProjectFactory.eINSTANCE.createMap();

        mapSite = new MapSite(getViewSite(), this);
        // create overview
        overviewmapviewer = new OverviewMapViewer(parent, this,
                SWT.MULTI | SWT.NO_BACKGROUND | SWT.BORDER, mainmap);
        int size = 25;
        FormData fd = new FormData();
        fd.left = new FormAttachment(0);
        fd.top = new FormAttachment(100 - size);
        fd.right = new FormAttachment(size);
        fd.bottom = new FormAttachment(100);
        overviewmapviewer.getControl().setLayoutData(fd);

        // create map
        editDomain = new MapEditDomain(null);

        mapviewer = new MapViewer(parent, this, SWT.MULTI | SWT.NO_BACKGROUND);
        mapviewer.setMap(mainmap);
        fd = new FormData();
        fd.left = new FormAttachment(0);
        fd.top = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        mapviewer.getControl().setLayoutData(fd);

        // must be called after the main map has a MapViewer otherwise
        // we cannot correctly add necessary listeners
        overviewmapviewer.setMap(overviewmap);
        overviewmapviewer.createLocationBox(mapviewer.getViewport());

        IMenuManager viewMenu = getViewSite().getActionBars().getMenuManager();

        viewMenu.add(new SetBackgroundWMSCAction());
        viewMenu.add(new SetGlassSeagullsAction());
        viewMenu.add(new SetTrackGlassSeagullsAction());

        IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
        toolbar.add(new SetPanToolAction());
        toolbar.add(new SetZoomExtentToolAction());
        toolbar.add(new SetRefreshToolAction());
        toolbar.add(new SetZoomToMapToolAction());

    }

    @Override
    public void setFocus() {
        mapviewer.getViewport().getControl().setFocus();
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        // Normally we might do other stuff here.
    }

    @Override
    public void dispose() {
        if (mapviewer != null && mapviewer.getViewport() != null && getMap() != null) {
            mapviewer.getViewport().removePaneListener(getMap().getViewportModelInternal());
        }
        if (overviewmapviewer != null) {
            overviewmapviewer.dispose();
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

    class SetZoomToMapToolAction extends Action {
        public SetZoomToMapToolAction() {
            super("Zoom to Map"); //$NON-NLS-1$
        }

        @Override
        public void run() {
            ReferencedEnvelope bounds = getMap().getBounds(new NullProgressMonitor());
            getMap().sendCommandASync(new SetViewportBBoxCommand(bounds));
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

    class SetGlassSeagullsAction extends Action {
        private SeagullGlassPaneOp seagullOp;

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
                seagullOp.op(display, getMap(), new NullProgressMonitor());
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
        }

    }

    class SetTrackGlassSeagullsAction extends Action {
        private TrackSeagullOp seagullOp;

        public SetTrackGlassSeagullsAction() {
            super("Add Glass Seagull Tracking layer"); //$NON-NLS-1$
        }

        @Override
        public void run() {
            Display display = Display.getCurrent();
            if (seagullOp == null) {
                seagullOp = new TrackSeagullOp();
            }
            // create a flock of seagulls on a glasspane
            try {
                seagullOp.op(display, getMap(), new NullProgressMonitor());
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
        }

    }

    @Override
    public UDIGDropHandler getDropHandler() {
        // view has no drop support
        return null;
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
    public IStatusLineManager getStatusLineManager() {
        return getViewSite().getActionBars().getStatusLineManager();
    }

    @Override
    public MapSite getMapSite() {
        return mapSite;
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

class OverviewLayoutManager extends Layout {

    @Override
    protected Point computeSize(Composite composite, int hint, int hint2, boolean flushCache) {
        return null;
    }

    @Override
    protected void layout(Composite composite, boolean flushCache) {

    }

}
