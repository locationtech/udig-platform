package x;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.internal.ui.IDropTargetProvider;
import net.refractions.udig.project.IProject;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.commands.CreateMapCommand;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.MapEditorSelectionProvider;
import net.refractions.udig.project.ui.internal.MapPart;
import net.refractions.udig.project.ui.tool.IMapEditorSelectionProvider;
import net.refractions.udig.project.ui.tool.IToolManager;
import net.refractions.udig.project.ui.viewers.MapViewer;
import net.refractions.udig.ui.UDIGDragDropUtilities;
import net.refractions.udig.ui.UDIGDragDropUtilities.DropTargetDescriptor;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

public class View extends ViewPart implements MapPart, IDropTargetProvider{
	public static final String ID = "X.view";

	MapViewer viewer;

	private MapEditorSelectionProvider selectionProvider;

	private DropTargetDescriptor dropTarget;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		try {
			
			viewer = new MapViewer(parent, SWT.DOUBLE_BUFFERED);
//			List<IGeoResource> resources = addTileService();
			List<IGeoResource> resources = addWMSService();
			
			IProject activeProject = ApplicationGIS.getActiveProject();

			CreateMapCommand command = new CreateMapCommand("NewMap",resources , activeProject);
			activeProject.sendSync(command);
			Map createdMap = (Map) command.getCreatedMap();
			viewer.setMap(createdMap);
			viewer.init(this);

			viewer.getMap().getViewportModelInternal().setBounds(485412.344, 833840.7, 75270, 295935);
			// ---------------
			
	        this.selectionProvider = new MapEditorSelectionProvider();

	        selectionProvider.setActiveMap(createdMap, this);
	        getSite().setSelectionProvider(selectionProvider);

	        selectionProvider.setSelection(new StructuredSelection(new Object[]{createdMap}));
	        
	        createContextMenu();
	        
	        IToolManager toolManager = ApplicationGIS.getToolManager();
	        IActionBars bars = getViewSite().getActionBars();
			IToolBarManager toolbarManager = bars.getToolBarManager();
	        toolManager.contributeModalTools(toolbarManager, bars);
	        toolbarManager.add(new Separator());
	        toolManager.contributeActionTools(toolbarManager, bars);
	        
	        createdMap.getEditManagerInternal().setEditLayerLocked(true);
	        createdMap.getEditManagerInternal().setSelectedLayer(createdMap.getLayersInternal().get(0));
	        
	        enableDropSupport();
	        

	        toolManager.setCurrentEditor(this);
	        
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	

	private void enableDropSupport() {
		dropTarget = UDIGDragDropUtilities.addDropSupport(viewer.getViewport().getControl(), this);
	}

	private List<IGeoResource> addWMSService() throws IOException {
		IService service = CatalogPlugin.getDefault().getLocalCatalog().acquire(new URL("http://localhost:8080/geoserver/wms?version=1.3.0&service=WMS&request=getCapabilities"),null);

		List<IGeoResource> resources = new ArrayList<IGeoResource>();
		for (IGeoResource resource : service.resources(null)) {
			String title = resource.getInfo(null).getTitle();
			if(title.contains("CH")) {
				resources.add(resource);
				break;
			}
		}
		
		addShpService("/Users/jeichar/Desktop/countries.shp",resources);
		return resources;
	}
	
	private void addShpService(String path,List<IGeoResource> resources) throws IOException {
		IService service = CatalogPlugin.getDefault().getLocalCatalog().acquire(new URL("file://"+path),null);
		
		resources.addAll(service.resources(null));
	}

	private List<IGeoResource> addTileService() throws IOException {
		IService service = CatalogPlugin.getDefault().getLocalCatalog().acquire(new URL("http://localhost:8080/geoserver/gwc/service/wms?version=1.1.1&service=WMS&request=getCapabilities&tiled=true"),null);

		List<IGeoResource> resources = new ArrayList<IGeoResource>();
		for (IGeoResource resource : service.resources(null)) {
			String title = resource.getInfo(null).getTitle();
			if(title.contains("CH") && title.contains("png")) {
				resources.add(resource);
				break;
			}
		}	
		return resources;
	}

	@Override
	public void dispose() {
		super.dispose();
		dropTarget.target.dispose();
	}
	
    void createContextMenu() {
        Menu menu;
        menu = viewer.getMenu();
        if (menu == null) {
            final MenuManager contextMenu = new MenuManager();
            contextMenu.setRemoveAllWhenShown(true);
            contextMenu.addMenuListener(new IMenuListener(){
                public void menuAboutToShow( IMenuManager mgr ) {
                    IToolManager tm = ApplicationGIS.getToolManager();

                    contextMenu.add(new Separator());
                    contextMenu.add(tm.getBACKWARD_HISTORYAction());
                    contextMenu.add(tm.getFORWARD_HISTORYAction());
                    contextMenu.add(new Separator());
                    /*
                     * Gets contributions from active modal tool if possible
                     */
//                    tm.contributeActiveModalTool(contextMenu);

                    contextMenu.add(new Separator());
                    contextMenu.add(ApplicationGIS.getToolManager().createOperationsContextMenu(selectionProvider.getSelection()));
                }
            });

            // Create menu.
            menu = contextMenu.createContextMenu(viewer.getViewport().getControl());
            viewer.setMenu(menu);
            getSite().registerContextMenu(contextMenu, getSite().getSelectionProvider());
        }
    }
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public Map getMap() {
		return viewer.getMap();
	}

	@Override
	public void openContextMenu() {
		viewer.openContextMenu();
	}

	@Override
	public void setFont(Control textArea) {
		viewer.setFont(textArea);
	}

	@Override
	public void setSelectionProvider(
			IMapEditorSelectionProvider selectionProvider) {
		viewer.setSelectionProvider(selectionProvider);		
	}

	@Override
	public Object getTarget(DropTargetEvent event) {
		return this;
	}
}