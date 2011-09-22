package net.refractions.udig.project.ui.boundary;

import java.util.Arrays;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.renderer.category.WaterfallBarRenderer;

import com.sun.org.apache.bcel.internal.generic.NEW;

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
    private Listener serviceWatcher = new Listener(){
        public void handleEvent( Event event ) {
            String name; 
            if( event.data instanceof String){
                name = (String) event.data;
            }
            else {
                IBoundaryStrategy currentStrategy = PlatformGIS.getBoundaryService().currentStrategy();
                name = currentStrategy.getName();
            }
            final int index = Arrays.asList( combo.getItems() ).indexOf(name);
            PlatformGIS.asyncInDisplayThread(new Runnable(){
                @Override
                public void run() {
                    combo.select( index );
                }
            }, true);
        }
    };
    private Combo combo;
    
	/**
	 * Boundary View constructor adds the known strategies
	 */
	public BoundaryView() {
	}

	   
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        super.init(site, memento);
        // this is where you read your memento to remember
        // anything the user told you from last time
        // add the default strategy
        
        // This is a workaround to boot strap the strategy choices
        // (we will do an extension point later)
        
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
        IBoundaryService boundaryService = PlatformGIS.getBoundaryService();
        IBoundaryStrategy currentStrategy = boundaryService.currentStrategy();
        boundaryService.addListener(serviceWatcher);
        
        combo = new Combo(parent, SWT.NULL);
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
              String item = combo.getItem(combo.getSelectionIndex());
              IBoundaryStrategy selectedStrategy = strategyList.get(item);
              
              IBoundaryService boundaryService = PlatformGIS.getBoundaryService();
              boundaryService.setStrategy(selectedStrategy);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
              System.out.println("Default selected index: " + combo.getSelectionIndex() + ", selected item: " + (combo.getSelectionIndex() == -1 ? "<null>" : combo.getItem(combo.getSelectionIndex())) + ", text content in the text field: " + combo.getText());
            }
        });
	}

	@Override
	public void setFocus() {
	    combo.setFocus();
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
	@Override
	public void dispose() {
	    super.dispose();
	    if( serviceWatcher != null ){
	        IBoundaryService boundaryService = PlatformGIS.getBoundaryService();
	        if( boundaryService != null ){
	            boundaryService.removeListener( serviceWatcher );
	        }
	        serviceWatcher = null;
	    }
	}
}
