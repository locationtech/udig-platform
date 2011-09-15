package net.refractions.udig.project.ui.limit;

import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.limit.ILimitService;
import net.refractions.udig.limit.ILimitStrategy;
import net.refractions.udig.project.ui.internal.limit.LimitStrategyMapCrs;
import net.refractions.udig.project.ui.internal.limit.LimitStrategyScreen;
import net.refractions.udig.ui.PlatformGIS;

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
 * This is the view that allows a user to select the method to define the limit
 * @author pfeiffp
 *
 */
public class LimitView extends ViewPart {

    /*
     * A list of all the strategies and their labels
     */
    private Map<String,ILimitStrategy> strategyList = new HashMap<String,ILimitStrategy>();
    
    /*
     * the limit service
     */
    private static ILimitService limitService = PlatformGIS.getLimitService();
    
	/**
	 * Limit View constructor adds the known strategies
	 */
	public LimitView() {
		// add the default strategy
		this.addLimitStrategy(PlatformGIS.getLimitService().currentStrategy());
        // add other strategies
        this.addLimitStrategy(new LimitStrategyScreen());
        this.addLimitStrategy(new LimitStrategyMapCrs());
		
	}

	@Override
	public void createPartControl(Composite parent) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        parent.setLayout( layout );
        Label label = new Label(parent, SWT.LEFT );
        label.setLayoutData( new GridData(SWT.LEFT,SWT.TOP,false,false ) );
        label.setText("Limit: ");
        
        // get the current strategy
        ILimitStrategy currentStrategy = limitService.currentStrategy();
        
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
              ILimitStrategy selectedStrategy = strategyList.get(combo.getItem(combo.getSelectionIndex()));
              limitService.setStrategy(selectedStrategy);
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
	 * Adds a Limit Strategy to the view
	 * @param strategy
	 * @return boolean true if strategy was added
	 */
	public boolean addLimitStrategy(ILimitStrategy strategy) {
		if (!this.strategyList.containsKey(strategy.getName()) && !this.strategyList.containsValue(strategy)) {
			this.strategyList.put(strategy.getName(), strategy);
			return true;
		}
		return false;
	}
}
