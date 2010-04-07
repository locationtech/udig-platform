package net.refractions.udig.ui.operations;

import java.util.List;

import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.internal.ui.operations.OperationCategory;
import net.refractions.udig.internal.ui.operations.OperationMenuFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.actions.CompoundContributionItem;

/**
 * Used to fold an operation category into a menu or tool action bar.
 * <p>
 * This is performed with the following menu:
 * 
 * <pre>
 * &lt;extension
 *    point=&quot;org.eclipse.ui.menus&quot;&gt;
 *    ...
 *    &lt;menuContribution
 *      locationURI=&quot;menu:data&quot;&gt;
 *      &lt;separator
 *        name=&quot;mbStart&quot;&gt;
 *      &lt;/separator&gt;
 *      &lt;dynamic
 *      class=&quot;net.refractions.udig.ui.operations.OpCategoryContributionItem:net.refractions.udig.catalog.ui.operation.resourceCategory&quot;
 *      id=&quot;resource.ext&quot;&gt;
 *        &lt;visibleWhen
 *          checkEnabled=&quot;true&quot;&gt;
 *        &lt;/visibleWhen&gt;
 *      &lt;/dynamic&gt;
 *     ...
 *    &lt;.menuContribution&gt;   
 *  &lt;/extension&gt;
 * </pre>
 * 
 * After the ActionBarAdvisor (example UDIGActionBarAdvisor) for your RCP application has been
 * called; the *org.eclipse.ui.menu* extension point is processed.
 * <p>
 * The above dynamic entry will call this class and provide the id of the operation category (via
 * the IExecutableExtension) setInitializationData method.
 * <p>
 * When action bars are doing there thing they will call this dynamic menu and the class will use
 * <code>iPlugin.getDefault().getOperationMenuFactory()</code> to locate the OperationCategory.
 * <p>
 * Based on the number of items in the OperationCategory this dynamic menu contribution will:
 * <ul>
 * <li>Display a seperator followed by a single OpAction
 * <li>Display a sub menu containing several OpActions
 * <li>pending: Display a sub menu containing several OPActions and an "OpCategoryOtherAction"
 * </ul>
 * 
 * @author Jody Garnett
 */
public class OpCategoryContributionItem extends CompoundContributionItem implements IExecutableExtension {
    private IContributionItem[] items;
    private String categoryId;
    
    /**
     * Default constructor - called by org.eclipse.ui.menus extention point.
     */
    public OpCategoryContributionItem() {
    }

    /**
     * I would love to know how or when this is called; my best guess
     * is an IExecutableExtention wrapper is supposed to call this one
     * but I cannot get it to work properly.
     * 
     * @param id I would love this to be the id of the operation category
     */
    public OpCategoryContributionItem( String id ) {
        super(id);
        this.categoryId = id;
    }

    
    /**
     * Generate an array of contribution items; these items
     * are cached (so visibility is the key to controlling this
     * list rather than inclusion).
     * <p>
     * Based on the number of actions in the OperationCategory provided:
     * <ul>
     * <li>1: single operation
     * <li>2-5: sub menu list operations
     * <li>pending:<br>5+: sub menu with operations selected by perspective; along with "Other" dialog
     * </ul> 
     */
    @Override
    protected IContributionItem[] getContributionItems() {
        if( items == null ){
            OperationMenuFactory factory = UiPlugin.getDefault().getOperationMenuFactory();            
            List<IContributionItem> list = factory.createContributionItems( categoryId );
            if( list.size() == 0 ){
                items = new IContributionItem[0];
            }
            else if( list.size() == 1 ){
                items = list.toArray( new IContributionItem[list.size()]);
            }
            else {
                OperationCategory category = factory.findCategory( categoryId );
                MenuManager subMenu = new MenuManager( category.getMenuText(), category.getId() );
                for( IContributionItem item : list ){
                    subMenu.add( item );
                }
                items = new IContributionItem[]{ subMenu, };
            }
        }
        return items;        
    }

    public void setInitializationData( IConfigurationElement config, String propertyName,
            Object data ) throws CoreException {
        if( data instanceof String){
            this.categoryId = (String) data;
        }
    }
}
