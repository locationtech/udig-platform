package net.refractions.udig.project.ui.boundary;

import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.boundary.IBoundaryService;
import net.refractions.udig.boundary.IBoundaryStrategy;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.IProject;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.boundary.BoundaryStrategyMapCrs;
import net.refractions.udig.project.ui.internal.boundary.BoundaryStrategyScreen;
import net.refractions.udig.project.ui.tool.IToolManager;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * This is the view that allows a user to select the method to define the boundary
 * @author pfeiffp
 *
 */
public class BoundaryView extends ViewPart {

    /*
     * A list of all the strategies and their labels
     */
    private Map<String,IBoundaryStrategy> strategyList = new HashMap<String,IBoundaryStrategy>();
    
    /*
     * the boundary service
     */
    private static IBoundaryService boundaryService = PlatformGIS.getBoundaryService();
    
	/**
	 * Boundary View constructor adds the known strategies
	 */
	public BoundaryView() {
		// add the default strategy
		this.addBoundaryStrategy(PlatformGIS.getBoundaryService().currentStrategy());
        // add other strategies
        this.addBoundaryStrategy(new BoundaryStrategyScreen());
        this.addBoundaryStrategy(new BoundaryStrategyMapCrs());
		
	}

	@Override
	public void createPartControl(Composite parent) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        parent.setLayout( layout );
        Label label = new Label(parent, SWT.LEFT );
        label.setLayoutData( new GridData(SWT.LEFT,SWT.TOP,false,false ) );
        label.setText("Boundary: ");
        
        // get the current strategy
        IBoundaryStrategy currentStrategy = boundaryService.currentStrategy();
        
        final Combo combo = new Combo(parent, SWT.NULL);
        for (String comboLabel: this.strategyList.keySet()) {
        	combo.add(comboLabel);
        	// select the current strategy
        	if (currentStrategy.getClass().equals(this.strategyList.get(comboLabel).getClass())) {
        		combo.select(combo.indexOf(comboLabel));
        	}
        }
        combo.setLayoutData( new GridData(SWT.LEFT,SWT.TOP,true,false ) );
        
        
        combo.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
              IBoundaryStrategy selectedStrategy = strategyList.get(combo.getItem(combo.getSelectionIndex()));
              boundaryService.setStrategy(selectedStrategy);
              enableFunctions(selectedStrategy);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
              System.out.println("Default selected index: " + combo.getSelectionIndex() + ", selected item: " + (combo.getSelectionIndex() == -1 ? "<null>" : combo.getItem(combo.getSelectionIndex())) + ", text content in the text field: " + combo.getText());
            }
        });
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		// TODO Auto-generated method stub
		super.init(site, memento);
	}

	/**
	 * Adds a Boundary Strategy to the view
	 * @param strategy
	 * @return boolean true if strategy was added
	 */
	public boolean addBoundaryStrategy(IBoundaryStrategy strategy) {
		if (!this.strategyList.containsKey(strategy.getName()) && !this.strategyList.containsValue(strategy)) {
			this.strategyList.put(strategy.getName(), strategy);
			return true;
		}
		return false;
	}
	
	private void enableFunctions(IBoundaryStrategy selectedStrategy) {
		
		IMap activeMap = ApplicationGIS.getActiveMap();
		IProject activeProject = ApplicationGIS.getActiveProject();
		IToolManager toolManager = ApplicationGIS.getToolManager();
		IAction zoomExtentTool = toolManager.getToolAction("net.refractions.udig.tools.ZoomExtent", "net.refractions.udig.tool.category.zoom" );
		
		if (zoomExtentTool != null) {
			System.out.println(zoomExtentTool);
			zoomExtentTool.setEnabled(selectedStrategy.enableZoomToExtent());
		} else {
			System.out.println("zoom tool not found");
		}
		if (selectedStrategy.enableSearchCatalog()) {
			System.out.println("search enabled");
		} else {
			System.out.println("search disabled");
		}
		if (selectedStrategy.enableZoomToExtent()) {
			System.out.println("zoom enabled");
		} else {
			System.out.println("zoom disabled");
		}
	}
}
