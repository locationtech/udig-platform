package net.refractions.udig.tutorials.rcp;

import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectFactory;
import net.refractions.udig.project.internal.command.navigation.SetViewportBBoxCommand;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.MapImport;
import net.refractions.udig.project.ui.internal.MapPart;
import net.refractions.udig.project.ui.internal.tool.display.ToolManager;
import net.refractions.udig.project.ui.tool.IMapEditorSelectionProvider;
import net.refractions.udig.project.ui.tool.IToolManager;
import net.refractions.udig.project.ui.tool.ModalTool;
import net.refractions.udig.project.ui.viewers.MapEditDomain;
import net.refractions.udig.project.ui.viewers.MapViewer;
import net.refractions.udig.tools.internal.ScrollPanTool;
import net.refractions.udig.tools.internal.Zoom;
import net.refractions.udig.tutorials.tracking.glasspane.SeagullGlassPaneOp;
import net.refractions.udig.tutorials.tracking.glasspane.TrackSeagullOp;

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

    public static final String ID = "net.refractions.udig.tutorials.rcp.mapViewOverview"; //$NON-NLS-1$

    private MapViewer mapviewer; // main map viewer
    private OverviewMapViewer overviewmapviewer; // overview map viewer

	private MapEditDomain editDomain;

    public OverviewMapView() {
        super();
    }
    public Map getMap() {
        return mapviewer.getMap();
    }

    public void openContextMenu() {
        mapviewer.openContextMenu();
    }

    public void setFont( Control textArea ) {
        mapviewer.getViewport().getControl().setFocus();
    }

    public void setSelectionProvider( IMapEditorSelectionProvider selectionProvider ) {
        mapviewer.setSelectionProvider(selectionProvider);
    }

    @Override
    public void createPartControl( Composite parent ) {
        parent.setLayout(new FormLayout());

        // create two maps
        final Map overviewmap = (Map) ProjectFactory.eINSTANCE.createMap();
        final Map mainmap = (Map) ProjectFactory.eINSTANCE.createMap();

        // create overview
        overviewmapviewer = new OverviewMapViewer(parent, SWT.MULTI | SWT.NO_BACKGROUND
                | SWT.BORDER, mainmap);
        int size = 25;
        FormData fd = new FormData();
        fd.left = new FormAttachment(0);
        fd.top = new FormAttachment(100 - size);
        fd.right = new FormAttachment(size);
        fd.bottom = new FormAttachment(100);
        overviewmapviewer.getControl().setLayoutData(fd);

        // create map
    	editDomain = new MapEditDomain(null);

        mapviewer = new MapViewer(parent, SWT.MULTI | SWT.NO_BACKGROUND);
        mapviewer.setMap(mainmap);
        fd = new FormData();
        fd.left = new FormAttachment(0);
        fd.top = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        mapviewer.getControl().setLayoutData(fd);

        // must be called after the mainmap has a mapviewer otherwise
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
    public void init( IViewSite site ) throws PartInitException {
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

        private ScrollPanTool tool = new ScrollPanTool();
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
    class SetZoomToMapToolAction extends Action {
        public SetZoomToMapToolAction() {
            super("Zoom to Map"); //$NON-NLS-1$
        }
        public void run() {
            ReferencedEnvelope bounds = getMap().getBounds(new NullProgressMonitor());
            getMap().sendCommandASync(new SetViewportBBoxCommand(bounds));
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

    class SetGlassSeagullsAction extends Action {
        private SeagullGlassPaneOp seagullOp;
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

    class SetBackgroundWMSCAction extends Action {
        public SetBackgroundWMSCAction() {
            super("Add Background layer..."); //$NON-NLS-1$
        }
        public void run() {
            Display display = Display.getCurrent();
            display.syncExec(new Runnable(){
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
}

class OverviewLayoutManager extends Layout {

    @Override
    protected Point computeSize( Composite composite, int hint, int hint2, boolean flushCache ) {
        return null;
    }

    @Override
    protected void layout( Composite composite, boolean flushCache ) {
    }

}
