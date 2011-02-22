/**
 *
 */
package net.refractions.udig.project.ui.internal;

import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.tool.display.PlaceholderToolbarContributionItem;
import net.refractions.udig.project.ui.tool.IToolManager;
import net.refractions.udig.project.ui.tool.ToolConstants;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.SubCoolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * Used by MapEditor to contribute to the actions bars.
 * <p>
 * For more information please see <b>net.refractions.udig.project.ui.mapEditor</b>.
 * <p>
 * Specifically this class has the responsibility to:
 * <ul>
 * <li>adds {@link net.refractions.udig.project.ui.tool.Tool}s to the actions bars.
 * </ul>
 * This scope may expand to include context specific operations.
 *
 * @author jeichar
 */
public class MapEditorActionBarContributor extends EditorActionBarContributor {
	private SubCoolBarManager subManager;

    @Override
    public void init( IActionBars bars, IWorkbenchPage page ) {
        super.init(bars, page);
    }


	@Override
	public void contributeToCoolBar(ICoolBarManager coolBarManager) {
		if (coolBarManager instanceof SubCoolBarManager) {
			subManager = (SubCoolBarManager) coolBarManager;
		}else
			subManager=new SubCoolBarManager(coolBarManager);

      /*
      * Vitalus:
      * For correct toolbar management by Eclipse platform we MUST
      * provide an ID for the toolbar contribution item. It causes saving/restoring
      * workbench configuration by Eclipse with already created toolbar contribution
      * items right after starting, so the solution is just to create fresh set of contribution items when
      * the toolbar is created - placeholders are needed for correct toolbars management by the Eclipse
      * with the same IDs.
      *
      * If not to do these steps it causes problems during resetting perspective because
      * of some "features" of Eclispe JFace implementation.
      */
        subManager.setVisible(true);

        /*
         * Contribute action tools to the toolbar.
         */
        ToolBarManager actionToolBarManager = new ToolBarManager(SWT.FLAT);
        ApplicationGIS.getToolManager().contributeActionTools(actionToolBarManager, getActionBars());
        if ( actionToolBarManager.getItems().length > 0){
            IContributionItem item = subManager.find(ToolConstants.ACTION_TOOLBAR_ID);
            if(item != null){
                subManager.remove(ToolConstants.ACTION_TOOLBAR_ID);
            }
            ToolBarContributionItem toolBarContributionItem = new ToolBarContributionItem(actionToolBarManager, ToolConstants.ACTION_TOOLBAR_ID);
            subManager.add(toolBarContributionItem);
        }

        /*
         * Contribute modal tools to the toolbar.
         */
        ToolBarManager modalToolBarManager = new ToolBarManager(SWT.FLAT);
        ApplicationGIS.getToolManager().contributeModalTools(modalToolBarManager, getActionBars());
        if ( modalToolBarManager.getItems().length > 0){
            IContributionItem item = subManager.find(ToolConstants.MODAL_TOOLBAR_ID);
            if(item != null){
                subManager.remove(ToolConstants.MODAL_TOOLBAR_ID);
            }
            ToolBarContributionItem toolBarContributionItem = new ToolBarContributionItem(modalToolBarManager, ToolConstants.MODAL_TOOLBAR_ID);
            subManager.add(toolBarContributionItem);
        }

		super.contributeToCoolBar(coolBarManager);
	}

	@Override
	public void contributeToMenu(IMenuManager menuManager) {
	    ApplicationGIS.getToolManager().contributeToMenu(menuManager);
		super.contributeToMenu(menuManager);
	}

	@Override
	public void dispose() {
	    subManager.setVisible(false);

	    PlaceholderToolbarContributionItem actionToolbarPlaceholder = new PlaceholderToolbarContributionItem(ToolConstants.ACTION_TOOLBAR_ID);
	    subManager.add(actionToolbarPlaceholder);
	    PlaceholderToolbarContributionItem modalToolbarPlaceholder = new PlaceholderToolbarContributionItem(ToolConstants.MODAL_TOOLBAR_ID);
	    subManager.add(modalToolbarPlaceholder);

	    getActionBars().updateActionBars();
	    super.dispose();
	}

	@Override
	public void setActiveEditor(IEditorPart targetEditor) {
	    super.setActiveEditor(targetEditor);
        IToolManager toolManager = ApplicationGIS.getToolManager();
        toolManager.contributeGlobalActions(targetEditor, getActionBars());
        toolManager.registerActionsWithPart(targetEditor);
        getActionBars().updateActionBars();
	}
}
