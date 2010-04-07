package net.refractions.udig.project.ui.internal.tool.display;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.LayersView;
import net.refractions.udig.project.ui.internal.tool.display.ModalToolCategory.CurrentModalToolContribution;
import net.refractions.udig.ui.operations.OpFilter;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

/*
 * Not sure if this is the best name for this class.
 */
/**
 * Monitors events and things going on in uDig and activates or deactivates tools according to the
 * current context.
 */
public class ToolController implements ISelectionListener {

    OpFilter enablesFor;
    ModalItem toolProxy;

    public ToolController( OpFilter enablesFor, ModalItem toolProxy ) {
        this.enablesFor = enablesFor;
        this.toolProxy = toolProxy;
        IMap map = ApplicationGIS.getActiveMap();
        if (map != ApplicationGIS.NO_MAP) {
            ILayer selectedLayer = map.getEditManager().getSelectedLayer();
            if( selectedLayer!=null )
                enablesFor.accept(new StructuredSelection(selectedLayer));
        }
    }

    public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
        if (toolProxy.isDisposed())
            part.getSite().getPage().getWorkbenchWindow().getSelectionService()
                    .removeSelectionListener(this);

        if (part instanceof LayersView) {

            if (selection instanceof IStructuredSelection) {
                IStructuredSelection structuredSelection = (IStructuredSelection) selection;

                if (enablesFor.accept(structuredSelection)) {
                    activate();
                } else {
                    deactivate();
                }
            }
        }
    }

    /**
     * Activate the toolproxy.
     */
    private void activate() {
        toolProxy.setEnabled(true);
        for( CurrentContributionItem contrib : toolProxy.getContributions() ) {
            if (!(contrib instanceof CurrentModalToolContribution) && contrib instanceof IAction) {
                IAction action = (IAction) contrib;
                action.setEnabled(true);
            }

            if (contrib instanceof AbstractToolbarContributionItem) {
                AbstractToolbarContributionItem item = (AbstractToolbarContributionItem) contrib;
                if (item != null && item.toolItem != null && !item.toolItem.isDisposed())
                    item.toolItem.setEnabled(true);
            }
        }
    }

    /**
     * Deactivate the toolProxy.
     */
    private void deactivate() {
        toolProxy.setEnabled(false);
        for( CurrentContributionItem contrib : toolProxy.getContributions() ) {
            if (!(contrib instanceof CurrentModalToolContribution) && contrib instanceof IAction) {
                IAction action = (IAction) contrib;
                action.setEnabled(false);
            }

            if (contrib instanceof AbstractToolbarContributionItem) {
                AbstractToolbarContributionItem item = (AbstractToolbarContributionItem) contrib;

                boolean enableSelf = false; // If all tools are disabled, disable main button as
                                            // well.
                for( ModalItem tool : item.getTools() ) {
                    if (tool.isEnabled) {
                        enableSelf = true;
                        break;
                    }
                }
                if (!enableSelf) {
                    if (!item.toolItem.isDisposed())
                        item.toolItem.setEnabled(false);
                }
            }
        }

    }
}
