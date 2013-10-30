/*
 * Created on Mar 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.refractions.udig.internal.ui.operations;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.ui.operations.OpAction;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;

/**
 * A category of operations - Listens to the contained actions and disables it self
 * if none of its contained Actions are visible.
 * <p>
 * The operations extension point is acting as a short hand for setting up
 * ActionSets, Definitions, Commands, Handlers and Menus quickly and efficiently.
 * As such the OperationCategory information has been retargetted into the
 * following:
 * <ul>
 * <li>ActionSet: used to control visibility of associated operations in one go
 * <li>Definition; checks if the ActionSet is turned on for the current perspective
 * <li>Category: CommandCategory used to group commands in the Other Dialog
 * <li>Menu: Slotted into a  few specific menus using IStartup
 * <li>Popup: Slotted into a few Context menus
 * </ul>
 * @author jeichar
 */
public class OperationCategory extends MenuManager{
	List<OpAction> actions=new ArrayList<OpAction>();
	MenuManager manager;
    String description;
	
	/**
	 * new instance 
	 */
	public OperationCategory(IConfigurationElement element) {
		super( element.getAttribute("name"), element.getAttribute("id")); //$NON-NLS-1$ //$NON-NLS-2$
		description = element.getAttribute("description");
	}
	
	public MenuManager createContextMenu(){
		manager=new MenuManager(getMenuText(), getId());
		for( OpAction action:actions){
			if( action.isEnabled() ){
				manager.add( action );
			}
		}
		return manager;
	}
	
	/**
	 * Optional description of this category.
	 * 
	 * @return Description of this category, may be null.
	 */
	public String getDescription() {
        return description;
    }
	
	/**
	 * All actions added must be instances of OpAction
	 * @see org.eclipse.jface.action.ContributionManager#add(org.eclipse.jface.action.IAction)
	 */
	public void add(IAction action) {
		assert action instanceof OpAction;
		actions.add((OpAction) action);
        ((OpAction)action).setCategory(this);
		super.add(action);
	}

	/**
	 * @param selection
	 */
	public void setSelection(ISelection selection) {
		for(OpAction action:actions){
			action.selectionChanged(null, selection);
		}	
	}
    
    /**
     * Contained OpActions call this to notify the category when its enablement has changed.
     */
    public void enablementChanged( ){
        boolean enabled=false;
        for(OpAction action: actions){
            if( action.isEnabled() ){
                enabled=true;
                break;
            }
        }
        if( getMenu() != null && getMenu().getParentItem()!=null ){
            getMenu().getParentItem().setEnabled(enabled);
        }
    }
    
    /**
     * gets all the Operation actions in the category.
     */
    public List<OpAction> getActions() {
        return actions;
    }
    @Override
    public String toString() {
        return getMenuText();
    }
}
