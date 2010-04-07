package net.refractions.udig.project.ui.tool;

import java.util.List;

import org.eclipse.jface.action.IContributionItem;

/**
 * 
 * If the currently active <code>ModalTool</code> implements this interface
 * the <code>MapEditor</code> asks it to contribute to the context menu programmatically.
 * 
 * 
 * @author Vitalus
 * @since UDIG 1.1.0
 *
 */
public interface IContextMenuContributionTool {
    
    
    
    /**
     * The ModalTool may implement this method to contribute
     * programmatially to the context menu on the MapEditor.
     * 
     * <p>
     * For the contribution of <code>IAction</code> use 
     * <code>contributions.add(new ActionContributionItem(IAction))</code>.
     * 
     * @param contributions
     */
    public void contributeContextMenu(List<IContributionItem> contributions);
    

}
