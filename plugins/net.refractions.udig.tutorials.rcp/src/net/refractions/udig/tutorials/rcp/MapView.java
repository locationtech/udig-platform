/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
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
package net.refractions.udig.tutorials.rcp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.internal.command.navigation.SetViewportBBoxCommand;
import net.refractions.udig.project.internal.commands.AddLayersCommand;
import net.refractions.udig.project.ui.internal.MapImport;
import net.refractions.udig.project.ui.internal.MapPart;
import net.refractions.udig.project.ui.tool.IMapEditorSelectionProvider;
import net.refractions.udig.project.ui.tool.ModalTool;
import net.refractions.udig.project.ui.viewers.MapViewer;
import net.refractions.udig.tools.internal.FixedScalePan;
import net.refractions.udig.tools.internal.Zoom;
import net.refractions.udig.tutorials.tracking.glasspane.SeagullGlassPaneOp;

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

/**
 * A map view.
 * 
 * @author Emily Gouge, Graham Davis (Refractions Research, Inc.)
 * @since 1.1.0
 * @version 1.3.0
 */
public class MapView extends ViewPart implements MapPart {
    public static String ID = "net.refractions.udig.tutorials.rcp.mapView";
    // private GISWidget widget;
    private MapViewer mapviewer;
    // private RenderManager renderManager;
    private Map map;
    private SeagullGlassPaneOp seagullOp;

    public MapView() {
        super();
    }

    @Override
    public void createPartControl( Composite parent ) {
        FillLayout fillLayout = new FillLayout();
        fillLayout.type = SWT.VERTICAL;
        parent.setLayout(fillLayout);
        // mapviewer = new MapViewer(parent, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED | SWT.MULTI);
        mapviewer = new MapViewer(parent, SWT.SINGLE | SWT.DOUBLE_BUFFERED);

        // create a new empty map
        // if you are going to add layers do so now
        // prior to adding to the mapviewer
        // 
        map = (Map) ProjectFactory.eINSTANCE.createMap();
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
        // toolbar.add(new SetPrintTilesRMToolAction());
        // toolbar.add(new SetPrintTilesVPToolAction());
    }

    // class SetPrintTilesRMToolAction extends Action {
    // public SetPrintTilesRMToolAction() {
    //    	      super("Print RM Tiles"); //$NON-NLS-1$
    // }
    // public void run() {
    // if (map != null) {
    // // make this field public for testing
    // Collection<Tile> values =
    // ((TiledRenderManagerDynamic)mapviewer.getRenderManager()).tiles.values();
    // System.out.println("=============== RM tiles: ");
    // List<String> list = new ArrayList();
    // for (Tile tile : values ) {
    // list.add(tile.toString());
    // }
    // Collections.sort(list);
    // for (String s : list ) {
    // System.out.println( s );
    // }
    // System.out.println("=============== /end RM tiles: ");
    // }
    // }
    // }
    //    
    // class SetPrintTilesVPToolAction extends Action {
    // public SetPrintTilesVPToolAction() {
    //  	      super("Print VP Tiles"); //$NON-NLS-1$
    // }
    // public void run() {
    // if (map != null) {
    // // make this field public for testing
    // Collection<Tile> values =
    // ((ViewportPaneTiledSWT)mapviewer.getViewport()).readyTiles.values();
    // System.out.println("=============== VP tiles: ");
    // List<String> list = new ArrayList();
    // for (Tile tile : values ) {
    // list.add(tile.toString());
    // }
    // Collections.sort(list);
    // for (String s : list ) {
    // System.out.println( s );
    // }
    // System.out.println("=============== /end VP tiles: ");
    // }
    // }
    // }

    class SetPrintMapLayersToolAction extends Action {
        public SetPrintMapLayersToolAction() {
            super("Print Map Layers"); //$NON-NLS-1$
        }
        public void run() {
            if (map != null) {
                for( Layer layer : map.getLayersInternal() ) {
                    System.out.println(layer + ", isvisible: " + layer.isVisible());
                }
            }
        }
    }

    class SetPanToolAction extends Action {
        public SetPanToolAction() {
            super("Pan"); //$NON-NLS-1$
        }

        private FixedScalePan tool = new FixedScalePan();
        public void run() {
            setActive(tool);
        }
    }

    class SetZoomExtentToolAction extends Action {
        Zoom tool = new Zoom();
        public SetZoomExtentToolAction() {
            super("Zoom"); //$NON-NLS-1$
        }
        public void run() {
            setActive(tool);
        }
    }

    class SetZoomToMapToolAction extends Action {
        public SetZoomToMapToolAction() {
            super("Zoom to Map"); //$NON-NLS-1$
        }
        public void run() {
            ReferencedEnvelope bounds = map.getBounds(new NullProgressMonitor());
            map.sendCommandASync(new SetViewportBBoxCommand(bounds));
        }
    }

    class SetRefreshToolAction extends Action {
        public SetRefreshToolAction() {
            super("Refresh Map"); //$NON-NLS-1$
        }
        public void run() {
            mapviewer.getRenderManager().refresh(null);
        }
    }

    class SetBackgroundFileAction extends Action {
        public SetBackgroundFileAction() {
            super("Add Background layer from file..."); //$NON-NLS-1$
        }
        @SuppressWarnings("unchecked")
        public void run() {
            Display display = Display.getCurrent();
            final ArrayList<File> files = new ArrayList<File>();
            display.syncExec(new Runnable(){
                public void run() {
                    FileDialog openDialog = new FileDialog(getSite().getShell(), SWT.OPEN
                            | SWT.MULTI);
                    String file = openDialog.open();
                    if (file == null)
                        return;
                    for( String name : openDialog.getFileNames() ) {
                        files.add(new File(openDialog.getFilterPath(), name));
                    }
                }
            });
            if (files.isEmpty())
                return;
            List<IGeoResource> dataHandles = new ArrayList<IGeoResource>();
            ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
            
            for( File file : files ) {
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
                    String message = "Could not add " + file;
                    IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, message, eek);
                    // ExceptionDetailsDialog.openError(null, message, IStatus.ERROR,
                    // Activator.PLUGIN_ID, eek );
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
        public void run() {
            Display display = Display.getCurrent();
            // final ArrayList<File> files = new ArrayList<File>();
            display.syncExec(new Runnable(){
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

    public void setModalTool( ModalTool tool ) {
        tool.setActive(true);
    }
    public Map getMap() {
        return mapviewer.getMap();
    }

    @Override
    public void dispose() {
        if (mapviewer != null && mapviewer.getViewport() != null && getMap() != null) {
            mapviewer.getViewport().removePaneListener(getMap().getViewportModelInternal());
        }
    }

    public void openContextMenu() {
        mapviewer.openContextMenu();
    }

    public void setFont( Control control ) {
        mapviewer.setFont(control);
    }

    public void setSelectionProvider( IMapEditorSelectionProvider selectionProvider ) {
        mapviewer.setSelectionProvider(selectionProvider);
    }

	@Override
	public IStatusLineManager getStatusLineManager() {
		return getViewSite().getActionBars().getStatusLineManager();
	}

    ModalTool activeTool = null;
    public void setActive( ModalTool tool ){
        if( activeTool == tool ){
            return; // no change
        }
        if( activeTool != null ){
            activeTool.setActive(false);
            activeTool = null;
        }
        tool.setActive(true);
        activeTool = tool;
    }
}
