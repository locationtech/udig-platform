package net.refractions.udig.catalog.ui;


import net.refractions.udig.catalog.ui.search.SearchView;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

public class AOIViewActionDelegate extends Action  implements IViewActionDelegate {

    private SearchView view;
    private IStructuredSelection selection;

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run( IAction action ) {
        filterTable(action.isChecked());
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( IAction action, ISelection selection ) {
        try {
            this.selection = (IStructuredSelection) selection;
        } catch (Exception e) { // do nothing
        }
    }
    

    /* (non-Javadoc)
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    @Override
    public void init( IViewPart view ) {
        if (view != null && view instanceof SearchView) {
            this.view = (SearchView) view;
        }
    }
    
    public void setActivePart( IAction action, IWorkbenchPart targetPart ) {
        if (targetPart != null && targetPart instanceof SearchView) {
            view = (SearchView) targetPart;
        }
    }
    
    private void filterTable(boolean filter) {
        if (view != null) {
            view.setAOIFilter(filter);
        }
    }

}
