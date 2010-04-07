package net.refractions.udig.ui;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.core.commands.Category;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.AbstractContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.services.IServiceLocator;

/**
 * Add additional operation menu contributions to the screen.
 * <p>
 * This is an experiment it may be too late; since it appears the workbench
 * window is already set up?
 * 
 * @author Jody Garnett
 */
public class StartupOperations implements IStartup {

    public void earlyStartup() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        //workbench.getDisplay().asyncExec(new Runnable() {
        //    public void run() {
        // IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        // if (window != null) {            
        processOperations( workbench );
    }
    
    /**
     * Process operations for the provided scope.
     * 
     * @param workbench
     * @param scope
     */
    protected void processOperations( IWorkbench workbench ){
        IHandlerService handlers = (IHandlerService)workbench.getService( IHandlerService.class );
        ICommandService commands = (ICommandService)workbench.getService( ICommandService.class );
        IMenuService menuService = (IMenuService) workbench.getService(IMenuService.class);
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        
        List<IConfigurationElement> list = ExtensionPointList.getExtensionPointList("net.refractions.udig.ui.operation"); //$NON-NLS-1$
        List<IConfigurationElement> categoryElements = listCategories(list);
        if( categoryElements == null || categoryElements.isEmpty() ) return;
        
        for( IConfigurationElement element : categoryElements ) {
            final String ID = element.getAttribute("id");
            final String NAME = element.getName();
            final String DESCRIPTION = element.getName();
            List<IConfigurationElement> operationElements = listOperationsForCategory(list, ID );
            try {
                // Do not create operation category anymore; only worked for one window
                // categories.put(ID, new OperationCategory(element)); //$NON-NLS-1$
                    
                // Create a Command Category 
                Category category = commands.getCategory(ID);
                if( !category.isDefined()){
                    category.define(NAME, DESCRIPTION);                    
                }
                // TODO: Create an ActionSet
                
                // TODO: Create a Definition to Check the ActionSet
                
                // TODO: Create the MenuGroup
                AbstractContributionFactory categoryAdditions = operationsMenu( menuService, operationElements, "menu:nav?after=layer.ext", ID);
                menuService.addContributionFactory(categoryAdditions);
            } catch (Exception e) {
                UiPlugin.log("Operation category "+ID+":"+e, e);
            }
        }

        for( IConfigurationElement element : list ) {
            final String NAME = element.getName();
            final String ID = element.getAttribute("id");            
            try {
                if (NAME.equals("category")) {//$NON-NLS-1$
                    continue;
                }
                /*
                Command command = commands.getCommand(ID);            
                if( !command.isDefined()){
                    final String DESCRIPTION = element.getName();
                    final String CATEGORY = element.getAttribute("categoryId");
                    
                    // Create the Command
                    Category category = commands.getCategory(CATEGORY);               
                    command.define(NAME, DESCRIPTION, category );
                }
                IHandler handler = new OpHandler( element );
                handlers.activateHandler(ID, handler);
                */
                              
            }
            catch (Exception e) {
                UiPlugin.log("Operation "+ID+":"+e, e);
            }
        }
    }
    /**
     * List all IConfigurationElements with name "category".
     * @param list
     * @return IConfigurationElments with name "category"
     */
    List<IConfigurationElement> listCategories( List<IConfigurationElement> list) {
        List<IConfigurationElement> results = new ArrayList<IConfigurationElement>();
        for( IConfigurationElement element : list ) {
            final String NAME = element.getName();
            final String ID = element.getAttribute("id");            
            if (NAME.equals("category")) {//$NON-NLS-1$
                results.add( element );
            }            
        }
        return results;        
    }
    /**
     * List all IConfigurationElements operations that match the provided categoryId.
     * 
     * @param list List of IConfigurationElement, assumed to come from operation extension point.
     * @param categoryId
     * @return List, perhaps empty, of IConfigurationElements 
     */
    List<IConfigurationElement> listOperationsForCategory( List<IConfigurationElement> list, String categoryId) {
        List<IConfigurationElement> results = new ArrayList<IConfigurationElement>();
        for( IConfigurationElement element : list ) {
            final String NAME = element.getName();
            final String ID = element.getAttribute("id");            
            if (NAME.equals("category")) {//$NON-NLS-1$
                continue;
            }
            final String CATEGORY = element.getAttribute("categoryId");
            if( CATEGORY != null && CATEGORY.equals(categoryId)){
                results.add( element );
            }
        }
        return results;        
    }
    
    /**
     * This will produce an AbstractConfigurationFactory that adds a CommandContribution for
     * each operation in the indicated category.
     * <p>
     * The following are all related "categoryId":
     * <ul>
     * <li>actionSet - actionSet used to toggle this visibility of this contribution
     * <li>expression - true when actionSet is enabled
     * <li>
     * </ul>
     * @param list
     * @param locationURI
     */
    protected AbstractContributionFactory operationsMenu( IMenuService menuService, final List<IConfigurationElement> list, String locationURI, final String categoryId ){        
        return new AbstractContributionFactory(locationURI,null){
            public void createContributionItems( IServiceLocator serviceLocator,
                    IContributionRoot additions ) {
                
            }
        };
    }
}