/**
 * 
 */
package net.refractions.udig.project.ui.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.internal.ui.IDropTargetProvider;
import net.refractions.udig.project.IProject;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.commands.CreateMapCommand;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.tool.IMapEditorSelectionProvider;
import net.refractions.udig.project.ui.tool.IToolManager;
import net.refractions.udig.project.ui.viewers.MapEditDomain;
import net.refractions.udig.project.ui.viewers.MapViewer;
import net.refractions.udig.ui.UDIGDragDropUtilities;
import net.refractions.udig.ui.UDIGDragDropUtilities.DropTargetDescriptor;

import org.eclipse.core.runtime.IProgressMonitor;
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

/**
 * An viewpart for easily including a map in a view. To use, override getResources to configure the layers in the map.  Also override acquireToolbar if the
 * view should acquire a the toolbar when the view has focus.  Init map can be overridden to further configure the map.
 * 
 * @author jeichar
 * 
 * @version 1.3.0
 */
public abstract class DefaultMapViewPart extends ViewPart implements MapPart, IDropTargetProvider {

	protected Map map;

	MapViewer viewer;

	private MapEditorSelectionProvider selectionProvider;

	private DropTargetDescriptor dropTarget;

	private IToolManager toolManager;

	private MapEditDomain editDomain;

	/**
	 * Returns the map that is to be displayed in the view.
	 */
	public Map getMap() {
		return viewer.getMap();
	}
	public void openContextMenu() {
		viewer.openContextMenu();
	}

	public void setFont(Control textArea) {
		viewer.setFont(textArea);
	}

	public void setSelectionProvider(
			IMapEditorSelectionProvider selectionProvider) {
		viewer.setSelectionProvider(selectionProvider);		
	}

	@Override
	public final void createPartControl(Composite parent) {
		editDomain = new MapEditDomain(null);
		try {
			IProgressMonitor monitor = getViewSite().getActionBars().getStatusLineManager().getProgressMonitor();
			viewer = new MapViewer(parent, SWT.DOUBLE_BUFFERED);
			List<IGeoResource> resources = new ArrayList<IGeoResource>();
			createResources(resources, monitor);
			IProject activeProject = ApplicationGIS.getActiveProject();

			CreateMapCommand command = new CreateMapCommand("NewMap",resources , activeProject);
			activeProject.sendSync(command);
			Map createdMap = (Map) command.getCreatedMap();
			viewer.setMap(createdMap);
			viewer.init(this);

			
			// ---------------
	        this.selectionProvider = new MapEditorSelectionProvider();

	        selectionProvider.setActiveMap(createdMap, this);
	        getSite().setSelectionProvider(selectionProvider);

	        selectionProvider.setSelection(new StructuredSelection(new Object[]{createdMap}));
	        
	        createContextMenu();
	        
	        if(acquireToolbar()) {
		        toolManager = ApplicationGIS.getToolManager();
		        IActionBars bars = getViewSite().getActionBars();
				IToolBarManager toolbarManager = bars.getToolBarManager();
		        toolManager.contributeModalTools(toolbarManager, bars);
		        toolbarManager.add(new Separator());
		        toolManager.contributeActionTools(toolbarManager, bars);
		        toolManager.setCurrentEditor(this);
	        }
	        if(createdMap.getLayersInternal().size() > 0) {
	        	createdMap.getEditManagerInternal().setSelectedLayer(createdMap.getLayersInternal().get(createdMap.getMapLayers().size()-1));
	        }
	        
	        enableDropSupport();
	        
	        initMap(createdMap);
	        
	        
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * A hook for configuring the map.  The map may already be open and thus if many updates are made consideration about how often the map is
	 * re-rendered should be considered.  Likely eventing will need to be disabled and after one should manually re-enable them.
	 * 
	 * Default behaviour is to simply zoom to extents.
	 */
	protected void initMap(Map createdMap) {
		viewer.getMap().getViewportModelInternal().zoomToExtent();
	}
	/** 
	 * Create the resources to put in the map.  If more configuration is required then override {@link #initMap(Map)} and put other configuration in that method
	 * @param resources the collection to add the resources to 
	 * @throws IOException 
	 */
	protected abstract void createResources(List<IGeoResource> resources, 
			IProgressMonitor monitor) throws IOException;
	
	/**
	 * Override and return if the view should acquire the toolbar when focus is set on the viewer.
	 */
	protected boolean acquireToolbar() {
		return false;
	}
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		if(acquireToolbar()) {
	        toolManager.setCurrentEditor(this);
		}
		viewer.getControl().setFocus();
	}
	public Object getTarget(DropTargetEvent event) {
		return this;
	}


	private void enableDropSupport() {
		dropTarget = UDIGDragDropUtilities.addDropSupport(viewer.getViewport()
				.getControl(), this);
	}
	@Override
	public void dispose() {
		super.dispose();
		dropTarget.target.dispose();
	}
	
    protected void createContextMenu() {
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

}
